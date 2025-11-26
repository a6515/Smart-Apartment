package org.smartapartment.smartapartment.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.smartapartment.smartapartment.common.PageResult;
import org.smartapartment.smartapartment.entity.Bed;
import org.smartapartment.smartapartment.entity.CheckInApplication;
import org.smartapartment.smartapartment.entity.Room;
import org.smartapartment.smartapartment.entity.TransferApplication;
import org.smartapartment.smartapartment.exception.BusinessException;
import org.smartapartment.smartapartment.mapper.BedMapper;
import org.smartapartment.smartapartment.mapper.CheckInApplicationMapper;
import org.smartapartment.smartapartment.mapper.RoomMapper;
import org.smartapartment.smartapartment.mapper.TransferApplicationMapper;
import org.smartapartment.smartapartment.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 换宿申请服务类
 * 
 * 核心职责：处理学生换宿申请的完整流程
 * - 提交申请：学生申请从当前房间换到目标房间
 * - 审批申请：管理员审批并分配新床位
 * - 自动处理：释放旧床位、占用新床位、更新房间状态、更新入住记录
 */
@Service
@RequiredArgsConstructor
public class TransferService {
    
    private final TransferApplicationMapper transferMapper;
    private final CheckInApplicationMapper checkInMapper;
    private final RoomMapper roomMapper;
    private final BedMapper bedMapper;
    
    /**
     * 分页查询换宿申请
     * 
     * @param current 当前页码
     * @param size 每页大小
     * @param studentId 学生ID（可选，用于查询指定学生的申请）
     * @param status 申请状态（可选，1-待审核 2-已同意 3-已驳回 4-已撤销 5-已完成）
     * @return 分页结果，包含学生信息、当前房间、目标房间、审批信息
     * 
     * 涉及表：transfer_application + sys_user + building + room + bed（关联查询）
     */
    public PageResult<TransferApplication> getPage(Long current, Long size, Long studentId, Integer status) {
        PageHelper.startPage(current.intValue(), size.intValue());
        List<TransferApplication> list = transferMapper.selectPage(studentId, status);
        PageInfo<TransferApplication> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList(), current, size);
    }
    
    /**
     * 提交换宿申请
     * 
     * 业务流程：
     * 1. 验证学生必须已入住（check_in_application.status=4）
     * 2. 检查是否有未处理的换宿申请（状态1或12）
     * 3. 从当前入住记录获取当前房间信息：
     *    - 通过room表获取实际楼宇ID（修复了之前使用preferred_building_id的错误）
     *    - 当前房间ID、床位ID
     * 4. 插入transfer_application记录，状态为1（待审核）
     * 
     * @param application 换宿申请对象（前端提供：checkInApplicationId, targetBuildingId, targetRoomId, targetRoomType, reason, description）
     * @throws BusinessException 未入住、有未处理申请、当前房间信息不存在时抛出
     * 
     * 涉及表：transfer_application（INSERT）、check_in_application（SELECT）、room（SELECT）
     */
    @Transactional
    public void submitApplication(TransferApplication application) {
        // 1. 检查学生是否已入住
        CheckInApplication checkIn = checkInMapper.selectById(application.getCheckInApplicationId());
        if (checkIn == null || checkIn.getStatus() == null || checkIn.getStatus() != 4) {
            throw new BusinessException("您还未入住，无法申请换宿");
        }
        
        // 2. 检查是否有未处理的换宿申请
        List<TransferApplication> existingApplications = transferMapper.selectPage(
            SecurityUtils.getCurrentUserId(), null);
        boolean hasPending = existingApplications.stream()
            .anyMatch(app -> app.getStatus() == 1 || app.getStatus() == 2);
        if (hasPending) {
            throw new BusinessException("您已有待处理的换宿申请，请勿重复提交");
        }
        
        // 3. 填充当前房间信息（需要获取实际楼宇ID）
        application.setStudentId(SecurityUtils.getCurrentUserId());
        
        // 从当前房间获取楼宇ID
        Room currentRoom = roomMapper.selectById(checkIn.getAssignedRoomId());
        if (currentRoom == null) {
            throw new BusinessException("当前房间信息不存在");
        }
        
        application.setCurrentBuildingId(currentRoom.getBuildingId());
        application.setCurrentRoomId(checkIn.getAssignedRoomId());
        application.setCurrentBedId(checkIn.getAssignedBedId());
        
        // 4. 保存申请
        transferMapper.insert(application);
    }
    
    /**
     * 审批换宿申请（分配床位）
     * 
     * 业务流程：
     * 1. 验证申请状态必须为1（待审核）
     * 2. 更新审批人信息、审批时间、审批意见
     * 3. 如果审批通过（status=2）且分配了新床位：
     *    【步骤1】释放旧床位：
     *       - bed_status=1（空闲）
     *       - 清除student_id、student_name、check_in_time
     *    【步骤2】占用新床位：
     *       - bed_status=2（已占用）
     *       - 设置student_id=申请人ID
     *       - 设置student_name=申请人姓名
     *       - 设置check_in_time=当前时间
     *    【步骤3】更新旧房间：
     *       - available_beds+1
     *       - 更新room_status（1-空闲 2-部分入住 3-已满）
     *    【步骤4】更新新房间：
     *       - available_beds-1
     *       - 更新room_status
     *    【步骤5】更新入住申请记录（核心！）：
     *       - assigned_room_id=新房间ID
     *       - assigned_bed_id=新床位ID
     *       - 此步骤确保"我的房间"页面显示新位置
     *    【步骤6】更新换宿申请状态：
     *       - status=5（已完成）
     * 
     * @param id 申请ID
     * @param status 审批结果（2-同意 3-驳回）
     * @param remark 审批意见
     * @param bedId 分配的新床位ID（同意时必填）
     * @throws BusinessException 申请不存在、已处理、床位不可用时抛出
     * 
     * 涉及表：transfer_application（UPDATE 2次）、bed（UPDATE 2次）、room（UPDATE 2次）、check_in_application（UPDATE）
     * 注意：此方法使用事务，确保所有操作原子性，任何一步失败全部回滚
     */
    @Transactional
    public void approve(Long id, Integer status, String remark, Long bedId) {
        TransferApplication application = transferMapper.selectById(id);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        
        if (application.getStatus() != 1) {
            throw new BusinessException("该申请已被处理");
        }
        
        application.setId(id);
        application.setStatus(status);
        application.setApproverId(SecurityUtils.getCurrentUserId());
        application.setApproverName(SecurityUtils.getCurrentUsername());
        application.setRemark(remark);
        application.setApproveTime(LocalDateTime.now());
        
        if (status == 2 && bedId != null) {
            application.setAssignedBedId(bedId);
        }
        
        transferMapper.updateById(application);
        
        // 如果同意换宿，需要更新床位和房间状态
        if (status == 2 && bedId != null) {
            // 1. 释放原床位
            Bed oldBed = bedMapper.selectById(application.getCurrentBedId());
            if (oldBed != null) {
                oldBed.setBedStatus(1); // 设置为空闲
                oldBed.setStudentId(null);
                oldBed.setStudentName(null);
                oldBed.setCheckInTime(null);
                bedMapper.updateById(oldBed);
            }
            
            // 2. 占用新床位
            Bed newBed = bedMapper.selectById(bedId);
            if (newBed == null) {
                throw new BusinessException("床位不存在");
            }
            if (newBed.getBedStatus() != 1) {
                throw new BusinessException("床位已被占用");
            }
            
            // 设置新床位的学生信息
            newBed.setBedStatus(2); // 设置为已占用
            newBed.setStudentId(application.getStudentId());
            newBed.setStudentName(application.getStudentName());
            newBed.setCheckInTime(LocalDateTime.now());
            bedMapper.updateById(newBed);
            
            // 3. 更新原房间状态
            Room oldRoom = roomMapper.selectById(application.getCurrentRoomId());
            if (oldRoom != null) {
                oldRoom.setAvailableBeds(oldRoom.getAvailableBeds() + 1);
                if (oldRoom.getAvailableBeds() >= oldRoom.getTotalBeds()) {
                    oldRoom.setRoomStatus(1); // 空闲
                } else if (oldRoom.getAvailableBeds() > 0) {
                    oldRoom.setRoomStatus(2); // 部分入住
                }
                roomMapper.updateById(oldRoom);
            }
            
            // 4. 更新新房间状态
            Room newRoom = roomMapper.selectById(application.getTargetRoomId());
            if (newRoom != null) {
                newRoom.setAvailableBeds(newRoom.getAvailableBeds() - 1);
                if (newRoom.getAvailableBeds() == 0) {
                    newRoom.setRoomStatus(3); // 已满
                } else {
                    newRoom.setRoomStatus(2); // 部分入住
                }
                roomMapper.updateById(newRoom);
            }
            
            // 5. 更新入住申请的房间和床位信息
            CheckInApplication checkIn = checkInMapper.selectById(application.getCheckInApplicationId());
            if (checkIn != null) {
                checkIn.setId(application.getCheckInApplicationId());
                checkIn.setAssignedRoomId(application.getTargetRoomId());
                checkIn.setAssignedBedId(bedId);
                checkInMapper.updateById(checkIn);
            }
            
            // 6. 更新换宿申请状态为已完成
            application.setStatus(5);
            transferMapper.updateById(application);
        }
    }
}

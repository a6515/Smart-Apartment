package org.smartapartment.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.smartapartment.common.PageResult;
import org.smartapartment.entity.Bed;
import org.smartapartment.entity.CheckInApplication;
import org.smartapartment.entity.CheckoutApplication;
import org.smartapartment.entity.Room;
import org.smartapartment.exception.BusinessException;
import org.smartapartment.mapper.BedMapper;
import org.smartapartment.mapper.CheckInApplicationMapper;
import org.smartapartment.mapper.CheckoutApplicationMapper;
import org.smartapartment.mapper.RoomMapper;
import org.smartapartment.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 退宿申请服务类
 * 
 * 核心职责：处理学生退宿申请的完整流程
 * - 提交申请：学生申请退出宿舍
 * - 审批申请：管理员审批退宿申请
 * - 自动处理：释放床位、更新房间状态、更新入住申请状态为已退宿
 */
@Service
@RequiredArgsConstructor
public class CheckoutService {
    
    private final CheckoutApplicationMapper checkoutMapper;
    private final CheckInApplicationMapper checkInMapper;
    private final RoomMapper roomMapper;
    private final BedMapper bedMapper;
    private final RoomService roomService;
    
    /**
     * 分页查询退宿申请
     * 
     * @param current 当前页码
     * @param size 每页大小
     * @param studentId 学生ID（可选，用于查询指定学生的申请）
     * @param status 申请状态（可选，1-待审核 2-已同意 3-已驳回）
     * @return 分页结果，包含学生信息、房间信息、退宿日期、审批信息
     * 
     * 涉及表：checkout_application + sys_user + building + room + bed（关联查询）
     */
    public PageResult<CheckoutApplication> getPage(Long current, Long size, Long studentId, Integer status) {
        PageHelper.startPage(current.intValue(), size.intValue());
        List<CheckoutApplication> list = checkoutMapper.selectPage(studentId, status);
        PageInfo<CheckoutApplication> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList(), current, size);
    }
    
    /**
     * 提交退宿申请
     * 
     * 业务流程：
     * 1. 验证学生必须已入住（check_in_application.status=4）
     * 2. 检查是否有未处理的退宿申请（状态1或2）
     * 3. 从入住申请记录获取房间和床位信息
     * 4. 插入checkout_application记录，状态为1（待审核）
     * 
     * @param application 退宿申请对象（前端提供：checkInApplicationId, reason, description, checkoutDate）
     * @throws BusinessException 未入住、有未处理申请时抛出
     * 
     * 涉及表：checkout_application（INSERT）、check_in_application（SELECT）
     */
    @Transactional
    public void submitApplication(CheckoutApplication application) {
        // 1. 检查学生是否已入住
        CheckInApplication checkIn = checkInMapper.selectById(application.getCheckInApplicationId());
        if (checkIn == null || checkIn.getStatus() == null || checkIn.getStatus() != 4) {
            throw new BusinessException("您还未入住，无法申请退宿");
        }
        
        // 2. 检查是否有未处理的退宿申请（仅检查与当前入住记录相关的申请）
        List<CheckoutApplication> existingApplications = checkoutMapper.selectPage(
            SecurityUtils.getCurrentUserId(), null);
        boolean hasPending = existingApplications.stream()
            .anyMatch(app -> app.getCheckInApplicationId() != null && 
                     app.getCheckInApplicationId().equals(application.getCheckInApplicationId()) && 
                     (app.getStatus() == 1 || app.getStatus() == 2));
        if (hasPending) {
            throw new BusinessException("您已有待处理的退宿申请，请勿重复提交");
        }
        
        // 3. 填充申请信息
        application.setStudentId(SecurityUtils.getCurrentUserId());
        application.setBuildingId(checkIn.getPreferredBuildingId());
        application.setRoomId(checkIn.getAssignedRoomId());
        application.setBedId(checkIn.getAssignedBedId());
        
        // 4. 保存申请
        checkoutMapper.insert(application);
    }
    
    /**
     * 审批退宿申请
     * 
     * 业务流程：
     * 1. 验证申请状态必须为1（待审核）
     * 2. 更新审批人信息、审批时间、审批意见
     * 3. 如果审批通过（status=2）：
     *    【步骤1】释放床位：
     *       - bed_status=1（空闲）
     *       - 注意：这里只修改了状态，没有清除学生信息（与换宿不同）
     *       - TODO: 建议添加清除student_id、student_name、check_in_time
     *    【步骤2】更新房间：
     *       - available_beds+1
     *       - 更新room_status（1-空闲 2-部分入住）
     *    【步骤3】更新入住申请状态：
     *       - application_status=5（已退宿）
     *       - 此步骤确保"我的房间"页面不再显示房间信息
     * 
     * @param id 申请ID
     * @param status 审批结果（2-同意 3-驳回）
     * @param remark 审批意见
     * @throws BusinessException 申请不存在、已处理时抛出
     * 
     * 涉及表：checkout_application（UPDATE）、bed（UPDATE）、room（UPDATE）、check_in_application（UPDATE）
     * 注意：此方法使用事务，确保数据一致性
     */
    @Transactional
    public void approve(Long id, Integer status, String remark) {
        CheckoutApplication application = checkoutMapper.selectById(id);
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
        
        checkoutMapper.updateById(application);
        
        // 如果同意退宿，需要释放床位和更新入住申请状态
        if (status == 2) {
            // 1. 释放床位
            Bed bed = bedMapper.selectById(application.getBedId());
            if (bed != null) {
                bed.setBedStatus(1); // 设置为空闲
                bedMapper.updateById(bed);
            }
            
            // 2. 更新房间可用床位数和状态
            Room room = roomMapper.selectById(application.getRoomId());
            if (room != null) {
                room.setAvailableBeds(room.getAvailableBeds() + 1);
                // 更新房间状态：1-空闲 2-部分入住 3-已满
                if (room.getAvailableBeds() >= room.getTotalBeds()) {
                    room.setRoomStatus(1); // 空闲
                } else if (room.getAvailableBeds() > 0) {
                    room.setRoomStatus(2); // 部分入住
                }
                roomMapper.updateById(room);
                
                // 清除房间相关缓存
                roomService.updateBedStatus(application.getRoomId());
            }
            
            // 3. 更新入住申请状态（已退宿）
            CheckInApplication checkIn = checkInMapper.selectById(application.getCheckInApplicationId());
            if (checkIn != null) {
                checkIn.setId(application.getCheckInApplicationId());
                checkIn.setApplicationStatus(5); // 5-已退宿
                checkInMapper.updateById(checkIn);
            }
        }
    }
}

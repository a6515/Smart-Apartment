package org.smartapartment.smartapartment.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.smartapartment.smartapartment.common.PageResult;
import org.smartapartment.smartapartment.entity.Bed;
import org.smartapartment.smartapartment.entity.CheckInApplication;
import org.smartapartment.smartapartment.entity.Room;
import org.smartapartment.smartapartment.entity.SysUser;
import org.smartapartment.smartapartment.exception.BusinessException;
import org.smartapartment.smartapartment.mapper.BedMapper;
import org.smartapartment.smartapartment.mapper.CheckInApplicationMapper;
import org.smartapartment.smartapartment.mapper.RoomMapper;
import org.smartapartment.smartapartment.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 入住申请服务类
 */
@Service
@RequiredArgsConstructor
public class CheckInService {
    
    private final CheckInApplicationMapper applicationMapper;
    private final BedMapper bedMapper;
    private final RoomMapper roomMapper;
    
    /**
     * 分页查询入住申请列表
     * 
     * @param current 当前页码
     * @param size 每页大小
     * @param studentId 学生ID（可选，用于查询指定学生的申请）
     * @param status 申请状态（可选，1-待审核 2-已通过 3-已驳回 4-已入住 5-已退宿）
     * @param studentName 学生姓名（可选，模糊查询）
     * @return 分页结果
     * 
     * 涉及表：check_in_application（主表）+ sys_user + room + bed + building（关联查询）
     */
    public PageResult<CheckInApplication> getPage(Long current, Long size, Long studentId, Integer status, String studentName) {
        // 使用PageHelper进行分页
        PageHelper.startPage(current.intValue(), size.intValue());
        List<CheckInApplication> list = applicationMapper.selectPage(studentId, status, studentName);
        PageInfo<CheckInApplication> pageInfo = new PageInfo<>(list);
        
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList(), current, size);
    }
    
    /**
     * 提交入住申请
     * 
     * 业务流程：
     * 1. 从Token获取当前登录学生信息
     * 2. 自动填充学生基本信息到申请表
     * 3. 设置申请状态为1（待审核）
     * 4. 插入数据库
     * 
     * @param application 入住申请对象（前端只需提供：preferredBuildingId, preferredRoomType, applicationReason）
     * @throws BusinessException 用户未登录时抛出
     * 
     * 涉及表：check_in_application（INSERT）
     */
    public void submit(CheckInApplication application) {
        // 获取当前登录用户信息
        SysUser currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException("用户未登录");
        }
        
        // 自动填充学生信息
        application.setStudentId(currentUser.getId());
        application.setStudentName(currentUser.getRealName());
        application.setStudentNumber(currentUser.getStudentNumber());
        application.setPhone(currentUser.getPhone());
        application.setGender(currentUser.getGender());
        application.setDepartment(currentUser.getDepartment());
        application.setMajor(currentUser.getMajor());
        application.setClassName(currentUser.getClassName());
        application.setApplicationStatus(1); // 待审核
        
        applicationMapper.insert(application);
    }
    
    /**
     * 审批入住申请
     * 
     * 业务流程：
     * 1. 验证申请状态必须为1（待审核）
     * 2. 更新审批人信息、审批时间、审批意见
     * 3. 如果审批通过（status=2）：
     *    a. 验证床位可用性（bed_status必须为1）
     *    b. 分配房间和床位ID到申请记录
     *    c. 更新床位状态为2（已占用），记录学生信息和入住时间
     *    d. 房间可用床位数-1
     *    e. 更新房间状态（1-空闲 2-部分入住 3-已满）
     * 
     * @param id 申请ID
     * @param status 审批结果（2-通过 3-驳回）
     * @param remark 审批意见
     * @param roomId 分配的房间ID（通过时必填）
     * @param bedId 分配的床位ID（通过时必填）
     * @throws BusinessException 申请不存在、状态异常、床位不可用时抛出
     * 
     * 涉及表：check_in_application（UPDATE）、bed（UPDATE）、room（UPDATE）
     * 注意：此方法使用事务，确保数据一致性
     */
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id, Integer status, String remark, Long roomId, Long bedId) {
        CheckInApplication application = applicationMapper.selectById(id);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        
        if (application.getApplicationStatus() == null || application.getApplicationStatus() != 1) {
            throw new BusinessException("该申请已处理或状态异常");
        }
        
        // 获取当前登录的审批人信息
        SysUser approver = SecurityUtils.getCurrentUser();
        if (approver == null) {
            throw new BusinessException("用户未登录");
        }
        
        application.setApplicationStatus(status);
        application.setApproverId(approver.getId());
        application.setApproverName(approver.getRealName());
        application.setApproveTime(LocalDateTime.now());
        application.setApproveRemark(remark);
        
        if (status == 2) { // 通过
            if (roomId == null || bedId == null) {
                throw new BusinessException("请分配房间和床位");
            }
            
            // 检查床位是否可用
            Bed bed = bedMapper.selectById(bedId);
            if (bed == null || bed.getBedStatus() != 1) {
                throw new BusinessException("床位不可用");
            }
            
            application.setAssignedRoomId(roomId);
            application.setAssignedBedId(bedId);
            
            // 更新床位状态
            bed.setBedStatus(2); // 已占用
            bed.setStudentId(application.getStudentId());
            bed.setStudentName(application.getStudentName());
            bed.setCheckInTime(LocalDateTime.now());
            bedMapper.updateById(bed);
            
            // 更新房间可用床位数
            Room room = roomMapper.selectById(roomId);
            if (room != null) {
                room.setAvailableBeds(room.getAvailableBeds() - 1);
                // 更新房间状态
                if (room.getAvailableBeds() == 0) {
                    room.setRoomStatus(3); // 已满
                } else {
                    room.setRoomStatus(2); // 部分入住
                }
                roomMapper.updateById(room);
            }
        }
        
        applicationMapper.updateById(application);
    }
    
    /**
     * 确认入住
     * 
     * 业务流程：
     * 1. 验证申请状态必须为2（审批通过）
     * 2. 更新申请状态为4（已入住）
     * 3. 记录实际入住时间
     * 
     * 场景说明：
     * - 管理员审批通过后（status=2），学生实际到宿舍办理入住手续时调用此接口
     * - 只有status=4的记录才会在"我的房间"页面显示
     * 
     * @param id 申请ID
     * @throws BusinessException 申请不存在或未通过审批时抛出
     * 
     * 涉及表：check_in_application（UPDATE）
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmCheckIn(Long id) {
        CheckInApplication application = applicationMapper.selectById(id);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        
        if (application.getApplicationStatus() != 2) {
            throw new BusinessException("申请未通过审批");
        }
        
        application.setApplicationStatus(4); // 已入住
        application.setCheckInTime(LocalDateTime.now());
        applicationMapper.updateById(application);
    }
    
    /**
     * 撤销申请（学生撤销待审批的申请）
     * 
     * 业务流程：
     * 1. 验证申请所有权（只能撤销自己的申请）
     * 2. 验证申请状态必须为1（待审批）
     * 3. 逻辑删除申请记录（设置deleted=1）
     * 
     * 权限控制：
     * - 只能撤销本人的申请
     * - 只能撤销待审批状态的申请
     * - 已审批的申请不能撤销
     * 
     * @param id 申请ID
     * @throws BusinessException 用户未登录、无权限、状态不允许时抛出
     * 
     * 涉及表：check_in_application（逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelApplication(Long id) {
        CheckInApplication application = applicationMapper.selectById(id);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        
        // 验证是否是当前学生的申请
        SysUser currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException("用户未登录");
        }
        
        if (!application.getStudentId().equals(currentUser.getId())) {
            throw new BusinessException("无权撤销他人的申请");
        }
        
        // 只有待审批状态的申请才能撤销
        if (application.getApplicationStatus() != 1) {
            throw new BusinessException("只能撤销待审批的申请");
        }
        
        // 逻辑删除
        applicationMapper.deleteById(id);
    }
    
    /**
     * 删除申请（管理员删除申请，如果已分配床位需要释放）
     * 
     * 业务流程：
     * 1. 检查申请状态
     * 2. 如果申请已通过（status=2）且已分配床位：
     *    a. 释放床位（bed_status=1，清除学生信息）
     *    b. 房间可用床位数+1
     *    c. 更新房间状态
     * 3. 逻辑删除申请记录
     * 
     * 使用场景：
     * - 管理员需要删除错误的申请记录
     * - 与学生撤销不同，管理员可以删除任何状态的申请
     * - 如果已分配资源，会自动释放
     * 
     * @param id 申请ID
     * @throws BusinessException 申请不存在时抛出
     * 
     * 涉及表：check_in_application（逻辑删除）、bed（UPDATE，如果已分配）、room（UPDATE，如果已分配）
     * 注意：此方法会回滚已分配的资源
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteApplication(Long id) {
        CheckInApplication application = applicationMapper.selectById(id);
        if (application == null) {
            throw new BusinessException("申请不存在");
        }
        
        // 禁止删除已入住或已退宿的记录，防止数据不一致
        if (application.getApplicationStatus() == 4) {
            throw new BusinessException("不能删除已入住的记录，请先让学生办理退宿");
        }
        
        if (application.getApplicationStatus() == 5) {
            throw new BusinessException("不能删除已退宿的记录，这是历史数据");
        }
        
        // 如果申请已通过且分配了床位，需要释放床位
        if (application.getApplicationStatus() == 2 && application.getAssignedBedId() != null) {
            Bed bed = bedMapper.selectById(application.getAssignedBedId());
            if (bed != null) {
                bed.setBedStatus(1); // 空闲
                bed.setStudentId(null);
                bed.setStudentName(null);
                bed.setCheckInTime(null);
                bedMapper.updateById(bed);
                
                // 更新房间可用床位数
                Room room = roomMapper.selectById(application.getAssignedRoomId());
                if (room != null) {
                    room.setAvailableBeds(room.getAvailableBeds() + 1);
                    if (room.getAvailableBeds() > 0) {
                        room.setRoomStatus(room.getAvailableBeds() < room.getTotalBeds() ? 2 : 1); // 部分入住或空闲
                    }
                    roomMapper.updateById(room);
                }
            }
        }
        
        // 逻辑删除
        applicationMapper.deleteById(id);
    }
    
    /**
     * 获取房间室友列表
     * 
     * 查询逻辑：
     * - 查询assigned_room_id = roomId 的所有入住申请
     * - 仅返回application_status = 4（已入住）的记录
     * - 关联查询学生的真实姓名、学号、手机号、邮箱、床位号
     * 
     * @param roomId 房间ID
     * @return 室友信息列表，每个Map包含：realName, studentNumber, phone, email, bedNumber
     * 
     * 涉及表：check_in_application + sys_user + bed
     */
    public List<Map<String, Object>> getRoommates(Long roomId) {
        return applicationMapper.selectRoommatesByRoomId(roomId);
    }
}

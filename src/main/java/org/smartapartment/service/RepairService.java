package org.smartapartment.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.smartapartment.common.PageResult;
import org.smartapartment.entity.CheckInApplication;
import org.smartapartment.entity.RepairApplication;
import org.smartapartment.entity.SysUser;
import org.smartapartment.exception.BusinessException;
import org.smartapartment.mapper.CheckInApplicationMapper;
import org.smartapartment.mapper.RepairApplicationMapper;
import org.smartapartment.mapper.SysUserMapper;
import org.smartapartment.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报修服务类
 */
@Service
@RequiredArgsConstructor
public class RepairService {
    
    private final RepairApplicationMapper repairMapper;
    private final SysUserMapper userMapper;
    private final CheckInApplicationMapper checkInMapper;
    
    /**
     * 分页查询报修列表
     * 
     * @param current 当前页码
     * @param size 每页大小
     * @param studentId 学生ID（可选，用于学生查询自己的报修）
     * @param status 报修状态（可选，单选，1-待接单 2-已接单 3-处理中 4-已完成）
     * @param repairType 报修类型（可选，单选）
     * @param statuses 报修状态列表（可选，多选）
     * @param repairTypes 报修类型列表（可选，多选）
     * @return 分页结果
     */
    public PageResult<RepairApplication> getPage(Long current, Long size, Long studentId, 
                                                   Integer status, Integer repairType,
                                                   List<Integer> statuses, List<Integer> repairTypes) {
        // 使用PageHelper进行分页
        PageHelper.startPage(current.intValue(), size.intValue());
        List<RepairApplication> list = repairMapper.selectPage(studentId, status, repairType, statuses, repairTypes);
        PageInfo<RepairApplication> pageInfo = new PageInfo<>(list);
        
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList(), current, size);
    }
    
    /**
     * 提交报修申请
     * 
     * 业务流程：
     * 1. 参数验证（repairType、repairTitle、repairContent 必填）
     * 2. 从SecurityContext获取当前登录用户ID
     * 3. 查询用户详细信息（姓名、手机号等）
     * 4. 查询学生的入住记录，获取当前入住的房间ID
     * 5. 自动填充学生信息（studentId、studentName、phone）
     * 6. 自动填充房间信息（roomId）
     * 7. 设置默认值（repairStatus=1、urgencyLevel=2）
     * 8. 插入数据库
     * 
     * @param application 报修申请对象
     *        必填字段：repairType（报修类型）、repairTitle（报修标题）、repairContent（报修内容）
     *        可选字段：repairImages（报修图片）、urgencyLevel（紧急程度，默认2）
     *        自动填充：studentId、studentName、phone、roomId、repairStatus
     * @throws BusinessException 参数验证失败、用户不存在或未入住时抛出
     * 
     * 涉及表：sys_user（SELECT）、check_in_application（SELECT）、repair_application（INSERT）
     */
    public void submit(RepairApplication application) {
        // 【步骤1】参数验证
        if (application.getRepairType() == null) {
            throw new BusinessException("请选择报修类型");
        }
        if (application.getRepairTitle() == null || application.getRepairTitle().trim().isEmpty()) {
            throw new BusinessException("请填写报修标题");
        }
        if (application.getRepairContent() == null || application.getRepairContent().trim().isEmpty()) {
            throw new BusinessException("请填写报修内容");
        }
        
        // 【步骤2】获取当前登录用户ID
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        // 【步骤3】查询用户详细信息
        SysUser user = userMapper.selectById(currentUserId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 【步骤4】查询学生的入住记录（状态为4-已入住）
        List<CheckInApplication> checkInList = checkInMapper.selectPage(currentUserId, 4, null);
        if (checkInList == null || checkInList.isEmpty()) {
            throw new BusinessException("您还未入住，无法提交报修申请");
        }
        
        CheckInApplication checkIn = checkInList.get(0); // 获取第一条已入住记录
        if (checkIn.getAssignedRoomId() == null) {
            throw new BusinessException("未分配房间，无法提交报修申请");
        }
        
        // 【步骤5】自动填充学生信息
        application.setStudentId(user.getId());
        application.setStudentName(user.getRealName());
        application.setPhone(user.getPhone());
        
        // 【步骤6】自动填充房间信息
        application.setRoomId(checkIn.getAssignedRoomId());
        
        // 【步骤7】设置默认值
        application.setRepairStatus(1); // 1-待接单
        if (application.getUrgencyLevel() == null) {
            application.setUrgencyLevel(2); // 默认普通紧急程度
        }
        
        // 【步骤8】插入数据库
        repairMapper.insert(application);
    }
    
    /**
     * 接单（管理员或维修人员接受报修任务）
     * 
     * 业务流程：
     * 1. 获取当前登录用户（处理人）
     * 2. 查询报修申请是否存在
     * 3. 验证状态必须是"待接单"
     * 4. 设置处理人信息和接单时间
     * 5. 更新状态为"已接单"
     * 
     * @param id 报修申请ID
     * @throws BusinessException 申请不存在或已被处理时抛出
     * 
     * 涉及表：sys_user（SELECT）、repair_application（UPDATE）
     */
    @Transactional(rollbackFor = Exception.class)
    public void accept(Long id) {
        // 获取当前登录用户（处理人）
        Long currentUserId = SecurityUtils.getCurrentUserId();
        SysUser handler = userMapper.selectById(currentUserId);
        if (handler == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 查询报修申请
        RepairApplication application = repairMapper.selectById(id);
        if (application == null) {
            throw new BusinessException("报修申请不存在");
        }
        
        if (application.getRepairStatus() != 1) {
            throw new BusinessException("该报修已被处理");
        }
        
        // 设置处理人信息
        application.setRepairStatus(2); // 2-已接单
        application.setHandlerId(handler.getId());
        application.setHandlerName(handler.getRealName());
        application.setAcceptTime(LocalDateTime.now());
        
        repairMapper.updateById(application);
    }
    
    /**
     * 更新维修进度
     * 
     * 业务流程：
     * 1. 查询报修申请是否存在
     * 2. 更新维修状态（2-已接单 3-处理中 4-已完成）
     * 3. 更新维修备注
     * 4. 如果状态为"已完成"，记录完成时间
     * 
     * @param id 报修申请ID
     * @param status 新状态
     * @param remark 维修备注
     * @throws BusinessException 申请不存在时抛出
     * 
     * 涉及表：repair_application（UPDATE）
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status, String remark) {
        RepairApplication application = repairMapper.selectById(id);
        if (application == null) {
            throw new BusinessException("报修申请不存在");
        }
        
        application.setRepairStatus(status);
        application.setRepairRemark(remark);
        
        if (status == 4) { // 已完成
            application.setFinishTime(LocalDateTime.now());
        }
        
        repairMapper.updateById(application);
    }
    
    /**
     * 学生评价维修服务
     * 
     * 业务流程：
     * 1. 查询报修申请是否存在
     * 2. 验证维修必须已完成
     * 3. 记录满意度评分和评价内容
     * 
     * @param id 报修申请ID
     * @param rating 满意度评分（1-5分）
     * @param comment 评价内容
     * @throws BusinessException 申请不存在或维修未完成时抛出
     * 
     * 涉及表：repair_application（UPDATE）
     */
    @Transactional(rollbackFor = Exception.class)
    public void rate(Long id, Integer rating, String comment) {
        RepairApplication application = repairMapper.selectById(id);
        if (application == null) {
            throw new BusinessException("报修申请不存在");
        }
        
        if (application.getRepairStatus() != 4) {
            throw new BusinessException("维修未完成，无法评价");
        }
        
        application.setSatisfactionRating(rating);
        application.setSatisfactionComment(comment);
        
        repairMapper.updateById(application);
    }
}

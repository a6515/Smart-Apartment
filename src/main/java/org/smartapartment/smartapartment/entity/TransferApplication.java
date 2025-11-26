package org.smartapartment.smartapartment.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 换宿申请实体类
 */
@Data
public class TransferApplication implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentNumber;
    private Long checkInApplicationId; // 关联的入住申请ID
    // 当前房间信息
    private Long currentBuildingId;
    private String currentBuildingName;
    private Long currentRoomId;
    private String currentRoomNumber;
    private Long currentBedId;
    private String currentBedNumber;
    // 目标房间信息
    private Long targetBuildingId;
    private String targetBuildingName;
    private Long targetRoomId;
    private String targetRoomNumber;
    private Integer targetRoomType;
    private String reason; // 换宿原因
    private String description; // 详细说明
    private Integer status; // 1-待审核 2-已同意 3-已驳回 4-已撤销 5-已完成
    private Long approverId; // 审批人ID
    private String approverName; // 审批人姓名
    private String remark; // 审批意见
    private LocalDateTime approveTime; // 审批时间
    private Long assignedBedId; // 管理员分配的床位ID
    private String assignedBedNumber; // 分配的床位号
    private Integer deleted; // 0-未删除 1-已删除
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

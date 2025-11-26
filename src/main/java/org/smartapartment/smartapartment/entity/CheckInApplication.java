package org.smartapartment.smartapartment.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 入住申请实体类
 */
@Data
public class CheckInApplication implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentNumber;
    private String phone;
    private Integer gender;
    private String department;
    private String major;
    private String className;
    private Long preferredBuildingId;
    private Integer preferredRoomType;  // 期望房间类型：1-4人间，2-6人间
    private String applicationReason;
    private Integer applicationStatus;
    private Long assignedRoomId;
    private Long assignedBedId;
    private Long approverId;
    private String approverName;
    private LocalDateTime approveTime;
    private String approveRemark;
    private LocalDateTime checkInTime;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联查询字段（用于前端显示）
    private String buildingName;  // 实际分配的楼宇名称
    private String roomNumber;     // 分配房间号
    private Integer floor;         // 楼层
    private String bedNumber;      // 床位号
    private Integer status;        // 状态别名（与applicationStatus相同，用于前端）
}

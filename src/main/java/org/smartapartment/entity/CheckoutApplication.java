package org.smartapartment.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 退宿申请实体类
 */
@Data
public class CheckoutApplication implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentNumber;
    private Long checkInApplicationId; // 关联的入住申请ID
    private Long buildingId;
    private String buildingName;
    private Long roomId;
    private String roomNumber;
    private Long bedId;
    private String bedNumber;
    private String reason; // 退宿原因
    private String description; // 详细说明
    private LocalDate checkoutDate; // 预计退宿日期
    private Integer status; // 1-待审核 2-已同意 3-已驳回 4-已撤销
    private Long approverId; // 审批人ID
    private String approverName; // 审批人姓名
    private String remark; // 审批意见
    private LocalDateTime approveTime; // 审批时间
    private Integer deleted; // 0-未删除 1-已删除
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

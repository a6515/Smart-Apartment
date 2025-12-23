package org.smartapartment.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 费用记录实体类
 */
@Data
public class FeeRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentNumber;
    private Long roomId;
    private String buildingName;
    private String roomNumber;
    private Integer feeType;
    private String feeName;
    private BigDecimal feeAmount;
    private String billingPeriod;
    private BigDecimal usageAmount;
    private Integer feeStatus;
    private LocalDateTime paymentTime;
    private Integer paymentMethod;
    private LocalDate dueDate;
    private String remark;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

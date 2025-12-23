package org.smartapartment.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 报修申请实体类
 */
@Data
public class RepairApplication implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long studentId;
    private String studentName;
    private String phone;
    private Long roomId;
    private String buildingName;
    private String roomNumber;
    private Integer repairType;
    private String repairTitle;
    private String repairContent;
    private String repairImages;
    private Integer repairStatus;
    private Integer urgencyLevel;
    private Long handlerId;
    private String handlerName;
    private LocalDateTime acceptTime;
    private LocalDateTime finishTime;
    private String repairRemark;
    private Integer satisfactionRating;
    private String satisfactionComment;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

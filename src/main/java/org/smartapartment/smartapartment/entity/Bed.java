package org.smartapartment.smartapartment.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 床位实体类
 */
@Data
public class Bed implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long roomId;
    private String bedNumber;
    private Integer bedStatus;
    private Long studentId;
    private String studentName;
    private LocalDateTime checkInTime;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

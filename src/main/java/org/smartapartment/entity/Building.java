package org.smartapartment.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 楼宇实体类
 */
@Data
public class Building implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String buildingName;
    private String buildingCode;
    private Integer buildingType;
    private Integer floors;
    private Integer totalRooms;
    private String address;
    private Long managerId;
    private String managerName;
    private String managerPhone;
    private String description;
    private Integer status;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

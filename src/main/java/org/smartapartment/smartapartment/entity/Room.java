package org.smartapartment.smartapartment.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房间实体类
 */
@Data
public class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long buildingId;
    private String buildingName; // 楼宇名称（关联查询字段）
    private String roomNumber;
    private Integer floor;
    private Integer roomType;
    private Integer totalBeds;
    private Integer availableBeds;
    private BigDecimal area;
    private BigDecimal price;
    private String facilities;
    private Integer roomStatus;
    private String description;
    private Integer deleted;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

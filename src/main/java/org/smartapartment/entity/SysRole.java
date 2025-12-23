package org.smartapartment.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色实体类
 */
@Data
public class SysRole implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private Integer status;
    private Integer sort;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

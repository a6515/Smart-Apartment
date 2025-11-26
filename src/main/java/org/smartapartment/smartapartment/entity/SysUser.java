package org.smartapartment.smartapartment.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class SysUser implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private Integer gender;
    private String avatar;
    private Integer status;
    private Integer userType;
    private String studentNumber;
    private String department;
    private String major;
    private String className;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

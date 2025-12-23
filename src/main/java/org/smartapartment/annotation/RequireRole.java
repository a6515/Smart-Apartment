package org.smartapartment.annotation;

import java.lang.annotation.*;

/**
 * 角色权限注解
 * 用于标记需要特定角色才能访问的接口
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    /**
     * 需要的用户类型
     * 1-管理员, 2-宿管, 3-学生
     */
    int[] value();
    
    /**
     * 是否需要满足所有角色（默认满足其中一个即可）
     */
    boolean requireAll() default false;
}

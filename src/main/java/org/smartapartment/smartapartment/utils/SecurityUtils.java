package org.smartapartment.smartapartment.utils;

import org.smartapartment.smartapartment.entity.SysUser;
import org.smartapartment.smartapartment.mapper.SysUserMapper;
import org.smartapartment.smartapartment.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 安全工具类
 * 用于获取当前登录用户信息
 */
@Component
public class SecurityUtils {
    
    private static SysUserMapper userMapper;
    private static JwtUtil jwtUtil;
    
    @Autowired
    public void setUserMapper(SysUserMapper userMapper) {
        SecurityUtils.userMapper = userMapper;
    }
    
    @Autowired
    public void setJwtUtil(JwtUtil jwtUtil) {
        SecurityUtils.jwtUtil = jwtUtil;
    }
    
    /**
     * 获取当前登录用户
     * @return 当前登录的用户对象，如果未登录则返回 null
     */
    public static SysUser getCurrentUser() {
        try {
            // 获取当前HTTP请求
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            
            HttpServletRequest request = attributes.getRequest();
            
            // 从请求头中获取token
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return null;
            }
            
            // 去除 "Bearer " 前缀
            token = token.substring(7);
            
            // 解析token获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return null;
            }
            
            // 从数据库查询用户信息
            return userMapper.selectById(userId);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取当前登录用户的 ID
     * @return 用户 ID，如果未登录则返回 null
     */
    public static Long getCurrentUserId() {
        SysUser user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
    
    /**
     * 获取当前登录用户的用户名
     * @return 用户名，如果未登录则返回 null
     */
    public static String getCurrentUsername() {
        SysUser user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }
}

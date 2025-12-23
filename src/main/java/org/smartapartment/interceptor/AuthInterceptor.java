package org.smartapartment.interceptor;

import lombok.RequiredArgsConstructor;
import org.smartapartment.annotation.RequireRole;
import org.smartapartment.exception.BusinessException;
import org.smartapartment.utils.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

/**
 * 认证拦截器
 * 验证Token和权限
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        // 如果不是方法处理器，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // 检查是否需要权限验证
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }
        
        // 如果没有权限注解，直接放行
        if (requireRole == null) {
            return true;
        }
        
        // 获取Token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new BusinessException("未登录或登录已过期");
        }
        
        // 去除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException("Token无效或已过期");
        }
        
        // 获取用户信息
        Map<String, Object> claims = jwtUtil.getClaimsMapFromToken(token);
        Long userId = Long.valueOf(claims.get("userId").toString());
        
        // 验证Redis中的Token
        String redisKey = "user:token:" + userId;
        String redisToken = (String) redisTemplate.opsForValue().get(redisKey);
        if (redisToken == null || !redisToken.equals(token)) {
            throw new BusinessException("登录已过期，请重新登录");
        }
        
        // 获取用户类型（从Token中获取或从数据库查询）
        Integer userType = claims.get("userType") != null ? 
            Integer.valueOf(claims.get("userType").toString()) : null;
        
        if (userType == null) {
            throw new BusinessException("用户信息异常");
        }
        
        // 验证权限
        int[] requiredRoles = requireRole.value();
        boolean hasPermission = Arrays.stream(requiredRoles)
            .anyMatch(role -> role == userType);
        
        if (!hasPermission) {
            throw new BusinessException("权限不足，无法访问该资源");
        }
        
        // 将用户信息存入请求属性，供Controller使用
        request.setAttribute("userId", userId);
        request.setAttribute("userType", userType);
        
        return true;
    }
}

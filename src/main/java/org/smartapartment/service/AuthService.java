package org.smartapartment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smartapartment.entity.SysUser;
import org.smartapartment.exception.BusinessException;
import org.smartapartment.mapper.SysUserMapper;
import org.smartapartment.utils.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final SysUserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MessageService messageService;
    private final WebSocketService webSocketService;
    
    /**
     * 用户登录
     */
    public Map<String, Object> login(String username, String password) {
        // 查询用户
        SysUser user = userMapper.selectByUsername(username);
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 生成Token（包含用户类型）
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());
        
        // 将Token存入Redis
        String redisKey = "user:token:" + user.getId();
        redisTemplate.opsForValue().set(redisKey, token, 7, TimeUnit.DAYS);
        
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", buildUserInfo(user));
        
        return result;
    }
    
    /**
     * 用户注册
     */
    public void register(SysUser user) {
        // 检查用户名是否存在
        if (userMapper.countByUsername(user.getUsername()) > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        user.setUserType(3); // 默认学生用户
        
        userMapper.insert(user);
    }
    
    /**
     * 用户登出
     */
    public void logout(Long userId) {
        String redisKey = "user:token:" + userId;
        redisTemplate.delete(redisKey);
    }
    
    /**
     * 构建用户信息
     */
    private Map<String, Object> buildUserInfo(SysUser user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("phone", user.getPhone());
        userInfo.put("email", user.getEmail());
        userInfo.put("gender", user.getGender());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("userType", user.getUserType());
        userInfo.put("studentNumber", user.getStudentNumber());
        userInfo.put("department", user.getDepartment());
        userInfo.put("major", user.getMajor());
        userInfo.put("className", user.getClassName());
        
        // 添加未读消息数量
        try {
            int unreadCount = messageService.getUserUnreadMessageCount(user.getId());
            userInfo.put("unreadMessageCount", unreadCount);
        } catch (Exception e) {
            log.error("【获取未读消息数】失败, 用户ID: {}, 错误: {}", user.getId(), e.getMessage());
            userInfo.put("unreadMessageCount", 0);
        }
        
        return userInfo;
    }
}

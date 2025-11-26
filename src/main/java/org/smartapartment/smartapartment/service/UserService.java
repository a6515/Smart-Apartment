package org.smartapartment.smartapartment.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.smartapartment.smartapartment.common.PageResult;
import org.smartapartment.smartapartment.controller.UserProfileController;
import org.smartapartment.smartapartment.entity.SysUser;
import org.smartapartment.smartapartment.exception.BusinessException;
import org.smartapartment.smartapartment.mapper.SysUserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务类
 */
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final SysUserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    
    /**
     * 分页查询用户列表
     */
    public PageResult<SysUser> getPage(Long current, Long size, String username, String realName, Integer userType) {
        PageHelper.startPage(current.intValue(), size.intValue());
        List<SysUser> list = userMapper.selectByCondition(username, realName, userType);
        PageInfo<SysUser> pageInfo = new PageInfo<>(list);
        
        // 清除密码字段
        list.forEach(user -> user.setPassword(null));
        
        return PageResult.<SysUser>builder()
                .total(pageInfo.getTotal())
                .records(list)
                .current(current)
                .size(size)
                .build();
    }
    
    /**
     * 根据ID查询用户
     */
    public SysUser getById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 清除密码字段
        user.setPassword(null);
        return user;
    }
    
    /**
     * 新增用户
     */
    public void save(SysUser user) {
        // 检查用户名是否存在（只查未删除的）
        if (userMapper.countByUsername(user.getUsername()) > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查是否有已删除的同名用户
        SysUser deletedUser = userMapper.selectDeletedByUsername(user.getUsername());
        if (deletedUser != null) {
            // 如果有已删除的同名用户，恢复该用户
            user.setId(deletedUser.getId());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setStatus(1);
            userMapper.restoreUser(user);
        } else {
            // 没有已删除的用户，正常插入
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setStatus(1);
            userMapper.insert(user);
        }
    }
    
    /**
     * 更新用户
     */
    public void update(SysUser user) {
        SysUser existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 如果修改了用户名，检查是否重复
        if (!existUser.getUsername().equals(user.getUsername())) {
            if (userMapper.countByUsername(user.getUsername()) > 0) {
                throw new BusinessException("用户名已存在");
            }
        }
        
        // 不允许修改密码（通过重置密码功能）
        user.setPassword(null);
        
        userMapper.update(user);
    }
    
    /**
     * 删除用户（逻辑删除）
     */
    public void delete(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 不允许删除管理员
        if (user.getUserType() == 1) {
            throw new BusinessException("不允许删除管理员账号");
        }
        
        userMapper.deleteById(id);
    }
    
    /**
     * 重置密码
     */
    public void resetPassword(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 重置为默认密码123456
        String newPassword = passwordEncoder.encode("123456");
        userMapper.updatePassword(id, newPassword);
    }
    
    /**
     * 修改用户状态
     */
    public void updateStatus(Long id, Integer status) {
        if (status != 0 && status != 1) {
            throw new BusinessException("状态参数错误");
        }
        
        userMapper.updateStatus(id, status);
    }
    
    /**
     * 更新用户个人资料
     * 
     * @param dto 用户信息更新DTO，仅包含允许用户自行修改的字段
     */
    public void updateUserInfo(UserProfileController.UserInfoUpdateDTO dto) {
        // 检查用户是否存在
        SysUser existingUser = userMapper.selectById(dto.getId());
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 创建更新对象，只设置允许更新的字段
        SysUser updateUser = new SysUser();
        updateUser.setId(dto.getId());
        updateUser.setPhone(dto.getPhone());
        updateUser.setEmail(dto.getEmail());
        
        // 执行更新
        userMapper.updateSelective(updateUser);
    }
    
    /**
     * 修改密码
     * 
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @throws BusinessException 旧密码错误、新密码格式错误时抛出
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        // 检查用户是否存在
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        
        // 验证新密码格式
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException("新密码长度不能少于6个字符");
        }
        
        // 加密新密码并更新
        String encodedPassword = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(userId, encodedPassword);
    }
}

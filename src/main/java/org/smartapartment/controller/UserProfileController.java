package org.smartapartment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.smartapartment.common.Result;
import org.smartapartment.entity.SysUser;
import org.smartapartment.service.UserService;
import org.smartapartment.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 用户个人资料控制器
 * 提供用户查询和更新个人资料的接口
 */
@Tag(name = "用户个人资料", description = "用户查询和更新个人资料接口")
@RestController
@RequestMapping("/api/user/info")
@RequiredArgsConstructor
public class UserProfileController {
    
    private final UserService userService;
    
    @Operation(summary = "获取个人资料")
    @GetMapping
    public Result<SysUser> getCurrentUserInfo() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        SysUser user = userService.getById(currentUserId);
        // 出于安全考虑，清除密码字段
        user.setPassword(null);
        return Result.success(user);
    }
    
    @Operation(summary = "更新个人资料")
    @PutMapping
    public Result<?> updateUserInfo(@RequestBody UserInfoUpdateDTO dto) {
        // 获取当前登录用户ID
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        // 安全检查：确保只能修改自己的信息
        if (!currentUserId.equals(dto.getId())) {
            return Result.error("无权修改他人信息");
        }
        
        // 更新用户信息，只允许修改特定字段
        userService.updateUserInfo(dto);
        return Result.success("个人信息更新成功");
    }
    
    @Operation(summary = "修改密码")
    @PostMapping("/change-password")
    public Result<?> changePassword(@RequestBody ChangePasswordDTO dto) {
        // 获取当前登录用户ID
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        // 修改密码
        userService.changePassword(currentUserId, dto.getOldPassword(), dto.getNewPassword());
        return Result.success("密码修改成功");
    }
    
    /**
     * 用户信息更新DTO
     * 只包含允许用户自行修改的字段
     */
    @Data
    public static class UserInfoUpdateDTO {
        private Long id;
        private String phone;
        private String email;
    }
    
    /**
     * 修改密码DTO
     */
    @Data
    public static class ChangePasswordDTO {
        private String oldPassword;
        private String newPassword;
    }
}

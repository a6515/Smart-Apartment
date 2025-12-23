package org.smartapartment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.smartapartment.annotation.RequireRole;
import org.smartapartment.common.PageResult;
import org.smartapartment.common.Result;
import org.smartapartment.entity.SysUser;
import org.smartapartment.service.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理", description = "用户增删改查接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@RequireRole({1}) // 仅管理员可访问
public class UserController {
    
    private final UserService userService;
    
    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    public Result<PageResult<SysUser>> getPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer userType) {
        PageResult<SysUser> result = userService.getPage(current, size, username, realName, userType);
        return Result.success(result);
    }
    
    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        return Result.success(user);
    }
    
    @Operation(summary = "新增用户")
    @PostMapping
    public Result<?> save(@RequestBody SysUser user) {
        userService.save(user);
        return Result.success("新增成功");
    }
    
    @Operation(summary = "更新用户")
    @PutMapping
    public Result<?> update(@RequestBody SysUser user) {
        userService.update(user);
        return Result.success("更新成功");
    }
    
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success("删除成功");
    }
    
    @Operation(summary = "重置密码")
    @PostMapping("/reset-password/{id}")
    public Result<?> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success("密码已重置为123456");
    }
    
    @Operation(summary = "修改用户状态")
    @PostMapping("/status")
    public Result<?> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success("状态修改成功");
    }
}

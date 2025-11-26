package org.smartapartment.smartapartment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.smartapartment.smartapartment.annotation.RequireRole;
import org.smartapartment.smartapartment.common.PageResult;
import org.smartapartment.smartapartment.common.Result;
import org.smartapartment.smartapartment.entity.CheckInApplication;
import org.smartapartment.smartapartment.service.CheckInService;
import org.springframework.web.bind.annotation.*;

/**
 * 入住申请控制器
 */
@Tag(name = "入住管理", description = "入住申请、审批等接口")
@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
@RequireRole({1, 2, 3}) // 所有用户都可以访问
public class CheckInController {
    
    private final CheckInService checkInService;
    
    @Operation(summary = "分页查询入住申请列表")
    @GetMapping("/page")
    public Result<PageResult<CheckInApplication>> getPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String studentName) {
        PageResult<CheckInApplication> result = checkInService.getPage(current, size, studentId, status, studentName);
        return Result.success(result);
    }
    
    @Operation(summary = "提交入住申请")
    @PostMapping("/submit")
    public Result<?> submit(@RequestBody CheckInApplication application) {
        checkInService.submit(application);
        return Result.success("提交成功");
    }
    
    @Operation(summary = "审批入住申请")
    @PostMapping("/approve")
    @RequireRole({1, 2}) // 只有管理员和宿管可以审批
    public Result<?> approve(@RequestBody ApproveRequest request) {
        checkInService.approve(request.getId(), request.getStatus(), request.getRemark(), 
                request.getRoomId(), request.getBedId());
        return Result.success("审批成功");
    }
    
    @Operation(summary = "确认入住")
    @PostMapping("/confirm/{id}")
    @RequireRole({1, 2}) // 只有管理员和宿管可以确认
    public Result<?> confirmCheckIn(@PathVariable Long id) {
        checkInService.confirmCheckIn(id);
        return Result.success("确认入住成功");
    }
    
    @Operation(summary = "撤销申请")
    @DeleteMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Long id) {
        checkInService.cancelApplication(id);
        return Result.success("撤销成功");
    }
    
    @Operation(summary = "删除申请")
    @DeleteMapping("/{id}")
    @RequireRole({1, 2}) // 只有管理员和宿管可以删除
    public Result<?> delete(@PathVariable Long id) {
        checkInService.deleteApplication(id);
        return Result.success("删除成功");
    }
    
    @Operation(summary = "获取房间室友列表")
    @GetMapping("/roommates/{roomId}")
    public Result<?> getRoommates(@PathVariable Long roomId) {
        return Result.success(checkInService.getRoommates(roomId));
    }
    
    @Data
    public static class ApproveRequest {
        private Long id;
        private Long approverId;
        private String approverName;
        private Integer status;
        private String remark;
        private Long roomId;
        private Long bedId;
    }
}

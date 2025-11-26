package org.smartapartment.smartapartment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.smartapartment.smartapartment.annotation.RequireRole;
import org.smartapartment.smartapartment.common.PageResult;
import org.smartapartment.smartapartment.common.Result;
import org.smartapartment.smartapartment.entity.CheckoutApplication;
import org.smartapartment.smartapartment.service.CheckoutService;
import org.springframework.web.bind.annotation.*;

/**
 * 退宿申请控制器
 */
@Tag(name = "退宿管理", description = "退宿申请、审批等接口")
@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {
    
    private final CheckoutService checkoutService;
    
    @Operation(summary = "分页查询退宿申请列表")
    @GetMapping("/page")
    public Result<PageResult<CheckoutApplication>> getPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Integer status) {
        PageResult<CheckoutApplication> result = checkoutService.getPage(current, size, studentId, status);
        return Result.success(result);
    }
    
    @Operation(summary = "提交退宿申请")
    @PostMapping("/apply")
    @RequireRole({3}) // 学生
    public Result<?> apply(@RequestBody CheckoutApplication application) {
        checkoutService.submitApplication(application);
        return Result.success("提交成功");
    }
    
    @Operation(summary = "审批退宿申请")
    @PostMapping("/approve")
    @RequireRole({1, 2}) // 管理员、宿管
    public Result<?> approve(@RequestBody ApproveRequest request) {
        checkoutService.approve(request.getId(), request.getStatus(), request.getRemark());
        return Result.success("审批成功");
    }
    
    @Data
    public static class ApproveRequest {
        private Long id;
        private Integer status; // 2-同意 3-驳回
        private String remark;
    }
}

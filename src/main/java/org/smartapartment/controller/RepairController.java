package org.smartapartment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.smartapartment.annotation.RequireRole;
import org.smartapartment.common.PageResult;
import org.smartapartment.common.Result;
import org.smartapartment.entity.RepairApplication;
import org.smartapartment.service.RepairService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报修管理控制器
 */
@Tag(name = "报修管理", description = "报修申请、接单、维修等接口")
@RestController
@RequestMapping("/api/repair")
@RequiredArgsConstructor
public class RepairController {
    
    private final RepairService repairService;
    
    @Operation(summary = "分页查询报修列表")
    @GetMapping("/page")
    @RequireRole({1, 2, 3}) // 所有用户都可以查询
    public Result<PageResult<RepairApplication>> getPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer repairType,
            @RequestParam(required = false) List<Integer> statuses,
            @RequestParam(required = false) List<Integer> repairTypes) {
        PageResult<RepairApplication> result = repairService.getPage(current, size, studentId, status, repairType, statuses, repairTypes);
        return Result.success(result);
    }
    
    @Operation(summary = "提交报修申请")
    @PostMapping("/submit")
    @RequireRole({3}) // 仅学生可以提交报修
    public Result<?> submit(@RequestBody RepairApplication application) {
        repairService.submit(application);
        return Result.success("提交成功");
    }
    
    @Operation(summary = "接单")
    @PostMapping("/accept")
    @RequireRole({1, 2}) // 仅管理员和宿管可以接单
    public Result<?> accept(@RequestBody AcceptRequest request) {
        repairService.accept(request.getId());
        return Result.success("接单成功");
    }
    
    @Operation(summary = "更新维修进度")
    @PostMapping("/status")
    @RequireRole({1, 2}) // 仅管理员和宿管可以更新状态
    public Result<?> updateStatus(@RequestBody StatusRequest request) {
        repairService.updateStatus(request.getId(), request.getStatus(), request.getRemark());
        return Result.success("更新成功");
    }
    
    @Operation(summary = "评价")
    @PostMapping("/rate")
    @RequireRole({3}) // 仅学生可以评价
    public Result<?> rate(@RequestBody RateRequest request) {
        repairService.rate(request.getId(), request.getRating(), request.getComment());
        return Result.success("评价成功");
    }
    
    @Data
    public static class AcceptRequest {
        private Long id;
        private Long handlerId;
        private String handlerName;
    }
    
    @Data
    public static class StatusRequest {
        private Long id;
        private Integer status;
        private String remark;
    }
    
    @Data
    public static class RateRequest {
        private Long id;
        private Integer rating;
        private String comment;
    }
}

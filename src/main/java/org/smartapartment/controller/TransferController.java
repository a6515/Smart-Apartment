package org.smartapartment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.smartapartment.annotation.RequireRole;
import org.smartapartment.common.PageResult;
import org.smartapartment.common.Result;
import org.smartapartment.entity.TransferApplication;
import org.smartapartment.service.TransferService;
import org.springframework.web.bind.annotation.*;

/**
 * 换宿申请控制器
 */
@Tag(name = "换宿管理", description = "换宿申请、审批等接口")
@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {
    
    private final TransferService transferService;
    
    @Operation(summary = "分页查询换宿申请列表")
    @GetMapping("/page")
    public Result<PageResult<TransferApplication>> getPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Integer status) {
        PageResult<TransferApplication> result = transferService.getPage(current, size, studentId, status);
        return Result.success(result);
    }
    
    @Operation(summary = "提交换宿申请")
    @PostMapping("/apply")
    @RequireRole({3}) // 学生
    public Result<?> apply(@RequestBody TransferApplication application) {
        transferService.submitApplication(application);
        return Result.success("提交成功");
    }
    
    @Operation(summary = "审批换宿申请")
    @PostMapping("/approve")
    @RequireRole({1, 2}) // 管理员、宿管
    public Result<?> approve(@RequestBody ApproveRequest request) {
        transferService.approve(request.getId(), request.getStatus(), request.getRemark(), request.getBedId());
        return Result.success("审批成功");
    }
    
    @Data
    public static class ApproveRequest {
        private Long id;
        private Integer status; // 2-同意 3-驳回
        private String remark;
        private Long bedId; // 分配的床位ID（同意时必填）
    }
}

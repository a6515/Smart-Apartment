package org.smartapartment.smartapartment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.smartapartment.smartapartment.annotation.RequireRole;
import org.smartapartment.smartapartment.common.Result;
import org.smartapartment.smartapartment.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 数据统计控制器
 */
@Tag(name = "数据统计", description = "首页数据统计、图表数据接口")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@RequireRole({1, 2}) // 管理员和宿管可访问
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @Operation(summary = "获取首页统计数据")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardData() {
        Map<String, Object> data = statisticsService.getDashboardData();
        return Result.success(data);
    }
    
    @Operation(summary = "获取房间状态统计")
    @GetMapping("/room-status")
    public Result<Map<String, Object>> getRoomStatusStatistics() {
        Map<String, Object> data = statisticsService.getRoomStatusStatistics();
        return Result.success(data);
    }
    
    @Operation(summary = "获取报修类型统计")
    @GetMapping("/repair-type")
    public Result<Map<String, Object>> getRepairTypeStatistics() {
        Map<String, Object> data = statisticsService.getRepairTypeStatistics();
        return Result.success(data);
    }
    
    @Operation(summary = "获取费用统计")
    @GetMapping("/fee")
    public Result<Map<String, Object>> getFeeStatistics() {
        Map<String, Object> data = statisticsService.getFeeStatistics();
        return Result.success(data);
    }
}

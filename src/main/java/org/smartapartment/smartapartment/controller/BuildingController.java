package org.smartapartment.smartapartment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.smartapartment.smartapartment.annotation.RequireRole;
import org.smartapartment.smartapartment.common.PageResult;
import org.smartapartment.smartapartment.common.Result;
import org.smartapartment.smartapartment.entity.Building;
import org.smartapartment.smartapartment.service.BuildingService;
import org.springframework.web.bind.annotation.*;

/**
 * 楼宇管理控制器
 * 仅管理员和宿管可访问
 */
@Tag(name = "楼宇管理", description = "楼宇增删改查接口")
@RestController
@RequestMapping("/api/building")
@RequiredArgsConstructor
public class BuildingController {
    
    private final BuildingService buildingService;
    
    @Operation(summary = "分页查询楼宇列表")
    @GetMapping("/page")
    @RequireRole({1, 2})
    public Result<PageResult<Building>> getPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String buildingName,
            @RequestParam(required = false) Integer buildingType) {
        PageResult<Building> result = buildingService.getPage(current, size, buildingName, buildingType);
        return Result.success(result);
    }
    
    @Operation(summary = "根据ID查询楼宇")
    @GetMapping("/{id}")
    @RequireRole({1, 2})
    public Result<Building> getById(@PathVariable Long id) {
        Building building = buildingService.getById(id);
        return Result.success(building);
    }
    
    @Operation(summary = "新增楼宇")
    @PostMapping
    @RequireRole({1, 2})
    public Result<?> save(@RequestBody Building building) {
        buildingService.save(building);
        return Result.success("新增成功");
    }
    
    @Operation(summary = "更新楼宇")
    @PutMapping
    @RequireRole({1, 2})
    public Result<?> update(@RequestBody Building building) {
        buildingService.update(building);
        return Result.success("更新成功");
    }
    
    @Operation(summary = "删除楼宇")
    @DeleteMapping("/{id}")
    @RequireRole({1, 2})
    public Result<?> delete(@PathVariable Long id) {
        buildingService.delete(id);
        return Result.success("删除成功");
    }
    
    @Operation(summary = "获取所有楼宇列表")
    @GetMapping("/list")
    @RequireRole({1, 2, 3}) // 管理员、宿管、学生都可以查询楼宇列表
    public Result<?> getList() {
        return Result.success(buildingService.getList());
    }
}

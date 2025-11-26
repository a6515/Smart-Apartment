package org.smartapartment.smartapartment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.smartapartment.smartapartment.annotation.RequireRole;
import org.smartapartment.smartapartment.common.PageResult;
import org.smartapartment.smartapartment.common.Result;
import org.smartapartment.smartapartment.entity.Room;
import org.smartapartment.smartapartment.service.RoomService;
import org.springframework.web.bind.annotation.*;

/**
 * 房间管理控制器
 * 仅管理员和宿管可访问
 */
@Tag(name = "房间管理", description = "房间增删改查接口")
@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
@RequireRole({1, 2}) // 1-管理员, 2-宿管
public class RoomController {
    
    private final RoomService roomService;
    
    @Operation(summary = "分页查询房间列表")
    @GetMapping("/page")
    public Result<PageResult<Room>> getPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(name = "roomStatus", required = false) Integer roomStatus) {
        PageResult<Room> result = roomService.getPage(current, size, buildingId, roomNumber, roomStatus);
        return Result.success(result);
    }
    
    @Operation(summary = "根据ID查询房间")
    @GetMapping("/{id}")
    public Result<Room> getById(@PathVariable Long id) {
        Room room = roomService.getById(id);
        return Result.success(room);
    }
    
    @Operation(summary = "新增房间")
    @PostMapping
    public Result<?> save(@RequestBody Room room) {
        roomService.save(room);
        return Result.success("新增成功");
    }
    
    @Operation(summary = "更新房间")
    @PutMapping
    public Result<?> update(@RequestBody Room room) {
        roomService.update(room);
        return Result.success("更新成功");
    }
    
    @Operation(summary = "删除房间")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        roomService.delete(id);
        return Result.success("删除成功");
    }
    
    @Operation(summary = "获取可用房间列表（有空余床位）")
    @GetMapping("/available")
    @RequireRole({1, 2, 3}) // 管理员、宿管、学生都可以查询可用房间
    public Result<?> getAvailableRooms(@RequestParam(required = false) Long buildingId,
                                        @RequestParam(required = false) Integer roomType) {
        return Result.success(roomService.getAvailableRooms(buildingId, roomType));
    }
}

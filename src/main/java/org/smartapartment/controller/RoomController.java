package org.smartapartment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smartapartment.annotation.RequireRole;
import org.smartapartment.common.PageResult;
import org.smartapartment.common.Result;
import org.smartapartment.entity.Room;
import org.smartapartment.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房间管理控制器
 * 仅管理员和宿管可访问
 */
@Tag(name = "房间管理", description = "房间增删改查接口")
@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
@Slf4j
public class RoomController {
    
    private final RoomService roomService;
    
    @Operation(summary = "分页查询房间列表")
    @GetMapping("/page")
    @RequireRole({1, 2}) // 1-管理员, 2-宿管
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
    @RequireRole({1, 2}) // 1-管理员, 2-宿管
    public Result<Room> getById(@PathVariable Long id) {
        Room room = roomService.getById(id);
        return Result.success(room);
    }
    
    @Operation(summary = "新增房间")
    @PostMapping
    @RequireRole({1, 2}) // 1-管理员, 2-宿管
    public Result<?> save(@RequestBody Room room) {
        roomService.save(room);
        return Result.success("新增成功");
    }
    
    @Operation(summary = "更新房间")
    @PutMapping
    @RequireRole({1, 2}) // 1-管理员, 2-宿管
    public Result<?> update(@RequestBody Room room) {
        log.info("【开始变更房间】房间ID: {}, 房间号: {}", room.getId(), room.getRoomNumber());
        roomService.update(room);
        log.info("【房间变更完成】房间ID: {}, 房间号: {}", room.getId(), room.getRoomNumber());
        return Result.success("更新成功");
    }
    
    @Operation(summary = "删除房间")
    @DeleteMapping("/{id}")
    @RequireRole({1, 2}) // 1-管理员, 2-宿管
    public Result<?> delete(@PathVariable Long id) {
        roomService.delete(id);
        return Result.success("删除成功");
    }
    
    @Operation(summary = "获取可用房间列表（有空余床位）")
    @GetMapping("/available")
    public Result<?> getAvailableRooms(@RequestParam(required = false) Long buildingId,
                                      @RequestParam(required = false) Integer roomType,
                                      @RequestParam(required = false) Integer floorNumber,
                                      @RequestParam(required = false) String bedStatus) {
        // 详细记录接收到的参数，便于调试
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("【后端控制器】接收筛选条件: buildingId=" + buildingId 
                          + ", roomType=" + roomType 
                          + ", floorNumber=" + floorNumber 
                          + ", bedStatus=" + bedStatus);
        
        // 打印请求头信息
        System.out.println("【后端控制器】请求头 X-Debug-Info: " + bedStatus);

                          
        // 床位状态处理：available = 有空床位，full = 无空床位，null = 不筛选
        Boolean hasAvailableBeds = null;
        if ("available".equals(bedStatus)) {
            hasAvailableBeds = true;
        } else if ("full".equals(bedStatus)) {
            hasAvailableBeds = false;
        }
        
        // 调用服务层
        List<Room> rooms = roomService.getAvailableRooms(buildingId, roomType, floorNumber, hasAvailableBeds);
        
        // 记录返回数据
        System.out.println("【后端控制器】返回数据条数: " + (rooms != null ? rooms.size() : 0));
        if (floorNumber != null) {
            System.out.println("【后端控制器】筛选楼层" + floorNumber + "的结果数: " 
                + rooms.stream().filter(r -> r.getFloor() != null && r.getFloor().equals(floorNumber)).count());
        }
        
        return Result.success(rooms);
    }
}

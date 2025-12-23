package org.smartapartment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.smartapartment.annotation.RequireRole;
import org.smartapartment.common.Result;
import org.smartapartment.mapper.BedMapper;
import org.springframework.web.bind.annotation.*;

/**
 * 床位管理控制器
 */
@Tag(name = "床位管理", description = "床位查询接口")
@RestController
@RequestMapping("/api/bed")
@RequiredArgsConstructor
@RequireRole({1, 2}) // 1-管理员, 2-宿管
public class BedController {
    
    private final BedMapper bedMapper;
    
    @Operation(summary = "获取房间的可用床位列表")
    @GetMapping("/available")
    @RequireRole({1, 2, 3}) // 管理员、宿管、学生都可以查询可用床位
    public Result<?> getAvailableBeds(@RequestParam Long roomId) {
        return Result.success(bedMapper.selectAvailableByRoomId(roomId));
    }
}

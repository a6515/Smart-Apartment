package org.smartapartment.service;

import lombok.RequiredArgsConstructor;
import org.smartapartment.mapper.*;
import org.smartapartment.entity.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 统计服务类
 */
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;
    private final BedMapper bedMapper;
    private final CheckInApplicationMapper checkInMapper;
    private final RepairApplicationMapper repairMapper;

    /**
     * 获取首页统计数据
     */
    public Map<String, Object> getDashboardData() {
        Map<String, Object> result = new HashMap<>();

        // 楼宇总数
        Long buildingCount = buildingMapper.countTotal();
        result.put("buildingCount", buildingCount);

        // 房间总数
        Long roomCount = roomMapper.countTotal();
        result.put("roomCount", roomCount);

        // 总床位数
        Long totalBeds = roomMapper.countTotalBeds();
        result.put("totalBeds", totalBeds);

        // 可用床位数
        Long availableBeds = roomMapper.countAvailableBeds();
        result.put("availableBeds", availableBeds);

        // 已入住床位数
        Long occupiedBeds = totalBeds - availableBeds;
        result.put("occupiedBeds", occupiedBeds);

        // 入住率
        String occupancyRate = "0";
        if (totalBeds > 0) {
            BigDecimal rate = new BigDecimal(occupiedBeds)
                    .divide(new BigDecimal(totalBeds), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(100));
            occupancyRate = rate.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        }
        result.put("occupancyRate", occupancyRate);

        // 待审批入住申请
        Long pendingCheckIn = checkInMapper.countPending();
        result.put("pendingCheckIn", pendingCheckIn);

        // 待处理报修
        Long pendingRepair = repairMapper.countPending();
        result.put("pendingRepair", pendingRepair);

        // 未缴费记录
        result.put("unpaidFee", 0L);

        return result;
    }

    /**
     * 获取房间状态统计
     */
    public Map<String, Object> getRoomStatusStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 空闲（状态1）
        Long available = roomMapper.countByStatus(1);
        result.put("available", available);

        // 部分入住（状态2）
        Long partiallyOccupied = roomMapper.countByStatus(2);
        result.put("partiallyOccupied", partiallyOccupied);

        // 已满（状态3）
        Long full = roomMapper.countByStatus(3);
        result.put("full", full);

        // 维修中（状态4）
        Long maintenance = roomMapper.countByStatus(4);
        result.put("maintenance", maintenance);

        return result;
    }

    /**
     * 获取报修类型统计
     */
    public Map<String, Object> getRepairTypeStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 水电维修（类型1）
        Long waterElectric = repairMapper.countByType(1);
        result.put("waterElectric", waterElectric);

        // 家具维修（类型2）
        Long furniture = repairMapper.countByType(2);
        result.put("furniture", furniture);

        // 网络故障（类型3）
        Long network = repairMapper.countByType(3);
        result.put("network", network);

        // 其他（类型4）
        Long other = repairMapper.countByType(4);
        result.put("other", other);

        return result;
    }

    /**
     * 获取费用统计
     */
    public Map<String, Object> getFeeStatistics() {
        Map<String, Object> result = new HashMap<>();

        // TODO: 需要在FeeRecordMapper中添加统计方法
        // 临时返回模拟数据
        BigDecimal paidTotal = new BigDecimal("580000.00");
        BigDecimal unpaidTotal = new BigDecimal("120000.00");

        result.put("paidTotal", paidTotal);
        result.put("unpaidTotal", unpaidTotal);
        result.put("total", paidTotal.add(unpaidTotal));

        return result;
    }
}

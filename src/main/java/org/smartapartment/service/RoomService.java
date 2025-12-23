package org.smartapartment.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.smartapartment.common.PageResult;
import org.smartapartment.entity.Room;
import org.smartapartment.exception.BusinessException;

import org.smartapartment.mapper.RoomMapper;
import org.smartapartment.model.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 房间服务类
 */
@Service
@RequiredArgsConstructor
public class RoomService {

    private static final Logger log = LoggerFactory.getLogger(RoomService.class);

    private final RoomMapper roomMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MessageProducerService messageProducerService;

    private final WebSocketService webSocketService;

    // Redis缓存前缀
    private static final String ROOM_CACHE_PREFIX = "room:";
    private static final String ROOM_LIST_CACHE_KEY = ROOM_CACHE_PREFIX + "list:";
    private static final String ROOM_AVAILABLE_CACHE_KEY = ROOM_CACHE_PREFIX + "available:";
    private static final long CACHE_EXPIRE_TIME = 30; // 缓存过期时间（分钟）

    /**
     * 分页查询房间列表
     */
    public PageResult<Room> getPage(Long current, Long size, Long buildingId, String roomNumber, Integer roomStatus) {
        // 使用PageHelper进行分页
        PageHelper.startPage(current.intValue(), size.intValue());
        List<Room> list = roomMapper.selectPage(buildingId, roomNumber, roomStatus);
        PageInfo<Room> pageInfo = new PageInfo<>(list);

        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList(), current, size);
    }

    /**
     * 根据ID查询房间
     */
    public Room getById(Long id) {
        // 构建缓存key
        String cacheKey = ROOM_CACHE_PREFIX + id;

        // 尝试从缓存获取
        Room room = (Room) redisTemplate.opsForValue().get(cacheKey);
        if (room != null) {
            return room;
        }

        // 缓存未命中，查询数据库
        room = roomMapper.selectById(id);
        if (room == null) {
            throw new BusinessException("房间不存在");
        }

        // 写入缓存
        redisTemplate.opsForValue().set(cacheKey, room, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);

        return room;
    }

    /**
     * 新增房间
     */
    public void save(Room room) {
        // 初始化可用床位数
        if (room.getAvailableBeds() == null) {
            room.setAvailableBeds(room.getTotalBeds());
        }

        roomMapper.insert(room);

        // 清除相关缓存
        clearRoomListCache();

        // 发送房间创建消息
        NotificationMessage message = NotificationMessage.createRoomNotification(
                "房间创建",
                "新房间已创建: " + room.getRoomNumber(),
                room.getId());
        messageProducerService.sendRoomUpdate(message);
    }

    /**
     * 更新房间
     */
    public void update(Room room) {
        if (room.getId() == null) {
            throw new BusinessException("房间ID不能为空");
        }

        // 获取原房间信息用于比较
        Room oldRoom = getById(room.getId());
        log.info("【变更房间信息】从 {} 变更为 {}", oldRoom.getRoomNumber(), room.getRoomNumber());

        // 执行数据库更新操作
        roomMapper.updateById(room);
        log.info("【房间信息已更新到数据库】房间ID: {}", room.getId());

        // 清除相关缓存
        clearRoomCache(room.getId());
        clearRoomListCache();
        log.info("【房间缓存已清除】房间ID: {}", room.getId());

        // 生成变更详情
        String updateDetails = generateUpdateDetails(oldRoom, room);
        log.info("【生成房间变更详情】{}", updateDetails);

        // 创建消息对象
        NotificationMessage message = NotificationMessage.createRoomNotification(
                "房间更新",
                "房间 " + room.getRoomNumber() + " 信息已更新" + updateDetails,
                room.getId());

        // 发送房间变更消息
        log.info("【发送房间变更消息到MQ】房间ID: {}", room.getId());
        messageProducerService.sendRoomUpdate(message);
        log.info("【房间变更消息已发送】房间ID: {}", room.getId());
    }

    /**
     * 删除房间
     */
    public void delete(Long id) {
        Room room = getById(id);
        roomMapper.deleteById(id);

        // 清除相关缓存
        clearRoomCache(id);
        clearRoomListCache();

        // 发送房间删除消息
        NotificationMessage message = NotificationMessage.createRoomNotification(
                "房间删除",
                "房间 " + room.getRoomNumber() + " 已删除",
                id);
        messageProducerService.sendRoomUpdate(message);
    }

    /**
     * 获取可用房间列表（有空余床位的房间）
     * 
     * @param buildingId       楼栋ID（可选）
     * @param roomType         房间类型（可选）
     * @param floorNumber      楼层（可选）
     * @param hasAvailableBeds 是否有可用床位（可选，true=有空床位，false=无空床位，null=不筛选）
     * @return 房间列表
     */
    public List<Room> getAvailableRooms(Long buildingId, Integer roomType, Integer floorNumber,
            Boolean hasAvailableBeds) {
        long startTime = System.currentTimeMillis();
        String dataSource = "未知";

        // 尝试从特定筛选条件缓存获取
        String specificCacheKey = buildCacheKey(ROOM_AVAILABLE_CACHE_KEY, buildingId, roomType, floorNumber,
                hasAvailableBeds);
        List<Room> rooms = (List<Room>) redisTemplate.opsForValue().get(specificCacheKey);

        if (rooms != null) {
            dataSource = "Redis特定条件缓存";
            System.out.println("【服务层】从特定筛选条件缓存获取房间列表: " + specificCacheKey);
            long endTime = System.currentTimeMillis();
            System.out.println("【性能统计】数据来源: " + dataSource + ", 查询耗时: " + (endTime - startTime) + "ms");
            return rooms;
        }

        // 如果特定条件缓存未命中，检查全量缓存
        if (hasFilterConditions(buildingId, roomType, floorNumber, hasAvailableBeds)) {
            long cacheStartTime = System.currentTimeMillis();
            String allCacheKey = ROOM_AVAILABLE_CACHE_KEY + "all:";
            List<Room> allRooms = (List<Room>) redisTemplate.opsForValue().get(allCacheKey);

            if (allRooms != null) {
                long cacheEndTime = System.currentTimeMillis();
                System.out.println("【服务层】从全量缓存筛选房间列表: " + allCacheKey);

                // 在内存中进行筛选
                long filterStartTime = System.currentTimeMillis();
                List<Room> filteredRooms = filterRoomsInMemory(allRooms, buildingId, roomType, floorNumber,
                        hasAvailableBeds);
                long filterEndTime = System.currentTimeMillis();

                System.out.println("【服务层】内存筛选后房间数量: " + filteredRooms.size());

                // 如果筛选后有结果，则缓存并返回
                if (!filteredRooms.isEmpty()) {
                    // 将筛选结果存入特定条件缓存
                    redisTemplate.opsForValue().set(specificCacheKey, filteredRooms, CACHE_EXPIRE_TIME,
                            TimeUnit.MINUTES);

                    dataSource = "Redis全量缓存+内存筛选";
                    long endTime = System.currentTimeMillis();
                    System.out.println("【性能统计】数据来源: " + dataSource +
                            ", 总耗时: " + (endTime - startTime) + "ms" +
                            ", Redis读取: " + (cacheEndTime - cacheStartTime) + "ms" +
                            ", 内存筛选: " + (filterEndTime - filterStartTime) + "ms");

                    return filteredRooms;
                }
                // 内存筛选无结果，尝试从数据库查询
                System.out.println("【服务层】内存筛选无结果，尝试从数据库查询");
            }
        }

        // 日志记录传入的参数
        System.out.println("【服务层】缓存未命中或内存筛选无结果，从数据库查询: buildingId=" + buildingId
                + ", roomType=" + roomType
                + ", floorNumber=" + floorNumber
                + ", hasAvailableBeds=" + hasAvailableBeds);

        // 缓存未命中，查询数据库
        long dbStartTime = System.currentTimeMillis();
        rooms = roomMapper.selectAvailableRooms(buildingId, roomType, floorNumber, hasAvailableBeds);
        long dbEndTime = System.currentTimeMillis();

        // 写入特定条件缓存
        long cacheWriteStart = System.currentTimeMillis();
        redisTemplate.opsForValue().set(specificCacheKey, rooms, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);

        // 如果是查询所有房间，同时更新全量缓存
        if (!hasFilterConditions(buildingId, roomType, floorNumber, hasAvailableBeds)) {
            String allCacheKey = ROOM_AVAILABLE_CACHE_KEY + "all:";
            redisTemplate.opsForValue().set(allCacheKey, rooms, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
            System.out.println("【服务层】更新全量房间缓存: " + allCacheKey);
        }
        long cacheWriteEnd = System.currentTimeMillis();

        // 记录返回数据
        System.out.println("【服务层】返回数据条数: " + (rooms != null ? rooms.size() : 0));
        if (floorNumber != null) {
            System.out.println("【服务层】确认楼层" + floorNumber + "的记录数: "
                    + rooms.stream().filter(r -> r.getFloor() != null && r.getFloor().equals(floorNumber)).count());
        }

        dataSource = "MySQL数据库";
        long endTime = System.currentTimeMillis();
        System.out.println("【性能统计】数据来源: " + dataSource +
                ", 总耗时: " + (endTime - startTime) + "ms" +
                ", 数据库查询: " + (dbEndTime - dbStartTime) + "ms" +
                ", 缓存写入: " + (cacheWriteEnd - cacheWriteStart) + "ms");

        return rooms;
    }

    /**
     * 判断是否有任何筛选条件
     */
    private boolean hasFilterConditions(Long buildingId, Integer roomType, Integer floorNumber,
            Boolean hasAvailableBeds) {
        return buildingId != null || roomType != null || floorNumber != null || hasAvailableBeds != null;
    }

    /**
     * 在内存中筛选房间列表
     */
    private List<Room> filterRoomsInMemory(List<Room> allRooms, Long buildingId, Integer roomType, Integer floorNumber,
            Boolean hasAvailableBeds) {
        return allRooms.stream()
                .filter(room -> buildingId == null
                        || (room.getBuildingId() != null && room.getBuildingId().equals(buildingId)))
                .filter(room -> roomType == null || (room.getRoomType() != null && room.getRoomType().equals(roomType)))
                .filter(room -> floorNumber == null || (room.getFloor() != null && room.getFloor().equals(floorNumber)))
                .filter(room -> hasAvailableBeds == null ||
                        (hasAvailableBeds && room.getAvailableBeds() != null && room.getAvailableBeds() > 0) ||
                        (!hasAvailableBeds && room.getAvailableBeds() != null && room.getAvailableBeds() == 0))
                .collect(Collectors.toList());
    }

    /**
     * 构建缓存key
     */
    private String buildCacheKey(String prefix, Object... params) {
        StringBuilder keyBuilder = new StringBuilder(prefix);
        for (Object param : params) {
            // 只有当参数非空时才添加到键中
            if (param != null) {
                keyBuilder.append(param).append(":");
            }
        }

        // 如果没有添加任何参数，使用"all"表示所有数据
        if (keyBuilder.toString().equals(prefix)) {
            keyBuilder.append("all:");
        }

        return keyBuilder.toString();
    }

    /**
     * 清除房间缓存
     */
    public void clearRoomCache(Long roomId) {
        String cacheKey = ROOM_CACHE_PREFIX + roomId;
        redisTemplate.delete(cacheKey);
        System.out.println("【缓存清理】已清除房间缓存: " + cacheKey);
    }

    /**
     * 清除房间列表相关缓存
     */
    public void clearRoomListCache() {
        // 获取所有与房间列表相关的缓存键
        String pattern = ROOM_LIST_CACHE_KEY + "*";
        String availablePattern = ROOM_AVAILABLE_CACHE_KEY + "*";

        // 清除房间列表缓存
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            System.out.println("【缓存清理】已清除房间列表缓存, 键数量: " + keys.size());
        }

        // 清除可用房间列表缓存
        Set<String> availableKeys = redisTemplate.keys(availablePattern);
        if (availableKeys != null && !availableKeys.isEmpty()) {
            redisTemplate.delete(availableKeys);
            System.out.println("【缓存清理】已清除可用房间列表缓存, 键数量: " + availableKeys.size());
        }
    }

    /**
     * 清除指定楼栋相关的所有房间缓存
     * 在楼栋信息变更时调用此方法
     * 
     * @param buildingId 楼栋ID
     */
    public void clearCacheByBuildingId(Long buildingId) {
        if (buildingId == null) {
            return;
        }

        // 清除特定楼栋相关的所有缓存
        String specificPattern = ROOM_AVAILABLE_CACHE_KEY + buildingId + ":*";
        Set<String> specificKeys = redisTemplate.keys(specificPattern);
        if (specificKeys != null && !specificKeys.isEmpty()) {
            redisTemplate.delete(specificKeys);
            System.out.println("【缓存清理】已清除楼栋ID: " + buildingId + " 相关缓存, 键数量: " + specificKeys.size());
        }

        // 同时清除全量缓存，因为它们可能包含该楼栋的房间
        clearRoomListCache();

        // 清除该楼栋下所有房间的单个缓存
        List<Room> rooms = roomMapper.selectByBuildingId(buildingId);
        if (rooms != null && !rooms.isEmpty()) {
            for (Room room : rooms) {
                clearRoomCache(room.getId());
            }
            System.out.println("【缓存清理】已清除楼栋ID: " + buildingId + " 下的 " + rooms.size() + " 个房间缓存");
        }
    }

    /**
     * 更新床位状态时清除相关缓存
     * 当床位状态发生变化时调用此方法
     * 
     * @param roomId 房间ID
     */
    public void updateBedStatus(Long roomId) {
        // 更新房间缓存
        clearRoomCache(roomId);

        // 清除房间列表缓存
        clearRoomListCache();

        // 获取房间信息
        Room room = getById(roomId);

        // 发送床位状态变更消息
        NotificationMessage message = NotificationMessage.createRoomNotification(
                "床位状态变更",
                "房间 " + room.getRoomNumber() + " 床位状态已更新，当前可用床位: " + room.getAvailableBeds(),
                roomId);
        messageProducerService.sendRoomUpdate(message);

        System.out.println("【缓存更新】床位状态变更，已清除相关缓存, 房间ID: " + roomId);
    }

    /**
     * 生成房间更新的详细信息
     */
    private String generateUpdateDetails(Room oldRoom, Room newRoom) {
        StringBuilder details = new StringBuilder();

        if (oldRoom != null && newRoom != null) {
            // 比较房间号变更
            if (!oldRoom.getRoomNumber().equals(newRoom.getRoomNumber())) {
                details.append("，房间号由 ").append(oldRoom.getRoomNumber())
                        .append(" 变更为 ").append(newRoom.getRoomNumber());
            }

            // 比较楼层变更
            if (!oldRoom.getFloor().equals(newRoom.getFloor())) {
                details.append("，楼层由 ").append(oldRoom.getFloor())
                        .append(" 变更为 ").append(newRoom.getFloor());
            }

            // 比较总床位数变更
            if (!oldRoom.getTotalBeds().equals(newRoom.getTotalBeds())) {
                details.append("，总床位由 ").append(oldRoom.getTotalBeds())
                        .append(" 变更为 ").append(newRoom.getTotalBeds());
            }

            // 比较可用床位数变更
            if (!oldRoom.getAvailableBeds().equals(newRoom.getAvailableBeds())) {
                details.append("，可用床位由 ").append(oldRoom.getAvailableBeds())
                        .append(" 变更为 ").append(newRoom.getAvailableBeds());
            }
        }

        return details.toString();
    }

    /**
     * 清除所有Room相关缓存
     * 在配置修改后调用此方法，确保使用新的格式
     */
    public void clearAllRoomCache() {
        // 清除单个房间缓存
        Set<String> roomKeys = redisTemplate.keys(ROOM_CACHE_PREFIX + "*");
        if (roomKeys != null && !roomKeys.isEmpty()) {
            redisTemplate.delete(roomKeys);
            System.out.println("【缓存清理】已清除所有房间缓存, 键数量: " + roomKeys.size());
        }

        // 清除房间列表缓存
        Set<String> listKeys = redisTemplate.keys(ROOM_LIST_CACHE_KEY + "*");
        if (listKeys != null && !listKeys.isEmpty()) {
            redisTemplate.delete(listKeys);
            System.out.println("【缓存清理】已清除所有房间列表缓存, 键数量: " + listKeys.size());
        }

        // 清除可用房间缓存
        Set<String> availableKeys = redisTemplate.keys(ROOM_AVAILABLE_CACHE_KEY + "*");
        if (availableKeys != null && !availableKeys.isEmpty()) {
            redisTemplate.delete(availableKeys);
            System.out.println("【缓存清理】已清除所有可用房间缓存, 键数量: " + availableKeys.size());
        }
    }
}

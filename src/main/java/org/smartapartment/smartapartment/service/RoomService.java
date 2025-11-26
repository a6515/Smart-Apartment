package org.smartapartment.smartapartment.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.smartapartment.smartapartment.common.PageResult;
import org.smartapartment.smartapartment.entity.Room;
import org.smartapartment.smartapartment.exception.BusinessException;
import org.smartapartment.smartapartment.mapper.RoomMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 房间服务类
 */
@Service
@RequiredArgsConstructor
public class RoomService {
    
    private final RoomMapper roomMapper;
    
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
        Room room = roomMapper.selectById(id);
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        return room;
    }
    
    /**
     * 新增房间
     */
    public void save(Room room) {
        // TODO: 需要在RoomMapper中添加checkRoomExists方法检查房间号是否存在
        
        // 初始化可用床位数
        if (room.getAvailableBeds() == null) {
            room.setAvailableBeds(room.getTotalBeds());
        }
        
        roomMapper.insert(room);
    }
    
    /**
     * 更新房间
     */
    public void update(Room room) {
        if (room.getId() == null) {
            throw new BusinessException("房间ID不能为空");
        }
        
        roomMapper.updateById(room);
    }
    
    /**
     * 删除房间
     */
    public void delete(Long id) {
        roomMapper.deleteById(id);
    }
    
    /**
     * 获取可用房间列表（有空余床位的房间）
     */
    public List<Room> getAvailableRooms(Long buildingId, Integer roomType) {
        return roomMapper.selectAvailableRooms(buildingId, roomType);
    }
}

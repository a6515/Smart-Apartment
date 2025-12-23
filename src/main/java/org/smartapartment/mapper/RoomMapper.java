package org.smartapartment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.smartapartment.entity.Room;
import java.util.List;

/**
 * 房间Mapper接口
 */
@Mapper
public interface RoomMapper {
    
    List<Room> selectPage(@Param("buildingId") Long buildingId,
                         @Param("roomNumber") String roomNumber,
                         @Param("roomStatus") Integer roomStatus);
    
    Room selectById(@Param("id") Long id);
    
    int insert(Room room);
    
    int updateById(Room room);
    
    int deleteById(@Param("id") Long id);
    
    List<Room> selectAvailableRooms(@Param("buildingId") Long buildingId, 
                                     @Param("roomType") Integer roomType,
                                     @Param("floorNumber") Integer floorNumber,
                                     @Param("hasAvailableBeds") Boolean hasAvailableBeds);
    
    /**
     * 按楼栋ID查询所有房间
     */
    List<Room> selectByBuildingId(@Param("buildingId") Long buildingId);
    
    /**
     * 统计房间总数
     */
    Long countTotal();
    
    /**
     * 统计各状态房间数量
     */
    Long countByStatus(@Param("status") Integer status);
    
    /**
     * 统计总床位数
     */
    Long countTotalBeds();
    
    /**
     * 统计剩余床位数
     */
    Long countAvailableBeds();
}

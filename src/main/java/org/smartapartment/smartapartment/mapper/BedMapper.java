package org.smartapartment.smartapartment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.smartapartment.smartapartment.entity.Bed;
import java.util.List;

/**
 * 床位Mapper接口
 */
@Mapper
public interface BedMapper {
    
    List<Bed> selectByRoomId(@Param("roomId") Long roomId);
    
    Bed selectById(@Param("id") Long id);
    
    int insert(Bed bed);
    
    int updateById(Bed bed);
    
    int deleteById(@Param("id") Long id);
    
    List<Bed> selectAvailableByRoomId(@Param("roomId") Long roomId);
}

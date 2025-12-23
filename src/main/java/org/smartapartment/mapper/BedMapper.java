package org.smartapartment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.smartapartment.entity.Bed;
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

    /**
     * 根据房间ID查询住在该房间的用户ID列表
     * 直接从bed表查询，床位状态为2(已占用)
     */
    List<Long> selectResidentIdsByRoomId(@Param("roomId") Long roomId);

    /**
     * 查询用户当前入住的房间ID
     * 直接从bed表查询
     */
    Long selectRoomIdByStudentId(@Param("studentId") Long studentId);
}

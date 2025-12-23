package org.smartapartment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.smartapartment.entity.CheckInApplication;
import java.util.List;
import java.util.Map;

/**
 * 入住申请Mapper接口
 */
@Mapper
public interface CheckInApplicationMapper {
    
    List<CheckInApplication> selectPage(@Param("studentId") Long studentId, 
                                        @Param("status") Integer status,
                                        @Param("studentName") String studentName);
    
    CheckInApplication selectById(@Param("id") Long id);
    
    int insert(CheckInApplication application);
    
    int updateById(CheckInApplication application);
    
    int deleteById(@Param("id") Long id);
    
    List<Map<String, Object>> selectRoommatesByRoomId(@Param("roomId") Long roomId);
    
    /**
     * 统计待审批的申请数量（状态为1）
     */
    Long countPending();
}

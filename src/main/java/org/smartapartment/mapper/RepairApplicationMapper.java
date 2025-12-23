package org.smartapartment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.smartapartment.entity.RepairApplication;
import java.util.List;

/**
 * 报修申请Mapper接口
 */
@Mapper
public interface RepairApplicationMapper {
    
    List<RepairApplication> selectPage(@Param("studentId") Long studentId,
                                       @Param("status") Integer status,
                                       @Param("repairType") Integer repairType,
                                       @Param("statuses") List<Integer> statuses,
                                       @Param("repairTypes") List<Integer> repairTypes);
    
    RepairApplication selectById(@Param("id") Long id);
    
    int insert(RepairApplication application);
    
    int updateById(RepairApplication application);
    
    int deleteById(@Param("id") Long id);
    
    /**
     * 统计待处理的报修数量（状态为1）
     */
    Long countPending();
    
    /**
     * 按报修类型统计数量
     */
    Long countByType(@Param("repairType") Integer repairType);
}

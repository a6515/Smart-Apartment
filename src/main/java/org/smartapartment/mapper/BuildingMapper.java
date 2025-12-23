package org.smartapartment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.smartapartment.entity.Building;
import java.util.List;

/**
 * 楼宇Mapper接口
 */
@Mapper
public interface BuildingMapper {
    
    /**
     * 分页查询楼宇列表
     */
    List<Building> selectPage(@Param("buildingName") String buildingName, 
                              @Param("buildingType") Integer buildingType);
    
    /**
     * 根据ID查询
     */
    Building selectById(@Param("id") Long id);
    
    /**
     * 插入楼宇
     */
    int insert(Building building);
    
    /**
     * 更新楼宇
     */
    int updateById(Building building);
    
    /**
     * 删除楼宇（逻辑删除）
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 统计楼宇总数
     */
    Long countTotal();
    
    /**
     * 获取热门楼栋列表，按入住率排序（最多返回指定数量）
     */
    List<Building> selectPopularBuildings(@Param("limit") Integer limit);
}

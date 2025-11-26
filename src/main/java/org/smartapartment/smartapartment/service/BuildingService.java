package org.smartapartment.smartapartment.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.smartapartment.smartapartment.common.PageResult;
import org.smartapartment.smartapartment.entity.Building;
import org.smartapartment.smartapartment.exception.BusinessException;
import org.smartapartment.smartapartment.mapper.BuildingMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 楼宇服务类
 */
@Service
@RequiredArgsConstructor
public class BuildingService {
    
    private final BuildingMapper buildingMapper;
    
    /**
     * 分页查询楼宇列表
     */
    public PageResult<Building> getPage(Long current, Long size, String buildingName, Integer buildingType) {
        // 使用PageHelper进行分页
        PageHelper.startPage(current.intValue(), size.intValue());
        List<Building> list = buildingMapper.selectPage(buildingName, buildingType);
        PageInfo<Building> pageInfo = new PageInfo<>(list);
        
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList(), current, size);
    }
    
    /**
     * 根据ID查询楼宇
     */
    public Building getById(Long id) {
        Building building = buildingMapper.selectById(id);
        if (building == null) {
            throw new BusinessException("楼宇不存在");
        }
        return building;
    }
    
    /**
     * 新增楼宇
     */
    public void save(Building building) {
        // TODO: 需要添加检查楼宇编号是否存在的逻辑
        // 可以在BuildingMapper中添加 countByCode 方法
        buildingMapper.insert(building);
    }
    
    /**
     * 更新楼宇
     */
    public void update(Building building) {
        if (building.getId() == null) {
            throw new BusinessException("楼宇ID不能为空");
        }
        
        // TODO: 需要添加检查楼宇编号是否重复的逻辑
        buildingMapper.updateById(building);
    }
    
    /**
     * 删除楼宇
     */
    public void delete(Long id) {
        buildingMapper.deleteById(id);
    }
    
    /**
     * 获取所有楼宇列表（不分页）
     */
    public List<Building> getList() {
        return buildingMapper.selectPage(null, null);
    }
}

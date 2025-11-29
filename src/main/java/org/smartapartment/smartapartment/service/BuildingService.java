package org.smartapartment.smartapartment.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.smartapartment.smartapartment.common.PageResult;
import org.smartapartment.smartapartment.entity.Building;
import org.smartapartment.smartapartment.exception.BusinessException;
import org.smartapartment.smartapartment.mapper.BuildingMapper;
import org.smartapartment.smartapartment.model.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 楼宇服务类
 */
@Service
@RequiredArgsConstructor
public class BuildingService {
    
    private final BuildingMapper buildingMapper;
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private MessageProducerService messageProducerService;
    
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
     * 新楼栋没有关联房间，无需清除缓存
     */
    public void save(Building building) {
        // TODO: 需要添加检查楼宇编号是否存在的逻辑
        // 可以在BuildingMapper中添加 countByCode 方法
        buildingMapper.insert(building);
        
        // 发送楼栋创建消息
        NotificationMessage message = NotificationMessage.createBuildingNotification(
            "楼栋创建",
            "新楼栋已创建: " + building.getBuildingName(),
            building.getId()
        );
        messageProducerService.sendNotification(message);
    }
    
    /**
     * 更新楼宇
     * 楼栋信息变更时，清除相关房间缓存
     */
    public void update(Building building) {
        if (building.getId() == null) {
            throw new BusinessException("楼宇ID不能为空");
        }
        
        // 获取原楼栋信息用于比较
        Building oldBuilding = getById(building.getId());
        
        // TODO: 需要添加检查楼宇编号是否重复的逻辑
        buildingMapper.updateById(building);
        
        // 清除与该楼栋相关的所有房间缓存
        roomService.clearCacheByBuildingId(building.getId());
        System.out.println("【楼栋更新】清除楼栋ID:" + building.getId() + "相关的所有房间缓存");
        
        // 发送楼栋更新消息
        String updateDetails = generateUpdateDetails(oldBuilding, building);
        NotificationMessage message = NotificationMessage.createBuildingNotification(
            "楼栋更新",
            "楼栋 " + building.getBuildingName() + " 信息已更新" + updateDetails,
            building.getId()
        );
        messageProducerService.sendNotification(message);
    }
    
    /**
     * 删除楼宇
     * 删除楼栋前，清除相关房间缓存
     */
    public void delete(Long id) {
        // 获取楼栋信息用于消息发送
        Building building = getById(id);
        
        // 删除前清除相关缓存
        roomService.clearCacheByBuildingId(id);
        System.out.println("【楼栋删除】清除楼栋ID:" + id + "相关的所有房间缓存");
        
        buildingMapper.deleteById(id);
        
        // 发送楼栋删除消息
        NotificationMessage message = NotificationMessage.createBuildingNotification(
            "楼栋删除",
            "楼栋 " + building.getBuildingName() + " 已删除",
            id
        );
        messageProducerService.sendNotification(message);
    }
    
    /**
     * 获取所有楼宇列表（不分页）
     */
    public List<Building> getList() {
        return buildingMapper.selectPage(null, null);
    }
    
    /**
     * 获取热门楼栋列表（按入住率排序）
     */
    public List<Building> getPopularBuildings(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 3; // 默认返回3个热门楼栋
        }
        return buildingMapper.selectPopularBuildings(limit);
    }
    
    /**
     * 生成楼栋更新的详细信息
     */
    private String generateUpdateDetails(Building oldBuilding, Building newBuilding) {
        StringBuilder details = new StringBuilder();
        
        if (oldBuilding != null && newBuilding != null) {
            // 比较楼栋名称变更
            if (!oldBuilding.getBuildingName().equals(newBuilding.getBuildingName())) {
                details.append("，楼栋名称由 ").append(oldBuilding.getBuildingName())
                      .append(" 变更为 ").append(newBuilding.getBuildingName());
            }
            
            // 比较楼栋类型变更
            if (oldBuilding.getBuildingType() != null && newBuilding.getBuildingType() != null &&
                !oldBuilding.getBuildingType().equals(newBuilding.getBuildingType())) {
                details.append("，楼栋类型已变更");
            }
            
            // 比较地址变更
            if (oldBuilding.getAddress() != null && newBuilding.getAddress() != null &&
                !oldBuilding.getAddress().equals(newBuilding.getAddress())) {
                details.append("，地址已更新");
            }
        }
        
        return details.toString();
    }
}

package org.smartapartment.smartapartment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.smartapartment.smartapartment.entity.SysUser;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface SysUserMapper {
    
    /**
     * 根据用户名查询用户
     */
    SysUser selectByUsername(@Param("username") String username);
    
    /**
     * 插入用户
     */
    int insert(SysUser user);
    
    /**
     * 根据ID查询用户
     */
    SysUser selectById(@Param("id") Long id);
    
    /**
     * 统计用户名数量
     */
    Long countByUsername(@Param("username") String username);
    
    /**
     * 条件查询用户列表
     */
    List<SysUser> selectByCondition(@Param("username") String username, 
                                     @Param("realName") String realName, 
                                     @Param("userType") Integer userType);
    
    /**
     * 更新用户
     */
    int update(SysUser user);
    
    /**
     * 逻辑删除用户
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 更新密码
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    
    /**
     * 更新状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 查询已删除的用户（根据用户名）
     */
    SysUser selectDeletedByUsername(@Param("username") String username);
    
    /**
     * 恢复已删除的用户
     */
    int restoreUser(SysUser user);
    
    /**
     * 选择性更新用户（只更新非null字段）
     */
    int updateSelective(SysUser user);
}

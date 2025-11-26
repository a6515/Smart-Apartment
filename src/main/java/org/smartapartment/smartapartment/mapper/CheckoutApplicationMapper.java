package org.smartapartment.smartapartment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.smartapartment.smartapartment.entity.CheckoutApplication;
import java.util.List;

/**
 * 退宿申请Mapper接口
 */
@Mapper
public interface CheckoutApplicationMapper {
    
    List<CheckoutApplication> selectPage(@Param("studentId") Long studentId,
                                         @Param("status") Integer status);
    
    CheckoutApplication selectById(@Param("id") Long id);
    
    int insert(CheckoutApplication application);
    
    int updateById(CheckoutApplication application);
    
    int deleteById(@Param("id") Long id);
}

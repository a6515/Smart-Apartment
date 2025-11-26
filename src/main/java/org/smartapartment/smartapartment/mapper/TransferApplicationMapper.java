package org.smartapartment.smartapartment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.smartapartment.smartapartment.entity.TransferApplication;
import java.util.List;

/**
 * 换宿申请Mapper接口
 */
@Mapper
public interface TransferApplicationMapper {
    
    List<TransferApplication> selectPage(@Param("studentId") Long studentId,
                                         @Param("status") Integer status);
    
    TransferApplication selectById(@Param("id") Long id);
    
    int insert(TransferApplication application);
    
    int updateById(TransferApplication application);
    
    int deleteById(@Param("id") Long id);
}

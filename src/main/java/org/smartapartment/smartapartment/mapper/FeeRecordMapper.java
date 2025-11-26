package org.smartapartment.smartapartment.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.smartapartment.smartapartment.entity.FeeRecord;
import java.util.List;

/**
 * 费用记录Mapper接口
 */
@Mapper
public interface FeeRecordMapper {
    
    List<FeeRecord> selectByStudentId(@Param("studentId") Long studentId);
    
    FeeRecord selectById(@Param("id") Long id);
    
    int insert(FeeRecord feeRecord);
    
    int updateById(FeeRecord feeRecord);
    
    int deleteById(@Param("id") Long id);
}

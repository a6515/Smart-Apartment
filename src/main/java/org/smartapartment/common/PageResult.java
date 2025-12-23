package org.smartapartment.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 分页结果类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long total;
    private List<T> records;
    private Long current;
    private Long size;
    
    /**
     * 从Page对象创建PageResult
     */
    public static <E> PageResult<E> of(Page<E> page) {
        return PageResult.<E>builder()
                .total(page.getTotal())
                .records(page.getRecords())
                .current((long) page.getCurrent())
                .size((long) page.getSize())
                .build();
    }
    
    /**
     * 创建PageResult的工厂方法
     */
    public static <E> PageResult<E> of(long total, List<E> records, long current, long size) {
        return PageResult.<E>builder()
                .total(total)
                .records(records)
                .current(current)
                .size(size)
                .build();
    }
}

package org.smartapartment.exception;

/**
 * 业务异常类
 */
public class BusinessException extends RuntimeException {
    
    private Integer code = 500;
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public Integer getCode() {
        return code;
    }
}

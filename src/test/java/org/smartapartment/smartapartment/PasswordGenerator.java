package org.smartapartment.smartapartment;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成器 - 用于生成BCrypt加密密码
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成密码
        String password = "admin123";
        String encoded = encoder.encode(password);
        
        System.out.println("原始密码: " + password);
        System.out.println("加密密码: " + encoded);
        System.out.println("\n将上面的加密密码更新到数据库的password字段");
        
        // 验证
        System.out.println("\n验证密码是否匹配: " + encoder.matches(password, encoded));
    }
}

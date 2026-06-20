package com.origin;

import com.origin.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Spring Boot 启动类
 * 农产品电商仓配管理系统
 */
@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class AgriculturalWarehouseManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgriculturalWarehouseManagementApplication.class, args);
    }

}

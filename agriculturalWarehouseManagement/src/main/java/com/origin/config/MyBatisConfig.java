package com.origin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 配置类
 * 扫描 com.origin.mapper 包下的所有 Mapper 接口
 */
@Configuration
@MapperScan("com.origin.mapper")
public class MyBatisConfig {
}

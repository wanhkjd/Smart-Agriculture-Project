package com.origin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置类
 * 配置 CORS 跨域访问，允许前端页面跨域请求 /api/** 接口
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 添加 CORS 跨域映射，开放所有 Origin 和常用 HTTP 方法
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

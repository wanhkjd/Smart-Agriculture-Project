package com.origin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性类
 * 从 application.yaml 中读取 hm.jwt 前缀的配置项
 */
@Data
@Component
@ConfigurationProperties(prefix = "hm.jwt")
public class JwtProperties {
    /** JWT签名密钥（HS256） */
    private String secretKey = "agricultural-warehouse-secret-key-2026";
    /** Token有效期，单位毫秒，默认1小时 */
    private long ttlMillis = 3600000;
}

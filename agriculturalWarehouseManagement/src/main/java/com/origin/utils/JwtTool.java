package com.origin.utils;

import com.origin.config.JwtProperties;
import com.origin.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import com.origin.utils.UserContext.UserInfo;

/**
 * JWT 工具类
 * 提供 HS256 签名算法的 Token 创建与解析功能
 */
@Component
@RequiredArgsConstructor
public class JwtTool {
    private final JwtProperties jwtProperties;

    /**
     * 创建 JWT Token
     * @param userId 用户ID，作为载荷存入 token
     * @param role 用户角色
     * @param realName 用户真实姓名
     * @param ttlMillis 有效期（毫秒）
     * @return 签名的 JWT 字符串
     */
    public String createToken(Long userId, String role, String realName, long ttlMillis) {
        SecretKey key = new SecretKeySpec(
                jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");

        long expMillis = System.currentTimeMillis() + ttlMillis;
        return Jwts.builder()
                .claims(Map.of("userId", userId, "role", role, "realName", realName))
                .expiration(new Date(expMillis))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 JWT Token，提取用户信息和角色
     * @param token JWT 字符串
     * @return 用户信息（ID、角色）
     * @throws UnauthorizedException token 无效、过期或签名错误时抛出
     */
    public UserInfo parseToken(String token) {
        if (token == null) {
            throw new UnauthorizedException("未登录");
        }
        try {
            SecretKey key = new SecretKeySpec(
                    jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256");
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Object userId = claims.get("userId");
            Object role = claims.get("role");
            if (userId == null) {
                throw new UnauthorizedException("无效的token");
            }
            Object realName = claims.get("realName");
            return new UserInfo(Long.valueOf(userId.toString()),
                    role != null ? role.toString() : "仓管员",
                    realName != null ? realName.toString() : "");
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("token已经过期");
        } catch (SignatureException e) {
            throw new UnauthorizedException("无效的token签名");
        } catch (Exception e) {
            throw new UnauthorizedException("无效的token");
        }
    }
}

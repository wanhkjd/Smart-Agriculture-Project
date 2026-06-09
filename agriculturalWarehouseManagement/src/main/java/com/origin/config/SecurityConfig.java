package com.origin.config;

import com.origin.exception.UnauthorizedException;
import com.origin.utils.JwtTool;
import com.origin.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 安全配置类
 * 配置 JWT 登录拦截器，对 /api/** 请求进行身份验证（登录接口等白名单路径除外）
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {
    private final JwtTool jwtTool;

    /**
     * 提供 BCrypt 密码编码器 Bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 注册 JWT 登录拦截器，排除登录接口和静态资源
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor(jwtTool))
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/users/login",
                        "/api/reports/dashboard",
                        "/api/system/logs",
                        "/api/system/config",
                        "/static/**",
                        "/css/**",
                        "/js/**"
                );
    }

    /**
     * JWT 登录拦截器
     * 从 Authorization 请求头提取 Bearer Token，解析用户ID并存入 ThreadLocal
     */
    @RequiredArgsConstructor
    public static class LoginInterceptor implements HandlerInterceptor {
        private final JwtTool jwtTool;

        /**
         * 前置拦截：校验 Authorization 头中的 JWT token，提取用户信息
         * @return true 通过，否则抛出 UnauthorizedException
         */
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                 Object handler) {
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                return true;
            }

            String token = request.getHeader("Authorization");
            if (token == null || token.isEmpty()) {
                throw new UnauthorizedException("未登录，请先登录");
            }
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            var userInfo = jwtTool.parseToken(token);
            UserContext.setUser(userInfo.getUserId(), userInfo.getRole(), userInfo.getRealName());
            return true;
        }

        /**
         * 请求完成后清理 ThreadLocal，防止内存泄漏
         */
        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                    Object handler, Exception ex) {
            UserContext.removeUser();
        }
    }
}

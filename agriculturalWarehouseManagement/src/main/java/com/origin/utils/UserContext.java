package com.origin.utils;

import lombok.Data;

/**
 * 用户上下文（ThreadLocal）
 * 存储当前登录用户的信息，包括用户ID、角色和真实姓名
 */
public class UserContext {
    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    public static void setUser(Long userId, String role, String realName) {
        tl.set(new UserInfo(userId, role, realName));
    }

    public static Long getUser() {
        UserInfo info = tl.get();
        return info != null ? info.getUserId() : null;
    }

    public static String getRole() {
        UserInfo info = tl.get();
        return info != null ? info.getRole() : null;
    }

    public static String getRealName() {
        UserInfo info = tl.get();
        return info != null ? info.getRealName() : null;
    }

    public static void removeUser() {
        tl.remove();
    }

    @Data
    public static class UserInfo {
        private final Long userId;
        private final String role;
        private final String realName;
    }
}

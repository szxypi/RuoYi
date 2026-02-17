package com.zjjh.fdtemp.common.utils.security;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Token 黑名单管理（内存实现）
 * 生产环境建议使用 Redis
 */
@Component
public class TokenBlacklist {
    // 存储: token -> 过期时间
    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();

    /**
     * 将 token 加入黑名单
     */
    public void addToBlacklist(String token, long expirationTime) {
        blacklist.put(token, expirationTime);
        // 清理过期的 token
        cleanupExpired();
    }

    /**
     * 检查 token 是否在黑名单中
     */
    public boolean isBlacklisted(String token) {
        Long expiration = blacklist.get(token);
        if (expiration == null) {
            return false;
        }
        // 如果 token 已过期，从黑名单移除
        if (expiration < System.currentTimeMillis()) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }

    /**
     * 清理过期的 token
     */
    private void cleanupExpired() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> entry.getValue() < now);
    }
}

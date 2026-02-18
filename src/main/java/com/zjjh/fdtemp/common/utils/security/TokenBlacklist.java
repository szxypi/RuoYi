package com.zjjh.fdtemp.common.utils.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HexFormat;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token 黑名单管理
 * 支持内存模式（memory）和数据库持久化模式（db）
 */
@Component
public class TokenBlacklist {
    private static final Logger log = LoggerFactory.getLogger(TokenBlacklist.class);

    /**
     * 内存模式存储：token -> 过期时间
     */
    private final ConcurrentHashMap<String, Long> inMemoryBlacklist = new ConcurrentHashMap<>();

    /**
     * 黑名单存储模式：
     * memory - 单机内存存储
     * db     - 数据库存储（适合多实例部署）
     */
    @Value("${security.token-blacklist.mode:memory}")
    private String blacklistMode = "memory";

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    /**
     * 数据库存储可用开关，避免表缺失时每次请求都报错
     */
    private volatile boolean dbStoreAvailable = true;

    /**
     * 将 token 加入黑名单
     */
    public void addToBlacklist(String token, long expirationTime) {
        if (token == null || token.isEmpty()) {
            return;
        }
        if (isDbModeEnabled()) {
            try {
                String tokenHash = hashToken(token);
                Timestamp expiration = Timestamp.from(Instant.ofEpochMilli(expirationTime));
                int updated = jdbcTemplate.update(
                        "UPDATE sys_token_blacklist SET expiration_time = ? WHERE token_hash = ?",
                        expiration, tokenHash);
                if (updated == 0) {
                    jdbcTemplate.update(
                            "INSERT INTO sys_token_blacklist(token_hash, expiration_time, create_time) VALUES (?, ?, CURRENT_TIMESTAMP)",
                            tokenHash, expiration);
                }
                return;
            } catch (DataAccessException e) {
                disableDbMode(e);
            }
        }
        inMemoryBlacklist.put(token, expirationTime);
    }

    /**
     * 检查 token 是否在黑名单中
     */
    public boolean isBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        if (isDbModeEnabled()) {
            try {
                String tokenHash = hashToken(token);
                Integer count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(1) FROM sys_token_blacklist WHERE token_hash = ? AND expiration_time > CURRENT_TIMESTAMP",
                        Integer.class, tokenHash);
                return count != null && count > 0;
            } catch (DataAccessException e) {
                disableDbMode(e);
            }
        }
        Long expiration = inMemoryBlacklist.get(token);
        if (expiration == null) {
            return false;
        }

        // 如果 token 已过期，从黑名单移除
        if (expiration < System.currentTimeMillis()) {
            inMemoryBlacklist.remove(token);
            return false;
        }
        return true;
    }

    /**
     * 定时清理过期的 token（每分钟执行一次）
     * 替代原来每次请求都清理的方式，减少数据库写压力
     */
    @Scheduled(fixedRate = 60000)
    public void scheduledCleanup() {
        cleanupExpired();
    }

    /**
     * 清理过期的 token
     */
    private void cleanupExpired() {
        if (isDbModeEnabled()) {
            try {
                jdbcTemplate.update("DELETE FROM sys_token_blacklist WHERE expiration_time <= CURRENT_TIMESTAMP");
                return;
            } catch (DataAccessException e) {
                disableDbMode(e);
            }
        }
        long now = System.currentTimeMillis();
        inMemoryBlacklist.entrySet().removeIf(entry -> entry.getValue() < now);
    }

    /**
     * 清除所有黑名单（用于测试）
     */
    public void clear() {
        inMemoryBlacklist.clear();
        if (isDbModeEnabled()) {
            try {
                jdbcTemplate.update("DELETE FROM sys_token_blacklist");
            } catch (DataAccessException e) {
                disableDbMode(e);
            }
        }
    }

    private boolean isDbModeEnabled() {
        return dbStoreAvailable
                && "db".equalsIgnoreCase(blacklistMode)
                && jdbcTemplate != null;
    }

    private void disableDbMode(Exception e) {
        if (dbStoreAvailable) {
            log.error("Token黑名单数据库模式不可用，已降级为内存模式。请检查表 sys_token_blacklist 是否存在。", e);
        }
        dbStoreAvailable = false;
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 不可用", e);
        }
    }
}

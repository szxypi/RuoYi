package com.zjjh.fdtemp.common.utils.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TokenBlacklist 单元测试
 */
@DisplayName("TokenBlacklist 测试")
class TokenBlacklistTest {

    private TokenBlacklist tokenBlacklist;

    @BeforeEach
    void setUp() {
        tokenBlacklist = new TokenBlacklist();
    }

    @Test
    @DisplayName("addToBlacklist - 正常添加token")
    void testAddToBlacklist_Normal() {
        String token = "test-token-123";
        long expiration = System.currentTimeMillis() + 3600000; // 1小时后过期

        tokenBlacklist.addToBlacklist(token, expiration);

        assertTrue(tokenBlacklist.isBlacklisted(token));
    }

    @Test
    @DisplayName("isBlacklisted - 不存在的token返回false")
    void testIsBlacklisted_NotExists() {
        assertFalse(tokenBlacklist.isBlacklisted("non-existent-token"));
    }

    @Test
    @DisplayName("isBlacklisted - 过期token自动移除并返回false")
    void testIsBlacklisted_ExpiredToken() {
        String token = "expired-token";
        long pastExpiration = System.currentTimeMillis() - 1000; // 1秒前过期

        tokenBlacklist.addToBlacklist(token, pastExpiration);

        // 过期token应该被自动移除
        assertFalse(tokenBlacklist.isBlacklisted(token));
    }

    @Test
    @DisplayName("isBlacklisted - 边界情况-刚好过期")
    void testIsBlacklisted_JustExpired() {
        String token = "just-expired-token";
        long expiration = System.currentTimeMillis() - 1;

        tokenBlacklist.addToBlacklist(token, expiration);

        assertFalse(tokenBlacklist.isBlacklisted(token));
    }

    @Test
    @DisplayName("addToBlacklist - 添加多个token")
    void testAddToBlacklist_MultipleTokens() {
        String token1 = "token-1";
        String token2 = "token-2";
        String token3 = "token-3";
        long expiration = System.currentTimeMillis() + 3600000;

        tokenBlacklist.addToBlacklist(token1, expiration);
        tokenBlacklist.addToBlacklist(token2, expiration);
        tokenBlacklist.addToBlacklist(token3, expiration);

        assertTrue(tokenBlacklist.isBlacklisted(token1));
        assertTrue(tokenBlacklist.isBlacklisted(token2));
        assertTrue(tokenBlacklist.isBlacklisted(token3));
    }

    @Test
    @DisplayName("addToBlacklist - 覆盖已存在的token")
    void testAddToBlacklist_Overwrite() {
        String token = "same-token";
        long expiration1 = System.currentTimeMillis() + 1000;
        long expiration2 = System.currentTimeMillis() + 3600000;

        tokenBlacklist.addToBlacklist(token, expiration1);
        tokenBlacklist.addToBlacklist(token, expiration2);

        assertTrue(tokenBlacklist.isBlacklisted(token));
    }

    @Test
    @DisplayName("isBlacklisted - 空token")
    void testIsBlacklisted_EmptyToken() {
        // ConcurrentHashMap不允许null key
        assertFalse(tokenBlacklist.isBlacklisted(""));
    }

    @Test
    @DisplayName("isBlacklisted - null token返回false")
    void testIsBlacklisted_NullToken() {
        assertFalse(tokenBlacklist.isBlacklisted(null));
    }

    @Test
    @DisplayName("addToBlacklist - 长token")
    void testAddToBlacklist_LongToken() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        String longToken = sb.toString();
        long expiration = System.currentTimeMillis() + 3600000;

        tokenBlacklist.addToBlacklist(longToken, expiration);

        assertTrue(tokenBlacklist.isBlacklisted(longToken));
    }

    @Test
    @DisplayName("addToBlacklist - 特殊字符token")
    void testAddToBlacklist_SpecialCharacters() {
        String token = "token-with-special!@#$%^&*()_+{}|:<>?~`-chars";
        long expiration = System.currentTimeMillis() + 3600000;

        tokenBlacklist.addToBlacklist(token, expiration);

        assertTrue(tokenBlacklist.isBlacklisted(token));
    }

    @Test
    @DisplayName("并发测试 - 多线程添加和检查")
    void testConcurrentAccess() throws InterruptedException {
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        long expiration = System.currentTimeMillis() + 3600000;

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                String token = "concurrent-token-" + index;
                tokenBlacklist.addToBlacklist(token, expiration);
                assertTrue(tokenBlacklist.isBlacklisted(token));
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // 验证所有token都在黑名单中
        for (int i = 0; i < threadCount; i++) {
            assertTrue(tokenBlacklist.isBlacklisted("concurrent-token-" + i));
        }
    }
}

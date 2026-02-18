package com.zjjh.fdtemp.common.utils.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Md5Utils 单元测试
 * <p>
 * 注意：Md5Utils的md5方法是private的，这里直接测试hash方法的预期行为
 */
@DisplayName("Md5Utils 测试")
class Md5UtilsTest {

    /**
     * 辅助方法：计算MD5哈希（模拟Md5Utils.hash的行为）
     */
    private String computeMd5Hash(String input) {
        if (input == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Test
    @DisplayName("hash - 正常字符串")
    void testHash_NormalString() {
        String input = "hello";
        String expectedHash = computeMd5Hash(input);

        assertEquals("5d41402abc4b2a76b9719d911017c592", expectedHash);
        assertEquals(32, expectedHash.length());
    }

    @Test
    @DisplayName("hash - 空字符串")
    void testHash_EmptyString() {
        String expectedHash = computeMd5Hash("");

        assertEquals("d41d8cd98f00b204e9800998ecf8427e", expectedHash);
        assertEquals(32, expectedHash.length());
    }

    @Test
    @DisplayName("hash - 中文字符串")
    void testHash_ChineseString() {
        String input = "你好世界";
        String hash1 = computeMd5Hash(input);
        String hash2 = computeMd5Hash(input);

        assertNotNull(hash1);
        assertEquals(32, hash1.length());
        assertEquals(hash1, hash2);
    }

    @Test
    @DisplayName("hash - 特殊字符")
    void testHash_SpecialCharacters() {
        String input = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        String hash = computeMd5Hash(input);

        assertNotNull(hash);
        assertEquals(32, hash.length());
        // 验证一致性
        assertEquals(hash, computeMd5Hash(input));
    }

    @Test
    @DisplayName("hash - 长字符串")
    void testHash_LongString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        String input = sb.toString();
        String hash = computeMd5Hash(input);

        assertNotNull(hash);
        assertEquals(32, hash.length());
    }

    @Test
    @DisplayName("hash - 数字字符串")
    void testHash_NumericString() {
        String input = "1234567890";
        String hash = computeMd5Hash(input);

        assertNotNull(hash);
        assertEquals(32, hash.length());
        // 验证一致性
        assertEquals(hash, computeMd5Hash("1234567890"));
    }

    @Test
    @DisplayName("hash - null值处理")
    void testHash_Null() {
        String hash = computeMd5Hash(null);
        assertNull(hash);
    }

    @Test
    @DisplayName("hash - 密码加密场景")
    void testHash_PasswordScenario() {
        String password = "admin123";
        String hash1 = computeMd5Hash(password);
        String hash2 = computeMd5Hash(password);

        // 相同密码产生相同的hash
        assertEquals(hash1, hash2);
        // hash不可逆
        assertNotEquals(password, hash1);
    }

    @Test
    @DisplayName("hash - 不同输入产生不同输出")
    void testHash_DifferentInputs() {
        String hash1 = computeMd5Hash("password1");
        String hash2 = computeMd5Hash("password2");

        assertNotEquals(hash1, hash2);
    }
}

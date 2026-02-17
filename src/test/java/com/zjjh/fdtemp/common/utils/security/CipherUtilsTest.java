package com.zjjh.fdtemp.common.utils.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CipherUtils 单元测试
 */
@DisplayName("CipherUtils 测试")
class CipherUtilsTest {

    @Test
    @DisplayName("generateNewKey - AES 128位")
    void testGenerateNewKey_Aes128() {
        Key key = CipherUtils.generateNewKey(128, "AES");

        assertNotNull(key);
        assertEquals("AES", key.getAlgorithm());
        assertEquals(16, key.getEncoded().length); // 128 bits = 16 bytes
    }

    @Test
    @DisplayName("generateNewKey - AES 256位")
    void testGenerateNewKey_Aes256() {
        Key key = CipherUtils.generateNewKey(256, "AES");

        assertNotNull(key);
        assertEquals("AES", key.getAlgorithm());
        assertEquals(32, key.getEncoded().length); // 256 bits = 32 bytes
    }

    @Test
    @DisplayName("generateNewKey - DES 56位")
    void testGenerateNewKey_Des56() {
        Key key = CipherUtils.generateNewKey(56, "DES");

        assertNotNull(key);
        assertEquals("DES", key.getAlgorithm());
        assertEquals(8, key.getEncoded().length); // DES uses 64-bit keys (56 effective)
    }

    @Test
    @DisplayName("generateNewKey - HmacSHA256")
    void testGenerateNewKey_HmacSha256() {
        Key key = CipherUtils.generateNewKey(256, "HmacSHA256");

        assertNotNull(key);
        assertEquals("HmacSHA256", key.getAlgorithm());
        assertEquals(32, key.getEncoded().length);
    }

    @Test
    @DisplayName("generateNewKey - 每次生成不同的密钥")
    void testGenerateNewKey_DifferentKeys() {
        Key key1 = CipherUtils.generateNewKey(128, "AES");
        Key key2 = CipherUtils.generateNewKey(128, "AES");

        assertNotNull(key1);
        assertNotNull(key2);
        // 两次生成的密钥应该不同
        assertNotEquals(key1, key2);
        assertFalse(java.util.Arrays.equals(key1.getEncoded(), key2.getEncoded()));
    }

    @Test
    @DisplayName("generateNewKey - 无效算法抛出异常")
    void testGenerateNewKey_InvalidAlgorithm() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            CipherUtils.generateNewKey(128, "InvalidAlgorithm");
        });

        assertTrue(exception.getMessage().contains("Unable to acquire"));
        assertTrue(exception.getCause() instanceof NoSuchAlgorithmException);
    }

    @Test
    @DisplayName("generateNewKey - 边界值测试-最小有效密钥长度")
    void testGenerateNewKey_MinimumKeySize() {
        // AES 最小支持128位
        Key key = CipherUtils.generateNewKey(128, "AES");
        assertNotNull(key);
        assertEquals(16, key.getEncoded().length);
    }

    @Test
    @DisplayName("generateNewKey - HmacMD5")
    void testGenerateNewKey_HmacMd5() {
        Key key = CipherUtils.generateNewKey(128, "HmacMD5");

        assertNotNull(key);
        assertEquals("HmacMD5", key.getAlgorithm());
        assertEquals(16, key.getEncoded().length);
    }

    @Test
    @DisplayName("generateNewKey - DESede (Triple DES)")
    void testGenerateNewKey_DESede() {
        Key key = CipherUtils.generateNewKey(168, "DESede");

        assertNotNull(key);
        assertEquals("DESede", key.getAlgorithm());
        assertEquals(24, key.getEncoded().length); // 168 bits = 24 bytes (with parity)
    }
}

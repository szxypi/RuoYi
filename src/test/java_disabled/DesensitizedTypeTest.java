package com.zjjh.fdtemp.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * DesensitizedType 脱敏类型枚举测试
 */
@DisplayName("DesensitizedType 脱敏类型枚举测试")
class DesensitizedTypeTest {

    @Nested
    @DisplayName("枚举值测试")
    class EnumValuesTest {
        @Test
        @DisplayName("所有枚举值存在")
        void values_ShouldContainAllTypes() {
            DesensitizedType[] values = DesensitizedType.values();
            assertEquals(7, values.length);

            assertNotNull(DesensitizedType.USERNAME);
            assertNotNull(DesensitizedType.PASSWORD);
            assertNotNull(DesensitizedType.ID_CARD);
            assertNotNull(DesensitizedType.PHONE);
            assertNotNull(DesensitizedType.EMAIL);
            assertNotNull(DesensitizedType.BANK_CARD);
            assertNotNull(DesensitizedType.CAR_LICENSE);
        }
    }

    @Nested
    @DisplayName("desensitizer 方法测试")
    class DesensitizerTest {
        @Test
        @DisplayName("用户名脱敏")
        void desensitizer_WhenUsername_ShouldDesensitize() {
            String result = DesensitizedType.USERNAME.desensitizer().apply("张三");
            assertTrue(result.contains("*"));
        }

        @Test
        @DisplayName("密码脱敏")
        void desensitizer_WhenPassword_ShouldDesensitize() {
            String result = DesensitizedType.PASSWORD.desensitizer().apply("123456");
            assertEquals("******", result);
        }

        @Test
        @DisplayName("身份证脱敏")
        void desensitizer_WhenIdCard_ShouldDesensitize() {
            String result = DesensitizedType.ID_CARD.desensitizer().apply("110101199001011234");
            assertTrue(result.contains("*"));
        }

        @Test
        @DisplayName("手机号脱敏")
        void desensitizer_WhenPhone_ShouldDesensitize() {
            String result = DesensitizedType.PHONE.desensitizer().apply("13812345678");
            assertTrue(result.contains("****"));
            assertTrue(result.startsWith("138"));
            assertTrue(result.endsWith("5678"));
        }

        @Test
        @DisplayName("邮箱脱敏")
        void desensitizer_WhenEmail_ShouldDesensitize() {
            String result = DesensitizedType.EMAIL.desensitizer().apply("test@example.com");
            assertTrue(result.contains("*"));
            assertTrue(result.contains("@"));
        }

        @Test
        @DisplayName("银行卡脱敏")
        void desensitizer_WhenBankCard_ShouldDesensitize() {
            String result = DesensitizedType.BANK_CARD.desensitizer().apply("6222021234567890123");
            assertTrue(result.contains("*"));
        }

        @Test
        @DisplayName("车牌号脱敏-普通车牌")
        void desensitizer_WhenCarLicenseNormal_ShouldDesensitize() {
            String result = DesensitizedType.CAR_LICENSE.desensitizer().apply("京A12345");
            assertTrue(result.contains("*"));
        }

        @Test
        @DisplayName("车牌号脱敏-新能源车牌")
        void desensitizer_WhenCarLicenseNewEnergy_ShouldDesensitize() {
            String result = DesensitizedType.CAR_LICENSE.desensitizer().apply("京AD12345");
            assertTrue(result.contains("*"));
        }
    }

    @Nested
    @DisplayName("valueOf 测试")
    class ValueOfTest {
        @Test
        @DisplayName("valueOf正确解析")
        void valueOf_ShouldReturnCorrectEnum() {
            assertEquals(DesensitizedType.USERNAME, DesensitizedType.valueOf("USERNAME"));
            assertEquals(DesensitizedType.PASSWORD, DesensitizedType.valueOf("PASSWORD"));
            assertEquals(DesensitizedType.ID_CARD, DesensitizedType.valueOf("ID_CARD"));
        }

        @Test
        @DisplayName("无效枚举值抛出异常")
        void valueOf_WhenInvalid_ShouldThrowException() {
            assertThrows(IllegalArgumentException.class, () -> DesensitizedType.valueOf("INVALID"));
        }
    }
}

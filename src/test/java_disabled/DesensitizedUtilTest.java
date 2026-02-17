package com.zjjh.fdtemp.common.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * DesensitizedUtil 脱敏工具类单元测试
 */
@DisplayName("DesensitizedUtil 脱敏工具类测试")
class DesensitizedUtilTest {

    @Nested
    @DisplayName("password 密码脱敏测试")
    class PasswordTest {
        @Test
        @DisplayName("正常密码脱敏")
        void password_WhenNormalPassword_ShouldReturnAsterisks() {
            String result = DesensitizedUtil.password("123456");
            assertEquals("******", result);
        }

        @Test
        @DisplayName("空密码返回空字符串")
        void password_WhenEmpty_ShouldReturnEmpty() {
            assertEquals("", DesensitizedUtil.password(""));
        }

        @Test
        @DisplayName("null密码返回空字符串")
        void password_WhenNull_ShouldReturnEmpty() {
            assertEquals("", DesensitizedUtil.password(null));
        }

        @Test
        @DisplayName("空白密码返回空字符串")
        void password_WhenBlank_ShouldReturnEmpty() {
            assertEquals("", DesensitizedUtil.password("   "));
        }

        @Test
        @DisplayName("长密码脱敏")
        void password_WhenLongPassword_ShouldReturnAsterisks() {
            String result = DesensitizedUtil.password("abcdefghijklmnopqrstuvwxyz");
            assertEquals("**************************", result);
        }
    }

    @Nested
    @DisplayName("carLicense 车牌脱敏测试")
    class CarLicenseTest {
        @Test
        @DisplayName("普通车牌(7位)脱敏")
        void carLicense_WhenNormalLicense_ShouldReturnDesensitized() {
            String result = DesensitizedUtil.carLicense("京A12345");
            assertEquals("京A1***5", result);
        }

        @Test
        @DisplayName("新能源车牌(8位)脱敏")
        void carLicense_WhenNewEnergyLicense_ShouldReturnDesensitized() {
            String result = DesensitizedUtil.carLicense("京AD12345");
            assertEquals("京AD****5", result);
        }

        @Test
        @DisplayName("空车牌返回空字符串")
        void carLicense_WhenEmpty_ShouldReturnEmpty() {
            assertEquals("", DesensitizedUtil.carLicense(""));
        }

        @Test
        @DisplayName("null车牌返回空字符串")
        void carLicense_WhenNull_ShouldReturnEmpty() {
            assertEquals("", DesensitizedUtil.carLicense(null));
        }

        @Test
        @DisplayName("非标准长度车牌原样返回")
        void carLicense_WhenInvalidLength_ShouldReturnOriginal() {
            String result = DesensitizedUtil.carLicense("京A123");
            assertEquals("京A123", result);
        }

        @Test
        @DisplayName("空白车牌返回空字符串")
        void carLicense_WhenBlank_ShouldReturnEmpty() {
            assertEquals("", DesensitizedUtil.carLicense("   "));
        }
    }
}

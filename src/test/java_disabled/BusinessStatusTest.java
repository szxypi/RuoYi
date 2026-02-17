package com.zjjh.fdtemp.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * BusinessStatus 操作状态枚举测试
 */
@DisplayName("BusinessStatus 操作状态枚举测试")
class BusinessStatusTest {

    @Test
    @DisplayName("枚举值存在")
    void values_ShouldContainAllTypes() {
        BusinessStatus[] values = BusinessStatus.values();
        assertEquals(2, values.length);

        assertNotNull(BusinessStatus.SUCCESS);
        assertNotNull(BusinessStatus.FAIL);
    }

    @Test
    @DisplayName("valueOf正确解析")
    void valueOf_ShouldReturnCorrectEnum() {
        assertEquals(BusinessStatus.SUCCESS, BusinessStatus.valueOf("SUCCESS"));
        assertEquals(BusinessStatus.FAIL, BusinessStatus.valueOf("FAIL"));
    }

    @Test
    @DisplayName("无效枚举值抛出异常")
    void valueOf_WhenInvalid_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> BusinessStatus.valueOf("INVALID"));
    }

    @Test
    @DisplayName("枚举ordinal正确")
    void ordinal_ShouldBeCorrect() {
        assertEquals(0, BusinessStatus.SUCCESS.ordinal());
        assertEquals(1, BusinessStatus.FAIL.ordinal());
    }
}

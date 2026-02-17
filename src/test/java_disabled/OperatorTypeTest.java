package com.zjjh.fdtemp.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * OperatorType 操作人类别枚举测试
 */
@DisplayName("OperatorType 操作人类别枚举测试")
class OperatorTypeTest {

    @Test
    @DisplayName("枚举值存在")
    void values_ShouldContainAllTypes() {
        OperatorType[] values = OperatorType.values();
        assertEquals(3, values.length);

        assertNotNull(OperatorType.OTHER);
        assertNotNull(OperatorType.MANAGE);
        assertNotNull(OperatorType.MOBILE);
    }

    @Test
    @DisplayName("valueOf正确解析")
    void valueOf_ShouldReturnCorrectEnum() {
        assertEquals(OperatorType.OTHER, OperatorType.valueOf("OTHER"));
        assertEquals(OperatorType.MANAGE, OperatorType.valueOf("MANAGE"));
        assertEquals(OperatorType.MOBILE, OperatorType.valueOf("MOBILE"));
    }

    @Test
    @DisplayName("无效枚举值抛出异常")
    void valueOf_WhenInvalid_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> OperatorType.valueOf("INVALID"));
    }

    @Test
    @DisplayName("枚举ordinal正确")
    void ordinal_ShouldBeCorrect() {
        assertEquals(0, OperatorType.OTHER.ordinal());
        assertEquals(1, OperatorType.MANAGE.ordinal());
        assertEquals(2, OperatorType.MOBILE.ordinal());
    }
}

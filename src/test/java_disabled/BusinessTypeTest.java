package com.zjjh.fdtemp.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * BusinessType 业务操作类型枚举测试
 */
@DisplayName("BusinessType 业务操作类型枚举测试")
class BusinessTypeTest {

    @Test
    @DisplayName("枚举值存在")
    void values_ShouldContainAllTypes() {
        BusinessType[] values = BusinessType.values();
        assertTrue(values.length > 0);

        // 验证所有预期的枚举值存在
        assertNotNull(BusinessType.OTHER);
        assertNotNull(BusinessType.INSERT);
        assertNotNull(BusinessType.UPDATE);
        assertNotNull(BusinessType.DELETE);
        assertNotNull(BusinessType.GRANT);
        assertNotNull(BusinessType.EXPORT);
        assertNotNull(BusinessType.IMPORT);
        assertNotNull(BusinessType.FORCE);
        assertNotNull(BusinessType.GENCODE);
        assertNotNull(BusinessType.CLEAN);
    }

    @Test
    @DisplayName("valueOf正确解析")
    void valueOf_ShouldReturnCorrectEnum() {
        assertEquals(BusinessType.OTHER, BusinessType.valueOf("OTHER"));
        assertEquals(BusinessType.INSERT, BusinessType.valueOf("INSERT"));
        assertEquals(BusinessType.UPDATE, BusinessType.valueOf("UPDATE"));
        assertEquals(BusinessType.DELETE, BusinessType.valueOf("DELETE"));
        assertEquals(BusinessType.GRANT, BusinessType.valueOf("GRANT"));
        assertEquals(BusinessType.EXPORT, BusinessType.valueOf("EXPORT"));
        assertEquals(BusinessType.IMPORT, BusinessType.valueOf("IMPORT"));
        assertEquals(BusinessType.FORCE, BusinessType.valueOf("FORCE"));
        assertEquals(BusinessType.GENCODE, BusinessType.valueOf("GENCODE"));
        assertEquals(BusinessType.CLEAN, BusinessType.valueOf("CLEAN"));
    }

    @Test
    @DisplayName("无效枚举值抛出异常")
    void valueOf_WhenInvalid_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> BusinessType.valueOf("INVALID"));
    }

    @Test
    @DisplayName("枚举名称正确")
    void name_ShouldReturnCorrectName() {
        assertEquals("OTHER", BusinessType.OTHER.name());
        assertEquals("INSERT", BusinessType.INSERT.name());
        assertEquals("UPDATE", BusinessType.UPDATE.name());
        assertEquals("DELETE", BusinessType.DELETE.name());
    }

    @Test
    @DisplayName("枚举ordinal正确")
    void ordinal_ShouldBeCorrect() {
        assertEquals(0, BusinessType.OTHER.ordinal());
        assertEquals(1, BusinessType.INSERT.ordinal());
        assertEquals(2, BusinessType.UPDATE.ordinal());
        assertEquals(3, BusinessType.DELETE.ordinal());
    }
}

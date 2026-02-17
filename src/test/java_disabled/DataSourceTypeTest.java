package com.zjjh.fdtemp.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * DataSourceType 数据源枚举测试
 */
@DisplayName("DataSourceType 数据源枚举测试")
class DataSourceTypeTest {

    @Test
    @DisplayName("枚举值存在")
    void values_ShouldContainAllTypes() {
        DataSourceType[] values = DataSourceType.values();
        assertEquals(2, values.length);

        assertNotNull(DataSourceType.MASTER);
        assertNotNull(DataSourceType.SLAVE);
    }

    @Test
    @DisplayName("valueOf正确解析")
    void valueOf_ShouldReturnCorrectEnum() {
        assertEquals(DataSourceType.MASTER, DataSourceType.valueOf("MASTER"));
        assertEquals(DataSourceType.SLAVE, DataSourceType.valueOf("SLAVE"));
    }

    @Test
    @DisplayName("无效枚举值抛出异常")
    void valueOf_WhenInvalid_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> DataSourceType.valueOf("INVALID"));
    }

    @Test
    @DisplayName("枚举ordinal正确")
    void ordinal_ShouldBeCorrect() {
        assertEquals(0, DataSourceType.MASTER.ordinal());
        assertEquals(1, DataSourceType.SLAVE.ordinal());
    }
}

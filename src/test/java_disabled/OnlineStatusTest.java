package com.zjjh.fdtemp.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * OnlineStatus 用户会话状态枚举测试
 */
@DisplayName("OnlineStatus 用户会话状态枚举测试")
class OnlineStatusTest {

    @Nested
    @DisplayName("枚举值测试")
    class EnumValuesTest {
        @Test
        @DisplayName("枚举值存在")
        void values_ShouldContainAllTypes() {
            OnlineStatus[] values = OnlineStatus.values();
            assertEquals(2, values.length);

            assertNotNull(OnlineStatus.on_line);
            assertNotNull(OnlineStatus.off_line);
        }

        @Test
        @DisplayName("valueOf正确解析")
        void valueOf_ShouldReturnCorrectEnum() {
            assertEquals(OnlineStatus.on_line, OnlineStatus.valueOf("on_line"));
            assertEquals(OnlineStatus.off_line, OnlineStatus.valueOf("off_line"));
        }
    }

    @Nested
    @DisplayName("getInfo 方法测试")
    class GetInfoTest {
        @Test
        @DisplayName("在线状态信息")
        void getInfo_WhenOnline_ShouldReturnOnline() {
            assertEquals("在线", OnlineStatus.on_line.getInfo());
        }

        @Test
        @DisplayName("离线状态信息")
        void getInfo_WhenOffline_ShouldReturnOffline() {
            assertEquals("离线", OnlineStatus.off_line.getInfo());
        }
    }
}

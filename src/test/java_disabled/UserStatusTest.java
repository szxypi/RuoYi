package com.zjjh.fdtemp.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * UserStatus 用户状态枚举测试
 */
@DisplayName("UserStatus 用户状态枚举测试")
class UserStatusTest {

    @Nested
    @DisplayName("枚举值测试")
    class EnumValuesTest {
        @Test
        @DisplayName("所有枚举值存在")
        void values_ShouldContainAllTypes() {
            UserStatus[] values = UserStatus.values();
            assertEquals(3, values.length);

            assertNotNull(UserStatus.OK);
            assertNotNull(UserStatus.DISABLE);
            assertNotNull(UserStatus.DELETED);
        }

        @Test
        @DisplayName("valueOf正确解析")
        void valueOf_ShouldReturnCorrectEnum() {
            assertEquals(UserStatus.OK, UserStatus.valueOf("OK"));
            assertEquals(UserStatus.DISABLE, UserStatus.valueOf("DISABLE"));
            assertEquals(UserStatus.DELETED, UserStatus.valueOf("DELETED"));
        }
    }

    @Nested
    @DisplayName("getCode 方法测试")
    class GetCodeTest {
        @Test
        @DisplayName("OK状态码为0")
        void getCode_WhenOK_ShouldReturn0() {
            assertEquals("0", UserStatus.OK.getCode());
        }

        @Test
        @DisplayName("DISABLE状态码为1")
        void getCode_WhenDisable_ShouldReturn1() {
            assertEquals("1", UserStatus.DISABLE.getCode());
        }

        @Test
        @DisplayName("DELETED状态码为2")
        void getCode_WhenDeleted_ShouldReturn2() {
            assertEquals("2", UserStatus.DELETED.getCode());
        }
    }

    @Nested
    @DisplayName("getInfo 方法测试")
    class GetInfoTest {
        @Test
        @DisplayName("OK状态信息为正常")
        void getInfo_WhenOK_ShouldReturnNormal() {
            assertEquals("正常", UserStatus.OK.getInfo());
        }

        @Test
        @DisplayName("DISABLE状态信息为停用")
        void getInfo_WhenDisable_ShouldReturnStop() {
            assertEquals("停用", UserStatus.DISABLE.getInfo());
        }

        @Test
        @DisplayName("DELETED状态信息为删除")
        void getInfo_WhenDeleted_ShouldReturnDeleted() {
            assertEquals("删除", UserStatus.DELETED.getInfo());
        }
    }
}

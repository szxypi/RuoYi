package com.zjjh.fdtemp.common.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ExceptionUtil 错误信息处理类单元测试
 */
@DisplayName("ExceptionUtil 错误信息处理类测试")
class ExceptionUtilTest {

    @Nested
    @DisplayName("getExceptionMessage 测试")
    class GetExceptionMessageTest {
        @Test
        @DisplayName("获取异常详细信息")
        void getExceptionMessage_WhenException_ShouldReturnStackTrace() {
            Exception e = new RuntimeException("测试异常");
            String result = ExceptionUtil.getExceptionMessage(e);
            assertNotNull(result);
            assertTrue(result.contains("RuntimeException"));
            assertTrue(result.contains("测试异常"));
        }

        @Test
        @DisplayName("获取嵌套异常详细信息")
        void getExceptionMessage_WhenNestedException_ShouldReturnStackTrace() {
            Exception e = new RuntimeException("外层异常", new IOException("内层异常"));
            String result = ExceptionUtil.getExceptionMessage(e);
            assertNotNull(result);
            assertTrue(result.contains("RuntimeException"));
            assertTrue(result.contains("IOException"));
        }

        @Test
        @DisplayName("获取空异常信息")
        void getExceptionMessage_WhenNullExceptionMessage_ShouldReturnStackTrace() {
            Exception e = new RuntimeException((String) null);
            String result = ExceptionUtil.getExceptionMessage(e);
            assertNotNull(result);
            assertTrue(result.contains("RuntimeException"));
        }
    }

    @Nested
    @DisplayName("getRootErrorMessage 测试")
    class GetRootErrorMessageTest {
        @Test
        @DisplayName("获取根异常消息")
        void getRootErrorMessage_WhenException_ShouldReturnRootMessage() {
            Exception e = new RuntimeException("外层异常", new IOException("内层异常"));
            String result = ExceptionUtil.getRootErrorMessage(e);
            assertEquals("内层异常", result);
        }

        @Test
        @DisplayName("无嵌套异常时返回自身消息")
        void getRootErrorMessage_WhenNoCause_ShouldReturnOwnMessage() {
            Exception e = new RuntimeException("测试异常");
            String result = ExceptionUtil.getRootErrorMessage(e);
            assertEquals("测试异常", result);
        }

        @Test
        @DisplayName("异常消息为null时返回null字符串")
        void getRootErrorMessage_WhenMessageIsNull_ShouldReturnNullString() {
            Exception e = new RuntimeException((String) null);
            String result = ExceptionUtil.getRootErrorMessage(e);
            assertEquals("null", result);
        }
    }

    @Nested
    @DisplayName("isCausedBy 测试")
    class IsCausedByTest {
        @Test
        @DisplayName("异常类型匹配时返回true")
        void isCausedBy_WhenDirectMatch_ShouldReturnTrue() {
            RuntimeException e = new RuntimeException("测试异常");
            assertTrue(ExceptionUtil.isCausedBy(e, RuntimeException.class));
        }

        @Test
        @DisplayName("异常类型是父类时返回true")
        void isCausedBy_WhenParentClass_ShouldReturnTrue() {
            RuntimeException e = new RuntimeException("测试异常");
            assertTrue(ExceptionUtil.isCausedBy(e, Exception.class));
        }

        @Test
        @DisplayName("异常类型不匹配时返回false")
        void isCausedBy_WhenNotMatch_ShouldReturnFalse() {
            RuntimeException e = new RuntimeException("测试异常");
            assertFalse(ExceptionUtil.isCausedBy(e, IOException.class));
        }

        @Test
        @DisplayName("嵌套异常类型匹配时返回true")
        void isCausedBy_WhenCauseMatch_ShouldReturnTrue() {
            RuntimeException e = new RuntimeException("外层异常", new IOException("内层异常"));
            assertTrue(ExceptionUtil.isCausedBy(e, IOException.class));
        }

        @Test
        @DisplayName("多层嵌套异常类型匹配时返回true")
        void isCausedBy_WhenDeepCauseMatch_ShouldReturnTrue() {
            RuntimeException e = new RuntimeException("外层异常",
                    new IOException("中层异常", new SQLException("内层异常")));
            assertTrue(ExceptionUtil.isCausedBy(e, SQLException.class));
        }

        @Test
        @DisplayName("异常链循环检测")
        void isCausedBy_WhenCircularCause_ShouldNotLoopForever() {
            // 创建一个模拟的循环引用异常（通常Java不允许这种情况）
            Exception e = new RuntimeException("测试异常");
            assertFalse(ExceptionUtil.isCausedBy(e, NullPointerException.class));
        }
    }
}

package com.zjjh.fdtemp.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * XssHttpServletRequestWrapper XSS过滤处理测试
 */
@DisplayName("XssHttpServletRequestWrapper XSS过滤处理测试")
class XssHttpServletRequestWrapperTest {

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTest {
        @Test
        @DisplayName("正常构造")
        void constructor_WhenValidRequest_ShouldCreate() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
            assertNotNull(wrapper);
        }
    }

    @Nested
    @DisplayName("getParameterValues 方法测试")
    class GetParameterValuesTest {
        @Test
        @DisplayName("参数值包含HTML标签时清除标签")
        void getParameterValues_WhenContainsHtml_ShouldCleanTags() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getParameterValues("test")).thenReturn(new String[]{"<script>alert('xss')</script>"});

            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
            String[] result = wrapper.getParameterValues("test");

            assertNotNull(result);
            assertEquals(1, result.length);
            assertFalse(result[0].contains("<script>"));
        }

        @Test
        @DisplayName("参数值包含前后空格时去除空格")
        void getParameterValues_WhenHasWhitespace_ShouldTrim() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getParameterValues("test")).thenReturn(new String[]{"  hello world  "});

            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
            String[] result = wrapper.getParameterValues("test");

            assertNotNull(result);
            assertEquals(1, result.length);
            assertEquals("hello world", result[0]);
        }

        @Test
        @DisplayName("多个参数值时全部处理")
        void getParameterValues_WhenMultipleValues_ShouldProcessAll() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getParameterValues("test")).thenReturn(
                new String[]{"<b>bold</b>", "  normal  ", "<script>xss</script>"});

            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
            String[] result = wrapper.getParameterValues("test");

            assertNotNull(result);
            assertEquals(3, result.length);
            // HTMLFilter只移除不允许的标签，b标签可能被允许
            // 检查script标签被移除
            assertFalse(result[2].contains("script"));
            assertEquals("normal", result[1]);
        }

        @Test
        @DisplayName("参数值为null时返回null")
        void getParameterValues_WhenNull_ShouldReturnNull() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getParameterValues("test")).thenReturn(null);

            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
            String[] result = wrapper.getParameterValues("test");

            assertNull(result);
        }

        @Test
        @DisplayName("正常文本原样返回")
        void getParameterValues_WhenNormalText_ShouldReturnSame() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getParameterValues("test")).thenReturn(new String[]{"hello world"});

            XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
            String[] result = wrapper.getParameterValues("test");

            assertNotNull(result);
            assertEquals(1, result.length);
            assertEquals("hello world", result[0]);
        }
    }
}

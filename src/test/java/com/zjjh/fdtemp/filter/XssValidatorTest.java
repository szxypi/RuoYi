package com.zjjh.fdtemp.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * XssValidator XSS校验器测试
 */
@DisplayName("XssValidator XSS校验器测试")
class XssValidatorTest {

    @Nested
    @DisplayName("containsHtml 方法测试")
    class ContainsHtmlTest {
        @Test
        @DisplayName("包含HTML标签返回true")
        void containsHtml_WhenHtmlTag_ShouldReturnTrue() {
            assertTrue(XssValidator.containsHtml("<script>alert('xss')</script>"));
        }

        @Test
        @DisplayName("包含简单HTML标签返回true")
        void containsHtml_WhenSimpleTag_ShouldReturnTrue() {
            assertTrue(XssValidator.containsHtml("<b>text</b>"));
        }

        @Test
        @DisplayName("包含自闭合标签返回true")
        void containsHtml_WhenSelfClosingTag_ShouldReturnTrue() {
            assertTrue(XssValidator.containsHtml("<img src='x' />"));
        }

        @Test
        @DisplayName("纯文本返回false")
        void containsHtml_WhenPlainText_ShouldReturnFalse() {
            assertFalse(XssValidator.containsHtml("plain text"));
        }

        @Test
        @DisplayName("空字符串返回false")
        void containsHtml_WhenEmpty_ShouldReturnFalse() {
            assertFalse(XssValidator.containsHtml(""));
        }

        @Test
        @DisplayName("特殊字符但非HTML返回false")
        void containsHtml_WhenSpecialCharsButNotHtml_ShouldReturnFalse() {
            assertFalse(XssValidator.containsHtml("test < not a tag"));
        }

        @Test
        @DisplayName("嵌套HTML标签")
        void containsHtml_WhenNestedTags_ShouldReturnTrue() {
            assertTrue(XssValidator.containsHtml("<div><span>nested</span></div>"));
        }
    }

    @Nested
    @DisplayName("isValid 方法测试")
    class IsValidTest {
        @Test
        @DisplayName("null值返回true")
        void isValid_WhenNull_ShouldReturnTrue() {
            XssValidator validator = new XssValidator();
            assertTrue(validator.isValid(null, null));
        }

        @Test
        @DisplayName("空字符串返回true")
        void isValid_WhenEmpty_ShouldReturnTrue() {
            XssValidator validator = new XssValidator();
            assertTrue(validator.isValid("", null));
        }

        @Test
        @DisplayName("空白字符串返回true")
        void isValid_WhenBlank_ShouldReturnTrue() {
            XssValidator validator = new XssValidator();
            assertTrue(validator.isValid("   ", null));
        }

        @Test
        @DisplayName("纯文本返回true")
        void isValid_WhenPlainText_ShouldReturnTrue() {
            XssValidator validator = new XssValidator();
            assertTrue(validator.isValid("Hello World", null));
        }

        @Test
        @DisplayName("包含HTML返回false")
        void isValid_WhenContainsHtml_ShouldReturnFalse() {
            XssValidator validator = new XssValidator();
            assertFalse(validator.isValid("<script>alert('xss')</script>", null));
        }
    }
}

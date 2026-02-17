package com.zjjh.fdtemp.common.core.text;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * StrFormatter 字符串格式化工具类测试
 */
@DisplayName("StrFormatter 字符串格式化工具类测试")
class StrFormatterTest {

    @Nested
    @DisplayName("format 方法测试")
    class FormatTest {
        @Test
        @DisplayName("正常格式化")
        void format_WhenNormalCase_ShouldFormatCorrectly() {
            String result = StrFormatter.format("this is {} for {}", "a", "b");
            assertEquals("this is a for b", result);
        }

        @Test
        @DisplayName("单个占位符")
        void format_WhenSinglePlaceholder_ShouldFormatCorrectly() {
            String result = StrFormatter.format("Hello {}", "World");
            assertEquals("Hello World", result);
        }

        @Test
        @DisplayName("多个占位符")
        void format_WhenMultiplePlaceholders_ShouldFormatCorrectly() {
            String result = StrFormatter.format("{} {} {}", "a", "b", "c");
            assertEquals("a b c", result);
        }

        @Test
        @DisplayName("模板为null返回null")
        void format_WhenTemplateIsNull_ShouldReturnNull() {
            assertNull(StrFormatter.format(null, "a", "b"));
        }

        @Test
        @DisplayName("模板为空字符串返回空字符串")
        void format_WhenTemplateIsEmpty_ShouldReturnEmpty() {
            assertEquals("", StrFormatter.format("", "a", "b"));
        }

        @Test
        @DisplayName("参数数组为null返回原模板")
        void format_WhenParamsIsNull_ShouldReturnTemplate() {
            assertEquals("test {}", StrFormatter.format("test {}", (Object[]) null));
        }

        @Test
        @DisplayName("参数数组为空返回原模板")
        void format_WhenParamsIsEmpty_ShouldReturnTemplate() {
            assertEquals("test {}", StrFormatter.format("test {}"));
        }

        @Test
        @DisplayName("参数数量少于占位符")
        void format_WhenParamsLessThanPlaceholders_ShouldFormatAvailable() {
            String result = StrFormatter.format("{} {} {}", "a", "b");
            assertEquals("a b {}", result);
        }

        @Test
        @DisplayName("参数数量多于占位符")
        void format_WhenParamsMoreThanPlaceholders_ShouldIgnoreExtra() {
            String result = StrFormatter.format("test {}", "a", "b", "c");
            assertEquals("test a", result);
        }

        @Test
        @DisplayName("转义{} - 使用\\转义")
        void format_WhenEscapedPlaceholder_ShouldKeepBraces() {
            String result = StrFormatter.format("this is \\{} for {}", "a");
            assertEquals("this is {} for a", result);
        }

        @Test
        @DisplayName("双转义\\\\后正常替换")
        void format_WhenDoubleEscaped_ShouldReplace() {
            String result = StrFormatter.format("this is \\\\{} for {}", "a", "b");
            assertEquals("this is \\a for b", result);
        }

        @Test
        @DisplayName("参数为null")
        void format_WhenParamIsNull_ShouldUseNullString() {
            String result = StrFormatter.format("value is {}", (Object) null);
            assertEquals("value is null", result);
        }

        @Test
        @DisplayName("无占位符返回原模板")
        void format_WhenNoPlaceholder_ShouldReturnTemplate() {
            assertEquals("hello world", StrFormatter.format("hello world", "a", "b"));
        }

        @Test
        @DisplayName("混合转义和正常占位符")
        void format_WhenMixedEscape_ShouldHandleCorrectly() {
            String result = StrFormatter.format("\\{} {} \\{} {}", "a", "b");
            assertEquals("{} a {} b", result);
        }

        @Test
        @DisplayName("连续占位符")
        void format_WhenConsecutivePlaceholders_ShouldFormatCorrectly() {
            String result = StrFormatter.format("{}{}", "a", "b");
            assertEquals("ab", result);
        }
    }

    @Nested
    @DisplayName("常量测试")
    class ConstantsTest {
        @Test
        @DisplayName("EMPTY_JSON常量")
        void emptyJson_ShouldBeCorrect() {
            assertEquals("{}", StrFormatter.EMPTY_JSON);
        }

        @Test
        @DisplayName("C_BACKSLASH常量")
        void cBackslash_ShouldBeCorrect() {
            assertEquals('\\', StrFormatter.C_BACKSLASH);
        }

        @Test
        @DisplayName("C_DELIM_START常量")
        void cDelimStart_ShouldBeCorrect() {
            assertEquals('{', StrFormatter.C_DELIM_START);
        }

        @Test
        @DisplayName("C_DELIM_END常量")
        void cDelimEnd_ShouldBeCorrect() {
            assertEquals('}', StrFormatter.C_DELIM_END);
        }
    }
}

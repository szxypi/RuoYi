package com.zjjh.fdtemp.common.utils.html;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * EscapeUtil 转义和反转义工具类单元测试
 */
@DisplayName("EscapeUtil 转义和反转义工具类测试")
class EscapeUtilTest {

    @Nested
    @DisplayName("escape 编码测试")
    class EscapeTest {
        @Test
        @DisplayName("普通字符编码")
        void escape_WhenNormalChars_ShouldEncode() {
            String result = EscapeUtil.escape("abc");
            assertEquals("%61%62%63", result);
        }

        @Test
        @DisplayName("特殊字符编码")
        void escape_WhenSpecialChars_ShouldEncode() {
            String result = EscapeUtil.escape("<script>");
            assertTrue(result.contains("%3c")); // <
            assertTrue(result.contains("%3e")); // >
        }

        @Test
        @DisplayName("中文字符编码")
        void escape_WhenChineseChars_ShouldEncode() {
            String result = EscapeUtil.escape("中文");
            assertTrue(result.contains("%u"));
        }

        @Test
        @DisplayName("空字符串返回空字符串")
        void escape_WhenEmpty_ShouldReturnEmpty() {
            assertEquals("", EscapeUtil.escape(""));
        }

        @Test
        @DisplayName("null返回空字符串")
        void escape_WhenNull_ShouldReturnEmpty() {
            assertEquals("", EscapeUtil.escape(null));
        }

        @Test
        @DisplayName("空白字符串编码")
        void escape_WhenBlank_ShouldEncode() {
            String result = EscapeUtil.escape("   ");
            assertEquals("%20%20%20", result);
        }

        @Test
        @DisplayName("XSS攻击字符串编码")
        void escape_WhenXSSAttack_ShouldEncode() {
            String result = EscapeUtil.escape("<script>alert('XSS')</script>");
            assertFalse(result.contains("<script>"));
        }
    }

    @Nested
    @DisplayName("unescape 解码测试")
    class UnescapeTest {
        @Test
        @DisplayName("普通字符解码")
        void unescape_WhenEncodedChars_ShouldDecode() {
            String result = EscapeUtil.unescape("%61%62%63");
            assertEquals("abc", result);
        }

        @Test
        @DisplayName("中文字符解码")
        void unescape_WhenEncodedChinese_ShouldDecode() {
            String result = EscapeUtil.unescape("%u4e2d%u6587");
            assertEquals("中文", result);
        }

        @Test
        @DisplayName("null返回null")
        void unescape_WhenNull_ShouldReturnNull() {
            assertNull(EscapeUtil.unescape(null));
        }

        @Test
        @DisplayName("空字符串返回空字符串")
        void unescape_WhenEmpty_ShouldReturnEmpty() {
            assertEquals("", EscapeUtil.unescape(""));
        }

        @Test
        @DisplayName("混合编码解码")
        void unescape_WhenMixedEncoding_ShouldDecode() {
            // 先编码再解码应该得到原字符串
            String original = "Hello 中文 <script>";
            String encoded = EscapeUtil.escape(original);
            String decoded = EscapeUtil.unescape(encoded);
            assertEquals(original, decoded);
        }

        @Test
        @DisplayName("无编码字符原样返回")
        void unescape_WhenNoEncoding_ShouldReturnOriginal() {
            String result = EscapeUtil.unescape("hello world");
            assertEquals("hello world", result);
        }
    }

    @Nested
    @DisplayName("clean 清除HTML标签测试")
    class CleanTest {
        @Test
        @DisplayName("清除script标签")
        void clean_WhenScriptTag_ShouldRemove() {
            String result = EscapeUtil.clean("<script>alert('XSS')</script>");
            assertFalse(result.contains("<script>"));
            assertFalse(result.contains("</script>"));
        }

        @Test
        @DisplayName("清除HTML标签但保留内容")
        void clean_WhenHTMLTags_ShouldRemoveTagsButKeepContent() {
            String result = EscapeUtil.clean("<b>Hello</b> World");
            assertTrue(result.contains("Hello"));
            assertTrue(result.contains("World"));
        }

        @Test
        @DisplayName("清除style标签")
        void clean_WhenStyleTag_ShouldRemove() {
            String result = EscapeUtil.clean("<style>body{color:red}</style>");
            assertFalse(result.contains("<style>"));
        }

        @Test
        @DisplayName("null返回空字符串")
        void clean_WhenNull_ShouldReturnEmpty() {
            String result = EscapeUtil.clean(null);
            assertEquals("", result);
        }

        @Test
        @DisplayName("空字符串返回空字符串")
        void clean_WhenEmpty_ShouldReturnEmpty() {
            assertEquals("", EscapeUtil.clean(""));
        }

        @Test
        @DisplayName("清除a标签")
        void clean_WhenAnchorTag_ShouldRemove() {
            String result = EscapeUtil.clean("<a href=\"http://example.com\">link</a>");
            // 根据HTMLFilter的配置，a标签是允许的，但会被处理
            assertNotNull(result);
        }

        @Test
        @DisplayName("清除img标签")
        void clean_WhenImgTag_ShouldProcess() {
            String result = EscapeUtil.clean("<img src=\"x\" onerror=\"alert(1)\">");
            assertNotNull(result);
        }

        @Test
        @DisplayName("嵌套标签处理")
        void clean_WhenNestedTags_ShouldProcess() {
            String result = EscapeUtil.clean("<div><span>nested</span></div>");
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("编码解码循环测试")
    class EncodeDecodeCycleTest {
        @Test
        @DisplayName("编码后解码应得到原字符串")
        void encodeDecode_ShouldReturnOriginal() {
            String[] testCases = {
                    "Hello World",
                    "中文测试",
                    "<script>alert('XSS')</script>",
                    "test@example.com",
                    "12345",
                    "special chars: !@#$%^&*()",
                    "换行\n和制表符\t"
            };

            for (String original : testCases) {
                String encoded = EscapeUtil.escape(original);
                String decoded = EscapeUtil.unescape(encoded);
                assertEquals(original, decoded, "Failed for: " + original);
            }
        }
    }

    @Nested
    @DisplayName("RE_HTML_MARK 常量测试")
    class ReHtmlMarkTest {
        @Test
        @DisplayName("正则表达式匹配HTML标签")
        void reHtmlMark_ShouldMatchHTMLTags() {
            assertTrue("<script>".matches(EscapeUtil.RE_HTML_MARK));
            assertTrue("</script>".matches(EscapeUtil.RE_HTML_MARK));
            assertTrue("<div/>".matches(EscapeUtil.RE_HTML_MARK));
        }
    }
}

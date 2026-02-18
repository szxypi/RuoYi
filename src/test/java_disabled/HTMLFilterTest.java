package com.zjjh.fdtemp.common.utils.html;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * HTMLFilter HTML过滤器单元测试
 */
@DisplayName("HTMLFilter HTML过滤器测试")
class HTMLFilterTest {

    @Nested
    @DisplayName("默认构造函数测试")
    class DefaultConstructorTest {
        @Test
        @DisplayName("过滤script标签")
        void filter_WhenScriptTag_ShouldRemove() {
            HTMLFilter filter = new HTMLFilter();
            String result = filter.filter("<script>alert('XSS')</script>");
            assertFalse(result.contains("script"));
        }

        @Test
        @DisplayName("保留允许的标签")
        void filter_WhenAllowedTag_ShouldKeep() {
            HTMLFilter filter = new HTMLFilter();
            String result = filter.filter("<b>bold</b>");
            assertTrue(result.contains("<b>"));
            assertTrue(result.contains("</b>"));
        }

        @Test
        @DisplayName("处理a标签")
        void filter_WhenAnchorTag_ShouldProcess() {
            HTMLFilter filter = new HTMLFilter();
            String result = filter.filter("<a href=\"http://example.com\">link</a>");
            assertNotNull(result);
        }

        @Test
        @DisplayName("处理img标签")
        void filter_WhenImgTag_ShouldProcess() {
            HTMLFilter filter = new HTMLFilter();
            String result = filter.filter("<img src=\"test.jpg\" alt=\"test\">");
            assertNotNull(result);
        }

        @Test
        @DisplayName("null输入返回空字符串")
        void filter_WhenNull_ShouldReturnEmpty() {
            HTMLFilter filter = new HTMLFilter();
            assertEquals("", filter.filter(null));
        }

        @Test
        @DisplayName("空字符串返回空字符串")
        void filter_WhenEmpty_ShouldReturnEmpty() {
            HTMLFilter filter = new HTMLFilter();
            assertEquals("", filter.filter(""));
        }

        @Test
        @DisplayName("纯文本原样返回")
        void filter_WhenPlainText_ShouldReturnSame() {
            HTMLFilter filter = new HTMLFilter();
            String result = filter.filter("Hello World");
            assertEquals("Hello World", result);
        }

        @Test
        @DisplayName("处理HTML注释")
        void filter_WhenHTMLComment_ShouldProcess() {
            HTMLFilter filter = new HTMLFilter();
            String result = filter.filter("<!-- comment -->text");
            // 默认配置会剥离注释
            assertNotNull(result);
        }

        @Test
        @DisplayName("处理不匹配的标签")
        void filter_WhenUnmatchedTags_ShouldBalance() {
            HTMLFilter filter = new HTMLFilter();
            String result = filter.filter("<b>text");
            assertNotNull(result);
        }

        @Test
        @DisplayName("处理XSS攻击向量")
        void filter_WhenXSSVector_ShouldSanitize() {
            HTMLFilter filter = new HTMLFilter();
            String result = filter.filter("<img src=\"x\" onerror=\"alert(1)\">");
            assertFalse(result.contains("onerror"));
        }
    }

    @Nested
    @DisplayName("自定义配置构造函数测试")
    class CustomConstructorTest {
        @Test
        @DisplayName("使用自定义配置")
        void filter_WhenCustomConfig_ShouldUseConfig() {
            Map<String, Object> conf = new HashMap<>();

            List<String> aAtts = new ArrayList<>();
            aAtts.add("href");
            Map<String, List<String>> vAllowed = new HashMap<>();
            vAllowed.put("a", aAtts);
            conf.put("vAllowed", vAllowed);
            conf.put("vSelfClosingTags", new String[]{});
            conf.put("vNeedClosingTags", new String[]{"a"});
            conf.put("vDisallowed", new String[]{});
            conf.put("vAllowedProtocols", new String[]{"http", "https"});
            conf.put("vProtocolAtts", new String[]{"href", "src"});
            conf.put("vRemoveBlanks", new String[]{"a"});
            conf.put("vAllowedEntities", new String[]{"amp", "lt", "gt"});
            conf.put("stripComment", true);
            conf.put("encodeQuotes", true);
            conf.put("alwaysMakeTags", true);

            HTMLFilter filter = new HTMLFilter(conf);
            String result = filter.filter("<a href=\"http://example.com\">link</a>");
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("静态方法测试")
    class StaticMethodsTest {
        @Test
        @DisplayName("chr方法返回对应字符")
        void chr_ShouldReturnChar() {
            assertEquals("A", HTMLFilter.chr(65));
            assertEquals("a", HTMLFilter.chr(97));
            assertEquals("0", HTMLFilter.chr(48));
        }

        @Test
        @DisplayName("htmlSpecialChars转义特殊字符")
        void htmlSpecialChars_ShouldEscape() {
            String result = HTMLFilter.htmlSpecialChars("<script>&\"test");
            assertTrue(result.contains("&lt;"));
            assertTrue(result.contains("&gt;"));
            assertTrue(result.contains("&amp;"));
            assertTrue(result.contains("&quot;"));
        }
    }

    @Nested
    @DisplayName("isAlwaysMakeTags 测试")
    class IsAlwaysMakeTagsTest {
        @Test
        @DisplayName("默认配置返回false")
        void isAlwaysMakeTags_WhenDefault_ShouldReturnFalse() {
            HTMLFilter filter = new HTMLFilter();
            assertFalse(filter.isAlwaysMakeTags());
        }
    }

    @Nested
    @DisplayName("isStripComments 测试")
    class IsStripCommentsTest {
        @Test
        @DisplayName("默认配置返回true")
        void isStripComments_WhenDefault_ShouldReturnTrue() {
            HTMLFilter filter = new HTMLFilter();
            assertTrue(filter.isStripComments());
        }
    }

    @Nested
    @DisplayName("协议处理测试")
    class ProtocolHandlingTest {
        @Test
        @DisplayName("允许http协议")
        void filter_WhenHttpProtocol_ShouldAllow() {
            HTMLFilter filter = new HTMLFilter();
            String result = filter.filter("<a href=\"http://example.com\">link</a>");
            assertTrue(result.contains("http://"));
        }

        @Test
        @DisplayName("允许https协议")
        void filter_WhenHttpsProtocol_ShouldAllow() {
            HTMLFilter filter = new HTMLFilter();
            String result = filter.filter("<a href=\"https://example.com\">link</a>");
            assertTrue(result.contains("https://"));
        }

        @Test
        @DisplayName("允许mailto协议")
        void filter_WhenMailtoProtocol_ShouldAllow() {
            HTMLFilter filter = new HTMLFilter();
            String result = filter.filter("<a href=\"mailto:test@example.com\">email</a>");
            assertTrue(result.contains("mailto:"));
        }

        @Test
        @DisplayName("过滤危险协议")
        void filter_WhenDangerousProtocol_ShouldFilter() {
            HTMLFilter filter = new HTMLFilter();
            String result = filter.filter("<a href=\"javascript:alert(1)\">link</a>");
            assertFalse(result.contains("javascript:"));
        }
    }

    @Nested
    @DisplayName("实体编码测试")
    class EntityEncodingTest {
        @Test
        @DisplayName("处理数字实体")
        void filter_WhenNumericEntity_ShouldProcess() {
            HTMLFilter filter = new HTMLFilter();
            String result = filter.filter("&#60;script&#62;");
            assertNotNull(result);
        }

        @Test
        @DisplayName("处理十六进制实体")
        void filter_WhenHexEntity_ShouldProcess() {
            HTMLFilter filter = new HTMLFilter();
            String result = filter.filter("&#x3c;script&#x3e;");
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("复杂XSS攻击测试")
    class ComplexXSSTest {
        @Test
        @DisplayName("处理嵌套script标签")
        void filter_WhenNestedScript_ShouldRemove() {
            HTMLFilter filter = new HTMLFilter();
            String result = filter.filter("<scr<script>ipt>alert(1)</scr</script>ipt>");
            assertFalse(result.toLowerCase().contains("script"));
        }

        @Test
        @DisplayName("处理大小写混合标签")
        void filter_WhenMixedCaseTag_ShouldHandle() {
            HTMLFilter filter = new HTMLFilter();
            String result = filter.filter("<ScRiPt>alert(1)</sCrIpT>");
            assertFalse(result.toLowerCase().contains("script"));
        }

        @Test
        @DisplayName("处理带空格的标签")
        void filter_WhenTagWithSpaces_ShouldHandle() {
            HTMLFilter filter = new HTMLFilter();
            String result = filter.filter("<script >alert(1)</script >");
            assertFalse(result.toLowerCase().contains("script"));
        }
    }
}

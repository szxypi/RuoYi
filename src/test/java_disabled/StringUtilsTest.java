package com.zjjh.fdtemp.common.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * StringUtils 工具类单元测试
 */
@DisplayName("StringUtils 工具类测试")
class StringUtilsTest {

    @Nested
    @DisplayName("nvl 方法测试")
    class NvlTest {
        @Test
        @DisplayName("当值为null时返回默认值")
        void nvl_WhenValueIsNull_ShouldReturnDefault() {
            String result = StringUtils.nvl(null, "default");
            assertEquals("default", result);
        }

        @Test
        @DisplayName("当值不为null时返回原值")
        void nvl_WhenValueIsNotNull_ShouldReturnValue() {
            String result = StringUtils.nvl("value", "default");
            assertEquals("value", result);
        }

        @Test
        @DisplayName("当数值为null时返回默认值")
        void nvl_WhenNumberIsNull_ShouldReturnDefault() {
            Integer result = StringUtils.nvl(null, 0);
            assertEquals(0, result);
        }

        @Test
        @DisplayName("当数值不为null时返回原值")
        void nvl_WhenNumberIsNotNull_ShouldReturnValue() {
            Integer result = StringUtils.nvl(100, 0);
            assertEquals(100, result);
        }
    }

    @Nested
    @DisplayName("isEmpty Collection 方法测试")
    class IsEmptyCollectionTest {
        @Test
        @DisplayName("当集合为null时返回true")
        void isEmpty_WhenCollectionIsNull_ShouldReturnTrue() {
            assertTrue(StringUtils.isEmpty((Collection<?>) null));
        }

        @Test
        @DisplayName("当集合为空时返回true")
        void isEmpty_WhenCollectionIsEmpty_ShouldReturnTrue() {
            assertTrue(StringUtils.isEmpty(new ArrayList<>()));
        }

        @Test
        @DisplayName("当集合不为空时返回false")
        void isEmpty_WhenCollectionIsNotEmpty_ShouldReturnFalse() {
            List<String> list = Arrays.asList("a", "b");
            assertFalse(StringUtils.isEmpty(list));
        }
    }

    @Nested
    @DisplayName("isNotEmpty Collection 方法测试")
    class IsNotEmptyCollectionTest {
        @Test
        @DisplayName("当集合为null时返回false")
        void isNotEmpty_WhenCollectionIsNull_ShouldReturnFalse() {
            assertFalse(StringUtils.isNotEmpty((Collection<?>) null));
        }

        @Test
        @DisplayName("当集合不为空时返回true")
        void isNotEmpty_WhenCollectionIsNotEmpty_ShouldReturnTrue() {
            List<String> list = Arrays.asList("a", "b");
            assertTrue(StringUtils.isNotEmpty(list));
        }
    }

    @Nested
    @DisplayName("isEmpty Object数组 方法测试")
    class IsEmptyArrayTest {
        @Test
        @DisplayName("当数组为null时返回true")
        void isEmpty_WhenArrayIsNull_ShouldReturnTrue() {
            assertTrue(StringUtils.isEmpty((Object[]) null));
        }

        @Test
        @DisplayName("当数组为空时返回true")
        void isEmpty_WhenArrayIsEmpty_ShouldReturnTrue() {
            assertTrue(StringUtils.isEmpty(new Object[]{}));
        }

        @Test
        @DisplayName("当数组不为空时返回false")
        void isEmpty_WhenArrayIsNotEmpty_ShouldReturnFalse() {
            assertFalse(StringUtils.isEmpty(new Object[]{"a", "b"}));
        }
    }

    @Nested
    @DisplayName("isEmpty Map 方法测试")
    class IsEmptyMapTest {
        @Test
        @DisplayName("当Map为null时返回true")
        void isEmpty_WhenMapIsNull_ShouldReturnTrue() {
            assertTrue(StringUtils.isEmpty((Map<?, ?>) null));
        }

        @Test
        @DisplayName("当Map为空时返回true")
        void isEmpty_WhenMapIsEmpty_ShouldReturnTrue() {
            assertTrue(StringUtils.isEmpty(new HashMap<>()));
        }

        @Test
        @DisplayName("当Map不为空时返回false")
        void isEmpty_WhenMapIsNotEmpty_ShouldReturnFalse() {
            Map<String, String> map = new HashMap<>();
            map.put("key", "value");
            assertFalse(StringUtils.isEmpty(map));
        }
    }

    @Nested
    @DisplayName("isEmpty String 方法测试")
    class IsEmptyStringTest {
        @Test
        @DisplayName("当字符串为null时返回true")
        void isEmpty_WhenStringIsNull_ShouldReturnTrue() {
            assertTrue(StringUtils.isEmpty((String) null));
        }

        @Test
        @DisplayName("当字符串为空时返回true")
        void isEmpty_WhenStringIsEmpty_ShouldReturnTrue() {
            assertTrue(StringUtils.isEmpty(""));
        }

        @Test
        @DisplayName("当字符串为空白字符时返回true")
        void isEmpty_WhenStringIsBlank_ShouldReturnTrue() {
            assertTrue(StringUtils.isEmpty("   "));
        }

        @Test
        @DisplayName("当字符串不为空时返回false")
        void isEmpty_WhenStringIsNotEmpty_ShouldReturnFalse() {
            assertFalse(StringUtils.isEmpty("test"));
        }
    }

    @Nested
    @DisplayName("isNull/isNotNull 方法测试")
    class IsNullTest {
        @Test
        @DisplayName("当对象为null时isNull返回true")
        void isNull_WhenObjectIsNull_ShouldReturnTrue() {
            assertTrue(StringUtils.isNull(null));
        }

        @Test
        @DisplayName("当对象不为null时isNull返回false")
        void isNull_WhenObjectIsNotNull_ShouldReturnFalse() {
            assertFalse(StringUtils.isNull(new Object()));
        }

        @Test
        @DisplayName("当对象为null时isNotNull返回false")
        void isNotNull_WhenObjectIsNull_ShouldReturnFalse() {
            assertFalse(StringUtils.isNotNull(null));
        }

        @Test
        @DisplayName("当对象不为null时isNotNull返回true")
        void isNotNull_WhenObjectIsNotNull_ShouldReturnTrue() {
            assertTrue(StringUtils.isNotNull(new Object()));
        }
    }

    @Nested
    @DisplayName("isArray 方法测试")
    class IsArrayTest {
        @Test
        @DisplayName("当对象为null时返回false")
        void isArray_WhenObjectIsNull_ShouldReturnFalse() {
            assertFalse(StringUtils.isArray(null));
        }

        @Test
        @DisplayName("当对象是数组时返回true")
        void isArray_WhenObjectIsArray_ShouldReturnTrue() {
            assertTrue(StringUtils.isArray(new String[]{"a", "b"}));
        }

        @Test
        @DisplayName("当对象不是数组时返回false")
        void isArray_WhenObjectIsNotArray_ShouldReturnFalse() {
            assertFalse(StringUtils.isArray("string"));
        }
    }

    @Nested
    @DisplayName("trim 方法测试")
    class TrimTest {
        @Test
        @DisplayName("当字符串为null时返回空字符串")
        void trim_WhenStringIsNull_ShouldReturnEmpty() {
            assertEquals("", StringUtils.trim(null));
        }

        @Test
        @DisplayName("当字符串有前后空格时去除空格")
        void trim_WhenStringHasWhitespace_ShouldRemoveWhitespace() {
            assertEquals("test", StringUtils.trim("  test  "));
        }
    }

    @Nested
    @DisplayName("hide 方法测试")
    class HideTest {
        @Test
        @DisplayName("当字符串为null时返回空字符串")
        void hide_WhenStringIsNull_ShouldReturnEmpty() {
            assertEquals("", StringUtils.hide(null, 0, 3));
        }

        @Test
        @DisplayName("当字符串为空时返回空字符串")
        void hide_WhenStringIsEmpty_ShouldReturnEmpty() {
            assertEquals("", StringUtils.hide("", 0, 3));
        }

        @Test
        @DisplayName("正常隐藏中间字符")
        void hide_WhenNormalCase_ShouldHideChars() {
            assertEquals("ab**ef", StringUtils.hide("abcdef", 2, 4));
        }

        @Test
        @DisplayName("当起始位置大于字符串长度时返回空字符串")
        void hide_WhenStartExceedsLength_ShouldReturnEmpty() {
            assertEquals("", StringUtils.hide("abc", 5, 6));
        }

        @Test
        @DisplayName("当结束位置大于字符串长度时自动调整")
        void hide_WhenEndExceedsLength_ShouldAdjust() {
            assertEquals("ab***", StringUtils.hide("abcde", 2, 10));
        }

        @Test
        @DisplayName("当起始位置大于结束位置时返回空字符串")
        void hide_WhenStartGreaterThanEnd_ShouldReturnEmpty() {
            assertEquals("", StringUtils.hide("abc", 3, 1));
        }
    }

    @Nested
    @DisplayName("substring 单参数 方法测试")
    class SubstringSingleParamTest {
        @Test
        @DisplayName("当字符串为null时返回空字符串")
        void substring_WhenStringIsNull_ShouldReturnEmpty() {
            assertEquals("", StringUtils.substring(null, 2));
        }

        @Test
        @DisplayName("正常截取字符串")
        void substring_WhenNormalCase_ShouldReturnSubstring() {
            assertEquals("cdef", StringUtils.substring("abcdef", 2));
        }

        @Test
        @DisplayName("当起始位置为负数时从末尾计算")
        void substring_WhenStartIsNegative_ShouldCalculateFromEnd() {
            assertEquals("ef", StringUtils.substring("abcdef", -2));
        }

        @Test
        @DisplayName("当起始位置超过字符串长度时返回空字符串")
        void substring_WhenStartExceedsLength_ShouldReturnEmpty() {
            assertEquals("", StringUtils.substring("abc", 5));
        }
    }

    @Nested
    @DisplayName("substring 双参数 方法测试")
    class SubstringTwoParamsTest {
        @Test
        @DisplayName("当字符串为null时返回空字符串")
        void substring_WhenStringIsNull_ShouldReturnEmpty() {
            assertEquals("", StringUtils.substring(null, 1, 3));
        }

        @Test
        @DisplayName("正常截取字符串")
        void substring_WhenNormalCase_ShouldReturnSubstring() {
            assertEquals("bc", StringUtils.substring("abcdef", 1, 3));
        }

        @Test
        @DisplayName("当结束位置为负数时从末尾计算")
        void substring_WhenEndIsNegative_ShouldCalculateFromEnd() {
            assertEquals("bcd", StringUtils.substring("abcdef", 1, -2));
        }

        @Test
        @DisplayName("当起始位置大于结束位置时返回空字符串")
        void substring_WhenStartGreaterThanEnd_ShouldReturnEmpty() {
            assertEquals("", StringUtils.substring("abcdef", 4, 2));
        }
    }

    @Nested
    @DisplayName("substringBetweenLast 方法测试")
    class SubstringBetweenLastTest {
        @Test
        @DisplayName("正常情况")
        void substringBetweenLast_WhenNormalCase_ShouldReturnSubstring() {
            assertEquals("cd", StringUtils.substringBetweenLast("abcdef", "ab", "ef"));
        }

        @Test
        @DisplayName("当字符串为null时返回空字符串")
        void substringBetweenLast_WhenStringIsNull_ShouldReturnEmpty() {
            assertEquals("", StringUtils.substringBetweenLast(null, "a", "b"));
        }

        @Test
        @DisplayName("当open为null时返回空字符串")
        void substringBetweenLast_WhenOpenIsNull_ShouldReturnEmpty() {
            assertEquals("", StringUtils.substringBetweenLast("abcdef", null, "ef"));
        }

        @Test
        @DisplayName("当找不到匹配时返回空字符串")
        void substringBetweenLast_WhenNoMatch_ShouldReturnEmpty() {
            assertEquals("", StringUtils.substringBetweenLast("abcdef", "x", "y"));
        }
    }

    @Nested
    @DisplayName("format 方法测试")
    class FormatTest {
        @Test
        @DisplayName("正常格式化")
        void format_WhenNormalCase_ShouldFormatString() {
            assertEquals("this is a for b", StringUtils.format("this is {} for {}", "a", "b"));
        }

        @Test
        @DisplayName("当模板为null时返回null")
        void format_WhenTemplateIsNull_ShouldReturnNull() {
            assertNull(StringUtils.format(null, "a", "b"));
        }

        @Test
        @DisplayName("当参数为空时返回原模板")
        void format_WhenParamsIsEmpty_ShouldReturnTemplate() {
            assertEquals("test {}", StringUtils.format("test {}"));
        }
    }

    @Nested
    @DisplayName("str2Set/str2List 方法测试")
    class Str2CollectionTest {
        @Test
        @DisplayName("字符串转Set")
        void str2Set_WhenNormalCase_ShouldReturnSet() {
            Set<String> result = StringUtils.str2Set("a,b,c", ",");
            assertEquals(new HashSet<>(Arrays.asList("a", "b", "c")), result);
        }

        @Test
        @DisplayName("字符串转List")
        void str2List_WhenNormalCase_ShouldReturnList() {
            List<String> result = StringUtils.str2List("a,b,c", ",");
            assertEquals(Arrays.asList("a", "b", "c"), result);
        }

        @Test
        @DisplayName("字符串转List带过滤空白")
        void str2List_WithFilterBlank_ShouldFilterBlank() {
            List<String> result = StringUtils.str2List("a,  ,c", ",", true, false);
            assertEquals(Arrays.asList("a", "c"), result);
        }

        @Test
        @DisplayName("字符串转List带trim")
        void str2List_WithTrim_ShouldTrim() {
            List<String> result = StringUtils.str2List("a , b , c", ",", false, true);
            assertEquals(Arrays.asList("a", "b", "c"), result);
        }

        @Test
        @DisplayName("当字符串为null时返回空列表")
        void str2List_WhenStringIsNull_ShouldReturnEmptyList() {
            List<String> result = StringUtils.str2List(null, ",");
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("containsAny 方法测试")
    class ContainsAnyTest {
        @Test
        @DisplayName("当包含任意元素时返回true")
        void containsAny_WhenContainsElement_ShouldReturnTrue() {
            List<String> collection = Arrays.asList("a", "b", "c");
            assertTrue(StringUtils.containsAny(collection, "b", "d"));
        }

        @Test
        @DisplayName("当不包含任何元素时返回false")
        void containsAny_WhenNotContainsElement_ShouldReturnFalse() {
            List<String> collection = Arrays.asList("a", "b", "c");
            assertFalse(StringUtils.containsAny(collection, "d", "e"));
        }

        @Test
        @DisplayName("当集合为null时返回false")
        void containsAny_WhenCollectionIsNull_ShouldReturnFalse() {
            Collection<String> col = null;
            String[] arr = {"a", "b"};
            assertFalse(StringUtils.containsAny(col, arr));
        }

        @Test
        @DisplayName("当数组为null时返回false")
        void containsAny_WhenArrayIsNull_ShouldReturnFalse() {
            String[] arr = null;
            assertFalse(StringUtils.containsAny(Arrays.asList("a", "b"), arr));
        }
    }

    @Nested
    @DisplayName("toUnderScoreCase 方法测试")
    class ToUnderScoreCaseTest {
        @Test
        @DisplayName("驼峰转下划线")
        void toUnderScoreCase_WhenCamelCase_ShouldConvertToUnderscore() {
            assertEquals("user_name", StringUtils.toUnderScoreCase("userName"));
        }

        @Test
        @DisplayName("连续大写字母处理")
        void toUnderScoreCase_WhenConsecutiveUppercase_ShouldHandleCorrectly() {
            assertEquals("http_url", StringUtils.toUnderScoreCase("HTTPUrl"));
        }

        @Test
        @DisplayName("当字符串为null时返回null")
        void toUnderScoreCase_WhenStringIsNull_ShouldReturnNull() {
            assertNull(StringUtils.toUnderScoreCase(null));
        }

        @Test
        @DisplayName("单个大写字母")
        void toUnderScoreCase_WhenSingleUppercase_ShouldConvertToLower() {
            assertEquals("a", StringUtils.toUnderScoreCase("A"));
        }
    }

    @Nested
    @DisplayName("convertToCamelCase 方法测试")
    class ConvertToCamelCaseTest {
        @Test
        @DisplayName("下划线转驼峰")
        void convertToCamelCase_WhenUnderscoreCase_ShouldConvertToCamelCase() {
            assertEquals("HelloWorld", StringUtils.convertToCamelCase("HELLO_WORLD"));
        }

        @Test
        @DisplayName("当字符串为null时返回空字符串")
        void convertToCamelCase_WhenStringIsNull_ShouldReturnEmpty() {
            assertEquals("", StringUtils.convertToCamelCase(null));
        }

        @Test
        @DisplayName("当字符串不含下划线时首字母大写")
        void convertToCamelCase_WhenNoUnderscore_ShouldCapitalizeFirst() {
            assertEquals("Hello", StringUtils.convertToCamelCase("hello"));
        }
    }

    @Nested
    @DisplayName("toCamelCase 方法测试")
    class ToCamelCaseTest {
        @Test
        @DisplayName("下划线转小驼峰")
        void toCamelCase_WhenUnderscoreCase_ShouldConvertToSmallCamelCase() {
            assertEquals("userName", StringUtils.toCamelCase("user_name"));
        }

        @Test
        @DisplayName("当字符串为null时返回null")
        void toCamelCase_WhenStringIsNull_ShouldReturnNull() {
            assertNull(StringUtils.toCamelCase(null));
        }

        @Test
        @DisplayName("当字符串不含下划线时原样返回")
        void toCamelCase_WhenNoUnderscore_ShouldReturnOriginal() {
            assertEquals("username", StringUtils.toCamelCase("username"));
        }
    }

    @Nested
    @DisplayName("inStringIgnoreCase 方法测试")
    class InStringIgnoreCaseTest {
        @Test
        @DisplayName("忽略大小写匹配")
        void inStringIgnoreCase_WhenMatchIgnoreCase_ShouldReturnTrue() {
            assertTrue(StringUtils.inStringIgnoreCase("ABC", "abc", "def"));
        }

        @Test
        @DisplayName("不匹配时返回false")
        void inStringIgnoreCase_WhenNotMatch_ShouldReturnFalse() {
            assertFalse(StringUtils.inStringIgnoreCase("xyz", "abc", "def"));
        }

        @Test
        @DisplayName("当str为null时返回false")
        void inStringIgnoreCase_WhenStrIsNull_ShouldReturnFalse() {
            assertFalse(StringUtils.inStringIgnoreCase(null, "abc", "def"));
        }
    }

    @Nested
    @DisplayName("lastStringDel 方法测试")
    class LastStringDelTest {
        @Test
        @DisplayName("删除最后指定字符")
        void lastStringDel_WhenEndsWithSpit_ShouldRemove() {
            assertEquals("test", StringUtils.lastStringDel("test,", ","));
        }

        @Test
        @DisplayName("不以指定字符结尾时原样返回")
        void lastStringDel_WhenNotEndsWithSpit_ShouldReturnOriginal() {
            assertEquals("test", StringUtils.lastStringDel("test", ","));
        }
    }

    @Nested
    @DisplayName("padl 方法测试")
    class PadlTest {
        @Test
        @DisplayName("数字左补0")
        void padl_WhenNumber_ShouldPadWithZero() {
            assertEquals("00123", StringUtils.padl(123, 5));
        }

        @Test
        @DisplayName("当数字长度超过size时截取")
        void padl_WhenNumberExceedsSize_ShouldTruncate() {
            assertEquals("345", StringUtils.padl(12345, 3));
        }

        @Test
        @DisplayName("字符串左补指定字符")
        void padl_WhenString_ShouldPadWithChar() {
            assertEquals("***test", StringUtils.padl("test", 7, '*'));
        }

        @Test
        @DisplayName("当字符串为null时全补指定字符")
        void padl_WhenStringIsNull_ShouldPadAll() {
            assertEquals("*****", StringUtils.padl(null, 5, '*'));
        }
    }

    @Nested
    @DisplayName("isMatch 方法测试")
    class IsMatchTest {
        @Test
        @DisplayName("Ant风格匹配")
        void isMatch_WhenAntPattern_ShouldMatch() {
            assertTrue(StringUtils.isMatch("/api/**", "/api/user/list"));
        }

        @Test
        @DisplayName("不匹配时返回false")
        void isMatch_WhenNotMatch_ShouldReturnFalse() {
            assertFalse(StringUtils.isMatch("/api/user/*", "/api/admin/list"));
        }
    }

    @Nested
    @DisplayName("cast 方法测试")
    class CastTest {
        @Test
        @DisplayName("类型转换")
        void cast_WhenObject_ShouldCast() {
            Object obj = "test";
            String result = StringUtils.cast(obj);
            assertEquals("test", result);
        }
    }

    @Nested
    @DisplayName("containsAnyIgnoreCase 方法测试")
    class ContainsAnyIgnoreCaseTest {
        @Test
        @DisplayName("忽略大小写包含")
        void containsAnyIgnoreCase_WhenContainsIgnoreCase_ShouldReturnTrue() {
            assertTrue(StringUtils.containsAnyIgnoreCase("Hello World", "WORLD", "test"));
        }

        @Test
        @DisplayName("不包含时返回false")
        void containsAnyIgnoreCase_WhenNotContains_ShouldReturnFalse() {
            assertFalse(StringUtils.containsAnyIgnoreCase("Hello", "WORLD", "test"));
        }

        @Test
        @DisplayName("当字符串为null时返回false")
        void containsAnyIgnoreCase_WhenStringIsNull_ShouldReturnFalse() {
            assertFalse(StringUtils.containsAnyIgnoreCase(null, "test"));
        }
    }
}

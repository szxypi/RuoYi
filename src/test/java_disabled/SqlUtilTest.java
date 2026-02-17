package com.zjjh.fdtemp.common.utils.sql;

import static org.junit.jupiter.api.Assertions.*;

import com.zjjh.fdtemp.common.exception.UtilException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * SqlUtil SQL操作工具类单元测试
 */
@DisplayName("SqlUtil SQL操作工具类测试")
class SqlUtilTest {

    @Nested
    @DisplayName("escapeOrderBySql 测试")
    class EscapeOrderBySqlTest {
        @Test
        @DisplayName("有效的order by语句")
        void escapeOrderBySql_WhenValid_ShouldReturnSameValue() {
            String result = SqlUtil.escapeOrderBySql("create_time desc");
            assertEquals("create_time desc", result);
        }

        @Test
        @DisplayName("多字段排序")
        void escapeOrderBySql_WhenMultipleFields_ShouldReturnSameValue() {
            String result = SqlUtil.escapeOrderBySql("create_time desc, name asc");
            assertEquals("create_time desc, name asc", result);
        }

        @Test
        @DisplayName("带表别名的排序")
        void escapeOrderBySql_WhenWithAlias_ShouldReturnSameValue() {
            String result = SqlUtil.escapeOrderBySql("t.create_time desc");
            assertEquals("t.create_time desc", result);
        }

        @Test
        @DisplayName("null值返回null")
        void escapeOrderBySql_WhenNull_ShouldReturnNull() {
            String result = SqlUtil.escapeOrderBySql(null);
            assertNull(result);
        }

        @Test
        @DisplayName("空字符串返回空字符串")
        void escapeOrderBySql_WhenEmpty_ShouldReturnEmpty() {
            String result = SqlUtil.escapeOrderBySql("");
            assertEquals("", result);
        }

        @Test
        @DisplayName("包含SQL注入关键字抛出异常")
        void escapeOrderBySql_WhenContainsInjection_ShouldThrowException() {
            assertThrows(UtilException.class, () -> SqlUtil.escapeOrderBySql("id; drop table users"));
        }

        @Test
        @DisplayName("超长参数抛出异常")
        void escapeOrderBySql_WhenTooLong_ShouldThrowException() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 600; i++) {
                sb.append("a");
            }
            assertThrows(UtilException.class, () -> SqlUtil.escapeOrderBySql(sb.toString()));
        }

        @Test
        @DisplayName("包含select关键字抛出异常")
        void escapeOrderBySql_WhenContainsSelect_ShouldThrowException() {
            assertThrows(UtilException.class, () -> SqlUtil.escapeOrderBySql("select * from users"));
        }

        @Test
        @DisplayName("包含特殊字符抛出异常")
        void escapeOrderBySql_WhenContainsSpecialChars_ShouldThrowException() {
            assertThrows(UtilException.class, () -> SqlUtil.escapeOrderBySql("id/*comment*/"));
        }
    }

    @Nested
    @DisplayName("isValidOrderBySql 测试")
    class IsValidOrderBySqlTest {
        @Test
        @DisplayName("有效的排序语句返回true")
        void isValidOrderBySql_WhenValid_ShouldReturnTrue() {
            assertTrue(SqlUtil.isValidOrderBySql("create_time desc"));
        }

        @Test
        @DisplayName("多字段排序返回true")
        void isValidOrderBySql_WhenMultipleFields_ShouldReturnTrue() {
            assertTrue(SqlUtil.isValidOrderBySql("name asc, create_time desc"));
        }

        @Test
        @DisplayName("带点号的字段名返回true")
        void isValidOrderBySql_WhenWithDot_ShouldReturnTrue() {
            assertTrue(SqlUtil.isValidOrderBySql("t.name asc"));
        }

        @Test
        @DisplayName("包含特殊字符返回false")
        void isValidOrderBySql_WhenContainsSpecialChars_ShouldReturnFalse() {
            assertFalse(SqlUtil.isValidOrderBySql("name; drop table"));
            assertFalse(SqlUtil.isValidOrderBySql("name'"));
            assertFalse(SqlUtil.isValidOrderBySql("name\""));
        }

        @Test
        @DisplayName("包含空格和逗号返回true")
        void isValidOrderBySql_WhenContainsSpaceAndComma_ShouldReturnTrue() {
            assertTrue(SqlUtil.isValidOrderBySql("name asc, id desc"));
        }
    }

    @Nested
    @DisplayName("filterKeyword 测试")
    class FilterKeywordTest {
        @Test
        @DisplayName("null值不抛出异常")
        void filterKeyword_WhenNull_ShouldNotThrow() {
            assertDoesNotThrow(() -> SqlUtil.filterKeyword(null));
        }

        @Test
        @DisplayName("空字符串不抛出异常")
        void filterKeyword_WhenEmpty_ShouldNotThrow() {
            assertDoesNotThrow(() -> SqlUtil.filterKeyword(""));
        }

        @Test
        @DisplayName("正常字符串不抛出异常")
        void filterKeyword_WhenNormalString_ShouldNotThrow() {
            assertDoesNotThrow(() -> SqlUtil.filterKeyword("正常内容"));
        }

        @Test
        @DisplayName("包含select关键字抛出异常")
        void filterKeyword_WhenContainsSelect_ShouldThrowException() {
            assertThrows(UtilException.class, () -> SqlUtil.filterKeyword("select * from users"));
        }

        @Test
        @DisplayName("包含insert关键字抛出异常")
        void filterKeyword_WhenContainsInsert_ShouldThrowException() {
            assertThrows(UtilException.class, () -> SqlUtil.filterKeyword("insert into users"));
        }

        @Test
        @DisplayName("包含delete关键字抛出异常")
        void filterKeyword_WhenContainsDelete_ShouldThrowException() {
            assertThrows(UtilException.class, () -> SqlUtil.filterKeyword("delete from users"));
        }

        @Test
        @DisplayName("包含update关键字抛出异常")
        void filterKeyword_WhenContainsUpdate_ShouldThrowException() {
            assertThrows(UtilException.class, () -> SqlUtil.filterKeyword("update users set"));
        }

        @Test
        @DisplayName("包含drop关键字抛出异常")
        void filterKeyword_WhenContainsDrop_ShouldThrowException() {
            assertThrows(UtilException.class, () -> SqlUtil.filterKeyword("drop table users"));
        }

        @Test
        @DisplayName("包含union关键字抛出异常")
        void filterKeyword_WhenContainsUnion_ShouldThrowException() {
            assertThrows(UtilException.class, () -> SqlUtil.filterKeyword("union select"));
        }

        @Test
        @DisplayName("包含sleep关键字抛出异常")
        void filterKeyword_WhenContainsSleep_ShouldThrowException() {
            assertThrows(UtilException.class, () -> SqlUtil.filterKeyword("sleep(5)"));
        }

        @Test
        @DisplayName("包含information_schema抛出异常")
        void filterKeyword_WhenContainsInformationSchema_ShouldThrowException() {
            assertThrows(UtilException.class, () -> SqlUtil.filterKeyword("information_schema.tables"));
        }

        @Test
        @DisplayName("忽略大小写检测")
        void filterKeyword_WhenCaseInsensitive_ShouldThrowException() {
            assertThrows(UtilException.class, () -> SqlUtil.filterKeyword("SELECT * FROM users"));
            assertThrows(UtilException.class, () -> SqlUtil.filterKeyword("Select * From users"));
        }
    }

    @Nested
    @DisplayName("SQL_REGEX 常量测试")
    class SqlRegexTest {
        @Test
        @DisplayName("检查SQL_REGEX包含关键字")
        void sqlRegex_ShouldContainKeywords() {
            assertTrue(SqlUtil.SQL_REGEX.contains("select"));
            assertTrue(SqlUtil.SQL_REGEX.contains("insert"));
            assertTrue(SqlUtil.SQL_REGEX.contains("delete"));
            assertTrue(SqlUtil.SQL_REGEX.contains("update"));
            assertTrue(SqlUtil.SQL_REGEX.contains("drop"));
        }
    }
}

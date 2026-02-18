package com.zjjh.fdtemp.common.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * DateUtils 时间工具类单元测试
 */
@DisplayName("DateUtils 时间工具类测试")
class DateUtilsTest {

    @Nested
    @DisplayName("获取当前日期时间测试")
    class GetCurrentDateTimeTest {
        @Test
        @DisplayName("获取当前Date型日期")
        void getNowDate_ShouldReturnCurrentDate() {
            Date result = DateUtils.getNowDate();
            assertNotNull(result);
            assertTrue(result.getTime() > 0);
        }

        @Test
        @DisplayName("获取当前日期字符串 yyyy-MM-dd格式")
        void getDate_ShouldReturnFormattedDate() {
            String result = DateUtils.getDate();
            assertNotNull(result);
            assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2}"));
        }

        @Test
        @DisplayName("获取当前时间字符串 yyyy-MM-dd HH:mm:ss格式")
        void getTime_ShouldReturnFormattedTime() {
            String result = DateUtils.getTime();
            assertNotNull(result);
            assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
        }

        @Test
        @DisplayName("获取当前日期时间 yyyyMMddHHmmss格式")
        void dateTimeNow_ShouldReturnFormattedDateTime() {
            String result = DateUtils.dateTimeNow();
            assertNotNull(result);
            assertTrue(result.matches("\\d{14}"));
        }
    }

    @Nested
    @DisplayName("parseDateToStr 日期转字符串测试")
    class ParseDateToStrTest {
        @Test
        @DisplayName("yyyy-MM-dd格式")
        void parseDateToStr_WhenYYYYMMDD_ShouldReturnFormattedString() {
            Date date = DateUtils.dateTime("yyyy-MM-dd", "2024-01-15");
            String result = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, date);
            assertEquals("2024-01-15", result);
        }

        @Test
        @DisplayName("yyyy-MM-dd HH:mm:ss格式")
        void parseDateToStr_WhenFullFormat_ShouldReturnFormattedString() {
            Date date = DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", "2024-01-15 10:30:45");
            String result = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, date);
            assertEquals("2024-01-15 10:30:45", result);
        }

        @Test
        @DisplayName("自定义格式")
        void parseDateToStr_WhenCustomFormat_ShouldReturnFormattedString() {
            Date date = DateUtils.dateTime("yyyy-MM-dd", "2024-01-15");
            String result = DateUtils.parseDateToStr("yyyy/MM/dd", date);
            assertEquals("2024/01/15", result);
        }
    }

    @Nested
    @DisplayName("dateTime 字符串转日期测试")
    class DateTimeTest {
        @Test
        @DisplayName("yyyy-MM-dd格式转换")
        void dateTime_WhenYYYYMMDD_ShouldReturnDate() {
            Date result = DateUtils.dateTime("yyyy-MM-dd", "2024-01-15");
            assertNotNull(result);
        }

        @Test
        @DisplayName("yyyy-MM-dd HH:mm:ss格式转换")
        void dateTime_WhenFullFormat_ShouldReturnDate() {
            Date result = DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", "2024-01-15 10:30:45");
            assertNotNull(result);
        }

        @Test
        @DisplayName("无效格式抛出异常")
        void dateTime_WhenInvalidFormat_ShouldThrowException() {
            assertThrows(RuntimeException.class, () -> DateUtils.dateTime("yyyy-MM-dd", "invalid"));
        }
    }

    @Nested
    @DisplayName("datePath/dateTime 路径格式测试")
    class DatePathTest {
        @Test
        @DisplayName("日期路径格式 yyyy/MM/dd")
        void datePath_ShouldReturnPathFormat() {
            String result = DateUtils.datePath();
            assertNotNull(result);
            assertTrue(result.matches("\\d{4}/\\d{2}/\\d{2}"));
        }

        @Test
        @DisplayName("日期格式 yyyyMMdd")
        void dateTime_ShouldReturnCompactFormat() {
            String result = DateUtils.dateTime();
            assertNotNull(result);
            assertTrue(result.matches("\\d{8}"));
        }
    }

    @Nested
    @DisplayName("parseDate 通用解析测试")
    class ParseDateTest {
        @Test
        @DisplayName("解析yyyy-MM-dd格式")
        void parseDate_WhenYYYYMMDD_ShouldReturnDate() {
            Date result = DateUtils.parseDate("2024-01-15");
            assertNotNull(result);
        }

        @Test
        @DisplayName("解析yyyy/MM/dd格式")
        void parseDate_WhenSlashFormat_ShouldReturnDate() {
            Date result = DateUtils.parseDate("2024/01/15");
            assertNotNull(result);
        }

        @Test
        @DisplayName("解析yyyy.MM.dd格式")
        void parseDate_WhenDotFormat_ShouldReturnDate() {
            Date result = DateUtils.parseDate("2024.01.15");
            assertNotNull(result);
        }

        @Test
        @DisplayName("解析yyyy-MM-dd HH:mm:ss格式")
        void parseDate_WhenFullFormat_ShouldReturnDate() {
            Date result = DateUtils.parseDate("2024-01-15 10:30:45");
            assertNotNull(result);
        }

        @Test
        @DisplayName("当传入null时返回null")
        void parseDate_WhenNull_ShouldReturnNull() {
            Date result = DateUtils.parseDate(null);
            assertNull(result);
        }

        @Test
        @DisplayName("当格式不正确时返回null")
        void parseDate_WhenInvalidFormat_ShouldReturnNull() {
            Date result = DateUtils.parseDate("invalid-date");
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("differentDaysByMillisecond 相差天数测试")
    class DifferentDaysTest {
        @Test
        @DisplayName("计算相差天数")
        void differentDaysByMillisecond_ShouldReturnCorrectDays() throws ParseException {
            Date date1 = DateUtils.parseDate("2024-01-15");
            Date date2 = DateUtils.parseDate("2024-01-20");
            int result = DateUtils.differentDaysByMillisecond(date1, date2);
            assertEquals(5, result);
        }

        @Test
        @DisplayName("相差天数为0")
        void differentDaysByMillisecond_WhenSameDay_ShouldReturnZero() throws ParseException {
            Date date1 = DateUtils.parseDate("2024-01-15");
            Date date2 = DateUtils.parseDate("2024-01-15");
            int result = DateUtils.differentDaysByMillisecond(date1, date2);
            assertEquals(0, result);
        }

        @Test
        @DisplayName("反向计算也返回正数")
        void differentDaysByMillisecond_WhenReverseOrder_ShouldReturnPositive() throws ParseException {
            Date date1 = DateUtils.parseDate("2024-01-20");
            Date date2 = DateUtils.parseDate("2024-01-15");
            int result = DateUtils.differentDaysByMillisecond(date1, date2);
            assertEquals(5, result);
        }
    }

    @Nested
    @DisplayName("timeDistance 时间差测试")
    class TimeDistanceTest {
        @Test
        @DisplayName("计算时间差")
        void timeDistance_ShouldReturnFormattedString() throws ParseException {
            Date startTime = DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", "2024-01-15 10:00:00");
            Date endTime = DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", "2024-01-16 12:30:00");
            String result = DateUtils.timeDistance(endTime, startTime);
            assertTrue(result.contains("1天"));
            assertTrue(result.contains("2小时"));
            assertTrue(result.contains("30分钟"));
        }
    }

    @Nested
    @DisplayName("toDate LocalDateTime/LocalDate转换测试")
    class ToDateTest {
        @Test
        @DisplayName("LocalDateTime转Date")
        void toDate_WhenLocalDateTime_ShouldReturnDate() {
            LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
            Date result = DateUtils.toDate(localDateTime);
            assertNotNull(result);
        }

        @Test
        @DisplayName("LocalDate转Date")
        void toDate_WhenLocalDate_ShouldReturnDate() {
            LocalDate localDate = LocalDate.of(2024, 1, 15);
            Date result = DateUtils.toDate(localDate);
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("getServerStartDate 服务器启动时间测试")
    class GetServerStartDateTest {
        @Test
        @DisplayName("获取服务器启动时间")
        void getServerStartDate_ShouldReturnValidDate() {
            Date result = DateUtils.getServerStartDate();
            assertNotNull(result);
            assertTrue(result.getTime() > 0);
            assertTrue(result.getTime() <= System.currentTimeMillis());
        }
    }
}

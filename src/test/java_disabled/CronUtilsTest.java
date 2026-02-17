package com.zjjh.fdtemp.common.utils.quartz;

import com.zjjh.fdtemp.common.utils.CronUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CronUtils 工具类单元测试
 */
@DisplayName("CronUtils 工具类测试")
class CronUtilsTest {

    @Test
    @DisplayName("测试 isValid - 有效的 cron 表达式")
    void testIsValid_ValidCron() {
        assertTrue(CronUtils.isValid("0 0 12 * * ?"));
        assertTrue(CronUtils.isValid("0 0/5 * * * ?"));
        assertTrue(CronUtils.isValid("0 0 12 * * ? 2025"));
        assertTrue(CronUtils.isValid("0 15 10 ? * MON-FRI"));
        assertTrue(CronUtils.isValid("0 0 0 1 1 ?"));
    }

    @Test
    @DisplayName("测试 isValid - 无效的 cron 表达式")
    void testIsValid_InvalidCron() {
        assertFalse(CronUtils.isValid("invalid"));
        assertFalse(CronUtils.isValid("0 0 0 0 0 ?"));
        assertFalse(CronUtils.isValid(""));
        assertFalse(CronUtils.isValid("0 0 12 * * * *")); // 字段太多
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("测试 isValid - null 值")
    void testIsValid_Null(String cron) {
        assertFalse(CronUtils.isValid(cron));
    }

    @Test
    @DisplayName("测试 getInvalidMessage - 有效的 cron 表达式返回 null")
    void testGetInvalidMessage_ValidCron() {
        assertNull(CronUtils.getInvalidMessage("0 0 12 * * ?"));
        assertNull(CronUtils.getInvalidMessage("0 0/5 * * * ?"));
    }

    @Test
    @DisplayName("测试 getInvalidMessage - 无效的 cron 表达式返回错误信息")
    void testGetInvalidMessage_InvalidCron() {
        String message = CronUtils.getInvalidMessage("invalid cron");
        assertNotNull(message);
        assertTrue(message.length() > 0);
    }

    @Test
    @DisplayName("测试 getNextExecution - 有效的 cron 表达式")
    void testGetNextExecution_ValidCron() {
        Date now = new Date();
        Date nextTime = CronUtils.getNextExecution("0 0/5 * * * ?");

        assertNotNull(nextTime);
        assertTrue(nextTime.after(now) || nextTime.equals(now));
    }

    @Test
    @DisplayName("测试 getNextExecution - 每天中午12点执行")
    void testGetNextExecution_DailyNoon() {
        Date nextTime = CronUtils.getNextExecution("0 0 12 * * ?");

        assertNotNull(nextTime);
        // 验证时间是12点
        assertEquals(12, nextTime.getHours());
        assertEquals(0, nextTime.getMinutes());
        assertEquals(0, nextTime.getSeconds());
    }

    @Test
    @DisplayName("测试 getNextExecution - 无效的 cron 表达式抛出异常")
    void testGetNextExecution_InvalidCron() {
        assertThrows(IllegalArgumentException.class, () -> {
            CronUtils.getNextExecution("invalid");
        });
    }

    @Test
    @DisplayName("测试 getRecentTriggerTime - 获取近10次执行时间")
    void testGetRecentTriggerTime_ValidCron() {
        List<String> times = CronUtils.getRecentTriggerTime("0 0/5 * * * ?");

        assertNotNull(times);
        assertEquals(10, times.size());

        // 验证每个时间字符串都是有效格式
        for (String time : times) {
            assertNotNull(time);
            assertTrue(time.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
        }
    }

    @Test
    @DisplayName("测试 getRecentTriggerTime - 每小时执行")
    void testGetRecentTriggerTime_Hourly() {
        List<String> times = CronUtils.getRecentTriggerTime("0 0 * * * ?");

        assertNotNull(times);
        assertEquals(10, times.size());
    }

    @Test
    @DisplayName("测试 getRecentTriggerTime - 无效 cron 返回 null")
    void testGetRecentTriggerTime_InvalidCron() {
        List<String> times = CronUtils.getRecentTriggerTime("invalid");

        assertNull(times);
    }

    @Test
    @DisplayName("测试 getRecentTriggerTime - 复杂 cron 表达式")
    void testGetRecentTriggerTime_ComplexCron() {
        // 每周一到周五上午10:15执行
        List<String> times = CronUtils.getRecentTriggerTime("0 15 10 ? * MON-FRI");

        assertNotNull(times);
        assertEquals(10, times.size());
    }

    @Test
    @DisplayName("测试 cron 表达式边界情况 - 每秒执行")
    void testEverySecondCron() {
        assertTrue(CronUtils.isValid("* * * * * ?"));
        Date nextTime = CronUtils.getNextExecution("* * * * * ?");
        assertNotNull(nextTime);
    }

    @Test
    @DisplayName("测试 cron 表达式边界情况 - 每年执行")
    void testYearlyCron() {
        // 每年1月1日凌晨执行
        assertTrue(CronUtils.isValid("0 0 0 1 1 ?"));
        Date nextTime = CronUtils.getNextExecution("0 0 0 1 1 ?");
        assertNotNull(nextTime);
    }

    @Test
    @DisplayName("测试 cron 表达式 - 带年份")
    void testCronWithYear() {
        assertTrue(CronUtils.isValid("0 0 12 * * ? 2025"));
        Date nextTime = CronUtils.getNextExecution("0 0 12 * * ? 2025");
        // 如果2025年已过，nextTime可能为null或过去时间
        // 这个测试主要验证不抛异常
    }

    @Test
    @DisplayName("测试 cron 表达式 - L (最后一天)")
    void testCronWithLastDayOfMonth() {
        // 每月最后一天中午12点执行
        assertTrue(CronUtils.isValid("0 0 12 L * ?"));
    }

    @Test
    @DisplayName("测试 cron 表达式 - W (工作日)")
    void testCronWithWeekday() {
        // 每月15日最近的工作日中午12点执行
        assertTrue(CronUtils.isValid("0 0 12 15W * ?"));
    }

    @Test
    @DisplayName("测试 cron 表达式 - # (第几个周几)")
    void testCronWithNthDayOfWeek() {
        // 每月第二个周二的 noon
        assertTrue(CronUtils.isValid("0 0 12 ? * 2#2"));
    }

    @Test
    @DisplayName("测试 getInvalidMessage - 空字符串")
    void testGetInvalidMessage_EmptyString() {
        String message = CronUtils.getInvalidMessage("");
        assertNotNull(message);
    }

    @Test
    @DisplayName("测试 getInvalidMessage - null")
    void testGetInvalidMessage_Null() {
        String message = CronUtils.getInvalidMessage(null);
        assertNotNull(message);
    }
}

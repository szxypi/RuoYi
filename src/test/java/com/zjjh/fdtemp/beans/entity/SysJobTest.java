package com.zjjh.fdtemp.beans.entity;

import com.zjjh.fdtemp.constants.ScheduleConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysJob 实体类单元测试
 */
@DisplayName("SysJob 实体类测试")
class SysJobTest {

    @Test
    @DisplayName("创建 SysJob 并设置基本属性")
    void testCreateAndSetBasicProperties() {
        SysJob job = new SysJob();
        job.setId("test-id-001");
        job.setJobName("测试任务");
        job.setJobGroup("DEFAULT");
        job.setInvokeTarget("testService.testMethod()");
        job.setCronExpression("0 0/10 * * * ?");
        job.setMisfirePolicy(ScheduleConstants.MISFIRE_DEFAULT);
        job.setConcurrent("0");
        job.setStatus("0");

        assertEquals("test-id-001", job.getId());
        assertEquals("测试任务", job.getJobName());
        assertEquals("DEFAULT", job.getJobGroup());
        assertEquals("testService.testMethod()", job.getInvokeTarget());
        assertEquals("0 0/10 * * * ?", job.getCronExpression());
        assertEquals(ScheduleConstants.MISFIRE_DEFAULT, job.getMisfirePolicy());
        assertEquals("0", job.getConcurrent());
        assertEquals("0", job.getStatus());
    }

    @Test
    @DisplayName("测试默认 misfirePolicy 值")
    void testDefaultMisfirePolicy() {
        SysJob job = new SysJob();
        assertEquals(ScheduleConstants.MISFIRE_DEFAULT, job.getMisfirePolicy());
    }

    @Test
    @DisplayName("测试 getNextValidTime - 有效的 cron 表达式")
    void testGetNextValidTime_ValidCron() {
        SysJob job = new SysJob();
        job.setCronExpression("0 0/10 * * * ?");

        Date nextTime = job.getNextValidTime();
        assertNotNull(nextTime);
        assertTrue(nextTime.after(new Date()));
    }

    @Test
    @DisplayName("测试 getNextValidTime - 空 cron 表达式")
    void testGetNextValidTime_EmptyCron() {
        SysJob job = new SysJob();
        job.setCronExpression(null);

        Date nextTime = job.getNextValidTime();
        assertNull(nextTime);
    }

    @Test
    @DisplayName("测试 getNextValidTime - 空字符串 cron 表达式")
    void testGetNextValidTime_EmptyStringCron() {
        SysJob job = new SysJob();
        job.setCronExpression("");

        Date nextTime = job.getNextValidTime();
        assertNull(nextTime);
    }

    @Test
    @DisplayName("测试 getNextValidTime - 无效 cron 表达式")
    void testGetNextValidTime_InvalidCron() {
        SysJob job = new SysJob();
        job.setCronExpression("invalid cron");

        assertThrows(IllegalArgumentException.class, job::getNextValidTime);
    }

    @Test
    @DisplayName("测试 toString 方法")
    void testToString() {
        SysJob job = new SysJob();
        job.setId("test-id");
        job.setJobName("测试任务");
        job.setJobGroup("TEST_GROUP");
        job.setCronExpression("0 0 12 * * ?");
        job.setMisfirePolicy("1");
        job.setConcurrent("1");
        job.setStatus("0");

        String result = job.toString();

        assertNotNull(result);
        assertTrue(result.contains("test-id"));
        assertTrue(result.contains("测试任务"));
        assertTrue(result.contains("TEST_GROUP"));
    }

    @Test
    @DisplayName("测试继承自 BaseEntity 的属性")
    void testBaseEntityProperties() {
        SysJob job = new SysJob();
        job.setCreateUser("admin");
        job.setCreateUserNickname("管理员");
        job.setUpdateUser("user1");
        job.setUpdateUserNickname("用户1");
        job.setAttr1("额外信息");

        assertEquals("admin", job.getCreateUser());
        assertEquals("管理员", job.getCreateUserNickname());
        assertEquals("user1", job.getUpdateUser());
        assertEquals("用户1", job.getUpdateUserNickname());
        assertEquals("额外信息", job.getAttr1());
    }

    @Test
    @DisplayName("测试所有 misfirePolicy 类型")
    void testAllMisfirePolicies() {
        SysJob job = new SysJob();

        job.setMisfirePolicy(ScheduleConstants.MISFIRE_DEFAULT);
        assertEquals(ScheduleConstants.MISFIRE_DEFAULT, job.getMisfirePolicy());

        job.setMisfirePolicy(ScheduleConstants.MISFIRE_IGNORE_MISFIRES);
        assertEquals(ScheduleConstants.MISFIRE_IGNORE_MISFIRES, job.getMisfirePolicy());

        job.setMisfirePolicy(ScheduleConstants.MISFIRE_FIRE_AND_PROCEED);
        assertEquals(ScheduleConstants.MISFIRE_FIRE_AND_PROCEED, job.getMisfirePolicy());

        job.setMisfirePolicy(ScheduleConstants.MISFIRE_DO_NOTHING);
        assertEquals(ScheduleConstants.MISFIRE_DO_NOTHING, job.getMisfirePolicy());
    }

    @Test
    @DisplayName("测试 Status 枚举值")
    void testStatusEnumValues() {
        assertEquals("0", ScheduleConstants.Status.NORMAL.getValue());
        assertEquals("1", ScheduleConstants.Status.PAUSE.getValue());
    }

    @Test
    @DisplayName("测试并发执行设置")
    void testConcurrentSettings() {
        SysJob job = new SysJob();

        // 允许并发
        job.setConcurrent("0");
        assertEquals("0", job.getConcurrent());

        // 禁止并发
        job.setConcurrent("1");
        assertEquals("1", job.getConcurrent());
    }

    @Test
    @DisplayName("测试任务状态设置")
    void testStatusSettings() {
        SysJob job = new SysJob();

        // 正常状态
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        assertEquals("0", job.getStatus());

        // 暂停状态
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        assertEquals("1", job.getStatus());
    }
}

package com.zjjh.fdtemp.common.utils.quartz;

import com.zjjh.fdtemp.beans.entity.SysJob;
import com.zjjh.fdtemp.common.exception.job.TaskException;
import com.zjjh.fdtemp.constants.ScheduleConstants;
import com.zjjh.fdtemp.common.utils.ScheduleUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ScheduleUtils 工具类单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ScheduleUtils 工具类测试")
class ScheduleUtilsTest {

    @Mock
    private Scheduler scheduler;

    private SysJob testJob;

    @BeforeEach
    void setUp() {
        testJob = new SysJob();
        testJob.setId("test-job-001");
        testJob.setJobName("测试任务");
        testJob.setJobGroup("DEFAULT");
        testJob.setInvokeTarget("testService.testMethod()");
        testJob.setCronExpression("0 0/5 * * * ?");
        testJob.setMisfirePolicy(ScheduleConstants.MISFIRE_DEFAULT);
        testJob.setConcurrent("0");
        testJob.setStatus(ScheduleConstants.Status.NORMAL.getValue());
    }

    @Test
    @DisplayName("测试 getTriggerKey")
    void testGetTriggerKey() {
        TriggerKey triggerKey = ScheduleUtils.getTriggerKey("123", "DEFAULT");

        assertNotNull(triggerKey);
        assertEquals("TASK_CLASS_NAME123", triggerKey.getName());
        assertEquals("DEFAULT", triggerKey.getGroup());
    }

    @Test
    @DisplayName("测试 getJobKey")
    void testGetJobKey() {
        JobKey jobKey = ScheduleUtils.getJobKey("123", "DEFAULT");

        assertNotNull(jobKey);
        assertEquals("TASK_CLASS_NAME123", jobKey.getName());
        assertEquals("DEFAULT", jobKey.getGroup());
    }

    @Test
    @DisplayName("测试 createScheduleJob - 正常创建任务")
    void testCreateScheduleJob_Success() throws Exception {
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenReturn(new Date());

        assertDoesNotThrow(() -> ScheduleUtils.createScheduleJob(scheduler, testJob));

        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    @DisplayName("测试 createScheduleJob - 任务已存在，先删除再创建")
    void testCreateScheduleJob_JobExists() throws Exception {
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);
        when(scheduler.deleteJob(any(JobKey.class))).thenReturn(true);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenReturn(new Date());

        assertDoesNotThrow(() -> ScheduleUtils.createScheduleJob(scheduler, testJob));

        verify(scheduler).deleteJob(any(JobKey.class));
        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    @DisplayName("测试 createScheduleJob - 暂停状态的任务")
    void testCreateScheduleJob_PausedJob() throws Exception {
        testJob.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenReturn(new Date());

        assertDoesNotThrow(() -> ScheduleUtils.createScheduleJob(scheduler, testJob));

        verify(scheduler).pauseJob(any(JobKey.class));
    }

    @Test
    @DisplayName("测试 createScheduleJob - 正常状态的任务不暂停")
    void testCreateScheduleJob_NormalStatusJob() throws Exception {
        testJob.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenReturn(new Date());

        assertDoesNotThrow(() -> ScheduleUtils.createScheduleJob(scheduler, testJob));

        verify(scheduler, never()).pauseJob(any(JobKey.class));
    }

    @Test
    @DisplayName("测试 handleCronScheduleMisfirePolicy - 默认策略")
    void testHandleCronScheduleMisfirePolicy_Default() throws TaskException {
        CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule("0 0/5 * * * ?");

        CronScheduleBuilder result = ScheduleUtils.handleCronScheduleMisfirePolicy(testJob, builder);

        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    @DisplayName("测试 handleCronScheduleMisfirePolicy - 立即触发执行")
    void testHandleCronScheduleMisfirePolicy_IgnoreMisfires() throws TaskException {
        testJob.setMisfirePolicy(ScheduleConstants.MISFIRE_IGNORE_MISFIRES);
        CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule("0 0/5 * * * ?");

        CronScheduleBuilder result = ScheduleUtils.handleCronScheduleMisfirePolicy(testJob, builder);

        assertNotNull(result);
    }

    @Test
    @DisplayName("测试 handleCronScheduleMisfirePolicy - 触发一次执行")
    void testHandleCronScheduleMisfirePolicy_FireAndProceed() throws TaskException {
        testJob.setMisfirePolicy(ScheduleConstants.MISFIRE_FIRE_AND_PROCEED);
        CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule("0 0/5 * * * ?");

        CronScheduleBuilder result = ScheduleUtils.handleCronScheduleMisfirePolicy(testJob, builder);

        assertNotNull(result);
    }

    @Test
    @DisplayName("测试 handleCronScheduleMisfirePolicy - 不触发立即执行")
    void testHandleCronScheduleMisfirePolicy_DoNothing() throws TaskException {
        testJob.setMisfirePolicy(ScheduleConstants.MISFIRE_DO_NOTHING);
        CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule("0 0/5 * * * ?");

        CronScheduleBuilder result = ScheduleUtils.handleCronScheduleMisfirePolicy(testJob, builder);

        assertNotNull(result);
    }

    @Test
    @DisplayName("测试 handleCronScheduleMisfirePolicy - 无效策略抛出异常")
    void testHandleCronScheduleMisfirePolicy_InvalidPolicy() {
        testJob.setMisfirePolicy("99");
        CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule("0 0/5 * * * ?");

        assertThrows(TaskException.class, () -> {
            ScheduleUtils.handleCronScheduleMisfirePolicy(testJob, builder);
        });
    }

    @Test
    @DisplayName("测试 JobKey 和 TriggerKey 一致性")
    void testJobKeyAndTriggerKeyConsistency() {
        String jobId = "123";
        String jobGroup = "TEST_GROUP";

        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
        TriggerKey triggerKey = ScheduleUtils.getTriggerKey(jobId, jobGroup);

        assertEquals(jobKey.getName(), triggerKey.getName());
        assertEquals(jobKey.getGroup(), triggerKey.getGroup());
    }

    @Test
    @DisplayName("测试不同组名的 JobKey")
    void testJobKeyWithDifferentGroups() {
        JobKey key1 = ScheduleUtils.getJobKey("1", "GROUP_A");
        JobKey key2 = ScheduleUtils.getJobKey("1", "GROUP_B");

        assertEquals(key1.getName(), key2.getName());
        assertNotEquals(key1.getGroup(), key2.getGroup());
    }

    @Test
    @DisplayName("测试并发执行设置 - 允许并发")
    void testConcurrentExecution_Allowed() throws Exception {
        testJob.setConcurrent("0"); // 允许并发
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenReturn(new Date());

        ScheduleUtils.createScheduleJob(scheduler, testJob);

        // 验证任务被创建
        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    @DisplayName("测试并发执行设置 - 禁止并发")
    void testConcurrentExecution_Disallowed() throws Exception {
        testJob.setConcurrent("1"); // 禁止并发
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenReturn(new Date());

        ScheduleUtils.createScheduleJob(scheduler, testJob);

        // 验证任务被创建
        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    @DisplayName("测试 TaskException 的 Code")
    void testTaskExceptionCode() {
        TaskException ex = new TaskException("Test error", TaskException.Code.CONFIG_ERROR);

        assertEquals(TaskException.Code.CONFIG_ERROR, ex.getCode());
        assertEquals("Test error", ex.getMessage());
    }

    @Test
    @DisplayName("测试 TaskException 带嵌套异常")
    void testTaskExceptionWithNestedException() {
        Exception nested = new RuntimeException("Nested exception");
        TaskException ex = new TaskException("Test error", TaskException.Code.UNKNOWN, nested);

        assertEquals(TaskException.Code.UNKNOWN, ex.getCode());
        assertEquals("Test error", ex.getMessage());
        assertEquals(nested, ex.getCause());
    }

    @Test
    @DisplayName("测试所有 TaskException.Code 枚举值")
    void testAllTaskExceptionCodes() {
        TaskException.Code[] codes = TaskException.Code.values();

        assertTrue(codes.length > 0);
        assertNotNull(TaskException.Code.valueOf("TASK_EXISTS"));
        assertNotNull(TaskException.Code.valueOf("NO_TASK_EXISTS"));
        assertNotNull(TaskException.Code.valueOf("TASK_ALREADY_STARTED"));
        assertNotNull(TaskException.Code.valueOf("UNKNOWN"));
        assertNotNull(TaskException.Code.valueOf("CONFIG_ERROR"));
        assertNotNull(TaskException.Code.valueOf("TASK_NODE_NOT_AVAILABLE"));
    }
}

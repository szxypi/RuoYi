package com.zjjh.fdtemp.common.utils.quartz;

import com.zjjh.fdtemp.beans.entity.SysJob;
import com.zjjh.fdtemp.beans.entity.SysJobLog;
import com.zjjh.fdtemp.common.utils.AbstractQuartzJob;
import com.zjjh.fdtemp.constants.Constants;
import com.zjjh.fdtemp.constants.ScheduleConstants;
import com.zjjh.fdtemp.service.SysJobLogService;
import com.zjjh.fdtemp.common.utils.spring.SpringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AbstractQuartzJob 抽象类单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AbstractQuartzJob 抽象类测试")
class AbstractQuartzJobTest {

    @Mock
    private JobExecutionContext context;

    @Mock
    private SysJobLogService jobLogService;

    private SysJob testJob;
    private TestQuartzJob testQuartzJob;

    // 测试用的具体实现类
    private static class TestQuartzJob extends AbstractQuartzJob {
        private boolean doExecuteCalled = false;
        private Exception exceptionToThrow = null;

        @Override
        protected void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception {
            doExecuteCalled = true;
            if (exceptionToThrow != null) {
                throw exceptionToThrow;
            }
        }

        public void setExceptionToThrow(Exception exception) {
            this.exceptionToThrow = exception;
        }

        public boolean isDoExecuteCalled() {
            return doExecuteCalled;
        }

        public void reset() {
            doExecuteCalled = false;
            exceptionToThrow = null;
        }
    }

    @BeforeEach
    void setUp() {
        testJob = new SysJob();
        testJob.setId("test-job-001");
        testJob.setJobName("测试任务");
        testJob.setJobGroup("DEFAULT");
        testJob.setInvokeTarget("testService.testMethod()");
        testJob.setCronExpression("0 0/5 * * * ?");
        testJob.setStatus(ScheduleConstants.Status.NORMAL.getValue());

        testQuartzJob = new TestQuartzJob();
    }

    @Test
    @DisplayName("测试 execute - 正常执行流程")
    void testExecute_NormalFlow() {
        // 准备数据
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES, testJob);

        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);

        // 使用 MockedStatic 来模拟 SpringUtils.getBean
        try (MockedStatic<SpringUtils> springUtilsMock = mockStatic(SpringUtils.class)) {
            springUtilsMock.when(() -> SpringUtils.getBean(SysJobLogService.class))
                    .thenReturn(jobLogService);

            // 执行
            testQuartzJob.execute(context);

            // 验证
            assertTrue(testQuartzJob.isDoExecuteCalled());
            verify(jobLogService).addJobLog(any(SysJobLog.class));
        }
    }

    @Test
    @DisplayName("测试 execute - 执行异常情况")
    void testExecute_WithException() {
        // 准备数据
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES, testJob);

        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);
        testQuartzJob.setExceptionToThrow(new RuntimeException("Test exception"));

        try (MockedStatic<SpringUtils> springUtilsMock = mockStatic(SpringUtils.class)) {
            springUtilsMock.when(() -> SpringUtils.getBean(SysJobLogService.class))
                    .thenReturn(jobLogService);

            // 执行 - 不应该抛出异常
            assertDoesNotThrow(() -> testQuartzJob.execute(context));

            // 验证
            assertTrue(testQuartzJob.isDoExecuteCalled());
            verify(jobLogService).addJobLog(any(SysJobLog.class));
        }
    }

    @Test
    @DisplayName("测试日志记录 - 成功状态")
    void testLogRecording_SuccessStatus() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES, testJob);
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);

        ArgumentCaptor<SysJobLog> logCaptor = ArgumentCaptor.forClass(SysJobLog.class);

        try (MockedStatic<SpringUtils> springUtilsMock = mockStatic(SpringUtils.class)) {
            springUtilsMock.when(() -> SpringUtils.getBean(SysJobLogService.class))
                    .thenReturn(jobLogService);

            testQuartzJob.execute(context);

            verify(jobLogService).addJobLog(logCaptor.capture());

            SysJobLog capturedLog = logCaptor.getValue();
            assertEquals(testJob.getJobName(), capturedLog.getJobName());
            assertEquals(testJob.getJobGroup(), capturedLog.getJobGroup());
            assertEquals(testJob.getInvokeTarget(), capturedLog.getInvokeTarget());
            assertEquals(Constants.SUCCESS, capturedLog.getStatus());
            assertNull(capturedLog.getExceptionInfo());
            assertNotNull(capturedLog.getStartTime());
            assertNotNull(capturedLog.getEndTime());
            assertNotNull(capturedLog.getJobMessage());
            assertTrue(capturedLog.getJobMessage().contains("耗时"));
        }
    }

    @Test
    @DisplayName("测试日志记录 - 失败状态")
    void testLogRecording_FailureStatus() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES, testJob);
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);

        testQuartzJob.setExceptionToThrow(new RuntimeException("Test failure"));

        ArgumentCaptor<SysJobLog> logCaptor = ArgumentCaptor.forClass(SysJobLog.class);

        try (MockedStatic<SpringUtils> springUtilsMock = mockStatic(SpringUtils.class)) {
            springUtilsMock.when(() -> SpringUtils.getBean(SysJobLogService.class))
                    .thenReturn(jobLogService);

            testQuartzJob.execute(context);

            verify(jobLogService).addJobLog(logCaptor.capture());

            SysJobLog capturedLog = logCaptor.getValue();
            assertEquals(Constants.FAIL, capturedLog.getStatus());
            assertNotNull(capturedLog.getExceptionInfo());
            assertTrue(capturedLog.getExceptionInfo().contains("RuntimeException"));
        }
    }

    @Test
    @DisplayName("测试日志消息格式")
    void testLogMessageFormat() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES, testJob);
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);

        ArgumentCaptor<SysJobLog> logCaptor = ArgumentCaptor.forClass(SysJobLog.class);

        try (MockedStatic<SpringUtils> springUtilsMock = mockStatic(SpringUtils.class)) {
            springUtilsMock.when(() -> SpringUtils.getBean(SysJobLogService.class))
                    .thenReturn(jobLogService);

            testQuartzJob.execute(context);

            verify(jobLogService).addJobLog(logCaptor.capture());

            SysJobLog capturedLog = logCaptor.getValue();
            assertTrue(capturedLog.getJobMessage().contains(testJob.getJobName()));
            assertTrue(capturedLog.getJobMessage().contains("毫秒"));
        }
    }

    @Test
    @DisplayName("测试执行时间记录")
    void testExecutionTimeRecording() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES, testJob);
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);

        ArgumentCaptor<SysJobLog> logCaptor = ArgumentCaptor.forClass(SysJobLog.class);

        try (MockedStatic<SpringUtils> springUtilsMock = mockStatic(SpringUtils.class)) {
            springUtilsMock.when(() -> SpringUtils.getBean(SysJobLogService.class))
                    .thenReturn(jobLogService);

            testQuartzJob.execute(context);

            verify(jobLogService).addJobLog(logCaptor.capture());

            SysJobLog capturedLog = logCaptor.getValue();
            assertNotNull(capturedLog.getStartTime());
            assertNotNull(capturedLog.getEndTime());

            // 结束时间应该大于或等于开始时间
            assertTrue(capturedLog.getEndTime().compareTo(capturedLog.getStartTime()) >= 0);
        }
    }

    @Test
    @DisplayName("测试 NULL Job 数据处理")
    void testNullJobData() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES, null);
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);

        try (MockedStatic<SpringUtils> springUtilsMock = mockStatic(SpringUtils.class)) {
            springUtilsMock.when(() -> SpringUtils.getBean(SysJobLogService.class))
                    .thenReturn(jobLogService);

            // 执行 - 应该不抛出异常
            assertDoesNotThrow(() -> testQuartzJob.execute(context));
        }
    }

    @Test
    @DisplayName("测试异常信息截断")
    void testExceptionInfoTruncation() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES, testJob);
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);

        // 创建一个带有很长异常信息异常
        String longMessage = "a".repeat(3000);
        testQuartzJob.setExceptionToThrow(new RuntimeException(longMessage));

        ArgumentCaptor<SysJobLog> logCaptor = ArgumentCaptor.forClass(SysJobLog.class);

        try (MockedStatic<SpringUtils> springUtilsMock = mockStatic(SpringUtils.class)) {
            springUtilsMock.when(() -> SpringUtils.getBean(SysJobLogService.class))
                    .thenReturn(jobLogService);

            testQuartzJob.execute(context);

            verify(jobLogService).addJobLog(logCaptor.capture());

            SysJobLog capturedLog = logCaptor.getValue();
            // 异常信息应该被截断到 2000 字符
            assertTrue(capturedLog.getExceptionInfo().length() <= 2000);
        }
    }
}

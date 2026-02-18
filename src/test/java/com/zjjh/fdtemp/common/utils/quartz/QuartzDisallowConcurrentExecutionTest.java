package com.zjjh.fdtemp.common.utils.quartz;

import com.zjjh.fdtemp.beans.entity.SysJob;
import com.zjjh.fdtemp.common.utils.JobInvokeUtil;
import com.zjjh.fdtemp.common.utils.QuartzDisallowConcurrentExecution;
import com.zjjh.fdtemp.common.utils.QuartzJobExecution;
import com.zjjh.fdtemp.common.utils.ScheduleUtils;
import com.zjjh.fdtemp.common.utils.spring.SpringUtils;
import com.zjjh.fdtemp.constants.ScheduleConstants;
import com.zjjh.fdtemp.service.SysJobLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * QuartzDisallowConcurrentExecution 类单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("QuartzDisallowConcurrentExecution 类测试")
class QuartzDisallowConcurrentExecutionTest {

    @Mock
    private JobExecutionContext context;

    @Mock
    private SysJobLogService jobLogService;

    private SysJob testJob;
    private QuartzDisallowConcurrentExecution quartzJob;

    @BeforeEach
    void setUp() {
        testJob = new SysJob();
        testJob.setId("test-job-001");
        testJob.setJobName("禁止并发任务");
        testJob.setJobGroup("DEFAULT");
        testJob.setInvokeTarget("testService.testMethod()");
        testJob.setCronExpression("0 0/5 * * * ?");
        testJob.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        testJob.setConcurrent("1"); // 禁止并发

        quartzJob = new QuartzDisallowConcurrentExecution();
    }

    @Test
    @DisplayName("测试类有 @DisallowConcurrentExecution 注解")
    void testHasDisallowConcurrentExecutionAnnotation() {
        // QuartzDisallowConcurrentExecution 应该有 @DisallowConcurrentExecution 注解
        assertTrue(quartzJob.getClass().isAnnotationPresent(org.quartz.DisallowConcurrentExecution.class));
    }

    @Test
    @DisplayName("测试类继承自 AbstractQuartzJob")
    void testClassHierarchy() {
        assertTrue(com.zjjh.fdtemp.common.utils.AbstractQuartzJob.class.isAssignableFrom(QuartzDisallowConcurrentExecution.class));
    }

    @Test
    @DisplayName("测试 doExecute 调用 JobInvokeUtil.invokeMethod")
    void testDoExecute_CallsInvokeMethod() throws Exception {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES, testJob);
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);

        try (MockedStatic<JobInvokeUtil> jobInvokeUtilMock = mockStatic(JobInvokeUtil.class);
             MockedStatic<SpringUtils> springUtilsMock = mockStatic(SpringUtils.class);
             MockedStatic<ScheduleUtils> scheduleUtilsMock = mockStatic(ScheduleUtils.class)) {

            jobInvokeUtilMock.when(() -> JobInvokeUtil.invokeMethod(any(SysJob.class)))
                    .thenAnswer(invocation -> null);
            springUtilsMock.when(() -> SpringUtils.getBean(SysJobLogService.class))
                    .thenReturn(jobLogService);
            scheduleUtilsMock.when(() -> ScheduleUtils.whiteList(any(String.class)))
                    .thenReturn(true);

            quartzJob.execute(context);

            jobInvokeUtilMock.verify(() -> JobInvokeUtil.invokeMethod(any(SysJob.class)), times(1));
        }
    }

    @Test
    @DisplayName("测试 doExecute 传递正确的任务对象")
    void testDoExecute_PassesCorrectJob() throws Exception {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES, testJob);
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);

        try (MockedStatic<JobInvokeUtil> jobInvokeUtilMock = mockStatic(JobInvokeUtil.class);
             MockedStatic<SpringUtils> springUtilsMock = mockStatic(SpringUtils.class);
             MockedStatic<ScheduleUtils> scheduleUtilsMock = mockStatic(ScheduleUtils.class)) {

            jobInvokeUtilMock.when(() -> JobInvokeUtil.invokeMethod(any(SysJob.class)))
                    .thenAnswer(invocation -> null);
            springUtilsMock.when(() -> SpringUtils.getBean(SysJobLogService.class))
                    .thenReturn(jobLogService);
            scheduleUtilsMock.when(() -> ScheduleUtils.whiteList(any(String.class)))
                    .thenReturn(true);

            quartzJob.execute(context);

            jobInvokeUtilMock.verify(() -> JobInvokeUtil.invokeMethod(argThat(job ->
                    job.getJobName().equals("禁止并发任务") &&
                            "1".equals(job.getConcurrent())
            )));
        }
    }

    @Test
    @DisplayName("测试异常处理")
    void testExceptionHandling() throws Exception {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES, testJob);
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);

        try (MockedStatic<JobInvokeUtil> jobInvokeUtilMock = mockStatic(JobInvokeUtil.class);
             MockedStatic<SpringUtils> springUtilsMock = mockStatic(SpringUtils.class);
             MockedStatic<ScheduleUtils> scheduleUtilsMock = mockStatic(ScheduleUtils.class)) {

            jobInvokeUtilMock.when(() -> JobInvokeUtil.invokeMethod(any(SysJob.class)))
                    .thenThrow(new RuntimeException("Test exception"));
            springUtilsMock.when(() -> SpringUtils.getBean(SysJobLogService.class))
                    .thenReturn(jobLogService);
            scheduleUtilsMock.when(() -> ScheduleUtils.whiteList(any(String.class)))
                    .thenReturn(true);

            // 不应该抛出异常
            assertDoesNotThrow(() -> quartzJob.execute(context));

            jobInvokeUtilMock.verify(() -> JobInvokeUtil.invokeMethod(any(SysJob.class)), times(1));
        }
    }

    @Test
    @DisplayName("测试与 QuartzJobExecution 的区别 - 注解")
    void testDifferenceFromQuartzJobExecution() {
        // QuartzDisallowConcurrentExecution 有禁止并发注解
        assertTrue(QuartzDisallowConcurrentExecution.class.isAnnotationPresent(
                org.quartz.DisallowConcurrentExecution.class));

        // QuartzJobExecution 没有禁止并发注解
        assertFalse(QuartzJobExecution.class.isAnnotationPresent(
                org.quartz.DisallowConcurrentExecution.class));
    }
}

package com.zjjh.fdtemp.common.utils.quartz;

import com.zjjh.fdtemp.beans.entity.SysJob;
import com.zjjh.fdtemp.common.utils.QuartzDisallowConcurrentExecution;
import com.zjjh.fdtemp.common.utils.JobInvokeUtil;
import com.zjjh.fdtemp.constants.ScheduleConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * QuartzDisallowConcurrentExecution 类单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("QuartzDisallowConcurrentExecution 类测试")
class QuartzDisallowConcurrentExecutionTest {

    @Mock
    private JobExecutionContext context;

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

        try (MockedStatic<JobInvokeUtil> jobInvokeUtilMock = mockStatic(JobInvokeUtil.class)) {
            jobInvokeUtilMock.when(() -> JobInvokeUtil.invokeMethod(testJob))
                    .thenAnswer(invocation -> null);

            quartzJob.execute(context);

            jobInvokeUtilMock.verify(() -> JobInvokeUtil.invokeMethod(testJob), times(1));
        }
    }

    @Test
    @DisplayName("测试 doExecute 传递正确的任务对象")
    void testDoExecute_PassesCorrectJob() throws Exception {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES, testJob);
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);

        try (MockedStatic<JobInvokeUtil> jobInvokeUtilMock = mockStatic(JobInvokeUtil.class)) {
            quartzJob.execute(context);

            jobInvokeUtilMock.verify(() -> JobInvokeUtil.invokeMethod(argThat(job ->
                    job.getId().equals("test-job-001") &&
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

        try (MockedStatic<JobInvokeUtil> jobInvokeUtilMock = mockStatic(JobInvokeUtil.class)) {
            jobInvokeUtilMock.when(() -> JobInvokeUtil.invokeMethod(testJob))
                    .thenThrow(new RuntimeException("Test exception"));

            // 不应该抛出异常
            assertDoesNotThrow(() -> quartzJob.execute(context));

            jobInvokeUtilMock.verify(() -> JobInvokeUtil.invokeMethod(testJob), times(1));
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

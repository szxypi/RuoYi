package com.zjjh.fdtemp.common.utils.quartz;

import com.zjjh.fdtemp.beans.entity.SysJob;
import com.zjjh.fdtemp.common.utils.QuartzJobExecution;
import com.zjjh.fdtemp.common.utils.JobInvokeUtil;
import com.zjjh.fdtemp.constants.ScheduleConstants;
import com.zjjh.fdtemp.service.SysJobLogService;
import com.zjjh.fdtemp.common.utils.spring.SpringUtils;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * QuartzJobExecution 类单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("QuartzJobExecution 类测试")
class QuartzJobExecutionTest {

    @Mock
    private JobExecutionContext context;

    @Mock
    private SysJobLogService jobLogService;

    private SysJob testJob;
    private QuartzJobExecution quartzJobExecution;

    @BeforeEach
    void setUp() {
        testJob = new SysJob();
        testJob.setId("test-job-001");
        testJob.setJobName("测试任务");
        testJob.setJobGroup("DEFAULT");
        testJob.setInvokeTarget("testService.testMethod()");
        testJob.setCronExpression("0 0/5 * * * ?");
        testJob.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        testJob.setConcurrent("0"); // 允许并发

        quartzJobExecution = new QuartzJobExecution();
    }

    @Test
    @DisplayName("测试 doExecute 调用 JobInvokeUtil.invokeMethod")
    void testDoExecute_CallsInvokeMethod() throws Exception {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES, testJob);
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);

        try (MockedStatic<JobInvokeUtil> jobInvokeUtilMock = mockStatic(JobInvokeUtil.class);
             MockedStatic<SpringUtils> springUtilsMock = mockStatic(SpringUtils.class)) {

            jobInvokeUtilMock.when(() -> JobInvokeUtil.invokeMethod(any(SysJob.class)))
                    .thenAnswer(invocation -> null);
            springUtilsMock.when(() -> SpringUtils.getBean(SysJobLogService.class))
                    .thenReturn(jobLogService);

            quartzJobExecution.execute(context);

            jobInvokeUtilMock.verify(() -> JobInvokeUtil.invokeMethod(any(SysJob.class)), times(1));
        }
    }

    @Test
    @DisplayName("测试 doExecute 传递正确的上下文和任务对象")
    void testDoExecute_PassesCorrectParameters() throws Exception {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES, testJob);
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);

        try (MockedStatic<JobInvokeUtil> jobInvokeUtilMock = mockStatic(JobInvokeUtil.class);
             MockedStatic<SpringUtils> springUtilsMock = mockStatic(SpringUtils.class)) {

            jobInvokeUtilMock.when(() -> JobInvokeUtil.invokeMethod(any(SysJob.class)))
                    .thenAnswer(invocation -> null);
            springUtilsMock.when(() -> SpringUtils.getBean(SysJobLogService.class))
                    .thenReturn(jobLogService);

            quartzJobExecution.execute(context);

            jobInvokeUtilMock.verify(() -> JobInvokeUtil.invokeMethod(argThat(job ->
                job.getJobName().equals("测试任务") &&
                job.getInvokeTarget().equals("testService.testMethod()")
            )));
        }
    }

    @Test
    @DisplayName("测试 QuartzJobExecution 类结构 - 继承自 AbstractQuartzJob")
    void testClassHierarchy() {
        assertTrue(com.zjjh.fdtemp.common.utils.AbstractQuartzJob.class.isAssignableFrom(QuartzJobExecution.class));
    }

    @Test
    @DisplayName("测试 QuartzJobExecution 没有 @DisallowConcurrentExecution 注解")
    void testNoDisallowConcurrentExecutionAnnotation() {
        // QuartzJobExecution 应该没有 @DisallowConcurrentExecution 注解（允许并发）
        assertFalse(quartzJobExecution.getClass().isAnnotationPresent(org.quartz.DisallowConcurrentExecution.class));
    }

    @Test
    @DisplayName("测试 doExecute 抛出异常时的处理")
    void testDoExecute_WithException() throws Exception {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES, testJob);
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);

        try (MockedStatic<JobInvokeUtil> jobInvokeUtilMock = mockStatic(JobInvokeUtil.class);
             MockedStatic<SpringUtils> springUtilsMock = mockStatic(SpringUtils.class)) {

            jobInvokeUtilMock.when(() -> JobInvokeUtil.invokeMethod(any(SysJob.class)))
                    .thenThrow(new RuntimeException("Test exception"));
            springUtilsMock.when(() -> SpringUtils.getBean(SysJobLogService.class))
                    .thenReturn(jobLogService);

            // 执行 - 不应该抛出异常（由父类捕获并记录日志）
            assertDoesNotThrow(() -> quartzJobExecution.execute(context));

            jobInvokeUtilMock.verify(() -> JobInvokeUtil.invokeMethod(any(SysJob.class)), times(1));
        }
    }

    @Test
    @DisplayName("测试不同 invokeTarget 的任务")
    void testDifferentInvokeTargets() throws Exception {
        testJob.setInvokeTarget("testService.methodWithParams('test', 123, true)");

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES, testJob);
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);

        try (MockedStatic<JobInvokeUtil> jobInvokeUtilMock = mockStatic(JobInvokeUtil.class);
             MockedStatic<SpringUtils> springUtilsMock = mockStatic(SpringUtils.class)) {

            jobInvokeUtilMock.when(() -> JobInvokeUtil.invokeMethod(any(SysJob.class)))
                    .thenAnswer(invocation -> null);
            springUtilsMock.when(() -> SpringUtils.getBean(SysJobLogService.class))
                    .thenReturn(jobLogService);

            quartzJobExecution.execute(context);

            jobInvokeUtilMock.verify(() -> JobInvokeUtil.invokeMethod(argThat(job ->
                job.getInvokeTarget().equals("testService.methodWithParams('test', 123, true)")
            )));
        }
    }

    @Test
    @DisplayName("测试完整类名的调用目标")
    void testFullClassNameInvokeTarget() throws Exception {
        testJob.setInvokeTarget("com.example.service.TestService.testMethod()");

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduleConstants.TASK_PROPERTIES, testJob);
        when(context.getMergedJobDataMap()).thenReturn(jobDataMap);

        try (MockedStatic<JobInvokeUtil> jobInvokeUtilMock = mockStatic(JobInvokeUtil.class);
             MockedStatic<SpringUtils> springUtilsMock = mockStatic(SpringUtils.class)) {

            jobInvokeUtilMock.when(() -> JobInvokeUtil.invokeMethod(any(SysJob.class)))
                    .thenAnswer(invocation -> null);
            springUtilsMock.when(() -> SpringUtils.getBean(SysJobLogService.class))
                    .thenReturn(jobLogService);

            quartzJobExecution.execute(context);

            jobInvokeUtilMock.verify(() -> JobInvokeUtil.invokeMethod(argThat(job ->
                job.getInvokeTarget().equals("com.example.service.TestService.testMethod()")
            )));
        }
    }
}

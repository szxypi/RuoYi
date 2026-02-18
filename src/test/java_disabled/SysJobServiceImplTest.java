package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysJob;
import com.zjjh.fdtemp.common.exception.job.TaskException;
import com.zjjh.fdtemp.constants.ScheduleConstants;
import com.zjjh.fdtemp.dao.SysJobDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SysJobServiceImpl 服务层单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysJobServiceImpl 服务层测试")
class SysJobServiceImplTest {

    @Mock
    private Scheduler scheduler;

    @Mock
    private SysJobDao jobMapper;

    @InjectMocks
    private SysJobServiceImpl jobService;

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
    @DisplayName("测试 selectJobList")
    void testSelectJobList() {
        List<SysJob> expectedList = new ArrayList<>();
        expectedList.add(testJob);

        when(jobMapper.selectJobList(any(SysJob.class))).thenReturn(expectedList);

        List<SysJob> result = jobService.selectJobList(new SysJob());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试任务", result.get(0).getJobName());
        verify(jobMapper).selectJobList(any(SysJob.class));
    }

    @Test
    @DisplayName("测试 selectJobById")
    void testSelectJobById() {
        when(jobMapper.selectJobById("test-job-001")).thenReturn(testJob);

        SysJob result = jobService.selectJobById("test-job-001");

        assertNotNull(result);
        assertEquals("test-job-001", result.getId());
        assertEquals("测试任务", result.getJobName());
        verify(jobMapper).selectJobById("test-job-001");
    }

    @Test
    @DisplayName("测试 pauseJob - 成功")
    void testPauseJob_Success() throws Exception {
        when(jobMapper.updateJob(any(SysJob.class))).thenReturn(1);
        when(scheduler.pauseJob(any(JobKey.class))).thenAnswer(invocation -> null);

        int result = jobService.pauseJob(testJob);

        assertEquals(1, result);
        assertEquals(ScheduleConstants.Status.PAUSE.getValue(), testJob.getStatus());
        verify(jobMapper).updateJob(any(SysJob.class));
        verify(scheduler).pauseJob(any(JobKey.class));
    }

    @Test
    @DisplayName("测试 pauseJob - 数据库更新失败")
    void testPauseJob_DbUpdateFailed() throws Exception {
        when(jobMapper.updateJob(any(SysJob.class))).thenReturn(0);

        int result = jobService.pauseJob(testJob);

        assertEquals(0, result);
        verify(scheduler, never()).pauseJob(any(JobKey.class));
    }

    @Test
    @DisplayName("测试 resumeJob - 成功")
    void testResumeJob_Success() throws Exception {
        when(jobMapper.updateJob(any(SysJob.class))).thenReturn(1);
        when(scheduler.resumeJob(any(JobKey.class))).thenAnswer(invocation -> null);

        int result = jobService.resumeJob(testJob);

        assertEquals(1, result);
        assertEquals(ScheduleConstants.Status.NORMAL.getValue(), testJob.getStatus());
        verify(jobMapper).updateJob(any(SysJob.class));
        verify(scheduler).resumeJob(any(JobKey.class));
    }

    @Test
    @DisplayName("测试 resumeJob - 数据库更新失败")
    void testResumeJob_DbUpdateFailed() throws Exception {
        when(jobMapper.updateJob(any(SysJob.class))).thenReturn(0);

        int result = jobService.resumeJob(testJob);

        assertEquals(0, result);
        verify(scheduler, never()).resumeJob(any(JobKey.class));
    }

    @Test
    @DisplayName("测试 deleteJob - 成功")
    void testDeleteJob_Success() throws Exception {
        when(jobMapper.deleteJobById("test-job-001")).thenReturn(1);
        when(scheduler.deleteJob(any(JobKey.class))).thenReturn(true);

        int result = jobService.deleteJob(testJob);

        assertEquals(1, result);
        verify(jobMapper).deleteJobById("test-job-001");
        verify(scheduler).deleteJob(any(JobKey.class));
    }

    @Test
    @DisplayName("测试 deleteJob - 数据库删除失败")
    void testDeleteJob_DbDeleteFailed() throws Exception {
        when(jobMapper.deleteJobById("test-job-001")).thenReturn(0);

        int result = jobService.deleteJob(testJob);

        assertEquals(0, result);
        verify(scheduler, never()).deleteJob(any(JobKey.class));
    }

    @Test
    @DisplayName("测试 deleteJobByIds")
    void testDeleteJobByIds() throws Exception {
        SysJob job1 = new SysJob();
        job1.setId("1");
        job1.setJobGroup("DEFAULT");

        SysJob job2 = new SysJob();
        job2.setId("2");
        job2.setJobGroup("DEFAULT");

        when(jobMapper.selectJobById("1")).thenReturn(job1);
        when(jobMapper.selectJobById("2")).thenReturn(job2);
        when(jobMapper.deleteJobById(anyString())).thenReturn(1);
        when(scheduler.deleteJob(any(JobKey.class))).thenReturn(true);

        jobService.deleteJobByIds("1,2");

        verify(jobMapper, times(2)).selectJobById(anyString());
        verify(jobMapper, times(2)).deleteJobById(anyString());
        verify(scheduler, times(2)).deleteJob(any(JobKey.class));
    }

    @Test
    @DisplayName("测试 changeStatus - 恢复任务")
    void testChangeStatus_Resume() throws Exception {
        testJob.setStatus(ScheduleConstants.Status.NORMAL.getValue());

        when(jobMapper.updateJob(any(SysJob.class))).thenReturn(1);
        when(scheduler.resumeJob(any(JobKey.class))).thenAnswer(invocation -> null);

        int result = jobService.changeStatus(testJob);

        assertEquals(1, result);
    }

    @Test
    @DisplayName("测试 changeStatus - 暂停任务")
    void testChangeStatus_Pause() throws Exception {
        testJob.setStatus(ScheduleConstants.Status.PAUSE.getValue());

        when(jobMapper.updateJob(any(SysJob.class))).thenReturn(1);
        when(scheduler.pauseJob(any(JobKey.class))).thenAnswer(invocation -> null);

        int result = jobService.changeStatus(testJob);

        assertEquals(1, result);
    }

    @Test
    @DisplayName("测试 run - 任务存在")
    void testRun_JobExists() throws Exception {
        when(jobMapper.selectJobById("test-job-001")).thenReturn(testJob);
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);
        when(scheduler.triggerJob(any(JobKey.class), any(JobDataMap.class))).thenAnswer(invocation -> null);

        boolean result = jobService.run(testJob);

        assertTrue(result);
        verify(scheduler).triggerJob(any(JobKey.class), any(JobDataMap.class));
    }

    @Test
    @DisplayName("测试 run - 任务不存在")
    void testRun_JobNotExists() throws Exception {
        when(jobMapper.selectJobById("test-job-001")).thenReturn(testJob);
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);

        boolean result = jobService.run(testJob);

        assertFalse(result);
        verify(scheduler, never()).triggerJob(any(JobKey.class), any(JobDataMap.class));
    }

    @Test
    @DisplayName("测试 insertJob - 成功")
    void testInsertJob_Success() throws Exception {
        when(jobMapper.insertJob(any(SysJob.class))).thenReturn(1);
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenReturn(new java.util.Date());

        int result = jobService.insertJob(testJob);

        assertEquals(1, result);
        assertEquals(ScheduleConstants.Status.PAUSE.getValue(), testJob.getStatus());
        verify(jobMapper).insertJob(any(SysJob.class));
    }

    @Test
    @DisplayName("测试 insertJob - 数据库插入失败")
    void testInsertJob_DbInsertFailed() throws Exception {
        when(jobMapper.insertJob(any(SysJob.class))).thenReturn(0);

        int result = jobService.insertJob(testJob);

        assertEquals(0, result);
        verify(scheduler, never()).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    @DisplayName("测试 updateJob - 成功")
    void testUpdateJob_Success() throws Exception {
        SysJob oldJob = new SysJob();
        oldJob.setId("test-job-001");
        oldJob.setJobGroup("DEFAULT");

        when(jobMapper.selectJobById("test-job-001")).thenReturn(oldJob);
        when(jobMapper.updateJob(any(SysJob.class))).thenReturn(1);
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);
        when(scheduler.deleteJob(any(JobKey.class))).thenReturn(true);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenReturn(new java.util.Date());

        int result = jobService.updateJob(testJob);

        assertEquals(1, result);
        verify(jobMapper).updateJob(any(SysJob.class));
    }

    @Test
    @DisplayName("测试 updateJob - 数据库更新失败")
    void testUpdateJob_DbUpdateFailed() throws Exception {
        SysJob oldJob = new SysJob();
        oldJob.setId("test-job-001");
        oldJob.setJobGroup("DEFAULT");

        when(jobMapper.selectJobById("test-job-001")).thenReturn(oldJob);
        when(jobMapper.updateJob(any(SysJob.class))).thenReturn(0);

        int result = jobService.updateJob(testJob);

        assertEquals(0, result);
        verify(scheduler, never()).deleteJob(any(JobKey.class));
    }

    @Test
    @DisplayName("测试 checkCronExpressionIsValid - 有效表达式")
    void testCheckCronExpressionIsValid_Valid() {
        boolean result = jobService.checkCronExpressionIsValid("0 0/5 * * * ?");
        assertTrue(result);
    }

    @Test
    @DisplayName("测试 checkCronExpressionIsValid - 无效表达式")
    void testCheckCronExpressionIsValid_Invalid() {
        boolean result = jobService.checkCronExpressionIsValid("invalid");
        assertFalse(result);
    }

    @Test
    @DisplayName("测试 checkCronExpressionIsValid - null")
    void testCheckCronExpressionIsValid_Null() {
        boolean result = jobService.checkCronExpressionIsValid(null);
        assertFalse(result);
    }

    @Test
    @DisplayName("测试 init - 初始化加载所有任务")
    void testInit() throws Exception {
        List<SysJob> jobList = new ArrayList<>();
        jobList.add(testJob);

        when(jobMapper.selectJobAll()).thenReturn(jobList);
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class))).thenReturn(new java.util.Date());

        jobService.init();

        verify(scheduler).clear();
        verify(jobMapper).selectJobAll();
        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    @DisplayName("测试 SchedulerException 传播")
    void testSchedulerExceptionPropagation() throws Exception {
        when(jobMapper.updateJob(any(SysJob.class))).thenReturn(1);
        doThrow(new SchedulerException("Test exception"))
                .when(scheduler).pauseJob(any(JobKey.class));

        assertThrows(SchedulerException.class, () -> jobService.pauseJob(testJob));
    }
}

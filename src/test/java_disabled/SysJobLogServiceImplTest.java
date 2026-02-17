package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysJobLog;
import com.zjjh.fdtemp.constants.Constants;
import com.zjjh.fdtemp.dao.SysJobLogDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SysJobLogServiceImpl 服务层单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysJobLogServiceImpl 服务层测试")
class SysJobLogServiceImplTest {

    @Mock
    private SysJobLogDao jobLogMapper;

    @InjectMocks
    private SysJobLogServiceImpl jobLogService;

    private SysJobLog testJobLog;

    @BeforeEach
    void setUp() {
        testJobLog = new SysJobLog();
        testJobLog.setId("log-001");
        testJobLog.setJobName("测试任务");
        testJobLog.setJobGroup("DEFAULT");
        testJobLog.setInvokeTarget("testService.testMethod()");
        testJobLog.setJobMessage("执行成功");
        testJobLog.setStatus(Constants.SUCCESS);
        testJobLog.setStartTime(new Date(System.currentTimeMillis() - 1000));
        testJobLog.setEndTime(new Date());
    }

    @Test
    @DisplayName("测试 selectJobLogList")
    void testSelectJobLogList() {
        List<SysJobLog> expectedList = new ArrayList<>();
        expectedList.add(testJobLog);

        when(jobLogMapper.selectJobLogList(any(SysJobLog.class))).thenReturn(expectedList);

        List<SysJobLog> result = jobLogService.selectJobLogList(new SysJobLog());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试任务", result.get(0).getJobName());
        verify(jobLogMapper).selectJobLogList(any(SysJobLog.class));
    }

    @Test
    @DisplayName("测试 selectJobLogList - 按任务名过滤")
    void testSelectJobLogList_FilterByJobName() {
        List<SysJobLog> expectedList = new ArrayList<>();
        expectedList.add(testJobLog);

        when(jobLogMapper.selectJobLogList(any(SysJobLog.class))).thenReturn(expectedList);

        SysJobLog query = new SysJobLog();
        query.setJobName("测试任务");

        List<SysJobLog> result = jobLogService.selectJobLogList(query);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("测试 selectJobLogById")
    void testSelectJobLogById() {
        when(jobLogMapper.selectJobLogById("log-001")).thenReturn(testJobLog);

        SysJobLog result = jobLogService.selectJobLogById("log-001");

        assertNotNull(result);
        assertEquals("log-001", result.getId());
        assertEquals("测试任务", result.getJobName());
        verify(jobLogMapper).selectJobLogById("log-001");
    }

    @Test
    @DisplayName("测试 selectJobLogById - 不存在的记录")
    void testSelectJobLogById_NotFound() {
        when(jobLogMapper.selectJobLogById("non-existent")).thenReturn(null);

        SysJobLog result = jobLogService.selectJobLogById("non-existent");

        assertNull(result);
        verify(jobLogMapper).selectJobLogById("non-existent");
    }

    @Test
    @DisplayName("测试 addJobLog")
    void testAddJobLog() {
        when(jobLogMapper.insertJobLog(any(SysJobLog.class))).thenReturn(1);

        jobLogService.addJobLog(testJobLog);

        verify(jobLogMapper).insertJobLog(any(SysJobLog.class));
    }

    @Test
    @DisplayName("测试 addJobLog - 失败日志")
    void testAddJobLog_FailureLog() {
        testJobLog.setStatus(Constants.FAIL);
        testJobLog.setExceptionInfo("NullPointerException: Test error");

        when(jobLogMapper.insertJobLog(any(SysJobLog.class))).thenReturn(1);

        jobLogService.addJobLog(testJobLog);

        verify(jobLogMapper).insertJobLog(argThat(log ->
            Constants.FAIL.equals(log.getStatus()) &&
            log.getExceptionInfo() != null
        ));
    }

    @Test
    @DisplayName("测试 deleteJobLogByIds")
    void testDeleteJobLogByIds() {
        String[] ids = {"1", "2", "3"};
        when(jobLogMapper.deleteJobLogByIds(ids)).thenReturn(3);

        int result = jobLogService.deleteJobLogByIds("1,2,3");

        assertEquals(3, result);
        verify(jobLogMapper).deleteJobLogByIds(ids);
    }

    @Test
    @DisplayName("测试 deleteJobLogByIds - 空字符串")
    void testDeleteJobLogByIds_EmptyString() {
        when(jobLogMapper.deleteJobLogByIds(any(String[].class))).thenReturn(0);

        int result = jobLogService.deleteJobLogByIds("");

        assertEquals(0, result);
    }

    @Test
    @DisplayName("测试 deleteJobLogByIds - 单个ID")
    void testDeleteJobLogByIds_SingleId() {
        String[] ids = {"1"};
        when(jobLogMapper.deleteJobLogByIds(ids)).thenReturn(1);

        int result = jobLogService.deleteJobLogByIds("1");

        assertEquals(1, result);
        verify(jobLogMapper).deleteJobLogByIds(ids);
    }

    @Test
    @DisplayName("测试 deleteJobLogById")
    void testDeleteJobLogById() {
        when(jobLogMapper.deleteJobLogById("log-001")).thenReturn(1);

        int result = jobLogService.deleteJobLogById("log-001");

        assertEquals(1, result);
        verify(jobLogMapper).deleteJobLogById("log-001");
    }

    @Test
    @DisplayName("测试 deleteJobLogById - 不存在的记录")
    void testDeleteJobLogById_NotFound() {
        when(jobLogMapper.deleteJobLogById("non-existent")).thenReturn(0);

        int result = jobLogService.deleteJobLogById("non-existent");

        assertEquals(0, result);
    }

    @Test
    @DisplayName("测试 cleanJobLog")
    void testCleanJobLog() {
        doNothing().when(jobLogMapper).cleanJobLog();

        jobLogService.cleanJobLog();

        verify(jobLogMapper).cleanJobLog();
    }

    @Test
    @DisplayName("测试完整日志生命周期")
    void testCompleteLogLifecycle() {
        // 创建日志
        when(jobLogMapper.insertJobLog(any(SysJobLog.class))).thenReturn(1);
        jobLogService.addJobLog(testJobLog);
        verify(jobLogMapper).insertJobLog(any(SysJobLog.class));

        // 查询日志
        when(jobLogMapper.selectJobLogById("log-001")).thenReturn(testJobLog);
        SysJobLog retrieved = jobLogService.selectJobLogById("log-001");
        assertNotNull(retrieved);

        // 删除日志
        when(jobLogMapper.deleteJobLogById("log-001")).thenReturn(1);
        int deleted = jobLogService.deleteJobLogById("log-001");
        assertEquals(1, deleted);
    }

    @Test
    @DisplayName("测试批量删除大量日志")
    void testBatchDeleteManyLogs() {
        // 模拟100个ID
        StringBuilder idsBuilder = new StringBuilder();
        for (int i = 1; i <= 100; i++) {
            if (i > 1) idsBuilder.append(",");
            idsBuilder.append(i);
        }
        String idsString = idsBuilder.toString();

        when(jobLogMapper.deleteJobLogByIds(any(String[].class))).thenReturn(100);

        int result = jobLogService.deleteJobLogByIds(idsString);

        assertEquals(100, result);
    }
}

package com.zjjh.fdtemp.beans.entity;

import com.zjjh.fdtemp.constants.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysJobLog 实体类单元测试
 */
@DisplayName("SysJobLog 实体类测试")
class SysJobLogTest {

    @Test
    @DisplayName("创建 SysJobLog 并设置基本属性")
    void testCreateAndSetBasicProperties() {
        SysJobLog jobLog = new SysJobLog();
        jobLog.setId("log-id-001");
        jobLog.setJobName("测试任务");
        jobLog.setJobGroup("DEFAULT");
        jobLog.setInvokeTarget("testService.testMethod()");
        jobLog.setJobMessage("执行成功");
        jobLog.setStatus(Constants.SUCCESS);
        jobLog.setExceptionInfo(null);

        assertEquals("log-id-001", jobLog.getId());
        assertEquals("测试任务", jobLog.getJobName());
        assertEquals("DEFAULT", jobLog.getJobGroup());
        assertEquals("testService.testMethod()", jobLog.getInvokeTarget());
        assertEquals("执行成功", jobLog.getJobMessage());
        assertEquals(Constants.SUCCESS, jobLog.getStatus());
        assertNull(jobLog.getExceptionInfo());
    }

    @Test
    @DisplayName("测试开始和结束时间")
    void testStartAndEndTime() {
        SysJobLog jobLog = new SysJobLog();
        Date startTime = new Date(System.currentTimeMillis() - 1000);
        Date endTime = new Date();

        jobLog.setStartTime(startTime);
        jobLog.setEndTime(endTime);

        assertEquals(startTime, jobLog.getStartTime());
        assertEquals(endTime, jobLog.getEndTime());
        assertTrue(endTime.after(startTime) || endTime.equals(startTime));
    }

    @Test
    @DisplayName("测试成功状态日志")
    void testSuccessLog() {
        SysJobLog jobLog = new SysJobLog();
        jobLog.setJobName("成功任务");
        jobLog.setStatus(Constants.SUCCESS);
        jobLog.setExceptionInfo(null);

        assertEquals(Constants.SUCCESS, jobLog.getStatus());
        assertNull(jobLog.getExceptionInfo());
    }

    @Test
    @DisplayName("测试失败状态日志")
    void testFailureLog() {
        SysJobLog jobLog = new SysJobLog();
        jobLog.setJobName("失败任务");
        jobLog.setStatus(Constants.FAIL);
        jobLog.setExceptionInfo("NullPointerException: xxx");

        assertEquals(Constants.FAIL, jobLog.getStatus());
        assertNotNull(jobLog.getExceptionInfo());
        assertTrue(jobLog.getExceptionInfo().contains("NullPointerException"));
    }

    @Test
    @DisplayName("测试 toString 方法")
    void testToString() {
        SysJobLog jobLog = new SysJobLog();
        jobLog.setId("log-id");
        jobLog.setJobName("测试任务");
        jobLog.setJobGroup("TEST_GROUP");
        jobLog.setJobMessage("执行成功");
        jobLog.setStatus("0");

        String result = jobLog.toString();

        assertNotNull(result);
        assertTrue(result.contains("log-id"));
        assertTrue(result.contains("测试任务"));
        assertTrue(result.contains("TEST_GROUP"));
    }

    @Test
    @DisplayName("测试长异常信息")
    void testLongExceptionInfo() {
        SysJobLog jobLog = new SysJobLog();
        String longException = "a".repeat(3000);
        jobLog.setExceptionInfo(longException);

        assertEquals(longException, jobLog.getExceptionInfo());
    }

    @Test
    @DisplayName("测试空属性值")
    void testNullProperties() {
        SysJobLog jobLog = new SysJobLog();

        assertNull(jobLog.getId());
        assertNull(jobLog.getJobName());
        assertNull(jobLog.getJobGroup());
        assertNull(jobLog.getInvokeTarget());
        assertNull(jobLog.getJobMessage());
        assertNull(jobLog.getStatus());
        assertNull(jobLog.getExceptionInfo());
        assertNull(jobLog.getStartTime());
        assertNull(jobLog.getEndTime());
    }

    @Test
    @DisplayName("测试继承自 BaseEntity 的属性")
    void testBaseEntityProperties() {
        SysJobLog jobLog = new SysJobLog();
        Date createTime = new Date();
        jobLog.setCreateTime(createTime);
        jobLog.setAttr1("额外信息");

        assertEquals(createTime, jobLog.getCreateTime());
        assertEquals("额外信息", jobLog.getAttr1());
    }

    @Test
    @DisplayName("测试完整日志记录场景")
    void testCompleteLogScenario() {
        SysJobLog jobLog = new SysJobLog();

        // 模拟任务执行开始
        Date startTime = new Date();
        jobLog.setStartTime(startTime);
        jobLog.setJobName("数据同步任务");
        jobLog.setJobGroup("SYNC");
        jobLog.setInvokeTarget("syncService.syncData('table1', 1000)");

        // 模拟任务执行结束
        Date endTime = new Date(System.currentTimeMillis() + 500);
        jobLog.setEndTime(endTime);
        jobLog.setJobMessage("数据同步任务 总共耗时：500毫秒");
        jobLog.setStatus(Constants.SUCCESS);

        // 验证
        assertNotNull(jobLog.getStartTime());
        assertNotNull(jobLog.getEndTime());
        assertTrue(jobLog.getEndTime().after(jobLog.getStartTime()) ||
                   jobLog.getEndTime().equals(jobLog.getStartTime()));
        assertEquals(Constants.SUCCESS, jobLog.getStatus());
        assertNull(jobLog.getExceptionInfo());
    }

    @Test
    @DisplayName("测试异常场景日志")
    void testExceptionScenario() {
        SysJobLog jobLog = new SysJobLog();

        Date startTime = new Date();
        jobLog.setStartTime(startTime);
        jobLog.setJobName("异常任务");
        jobLog.setJobGroup("ERROR");

        Date endTime = new Date(System.currentTimeMillis() + 100);
        jobLog.setEndTime(endTime);
        jobLog.setStatus(Constants.FAIL);
        jobLog.setExceptionInfo("java.lang.RuntimeException: Connection timeout");

        assertEquals(Constants.FAIL, jobLog.getStatus());
        assertNotNull(jobLog.getExceptionInfo());
        assertTrue(jobLog.getExceptionInfo().contains("RuntimeException"));
    }
}

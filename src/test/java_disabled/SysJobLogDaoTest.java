package com.zjjh.fdtemp.dao;

import com.zjjh.fdtemp.FdtempApplication;
import com.zjjh.fdtemp.beans.entity.SysJobLog;
import com.zjjh.fdtemp.constants.Constants;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysJobLogDao 数据访问层集成测试
 */
@SpringBootTest(classes = FdtempApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@DisplayName("SysJobLogDao 数据访问层测试")
class SysJobLogDaoTest {

    @Autowired
    private SysJobLogDao jobLogMapper;

    private SysJobLog createTestJobLog() {
        SysJobLog log = new SysJobLog();
        log.setId(UUID.randomUUID().toString().replace("-", ""));
        log.setJobName("测试任务_" + System.currentTimeMillis());
        log.setJobGroup("TEST_GROUP");
        log.setInvokeTarget("testService.testMethod()");
        log.setJobMessage("执行成功，耗时100毫秒");
        log.setStatus(Constants.SUCCESS);
        log.setExceptionInfo(null);
        log.setStartTime(new Date(System.currentTimeMillis() - 100));
        log.setEndTime(new Date());
        return log;
    }

    private SysJobLog createTestJobLogWithException() {
        SysJobLog log = createTestJobLog();
        log.setStatus(Constants.FAIL);
        log.setExceptionInfo("java.lang.RuntimeException: Test exception");
        log.setJobMessage("执行失败");
        return log;
    }

    @Test
    @Order(1)
    @DisplayName("测试 insertJobLog - 插入成功日志")
    void testInsertJobLog_Success() {
        SysJobLog log = createTestJobLog();

        int result = jobLogMapper.insertJobLog(log);

        assertEquals(1, result);

        // 验证插入后的数据
        SysJobLog inserted = jobLogMapper.selectJobLogById(log.getId());
        assertNotNull(inserted);
        assertEquals(log.getJobName(), inserted.getJobName());
        assertEquals(Constants.SUCCESS, inserted.getStatus());
    }

    @Test
    @Order(2)
    @DisplayName("测试 insertJobLog - 插入失败日志")
    void testInsertJobLog_Failure() {
        SysJobLog log = createTestJobLogWithException();

        int result = jobLogMapper.insertJobLog(log);

        assertEquals(1, result);

        SysJobLog inserted = jobLogMapper.selectJobLogById(log.getId());
        assertNotNull(inserted);
        assertEquals(Constants.FAIL, inserted.getStatus());
        assertNotNull(inserted.getExceptionInfo());
    }

    @Test
    @Order(3)
    @DisplayName("测试 selectJobLogById - 查询日志")
    void testSelectJobLogById() {
        SysJobLog log = createTestJobLog();
        jobLogMapper.insertJobLog(log);

        SysJobLog result = jobLogMapper.selectJobLogById(log.getId());

        assertNotNull(result);
        assertEquals(log.getId(), result.getId());
        assertEquals(log.getJobName(), result.getJobName());
    }

    @Test
    @Order(4)
    @DisplayName("测试 selectJobLogById - 不存在的日志")
    void testSelectJobLogById_NotFound() {
        SysJobLog result = jobLogMapper.selectJobLogById("non-existent-id");

        assertNull(result);
    }

    @Test
    @Order(5)
    @DisplayName("测试 selectJobLogList - 查询所有日志")
    void testSelectJobLogList_All() {
        SysJobLog log1 = createTestJobLog();
        jobLogMapper.insertJobLog(log1);

        SysJobLog log2 = createTestJobLog();
        jobLogMapper.insertJobLog(log2);

        List<SysJobLog> result = jobLogMapper.selectJobLogList(new SysJobLog());

        assertNotNull(result);
        assertTrue(result.size() >= 2);
    }

    @Test
    @Order(6)
    @DisplayName("测试 selectJobLogList - 按任务名模糊查询")
    void testSelectJobLogList_ByJobName() {
        SysJobLog log = createTestJobLog();
        log.setJobName("UniqueTaskLog_" + System.currentTimeMillis());
        jobLogMapper.insertJobLog(log);

        SysJobLog query = new SysJobLog();
        query.setJobName("UniqueTaskLog");

        List<SysJobLog> result = jobLogMapper.selectJobLogList(query);

        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    @Order(7)
    @DisplayName("测试 selectJobLogList - 按任务组查询")
    void testSelectJobLogList_ByJobGroup() {
        String uniqueGroup = "LOG_GROUP_" + System.currentTimeMillis();

        SysJobLog log = createTestJobLog();
        log.setJobGroup(uniqueGroup);
        jobLogMapper.insertJobLog(log);

        SysJobLog query = new SysJobLog();
        query.setJobGroup(uniqueGroup);

        List<SysJobLog> result = jobLogMapper.selectJobLogList(query);

        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertEquals(uniqueGroup, result.get(0).getJobGroup());
    }

    @Test
    @Order(8)
    @DisplayName("测试 selectJobLogList - 按状态查询")
    void testSelectJobLogList_ByStatus() {
        SysJobLog log = createTestJobLogWithException();
        jobLogMapper.insertJobLog(log);

        SysJobLog query = new SysJobLog();
        query.setStatus(Constants.FAIL);

        List<SysJobLog> result = jobLogMapper.selectJobLogList(query);

        assertNotNull(result);
        assertTrue(result.stream()
                .allMatch(l -> Constants.FAIL.equals(l.getStatus())));
    }

    @Test
    @Order(9)
    @DisplayName("测试 selectJobLogAll")
    void testSelectJobLogAll() {
        int initialCount = jobLogMapper.selectJobLogAll().size();

        SysJobLog log = createTestJobLog();
        jobLogMapper.insertJobLog(log);

        List<SysJobLog> result = jobLogMapper.selectJobLogAll();

        assertNotNull(result);
        assertEquals(initialCount + 1, result.size());
    }

    @Test
    @Order(10)
    @DisplayName("测试 deleteJobLogById - 删除日志")
    void testDeleteJobLogById() {
        SysJobLog log = createTestJobLog();
        jobLogMapper.insertJobLog(log);

        int result = jobLogMapper.deleteJobLogById(log.getId());

        assertEquals(1, result);

        SysJobLog deleted = jobLogMapper.selectJobLogById(log.getId());
        assertNull(deleted);
    }

    @Test
    @Order(11)
    @DisplayName("测试 deleteJobLogById - 删除不存在的日志")
    void testDeleteJobLogById_NotFound() {
        int result = jobLogMapper.deleteJobLogById("non-existent-id");

        assertEquals(0, result);
    }

    @Test
    @Order(12)
    @DisplayName("测试 deleteJobLogByIds - 批量删除")
    void testDeleteJobLogByIds() {
        SysJobLog log1 = createTestJobLog();
        jobLogMapper.insertJobLog(log1);

        SysJobLog log2 = createTestJobLog();
        jobLogMapper.insertJobLog(log2);

        String[] ids = {log1.getId(), log2.getId()};

        int result = jobLogMapper.deleteJobLogByIds(ids);

        assertEquals(2, result);

        assertNull(jobLogMapper.selectJobLogById(log1.getId()));
        assertNull(jobLogMapper.selectJobLogById(log2.getId()));
    }

    @Test
    @Order(13)
    @DisplayName("测试 cleanJobLog - 清空日志")
    void testCleanJobLog() {
        // 先插入一些日志
        SysJobLog log1 = createTestJobLog();
        jobLogMapper.insertJobLog(log1);

        SysJobLog log2 = createTestJobLog();
        jobLogMapper.insertJobLog(log2);

        // 清空
        jobLogMapper.cleanJobLog();

        // 验证清空后
        List<SysJobLog> result = jobLogMapper.selectJobLogAll();
        assertEquals(0, result.size());
    }

    @Test
    @Order(14)
    @DisplayName("测试完整日志 CRUD 流程")
    void testCompleteCrudFlow() {
        // Create
        SysJobLog log = createTestJobLog();
        log.setJobMessage("CRUD测试日志");
        int inserted = jobLogMapper.insertJobLog(log);
        assertEquals(1, inserted);

        // Read
        SysJobLog read = jobLogMapper.selectJobLogById(log.getId());
        assertNotNull(read);
        assertTrue(read.getJobMessage().contains("CRUD测试日志"));

        // Delete (日志通常不更新，只删除)
        int deleted = jobLogMapper.deleteJobLogById(log.getId());
        assertEquals(1, deleted);

        SysJobLog deletedRead = jobLogMapper.selectJobLogById(log.getId());
        assertNull(deletedRead);
    }

    @Test
    @Order(15)
    @DisplayName("测试长异常信息")
    void testLongExceptionInfo() {
        SysJobLog log = createTestJobLog();
        log.setStatus(Constants.FAIL);
        String longException = "java.lang.RuntimeException: " + "a".repeat(2000);
        log.setExceptionInfo(longException);

        jobLogMapper.insertJobLog(log);

        SysJobLog retrieved = jobLogMapper.selectJobLogById(log.getId());
        assertNotNull(retrieved.getExceptionInfo());
    }

    @Test
    @Order(16)
    @DisplayName("测试按调用目标查询")
    void testSelectJobLogList_ByInvokeTarget() {
        SysJobLog log = createTestJobLog();
        log.setInvokeTarget("uniqueService.uniqueMethod()");
        jobLogMapper.insertJobLog(log);

        SysJobLog query = new SysJobLog();
        query.setInvokeTarget("uniqueService");

        List<SysJobLog> result = jobLogMapper.selectJobLogList(query);

        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    @Order(17)
    @DisplayName("测试日志排序 - 按时间倒序")
    void testSelectJobLogList_Ordering() {
        // 插入多条日志
        for (int i = 0; i < 5; i++) {
            SysJobLog log = createTestJobLog();
            log.setJobName("排序测试_" + i);
            jobLogMapper.insertJobLog(log);
            try {
                Thread.sleep(10); // 确保时间不同
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        List<SysJobLog> result = jobLogMapper.selectJobLogList(new SysJobLog());

        // 验证按时间倒序排列（最新的在前面）
        for (int i = 0; i < result.size() - 1; i++) {
            Date current = result.get(i).getCreateTime();
            Date next = result.get(i + 1).getCreateTime();
            if (current != null && next != null) {
                assertTrue(current.compareTo(next) >= 0,
                        "日志应该按时间倒序排列");
            }
        }
    }
}

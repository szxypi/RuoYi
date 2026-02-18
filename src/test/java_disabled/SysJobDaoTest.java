package com.zjjh.fdtemp.dao;

import com.zjjh.fdtemp.FdtempApplication;
import com.zjjh.fdtemp.TestConfig;
import com.zjjh.fdtemp.beans.entity.SysJob;
import com.zjjh.fdtemp.constants.ScheduleConstants;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysJobDao 数据访问层集成测试
 */
@SpringBootTest(classes = FdtempApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@DisplayName("SysJobDao 数据访问层测试")
class SysJobDaoTest {

    @Autowired
    private SysJobDao jobMapper;

    private SysJob createTestJob() {
        SysJob job = new SysJob();
        job.setId(UUID.randomUUID().toString().replace("-", ""));
        job.setJobName("测试任务_" + System.currentTimeMillis());
        job.setJobGroup("TEST_GROUP");
        job.setInvokeTarget("testService.testMethod()");
        job.setCronExpression("0 0/5 * * * ?");
        job.setMisfirePolicy(ScheduleConstants.MISFIRE_DEFAULT);
        job.setConcurrent("0");
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        job.setCreateUser("test_user");
        return job;
    }

    @Test
    @Order(1)
    @DisplayName("测试 insertJob - 插入新任务")
    void testInsertJob() {
        SysJob job = createTestJob();

        int result = jobMapper.insertJob(job);

        assertEquals(1, result);

        // 验证插入后的数据
        SysJob inserted = jobMapper.selectJobById(job.getId());
        assertNotNull(inserted);
        assertEquals(job.getJobName(), inserted.getJobName());
        assertEquals(job.getJobGroup(), inserted.getJobGroup());
        assertEquals(job.getInvokeTarget(), inserted.getInvokeTarget());
    }

    @Test
    @Order(2)
    @DisplayName("测试 selectJobById - 查询任务")
    void testSelectJobById() {
        SysJob job = createTestJob();
        jobMapper.insertJob(job);

        SysJob result = jobMapper.selectJobById(job.getId());

        assertNotNull(result);
        assertEquals(job.getId(), result.getId());
        assertEquals(job.getJobName(), result.getJobName());
    }

    @Test
    @Order(3)
    @DisplayName("测试 selectJobById - 不存在的任务")
    void testSelectJobById_NotFound() {
        SysJob result = jobMapper.selectJobById("non-existent-id");

        assertNull(result);
    }

    @Test
    @Order(4)
    @DisplayName("测试 selectJobList - 查询所有任务")
    void testSelectJobList_All() {
        SysJob job1 = createTestJob();
        job1.setJobName("任务A");
        jobMapper.insertJob(job1);

        SysJob job2 = createTestJob();
        job2.setJobName("任务B");
        jobMapper.insertJob(job2);

        List<SysJob> result = jobMapper.selectJobList(new SysJob());

        assertNotNull(result);
        assertTrue(result.size() >= 2);
    }

    @Test
    @Order(5)
    @DisplayName("测试 selectJobList - 按任务名模糊查询")
    void testSelectJobList_ByJobName() {
        SysJob job = createTestJob();
        job.setJobName("UniqueTaskName_" + System.currentTimeMillis());
        jobMapper.insertJob(job);

        SysJob query = new SysJob();
        query.setJobName("UniqueTaskName");

        List<SysJob> result = jobMapper.selectJobList(query);

        assertNotNull(result);
        assertTrue(result.size() >= 1);
    }

    @Test
    @Order(6)
    @DisplayName("测试 selectJobList - 按任务组查询")
    void testSelectJobList_ByJobGroup() {
        String uniqueGroup = "GROUP_" + System.currentTimeMillis();

        SysJob job = createTestJob();
        job.setJobGroup(uniqueGroup);
        jobMapper.insertJob(job);

        SysJob query = new SysJob();
        query.setJobGroup(uniqueGroup);

        List<SysJob> result = jobMapper.selectJobList(query);

        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertEquals(uniqueGroup, result.get(0).getJobGroup());
    }

    @Test
    @Order(7)
    @DisplayName("测试 selectJobList - 按状态查询")
    void testSelectJobList_ByStatus() {
        SysJob job = createTestJob();
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        jobMapper.insertJob(job);

        SysJob query = new SysJob();
        query.setStatus(ScheduleConstants.Status.PAUSE.getValue());

        List<SysJob> result = jobMapper.selectJobList(query);

        assertNotNull(result);
        assertTrue(result.stream()
                .allMatch(j -> ScheduleConstants.Status.PAUSE.getValue().equals(j.getStatus())));
    }

    @Test
    @Order(8)
    @DisplayName("测试 selectJobAll")
    void testSelectJobAll() {
        int initialCount = jobMapper.selectJobAll().size();

        SysJob job = createTestJob();
        jobMapper.insertJob(job);

        List<SysJob> result = jobMapper.selectJobAll();

        assertNotNull(result);
        assertEquals(initialCount + 1, result.size());
    }

    @Test
    @Order(9)
    @DisplayName("测试 updateJob - 更新任务")
    void testUpdateJob() {
        SysJob job = createTestJob();
        jobMapper.insertJob(job);

        job.setJobName("更新后的任务名");
        job.setCronExpression("0 0 0 * * ?");
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());

        int result = jobMapper.updateJob(job);

        assertEquals(1, result);

        SysJob updated = jobMapper.selectJobById(job.getId());
        assertEquals("更新后的任务名", updated.getJobName());
        assertEquals("0 0 0 * * ?", updated.getCronExpression());
        assertEquals(ScheduleConstants.Status.PAUSE.getValue(), updated.getStatus());
    }

    @Test
    @Order(10)
    @DisplayName("测试 updateJob - 更新不存在的任务")
    void testUpdateJob_NotFound() {
        SysJob job = createTestJob();
        job.setId("non-existent-id");
        job.setJobName("更新任务");

        int result = jobMapper.updateJob(job);

        assertEquals(0, result);
    }

    @Test
    @Order(11)
    @DisplayName("测试 deleteJobById - 删除任务")
    void testDeleteJobById() {
        SysJob job = createTestJob();
        jobMapper.insertJob(job);

        int result = jobMapper.deleteJobById(job.getId());

        assertEquals(1, result);

        SysJob deleted = jobMapper.selectJobById(job.getId());
        assertNull(deleted);
    }

    @Test
    @Order(12)
    @DisplayName("测试 deleteJobById - 删除不存在的任务")
    void testDeleteJobById_NotFound() {
        int result = jobMapper.deleteJobById("non-existent-id");

        assertEquals(0, result);
    }

    @Test
    @Order(13)
    @DisplayName("测试 deleteJobByIds - 批量删除")
    void testDeleteJobByIds() {
        SysJob job1 = createTestJob();
        jobMapper.insertJob(job1);

        SysJob job2 = createTestJob();
        jobMapper.insertJob(job2);

        String[] ids = {job1.getId(), job2.getId()};

        int result = jobMapper.deleteJobByIds(ids);

        assertEquals(2, result);

        assertNull(jobMapper.selectJobById(job1.getId()));
        assertNull(jobMapper.selectJobById(job2.getId()));
    }

    @Test
    @Order(14)
    @DisplayName("测试完整 CRUD 流程")
    void testCompleteCrudFlow() {
        // Create
        SysJob job = createTestJob();
        job.setJobName("CRUD测试任务");
        int inserted = jobMapper.insertJob(job);
        assertEquals(1, inserted);

        // Read
        SysJob read = jobMapper.selectJobById(job.getId());
        assertNotNull(read);
        assertEquals("CRUD测试任务", read.getJobName());

        // Update
        read.setJobName("更新后的CRUD测试任务");
        int updated = jobMapper.updateJob(read);
        assertEquals(1, updated);

        SysJob updatedRead = jobMapper.selectJobById(job.getId());
        assertEquals("更新后的CRUD测试任务", updatedRead.getJobName());

        // Delete
        int deleted = jobMapper.deleteJobById(job.getId());
        assertEquals(1, deleted);

        SysJob deletedRead = jobMapper.selectJobById(job.getId());
        assertNull(deletedRead);
    }

    @Test
    @Order(15)
    @DisplayName("测试各种 misfirePolicy")
    void testDifferentMisfirePolicies() {
        // 测试各种 misfire 策略
        String[] policies = {
                ScheduleConstants.MISFIRE_DEFAULT,
                ScheduleConstants.MISFIRE_IGNORE_MISFIRES,
                ScheduleConstants.MISFIRE_FIRE_AND_PROCEED,
                ScheduleConstants.MISFIRE_DO_NOTHING
        };

        for (String policy : policies) {
            SysJob job = createTestJob();
            job.setMisfirePolicy(policy);
            jobMapper.insertJob(job);

            SysJob retrieved = jobMapper.selectJobById(job.getId());
            assertEquals(policy, retrieved.getMisfirePolicy());
        }
    }

    @Test
    @Order(16)
    @DisplayName("测试并发执行设置")
    void testConcurrentSettings() {
        // 测试允许并发
        SysJob job1 = createTestJob();
        job1.setConcurrent("0");
        jobMapper.insertJob(job1);
        SysJob retrieved1 = jobMapper.selectJobById(job1.getId());
        assertEquals("0", retrieved1.getConcurrent());

        // 测试禁止并发
        SysJob job2 = createTestJob();
        job2.setConcurrent("1");
        jobMapper.insertJob(job2);
        SysJob retrieved2 = jobMapper.selectJobById(job2.getId());
        assertEquals("1", retrieved2.getConcurrent());
    }
}

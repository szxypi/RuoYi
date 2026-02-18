package com.zjjh.fdtemp.controller.quartz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjjh.fdtemp.FdtempApplication;
import com.zjjh.fdtemp.TestConfig;
import com.zjjh.fdtemp.beans.entity.SysJob;
import com.zjjh.fdtemp.constants.ScheduleConstants;
import com.zjjh.fdtemp.service.SysJobService;
import org.junit.jupiter.api.*;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SysJobController 控制器层单元测试
 */
@SpringBootTest(classes = FdtempApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("SysJobController 控制器测试")
class SysJobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SysJobService jobService;

    private String adminToken;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    private String login(String username, String password) throws Exception {
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("username", username);
        loginBody.put("password", password);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(responseBody);
        return root.path("data").path("token").asText();
    }

    @BeforeEach
    void setUp() throws Exception {
        adminToken = login(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    private SysJob createTestJob() {
        SysJob job = new SysJob();
        job.setId("test-job-001");
        job.setJobName("测试任务");
        job.setJobGroup("DEFAULT");
        job.setInvokeTarget("testService.testMethod()");
        job.setCronExpression("0 0/5 * * * ?");
        job.setMisfirePolicy(ScheduleConstants.MISFIRE_DEFAULT);
        job.setConcurrent("0");
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        return job;
    }

    @Test
    @Order(1)
    @DisplayName("测试 list - 获取任务列表")
    void testList() throws Exception {
        List<SysJob> jobList = new ArrayList<>();
        jobList.add(createTestJob());

        when(jobService.selectJobList(any(SysJob.class))).thenReturn(jobList);

        mockMvc.perform(post("/monitor/job/list")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());

        verify(jobService).selectJobList(any(SysJob.class));
    }

    @Test
    @Order(2)
    @DisplayName("测试 detail - 获取任务详情")
    void testDetail() throws Exception {
        SysJob job = createTestJob();
        when(jobService.selectJobById("test-job-001")).thenReturn(job);

        mockMvc.perform(get("/monitor/job/detail/test-job-001")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("test-job-001"));

        verify(jobService).selectJobById("test-job-001");
    }

    @Test
    @Order(3)
    @DisplayName("测试 remove - 删除任务")
    void testRemove() throws Exception {
        doNothing().when(jobService).deleteJobByIds("1,2,3");

        mockMvc.perform(post("/monitor/job/remove")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("ids", "1,2,3"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(jobService).deleteJobByIds("1,2,3");
    }

    @Test
    @Order(4)
    @DisplayName("测试 changeStatus - 修改任务状态")
    void testChangeStatus() throws Exception {
        SysJob existingJob = createTestJob();
        when(jobService.selectJobById("test-job-001")).thenReturn(existingJob);
        when(jobService.changeStatus(any(SysJob.class))).thenReturn(1);

        mockMvc.perform(post("/monitor/job/changeStatus")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("id", "test-job-001")
                        .param("status", "1"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(jobService).selectJobById("test-job-001");
        verify(jobService).changeStatus(any(SysJob.class));
    }

    @Test
    @Order(5)
    @DisplayName("测试 run - 立即执行任务 - 成功")
    void testRun_Success() throws Exception {
        SysJob job = createTestJob();
        when(jobService.run(any(SysJob.class))).thenReturn(true);

        mockMvc.perform(post("/monitor/job/run")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("id", "test-job-001"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(jobService).run(any(SysJob.class));
    }

    @Test
    @Order(6)
    @DisplayName("测试 run - 立即执行任务 - 任务不存在")
    void testRun_NotFound() throws Exception {
        when(jobService.run(any(SysJob.class))).thenReturn(false);

        mockMvc.perform(post("/monitor/job/run")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("id", "non-existent"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        verify(jobService).run(any(SysJob.class));
    }

    @Test
    @Order(7)
    @DisplayName("测试 add - 新增任务 - 成功")
    void testAdd_Success() throws Exception {
        when(jobService.checkCronExpressionIsValid("0 0/5 * * * ?")).thenReturn(true);
        when(jobService.insertJob(any(SysJob.class))).thenReturn(1);

        mockMvc.perform(post("/monitor/job/add")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("jobName", "测试任务")
                        .param("jobGroup", "DEFAULT")
                        .param("invokeTarget", "testService.testMethod()")
                        .param("cronExpression", "0 0/5 * * * ?")
                        .param("misfirePolicy", "0")
                        .param("concurrent", "0"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(jobService).checkCronExpressionIsValid("0 0/5 * * * ?");
    }

    @Test
    @Order(8)
    @DisplayName("测试 add - 新增任务 - 无效 Cron 表达式")
    void testAdd_InvalidCron() throws Exception {
        when(jobService.checkCronExpressionIsValid("invalid")).thenReturn(false);

        mockMvc.perform(post("/monitor/job/add")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("jobName", "测试任务")
                        .param("jobGroup", "DEFAULT")
                        .param("invokeTarget", "testService.testMethod()")
                        .param("cronExpression", "invalid"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        verify(jobService).checkCronExpressionIsValid("invalid");
    }

    @Test
    @Order(9)
    @DisplayName("测试 edit - 修改任务 - 成功")
    void testEdit_Success() throws Exception {
        when(jobService.checkCronExpressionIsValid("0 0/5 * * * ?")).thenReturn(true);
        when(jobService.updateJob(any(SysJob.class))).thenReturn(1);

        mockMvc.perform(post("/monitor/job/edit")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "test-job-001")
                        .param("jobName", "更新后的任务")
                        .param("jobGroup", "DEFAULT")
                        .param("invokeTarget", "testService.testMethod()")
                        .param("cronExpression", "0 0/5 * * * ?"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(jobService).updateJob(any(SysJob.class));
    }

    @Test
    @Order(10)
    @DisplayName("测试 checkCronExpressionIsValid - 校验 Cron 表达式")
    void testCheckCronExpressionIsValid() throws Exception {
        when(jobService.checkCronExpressionIsValid("0 0/5 * * * ?")).thenReturn(true);

        mockMvc.perform(post("/monitor/job/checkCronExpressionIsValid")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("cronExpression", "0 0/5 * * * ?"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    Assertions.assertTrue(response.equals("true") || response.contains("true"));
                });

        verify(jobService).checkCronExpressionIsValid("0 0/5 * * * ?");
    }

    @Test
    @Order(11)
    @DisplayName("测试 queryCronExpression - 查询 Cron 执行时间")
    void testQueryCronExpression() throws Exception {
        when(jobService.checkCronExpressionIsValid("0 0/5 * * * ?")).thenReturn(true);

        mockMvc.perform(get("/monitor/job/queryCronExpression")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("cronExpression", "0 0/5 * * * ?"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(jobService).checkCronExpressionIsValid("0 0/5 * * * ?");
    }

    @Test
    @Order(12)
    @DisplayName("测试 queryCronExpression - 无效表达式")
    void testQueryCronExpression_Invalid() throws Exception {
        when(jobService.checkCronExpressionIsValid("invalid")).thenReturn(false);

        mockMvc.perform(get("/monitor/job/queryCronExpression")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("cronExpression", "invalid"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        verify(jobService).checkCronExpressionIsValid("invalid");
    }

    @Test
    @Order(20)
    @DisplayName("测试未认证访问被拒绝")
    void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(post("/monitor/job/list"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(21)
    @DisplayName("测试无效Token访问被拒绝")
    void testInvalidTokenAccess() throws Exception {
        mockMvc.perform(post("/monitor/job/list")
                        .header("Authorization", "Bearer invalidtoken"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}

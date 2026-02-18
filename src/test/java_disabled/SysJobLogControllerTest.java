package com.zjjh.fdtemp.controller.quartz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjjh.fdtemp.FdtempApplication;
import com.zjjh.fdtemp.TestConfig;
import com.zjjh.fdtemp.beans.entity.SysJobLog;
import com.zjjh.fdtemp.constants.Constants;
import com.zjjh.fdtemp.service.SysJobLogService;
import org.junit.jupiter.api.*;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SysJobLogController 控制器层单元测试
 */
@SpringBootTest(classes = FdtempApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("SysJobLogController 控制器测试")
class SysJobLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SysJobLogService jobLogService;

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

    private SysJobLog createTestJobLog() {
        SysJobLog log = new SysJobLog();
        log.setId("log-001");
        log.setJobName("测试任务");
        log.setJobGroup("DEFAULT");
        log.setInvokeTarget("testService.testMethod()");
        log.setJobMessage("执行成功，耗时100毫秒");
        log.setStatus(Constants.SUCCESS);
        log.setStartTime(new Date(System.currentTimeMillis() - 100));
        log.setEndTime(new Date());
        return log;
    }

    @Test
    @Order(1)
    @DisplayName("测试 list - 获取日志列表")
    void testList() throws Exception {
        List<SysJobLog> logList = new ArrayList<>();
        logList.add(createTestJobLog());

        when(jobLogService.selectJobLogList(any(SysJobLog.class))).thenReturn(logList);

        mockMvc.perform(post("/monitor/jobLog/list")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());

        verify(jobLogService).selectJobLogList(any(SysJobLog.class));
    }

    @Test
    @Order(2)
    @DisplayName("测试 detail - 获取日志详情")
    void testDetail() throws Exception {
        SysJobLog log = createTestJobLog();
        when(jobLogService.selectJobLogById("log-001")).thenReturn(log);

        mockMvc.perform(get("/monitor/jobLog/detail/log-001")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("log-001"));

        verify(jobLogService).selectJobLogById("log-001");
    }

    @Test
    @Order(3)
    @DisplayName("测试 detail - 日志不存在")
    void testDetail_NotFound() throws Exception {
        when(jobLogService.selectJobLogById("non-existent")).thenReturn(null);

        mockMvc.perform(get("/monitor/jobLog/detail/non-existent")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());

        verify(jobLogService).selectJobLogById("non-existent");
    }

    @Test
    @Order(4)
    @DisplayName("测试 remove - 删除日志")
    void testRemove() throws Exception {
        when(jobLogService.deleteJobLogByIds("1,2,3")).thenReturn(3);

        mockMvc.perform(post("/monitor/jobLog/remove")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("ids", "1,2,3"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(jobLogService).deleteJobLogByIds("1,2,3");
    }

    @Test
    @Order(5)
    @DisplayName("测试 remove - 删除单个日志")
    void testRemove_Single() throws Exception {
        when(jobLogService.deleteJobLogByIds("1")).thenReturn(1);

        mockMvc.perform(post("/monitor/jobLog/remove")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("ids", "1"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(jobLogService).deleteJobLogByIds("1");
    }

    @Test
    @Order(6)
    @DisplayName("测试 clean - 清空日志")
    void testClean() throws Exception {
        doNothing().when(jobLogService).cleanJobLog();

        mockMvc.perform(post("/monitor/jobLog/clean")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk());

        verify(jobLogService).cleanJobLog();
    }

    @Test
    @Order(7)
    @DisplayName("测试 list - 按任务名过滤")
    void testList_FilterByJobName() throws Exception {
        List<SysJobLog> logList = new ArrayList<>();
        SysJobLog log = createTestJobLog();
        log.setJobName("特定任务");
        logList.add(log);

        when(jobLogService.selectJobLogList(any(SysJobLog.class))).thenReturn(logList);

        mockMvc.perform(post("/monitor/jobLog/list")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("jobName", "特定任务"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());

        verify(jobLogService).selectJobLogList(any(SysJobLog.class));
    }

    @Test
    @Order(8)
    @DisplayName("测试 list - 按状态过滤")
    void testList_FilterByStatus() throws Exception {
        List<SysJobLog> logList = new ArrayList<>();
        SysJobLog log = createTestJobLog();
        log.setStatus(Constants.FAIL);
        logList.add(log);

        when(jobLogService.selectJobLogList(any(SysJobLog.class))).thenReturn(logList);

        mockMvc.perform(post("/monitor/jobLog/list")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("status", Constants.FAIL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());

        verify(jobLogService).selectJobLogList(any(SysJobLog.class));
    }

    @Test
    @Order(9)
    @DisplayName("测试 list - 按任务组过滤")
    void testList_FilterByJobGroup() throws Exception {
        List<SysJobLog> logList = new ArrayList<>();
        SysJobLog log = createTestJobLog();
        log.setJobGroup("SYSTEM");
        logList.add(log);

        when(jobLogService.selectJobLogList(any(SysJobLog.class))).thenReturn(logList);

        mockMvc.perform(post("/monitor/jobLog/list")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("jobGroup", "SYSTEM"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(jobLogService).selectJobLogList(any(SysJobLog.class));
    }

    @Test
    @Order(20)
    @DisplayName("测试未认证访问被拒绝")
    void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(post("/monitor/jobLog/list"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(21)
    @DisplayName("测试无效Token访问被拒绝")
    void testInvalidTokenAccess() throws Exception {
        mockMvc.perform(post("/monitor/jobLog/list")
                        .header("Authorization", "Bearer invalidtoken"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(22)
    @DisplayName("测试空Token访问被拒绝")
    void testEmptyTokenAccess() throws Exception {
        mockMvc.perform(post("/monitor/jobLog/list")
                        .header("Authorization", "Bearer "))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(23)
    @DisplayName("测试 detail - 无效ID格式")
    void testDetail_InvalidIdFormat() throws Exception {
        // 系统应该能处理任意ID格式
        when(jobLogService.selectJobLogById(anyString())).thenReturn(null);

        mockMvc.perform(get("/monitor/jobLog/detail/invalid-id-format!@#$%")
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(print())
                .andExpect(status().isOk());

        verify(jobLogService).selectJobLogById(anyString());
    }

    @Test
    @Order(24)
    @DisplayName("测试 remove - 空ID列表")
    void testRemove_EmptyIds() throws Exception {
        when(jobLogService.deleteJobLogByIds("")).thenReturn(0);

        mockMvc.perform(post("/monitor/jobLog/remove")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("ids", ""))
                .andDo(print())
                .andExpect(status().isOk());

        verify(jobLogService).deleteJobLogByIds("");
    }
}

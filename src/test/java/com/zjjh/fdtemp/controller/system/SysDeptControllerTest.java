package com.zjjh.fdtemp.controller.system;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjjh.fdtemp.FdtempApplication;
import com.zjjh.fdtemp.TestConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = FdtempApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class SysDeptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ROOT_DEPT_ID = "1";
    private static final String SHENZHEN_DEPT_ID = "2";

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

    @Test
    @Order(1)
    @DisplayName("1.1 管理员查询部门列表成功")
    void testList_WithAdmin() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(get("/system/dept/list")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(2)
    @DisplayName("1.2 未认证访问部门列表 - 返回401")
    void testList_Unauthorized() throws Exception {
        mockMvc.perform(get("/system/dept/list"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(10)
    @DisplayName("2.1 管理员新增部门成功")
    void testAdd_Success() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/dept/add")
                .header("Authorization", "Bearer " + token)
                .param("deptName", "测试新增部门")
                .param("parentId", SHENZHEN_DEPT_ID)
                .param("orderNum", "99")
                .param("leader", "test")
                .param("phone", "13800000000")
                .param("email", "test@test.com")
                .param("status", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(11)
    @DisplayName("2.2 新增部门 - 部门名称已存在")
    void testAdd_DuplicateDeptName() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/dept/add")
                .header("Authorization", "Bearer " + token)
                .param("deptName", "总公司")
                .param("parentId", "0")
                .param("orderNum", "99"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(30)
    @DisplayName("4.1 删除有子部门的部门 - 不允许")
    void testRemove_HasChildren() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(delete("/system/dept/" + SHENZHEN_DEPT_ID)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(301));
    }

    @Test
    @Order(31)
    @DisplayName("4.2 删除有用户的部门 - 不允许")
    void testRemove_HasUsers() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(delete("/system/dept/" + ROOT_DEPT_ID)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(301));
    }

    @Test
    @Order(40)
    @DisplayName("5.1 校验部门名称唯一性 - 唯一")
    void testCheckDeptNameUnique_Unique() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/dept/checkDeptNameUnique")
                .header("Authorization", "Bearer " + token)
                .param("deptName", "唯一部门名测试")
                .param("parentId", ROOT_DEPT_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}

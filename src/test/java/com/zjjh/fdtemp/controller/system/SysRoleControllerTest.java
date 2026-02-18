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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = FdtempApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class SysRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("1.1 管理员查询角色列表成功")
    void testList_WithAdmin() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/role/list")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());
    }

    @Test
    @Order(2)
    @DisplayName("1.2 未认证访问角色列表 - 返回401")
    void testList_Unauthorized() throws Exception {
        mockMvc.perform(post("/system/role/list"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(10)
    @DisplayName("2.1 管理员新增角色成功")
    void testAdd_Success() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/role/add")
                        .header("Authorization", "Bearer " + token)
                        .param("roleName", "测试新增角色")
                        .param("roleKey", "test_role")
                        .param("roleSort", "99")
                        .param("dataScope", "1")
                        .param("menuIds", "1")
                        .param("menuIds", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(11)
    @DisplayName("2.2 新增角色 - 角色名称已存在")
    void testAdd_DuplicateRoleName() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/role/add")
                        .header("Authorization", "Bearer " + token)
                        .param("roleName", "超级管理员")
                        .param("roleKey", "test_dup_name")
                        .param("roleSort", "99")
                        .param("status", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(30)
    @DisplayName("4.1 删除已分配用户的角色 - 不允许")
    void testRemove_Allocated() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/role/remove")
                        .header("Authorization", "Bearer " + token)
                        .param("ids", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(50)
    @DisplayName("6.1 校验角色名称唯一性 - 唯一")
    void testCheckRoleNameUnique_Unique() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/role/checkRoleNameUnique")
                        .header("Authorization", "Bearer " + token)
                        .param("roleName", "唯一角色名测试"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @Order(51)
    @DisplayName("6.2 校验角色名称唯一性 - 不唯一")
    void testCheckRoleNameUnique_NotUnique() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/role/checkRoleNameUnique")
                        .header("Authorization", "Bearer " + token)
                        .param("roleName", "超级管理员"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}

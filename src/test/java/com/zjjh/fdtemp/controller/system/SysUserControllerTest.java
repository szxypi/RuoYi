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
public class SysUserControllerTest {

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
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(responseBody);
        return root.path("data").path("token").asText();
    }

    @Test
    @Order(1)
    @DisplayName("1.1 管理员查询用户列表成功")
    void testList_WithAdmin() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/user/list")
                .header("Authorization", "Bearer " + token)
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());
    }

    @Test
    @Order(2)
    @DisplayName("1.2 未认证访问用户列表 - 返回401")
    void testList_Unauthorized() throws Exception {
        mockMvc.perform(post("/system/user/list"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(10)
    @DisplayName("2.1 管理员新增用户成功")
    void testAdd_Success() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/user/add")
                .header("Authorization", "Bearer " + token)
                .param("loginName", "testadduser")
                .param("userName", "测试新增用户")
                .param("password", "test123")
                .param("deptId", "2")
                .param("roleIds", "2")
                .param("postIds", "4"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(11)
    @DisplayName("2.2 新增用户 - 登录名已存在")
    void testAdd_DuplicateLoginName() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/user/add")
                .header("Authorization", "Bearer " + token)
                .param("loginName", "admin")
                .param("userName", "重复登录名测试")
                .param("password", "test123")
                .param("deptId", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(20)
    @DisplayName("3.1 管理员修改用户成功")
    void testEdit_Success() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/user/edit")
                .header("Authorization", "Bearer " + token)
                .param("id", "2")
                .param("loginName", "test")
                .param("userName", "修改后的测试用户")
                .param("deptId", "2")
                .param("roleIds", "2")
                .param("postIds", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(30)
    @DisplayName("4.1 删除用户成功")
    void testRemove_Success() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/user/remove")
                .header("Authorization", "Bearer " + token)
                .param("ids", "3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(40)
    @DisplayName("5.1 管理员重置用户密码成功")
    void testResetPwd_Success() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/user/resetPwd")
                .header("Authorization", "Bearer " + token)
                .param("id", "2")
                .param("password", "newPassword123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(50)
    @DisplayName("6.1 校验登录名唯一性 - 唯一")
    void testCheckLoginNameUnique_Unique() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/user/checkLoginNameUnique")
                .header("Authorization", "Bearer " + token)
                .param("loginName", "uniqueuser123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @Order(51)
    @DisplayName("6.2 校验登录名唯一性 - 不唯一")
    void testCheckLoginNameUnique_NotUnique() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/user/checkLoginNameUnique")
                .header("Authorization", "Bearer " + token)
                .param("loginName", "admin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}

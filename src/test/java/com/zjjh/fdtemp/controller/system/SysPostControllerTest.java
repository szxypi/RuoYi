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
public class SysPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String CEO_POST_ID = "1";
    private static final String PM_POST_ID = "2";

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
    @DisplayName("1.1 管理员查询岗位列表成功")
    void testList_WithAdmin() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/post/list")
                        .header("Authorization", "Bearer " + token)
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());
    }

    @Test
    @Order(2)
    @DisplayName("1.2 未认证访问岗位列表 - 返回401")
    void testList_Unauthorized() throws Exception {
        mockMvc.perform(post("/system/post/list"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(10)
    @DisplayName("2.1 管理员新增岗位成功")
    void testAdd_Success() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/post/add")
                        .header("Authorization", "Bearer " + token)
                        .param("postName", "测试新增岗位")
                        .param("postCode", "test_post")
                        .param("postSort", "99")
                        .param("status", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(11)
    @DisplayName("2.2 新增岗位 - 岗位名称已存在")
    void testAdd_DuplicatePostName() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/post/add")
                        .header("Authorization", "Bearer " + token)
                        .param("postName", "董事长")
                        .param("postCode", "test_dup_name")
                        .param("postSort", "99")
                        .param("status", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(20)
    @DisplayName("3.1 管理员修改岗位成功")
    void testEdit_Success() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/post/edit")
                        .header("Authorization", "Bearer " + token)
                        .param("id", PM_POST_ID)
                        .param("postName", "修改后的项目经理")
                        .param("postCode", "se")
                        .param("postSort", "2")
                        .param("status", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(30)
    @DisplayName("4.1 删除已分配用户的岗位 - 不允许")
    void testRemove_Allocated() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/post/remove")
                        .header("Authorization", "Bearer " + token)
                        .param("ids", CEO_POST_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(40)
    @DisplayName("5.1 校验岗位名称唯一性 - 唯一")
    void testCheckPostNameUnique_Unique() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/post/checkPostNameUnique")
                        .header("Authorization", "Bearer " + token)
                        .param("postName", "唯一岗位名测试"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}

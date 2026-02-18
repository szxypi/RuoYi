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
public class SysMenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String SYSTEM_MENU_ID = "1";
    private static final String ROLE_MENU_ID = "3";

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
    @DisplayName("1.1 管理员查询菜单列表成功")
    void testList_WithAdmin() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(get("/system/menu/list")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(2)
    @DisplayName("1.2 未认证访问菜单列表 - 返回401")
    void testList_Unauthorized() throws Exception {
        mockMvc.perform(get("/system/menu/list"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(10)
    @DisplayName("2.1 管理员新增菜单成功 - 目录类型")
    void testAdd_Directory() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/menu/add")
                        .header("Authorization", "Bearer " + token)
                        .param("menuName", "测试新增目录")
                        .param("parentId", "0")
                        .param("orderNum", "99")
                        .param("menuType", "M")
                        .param("visible", "0")
                        .param("url", "#")
                        .param("icon", "test")
                        .param("status", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(20)
    @DisplayName("3.1 管理员修改菜单成功")
    void testEdit_Success() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/menu/edit")
                        .header("Authorization", "Bearer " + token)
                        .param("id", ROLE_MENU_ID)
                        .param("menuName", "修改后的角色管理")
                        .param("parentId", SYSTEM_MENU_ID)
                        .param("orderNum", "2")
                        .param("menuType", "C")
                        .param("visible", "0")
                        .param("url", "/system/role")
                        .param("perms", "system:role:list")
                        .param("status", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(30)
    @DisplayName("4.1 删除有子菜单的菜单 - 不允许")
    void testRemove_HasChildren() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(delete("/system/menu/" + SYSTEM_MENU_ID)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(301));
    }

    @Test
    @Order(40)
    @DisplayName("5.1 校验菜单名称唯一性 - 唯一")
    void testCheckMenuNameUnique_Unique() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(post("/system/menu/checkMenuNameUnique")
                        .header("Authorization", "Bearer " + token)
                        .param("menuName", "唯一菜单名测试")
                        .param("parentId", SYSTEM_MENU_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @Order(50)
    @DisplayName("6.1 获取角色菜单树数据")
    void testRoleMenuTreeData() throws Exception {
        String token = login("admin", "admin123");

        mockMvc.perform(get("/system/menu/roleMenuTreeData")
                        .header("Authorization", "Bearer " + token)
                        .param("id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

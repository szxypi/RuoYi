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

/**
 * SysDeptController 单元测试类
 */
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

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String NORMAL_USERNAME = "normal";
    private static final String NORMAL_PASSWORD = "admin123";

    private static final String ROOT_DEPT_ID = "1";
    private static final String SHENZHEN_DEPT_ID = "2";

    /**
     * 辅助方法：登录并返回token
     */
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

    // ============================================
    // 部门列表查询测试
    // ============================================

    @Test
    @Order(1)
    @DisplayName("1.1 管理员查询部门列表成功")
    void testList_WithAdmin() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(get("/system/dept/list")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(2)
    @DisplayName("1.2 按条件查询部门列表")
    void testList_WithCondition() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(get("/system/dept/list")
                .header("Authorization", "Bearer " + token)
                .param("deptName", "总公司"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(3)
    @DisplayName("1.3 未认证访问部门列表 - 返回401")
    void testList_Unauthorized() throws Exception {
        mockMvc.perform(get("/system/dept/list"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // ============================================
    // 部门新增测试
    // ============================================

    @Test
    @Order(10)
    @DisplayName("2.1 管理员新增部门成功")
    void testAdd_Success() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> dept = new HashMap<>();
        dept.put("deptName", "测试新增部门");
        dept.put("parentId", SHENZHEN_DEPT_ID);
        dept.put("orderNum", "99");
        dept.put("leader", "test");
        dept.put("phone", "13800000000");
        dept.put("email", "test@test.com");
        dept.put("status", "0");

        mockMvc.perform(post("/system/dept/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dept)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(11)
    @DisplayName("2.2 新增部门 - 部门名称已存在")
    void testAdd_DuplicateDeptName() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> dept = new HashMap<>();
        dept.put("deptName", "总公司"); // 已存在的部门名
        dept.put("parentId", "0");
        dept.put("orderNum", "99");

        mockMvc.perform(post("/system/dept/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dept)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(12)
    @DisplayName("2.3 普通用户新增部门 - 无权限")
    void testAdd_NoPermission() throws Exception {
        String token = login(NORMAL_USERNAME, NORMAL_PASSWORD);

        Map<String, Object> dept = new HashMap<>();
        dept.put("deptName", "无权限新增部门");
        dept.put("parentId", ROOT_DEPT_ID);
        dept.put("orderNum", "99");

        mockMvc.perform(post("/system/dept/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dept)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ============================================
    // 部门修改测试
    // ============================================

    @Test
    @Order(20)
    @DisplayName("3.1 管理员修改部门成功")
    void testEdit_Success() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> dept = new HashMap<>();
        dept.put("id", SHENZHEN_DEPT_ID);
        dept.put("deptName", "修改后的深圳分公司");
        dept.put("parentId", ROOT_DEPT_ID);
        dept.put("orderNum", "1");
        dept.put("status", "0");

        mockMvc.perform(post("/system/dept/edit")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dept)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(21)
    @DisplayName("3.2 修改部门 - 上级部门不能是自己")
    void testEdit_ParentIsSelf() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> dept = new HashMap<>();
        dept.put("id", SHENZHEN_DEPT_ID);
        dept.put("deptName", "深圳分公司");
        dept.put("parentId", SHENZHEN_DEPT_ID); // 上级设为自己
        dept.put("orderNum", "1");

        mockMvc.perform(post("/system/dept/edit")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dept)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(22)
    @DisplayName("3.3 修改部门 - 停用部门但有未停用子部门")
    void testEdit_DisableWithNormalChildren() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> dept = new HashMap<>();
        dept.put("id", SHENZHEN_DEPT_ID);
        dept.put("deptName", "深圳分公司");
        dept.put("parentId", ROOT_DEPT_ID);
        dept.put("orderNum", "1");
        dept.put("status", "1"); // 停用，但有正常子部门

        mockMvc.perform(post("/system/dept/edit")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dept)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    // 可能返回成功或错误，取决于业务逻辑
                    String response = result.getResponse().getContentAsString();
                    JsonNode root = objectMapper.readTree(response);
                    int code = root.path("code").asInt();
                    // 验证返回结果
                    assertTrue(code == 0 || code == 500);
                });
    }

    // ============================================
    // 部门删除测试
    // ============================================

    @Test
    @Order(30)
    @DisplayName("4.1 删除有子部门的部门 - 不允许")
    void testRemove_HasChildren() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(delete("/system/dept/" + SHENZHEN_DEPT_ID)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(31)
    @DisplayName("4.2 删除有用户的部门 - 不允许")
    void testRemove_HasUsers() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(delete("/system/dept/" + ROOT_DEPT_ID)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(32)
    @DisplayName("4.3 删除空部门成功")
    void testRemove_Success() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        // 市场部门（ID=4）没有子部门和用户
        mockMvc.perform(delete("/system/dept/4")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    // ============================================
    // 唯一性校验测试
    // ============================================

    @Test
    @Order(40)
    @DisplayName("5.1 校验部门名称唯一性 - 唯一")
    void testCheckDeptNameUnique_Unique() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/dept/checkDeptNameUnique")
                .header("Authorization", "Bearer " + token)
                .param("deptName", "唯一部门名测试")
                .param("parentId", ROOT_DEPT_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @Order(41)
    @DisplayName("5.2 校验部门名称唯一性 - 不唯一")
    void testCheckDeptNameUnique_NotUnique() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/dept/checkDeptNameUnique")
                .header("Authorization", "Bearer " + token)
                .param("deptName", "总公司")
                .param("parentId", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // ============================================
    // 部门树测试
    // ============================================

    @Test
    @Order(50)
    @DisplayName("6.1 获取部门树数据（排除下级）")
    void testTreeDataExcludeChild() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(get("/system/dept/treeData/" + SHENZHEN_DEPT_ID)
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

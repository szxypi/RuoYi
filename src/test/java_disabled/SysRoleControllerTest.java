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
 * SysRoleController 单元测试类
 */
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

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String NORMAL_USERNAME = "normal";
    private static final String NORMAL_PASSWORD = "admin123";

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
    // 角色列表查询测试
    // ============================================

    @Test
    @Order(1)
    @DisplayName("1.1 管理员查询角色列表成功")
    void testList_WithAdmin() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/role/list")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());
    }

    @Test
    @Order(2)
    @DisplayName("1.2 按条件查询角色列表")
    void testList_WithCondition() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/role/list")
                .header("Authorization", "Bearer " + token)
                .param("roleName", "管理员"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());
    }

    @Test
    @Order(3)
    @DisplayName("1.3 未认证访问角色列表 - 返回401")
    void testList_Unauthorized() throws Exception {
        mockMvc.perform(post("/system/role/list"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // ============================================
    // 角色新增测试
    // ============================================

    @Test
    @Order(10)
    @DisplayName("2.1 管理员新增角色成功")
    void testAdd_Success() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> role = new HashMap<>();
        role.put("roleName", "测试角色");
        role.put("roleKey", "test_role");
        role.put("roleSort", "99");
        role.put("dataScope", "1");
        role.put("menuIds", new String[]{"1", "2"});

        mockMvc.perform(post("/system/role/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(11)
    @DisplayName("2.2 新增角色 - 角色名称已存在")
    void testAdd_DuplicateRoleName() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> role = new HashMap<>();
        role.put("roleName", "超级管理员"); // 已存在的角色名
        role.put("roleKey", "test_dup_name");
        role.put("roleSort", "99");

        mockMvc.perform(post("/system/role/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(12)
    @DisplayName("2.3 新增角色 - 角色权限键已存在")
    void testAdd_DuplicateRoleKey() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> role = new HashMap<>();
        role.put("roleName", "测试角色权限键重复");
        role.put("roleKey", "admin"); // 已存在的权限键
        role.put("roleSort", "99");

        mockMvc.perform(post("/system/role/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(13)
    @DisplayName("2.4 普通用户新增角色 - 无权限")
    void testAdd_NoPermission() throws Exception {
        String token = login(NORMAL_USERNAME, NORMAL_PASSWORD);

        Map<String, Object> role = new HashMap<>();
        role.put("roleName", "无权限新增角色");
        role.put("roleKey", "no_perm_role");
        role.put("roleSort", "99");

        mockMvc.perform(post("/system/role/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ============================================
    // 角色修改测试
    // ============================================

    @Test
    @Order(20)
    @DisplayName("3.1 管理员修改角色成功")
    void testEdit_Success() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> role = new HashMap<>();
        role.put("id", "2");
        role.put("roleName", "修改后的普通角色");
        role.put("roleKey", "common");
        role.put("roleSort", "2");
        role.put("menuIds", new String[]{"1", "2", "3"});

        mockMvc.perform(post("/system/role/edit")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(21)
    @DisplayName("3.2 修改超级管理员角色 - 不允许")
    void testEdit_AdminRole() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> role = new HashMap<>();
        role.put("id", "1");
        role.put("roleName", "尝试修改管理员角色");
        role.put("roleKey", "admin");

        mockMvc.perform(post("/system/role/edit")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ============================================
    // 角色删除测试
    // ============================================

    @Test
    @Order(30)
    @DisplayName("4.1 管理员删除角色成功")
    void testRemove_Success() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        // 先新增一个角色用于删除
        Map<String, Object> role = new HashMap<>();
        role.put("roleName", "待删除角色");
        role.put("roleKey", "to_be_deleted");
        role.put("roleSort", "99");

        mockMvc.perform(post("/system/role/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk());

        // 查询角色ID
        MvcResult result = mockMvc.perform(post("/system/role/list")
                .header("Authorization", "Bearer " + token)
                .param("roleName", "待删除角色"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(responseBody);
        String roleId = root.path("rows").get(0).path("id").asText();

        // 删除角色
        mockMvc.perform(post("/system/role/remove")
                .header("Authorization", "Bearer " + token)
                .param("ids", roleId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(31)
    @DisplayName("4.2 删除已分配用户的角色 - 不允许")
    void testRemove_Allocated() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        // admin角色已分配给用户
        mockMvc.perform(post("/system/role/remove")
                .header("Authorization", "Bearer " + token)
                .param("ids", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ============================================
    // 角色状态修改测试
    // ============================================

    @Test
    @Order(40)
    @DisplayName("5.1 管理员修改角色状态成功")
    void testChangeStatus_Success() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> role = new HashMap<>();
        role.put("id", "2");
        role.put("status", "1"); // 停用
        role.put("roleName", "普通角色");
        role.put("roleKey", "common");

        mockMvc.perform(post("/system/role/changeStatus")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(41)
    @DisplayName("5.2 修改超级管理员角色状态 - 不允许")
    void testChangeStatus_AdminRole() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> role = new HashMap<>();
        role.put("id", "1");
        role.put("status", "1");
        role.put("roleName", "超级管理员");
        role.put("roleKey", "admin");

        mockMvc.perform(post("/system/role/changeStatus")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ============================================
    // 唯一性校验测试
    // ============================================

    @Test
    @Order(50)
    @DisplayName("6.1 校验角色名称唯一性 - 唯一")
    void testCheckRoleNameUnique_Unique() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

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
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/role/checkRoleNameUnique")
                .header("Authorization", "Bearer " + token)
                .param("roleName", "超级管理员"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @Order(52)
    @DisplayName("6.3 校验角色权限键唯一性 - 唯一")
    void testCheckRoleKeyUnique_Unique() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/role/checkRoleKeyUnique")
                .header("Authorization", "Bearer " + token)
                .param("roleKey", "unique_role_key"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @Order(53)
    @DisplayName("6.4 校验角色权限键唯一性 - 不唯一")
    void testCheckRoleKeyUnique_NotUnique() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/role/checkRoleKeyUnique")
                .header("Authorization", "Bearer " + token)
                .param("roleKey", "admin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // ============================================
    // 数据权限测试
    // ============================================

    @Test
    @Order(60)
    @DisplayName("7.1 修改角色数据权限成功")
    void testAuthDataScope() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> role = new HashMap<>();
        role.put("id", "2");
        role.put("roleName", "普通角色");
        role.put("roleKey", "common");
        role.put("dataScope", "3"); // 本部门数据权限
        role.put("deptIds", new String[]{"2", "3", "4"});

        mockMvc.perform(post("/system/role/authDataScope")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    // ============================================
    // 部门树测试
    // ============================================

    @Test
    @Order(70)
    @DisplayName("8.1 获取角色部门树数据")
    void testDeptTreeData() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(get("/system/role/deptTreeData")
                .header("Authorization", "Bearer " + token)
                .param("id", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ============================================
    // 用户授权测试
    // ============================================

    @Test
    @Order(80)
    @DisplayName("9.1 查询已分配用户角色列表")
    void testAllocatedList() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/role/authUser/allocatedList")
                .header("Authorization", "Bearer " + token)
                .param("roleId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());
    }

    @Test
    @Order(81)
    @DisplayName("9.2 查询未分配用户角色列表")
    void testUnallocatedList() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/role/authUser/unallocatedList")
                .header("Authorization", "Bearer " + token)
                .param("roleId", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());
    }

    @Test
    @Order(82)
    @DisplayName("9.3 取消授权用户角色")
    void testCancelAuthUser() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/role/authUser/cancel")
                .header("Authorization", "Bearer " + token)
                .param("userId", "2")
                .param("roleId", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(83)
    @DisplayName("9.4 批量选择授权用户角色")
    void testSelectAuthUserAll() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/role/authUser/selectAll")
                .header("Authorization", "Bearer " + token)
                .param("roleId", "2")
                .param("userIds", "3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

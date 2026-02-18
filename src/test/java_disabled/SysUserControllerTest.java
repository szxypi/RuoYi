package com.zjjh.fdtemp.controller.system;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjjh.fdtemp.FdtempApplication;
import com.zjjh.fdtemp.TestConfig;
import com.zjjh.fdtemp.beans.entity.SysUser;
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
 * SysUserController 单元测试类
 */
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
    // 用户列表查询测试
    // ============================================

    @Test
    @Order(1)
    @DisplayName("1.1 管理员查询用户列表成功")
    void testList_WithAdmin() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/user/list")
                        .header("Authorization", "Bearer " + token)
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.total").isNumber());
    }

    @Test
    @Order(2)
    @DisplayName("1.2 管理员按条件查询用户列表")
    void testList_WithCondition() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/user/list")
                        .header("Authorization", "Bearer " + token)
                        .param("loginName", "admin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());
    }

    @Test
    @Order(3)
    @DisplayName("1.3 普通用户查询用户列表 - 有权限")
    void testList_WithNormalUser() throws Exception {
        String token = login(NORMAL_USERNAME, NORMAL_PASSWORD);

        // 普通用户有 system:user:query 权限
        mockMvc.perform(post("/system/user/list")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    @DisplayName("1.4 未认证访问用户列表 - 返回401")
    void testList_Unauthorized() throws Exception {
        mockMvc.perform(post("/system/user/list"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // ============================================
    // 用户新增测试
    // ============================================

    @Test
    @Order(10)
    @DisplayName("2.1 管理员新增用户成功")
    void testAdd_Success() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> user = new HashMap<>();
        user.put("loginName", "testadduser");
        user.put("userName", "测试新增用户");
        user.put("password", "test123");
        user.put("deptId", "2");
        user.put("roleIds", new String[]{"2"});
        user.put("postIds", new String[]{"4"});

        mockMvc.perform(post("/system/user/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(11)
    @DisplayName("2.2 新增用户 - 登录名已存在")
    void testAdd_DuplicateLoginName() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> user = new HashMap<>();
        user.put("loginName", "admin"); // 已存在的登录名
        user.put("userName", "重复登录名测试");
        user.put("password", "test123");
        user.put("deptId", "2");

        mockMvc.perform(post("/system/user/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(12)
    @DisplayName("2.3 新增用户 - 手机号已存在")
    void testAdd_DuplicatePhone() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> user = new HashMap<>();
        user.put("loginName", "testphoneuser");
        user.put("userName", "重复手机号测试");
        user.put("password", "test123");
        user.put("deptId", "2");
        user.put("phonenumber", "15888888888"); // admin的手机号

        mockMvc.perform(post("/system/user/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(13)
    @DisplayName("2.4 新增用户 - 邮箱已存在")
    void testAdd_DuplicateEmail() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> user = new HashMap<>();
        user.put("loginName", "testemailuser");
        user.put("userName", "重复邮箱测试");
        user.put("password", "test123");
        user.put("deptId", "2");
        user.put("email", "admin@fdtemp.com"); // admin的邮箱

        mockMvc.perform(post("/system/user/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(14)
    @DisplayName("2.5 普通用户新增用户 - 无权限")
    void testAdd_NoPermission() throws Exception {
        String token = login(NORMAL_USERNAME, NORMAL_PASSWORD);

        Map<String, Object> user = new HashMap<>();
        user.put("loginName", "unauthuser");
        user.put("userName", "无权限新增测试");
        user.put("password", "test123");
        user.put("deptId", "2");

        mockMvc.perform(post("/system/user/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ============================================
    // 用户修改测试
    // ============================================

    @Test
    @Order(20)
    @DisplayName("3.1 管理员修改用户成功")
    void testEdit_Success() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> user = new HashMap<>();
        user.put("id", "2");
        user.put("loginName", "test");
        user.put("userName", "修改后的测试用户");
        user.put("deptId", "2");
        user.put("roleIds", new String[]{"2"});
        user.put("postIds", new String[]{"2"});

        mockMvc.perform(post("/system/user/edit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(21)
    @DisplayName("3.2 修改超级管理员 - 不允许")
    void testEdit_AdminUser() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> user = new HashMap<>();
        user.put("id", "1");
        user.put("loginName", "admin");
        user.put("userName", "尝试修改管理员");
        user.put("deptId", "1");

        mockMvc.perform(post("/system/user/edit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ============================================
    // 用户删除测试
    // ============================================

    @Test
    @Order(30)
    @DisplayName("4.1 管理员删除用户成功")
    void testRemove_Success() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        // 先新增一个用户用于删除
        Map<String, Object> user = new HashMap<>();
        user.put("loginName", "tobedeleted");
        user.put("userName", "待删除用户");
        user.put("password", "test123");
        user.put("deptId", "2");

        mockMvc.perform(post("/system/user/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        // 查询用户ID
        MvcResult result = mockMvc.perform(post("/system/user/list")
                        .header("Authorization", "Bearer " + token)
                        .param("loginName", "tobedeleted"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(responseBody);
        String userId = root.path("rows").get(0).path("id").asText();

        // 删除用户
        mockMvc.perform(post("/system/user/remove")
                        .header("Authorization", "Bearer " + token)
                        .param("ids", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(31)
    @DisplayName("4.2 删除当前登录用户 - 不允许")
    void testRemove_Self() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/user/remove")
                        .header("Authorization", "Bearer " + token)
                        .param("ids", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(32)
    @DisplayName("4.3 普通用户删除用户 - 无权限")
    void testRemove_NoPermission() throws Exception {
        String token = login(NORMAL_USERNAME, NORMAL_PASSWORD);

        mockMvc.perform(post("/system/user/remove")
                        .header("Authorization", "Bearer " + token)
                        .param("ids", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ============================================
    // 重置密码测试
    // ============================================

    @Test
    @Order(40)
    @DisplayName("5.1 管理员重置用户密码成功")
    void testResetPwd_Success() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> user = new HashMap<>();
        user.put("id", "2");
        user.put("password", "newPassword123");

        mockMvc.perform(post("/system/user/resetPwd")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(41)
    @DisplayName("5.2 重置超级管理员密码 - 不允许")
    void testResetPwd_AdminUser() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> user = new HashMap<>();
        user.put("id", "1");
        user.put("password", "newPassword123");

        mockMvc.perform(post("/system/user/resetPwd")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ============================================
    // 状态修改测试
    // ============================================

    @Test
    @Order(50)
    @DisplayName("6.1 管理员修改用户状态成功")
    void testChangeStatus_Success() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> user = new HashMap<>();
        user.put("id", "2");
        user.put("status", "1"); // 停用

        mockMvc.perform(post("/system/user/changeStatus")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(51)
    @DisplayName("6.2 修改超级管理员状态 - 不允许")
    void testChangeStatus_AdminUser() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> user = new HashMap<>();
        user.put("id", "1");
        user.put("status", "1");

        mockMvc.perform(post("/system/user/changeStatus")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ============================================
    // 唯一性校验测试
    // ============================================

    @Test
    @Order(60)
    @DisplayName("7.1 校验登录名唯一性 - 唯一")
    void testCheckLoginNameUnique_Unique() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/user/checkLoginNameUnique")
                        .header("Authorization", "Bearer " + token)
                        .param("loginName", "uniqueuser123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @Order(61)
    @DisplayName("7.2 校验登录名唯一性 - 不唯一")
    void testCheckLoginNameUnique_NotUnique() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/user/checkLoginNameUnique")
                        .header("Authorization", "Bearer " + token)
                        .param("loginName", "admin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @Order(62)
    @DisplayName("7.3 校验手机号唯一性")
    void testCheckPhoneUnique() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/user/checkPhoneUnique")
                        .header("Authorization", "Bearer " + token)
                        .param("phonenumber", "19999999999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @Order(63)
    @DisplayName("7.4 校验邮箱唯一性")
    void testCheckEmailUnique() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/user/checkEmailUnique")
                        .header("Authorization", "Bearer " + token)
                        .param("email", "unique@test.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    // ============================================
    // 部门树测试
    // ============================================

    @Test
    @Order(70)
    @DisplayName("8.1 获取部门树数据")
    void testDeptTreeData() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(get("/system/user/deptTreeData")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ============================================
    // 用户授权角色测试
    // ============================================

    @Test
    @Order(80)
    @DisplayName("9.1 用户授权角色成功")
    void testInsertAuthRole() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/user/authRole/insertAuthRole")
                        .header("Authorization", "Bearer " + token)
                        .param("userId", "3")
                        .param("roleIds", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

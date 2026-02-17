package com.zjjh.fdtemp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 集成测试类 - 包含登录、权限、安全漏洞测试
 *
 * API 响应规范：
 * - HTTP 状态码始终为 200
 * - 业务状态通过 code 字段区分：0=成功，500=错误
 *
 * API 路径规范（RuoYi风格）：
 * - 列表查询: POST /xxx/list 或 GET /xxx/list
 * - 新增: POST /xxx/add
 * - 编辑: POST /xxx/edit
 * - 删除: POST /xxx/remove
 */
@SpringBootTest(classes = FdtempApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 测试用户凭据
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
    // 登录/登出测试
    // ============================================

    @Test
    @Order(1)
    @DisplayName("1.1 管理员登录成功")
    void testAdminLoginSuccess() throws Exception {
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("username", ADMIN_USERNAME);
        loginBody.put("password", ADMIN_PASSWORD);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginBody)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    @Order(2)
    @DisplayName("1.2 错误密码登录失败")
    void testLoginWithWrongPassword() throws Exception {
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("username", ADMIN_USERNAME);
        loginBody.put("password", "wrongpassword");

        // 业务错误返回 HTTP 200 + code 500
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginBody)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(3)
    @DisplayName("1.3 不存在的用户登录失败")
    void testLoginWithNonExistentUser() throws Exception {
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("username", "nonexistentuser");
        loginBody.put("password", "anypassword");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginBody)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(4)
    @DisplayName("1.4 获取用户信息成功")
    void testGetUserInfo() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(get("/auth/info")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.user.loginName").value(ADMIN_USERNAME))
                .andExpect(jsonPath("$.data.roles").isArray())
                .andExpect(jsonPath("$.data.permissions").isArray());
    }

    @Test
    @Order(5)
    @DisplayName("1.5 登出成功")
    void testLogout() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    // ============================================
    // 未认证访问测试
    // ============================================

    @Test
    @Order(10)
    @DisplayName("2.1 未认证访问受保护接口被拒绝")
    void testUnauthenticatedAccess() throws Exception {
        // Spring Security 返回 401 未认证
        mockMvc.perform(post("/system/user/list"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(11)
    @DisplayName("2.2 无效Token访问被拒绝")
    void testInvalidTokenAccess() throws Exception {
        mockMvc.perform(post("/system/user/list")
                .header("Authorization", "Bearer invalidtoken123"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // ============================================
    // 接口正常访问测试
    // ============================================

    @Test
    @Order(20)
    @DisplayName("3.1 管理员查询用户列表")
    void testAdminGetUserList() throws Exception {
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
    @Order(21)
    @DisplayName("3.2 管理员查询角色列表")
    void testAdminGetRoleList() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/role/list")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());
    }

    @Test
    @Order(22)
    @DisplayName("3.3 管理员查询部门列表")
    void testAdminGetDeptList() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(get("/system/dept/list")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(23)
    @DisplayName("3.4 管理员查询菜单列表")
    void testAdminGetMenuList() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(get("/system/menu/list")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(24)
    @DisplayName("3.5 管理员查询字典数据")
    void testAdminGetDictData() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/dict/data/list")
                .header("Authorization", "Bearer " + token)
                .param("dictType", "sys_user_sex"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());
    }

    // ============================================
    // 垂直越权测试 (普通用户尝试访问管理员接口)
    // 注意：权限拒绝返回 HTTP 200 + code=500，不是 HTTP 403
    // ============================================

    @Test
    @Order(30)
    @DisplayName("4.1 垂直越权-普通用户尝试新增用户")
    void testVerticalPrivilegeEscalation_AddUser() throws Exception {
        String token = login(NORMAL_USERNAME, NORMAL_PASSWORD);

        Map<String, Object> user = new HashMap<>();
        user.put("loginName", "hackeruser");
        user.put("userName", "黑客用户");
        user.put("password", "hack123");
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
    @Order(31)
    @DisplayName("4.2 垂直越权-普通用户尝试删除用户")
    void testVerticalPrivilegeEscalation_DeleteUser() throws Exception {
        String token = login(NORMAL_USERNAME, NORMAL_PASSWORD);

        mockMvc.perform(post("/system/user/remove")
                .header("Authorization", "Bearer " + token)
                .param("ids", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(32)
    @DisplayName("4.3 垂直越权-普通用户尝试修改角色")
    void testVerticalPrivilegeEscalation_EditRole() throws Exception {
        String token = login(NORMAL_USERNAME, NORMAL_PASSWORD);

        Map<String, Object> role = new HashMap<>();
        role.put("id", "2");
        role.put("roleName", "修改后的角色名");
        role.put("roleKey", "modified");

        mockMvc.perform(post("/system/role/edit")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(33)
    @DisplayName("4.4 垂直越权-普通用户尝试新增菜单")
    void testVerticalPrivilegeEscalation_AddMenu() throws Exception {
        String token = login(NORMAL_USERNAME, NORMAL_PASSWORD);

        Map<String, Object> menu = new HashMap<>();
        menu.put("menuName", "黑客菜单");
        menu.put("parentId", "0");

        mockMvc.perform(post("/system/menu/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menu)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ============================================
    // XSS漏洞测试
    // ============================================

    @Test
    @Order(40)
    @DisplayName("5.1 XSS测试-用户名注入脚本")
    void testXssInUserName() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> user = new HashMap<>();
        user.put("loginName", "xssuser");
        user.put("userName", "<script>alert('xss')</script>测试用户");
        user.put("password", "test123");
        user.put("deptId", "2");

        // 请求应该能正常处理（XSS过滤后），或者返回业务错误
        mockMvc.perform(post("/system/user/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // HTTP 状态码应该是 200（成功或业务错误）或 403（权限不足）
                    Assertions.assertTrue(status == 200 || status == 403,
                            "请求应该被正常处理或拒绝，当前状态码: " + status);
                });
    }

    @Test
    @Order(41)
    @DisplayName("5.2 XSS测试-搜索参数注入脚本")
    void testXssInSearchParams() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        String xssPayload = "<script>alert('xss')</script>";
        mockMvc.perform(post("/system/user/list")
                .header("Authorization", "Bearer " + token)
                .param("loginName", xssPayload))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());
    }

    // ============================================
    // 路径穿越测试
    // ============================================

    @Test
    @Order(50)
    @DisplayName("6.1 路径穿越-尝试访问系统文件")
    void testPathTraversal() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(get("/system/config/../../../etc/passwd")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // 应该返回404或400，而不是200
                    Assertions.assertNotEquals(200, status, "路径穿越攻击应该被阻止");
                });
    }

    @Test
    @Order(51)
    @DisplayName("6.2 路径穿越-尝试使用..%2F编码")
    void testPathTraversalEncoded() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(get("/system/config/..%2F..%2F..%2Fetc%2Fpasswd")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    Assertions.assertNotEquals(200, status, "编码路径穿越攻击应该被阻止");
                });
    }

    // ============================================
    // SQL注入测试
    // ============================================

    @Test
    @Order(60)
    @DisplayName("7.1 SQL注入-登录用户名")
    void testSqlInjection_Login() throws Exception {
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("username", "admin' OR '1'='1");
        loginBody.put("password", "anything");

        // SQL注入应该被MyBatis的预编译语句防止，返回认证失败
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginBody)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(61)
    @DisplayName("7.2 SQL注入-用户列表搜索")
    void testSqlInjection_UserList() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        // SQL注入应该被MyBatis的预编译语句防止
        mockMvc.perform(post("/system/user/list")
                .header("Authorization", "Bearer " + token)
                .param("loginName", "admin' OR '1'='1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());
    }

    // ============================================
    // 异常处理测试
    // ============================================

    @Test
    @Order(70)
    @DisplayName("8.1 异常处理-无效JSON格式")
    void testExceptionHandling_InvalidJson() throws Exception {
        // 无效 JSON 应该返回业务错误或 HTTP 400
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // 可以是 HTTP 400 或 HTTP 200 + code 500
                    Assertions.assertTrue(status == 400 || status == 200,
                            "无效JSON应该返回错误响应");
                });
    }

    @Test
    @Order(71)
    @DisplayName("8.2 异常处理-不支持的HTTP方法")
    void testExceptionHandling_UnsupportedMethod() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        // 使用 DELETE 方法访问只支持 GET/POST 的接口
        mockMvc.perform(delete("/auth/login")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    // 应该是 405 或 200 + 业务错误
                    Assertions.assertTrue(status == 405 || status == 200,
                            "不支持的HTTP方法应该返回错误响应");
                });
    }

    // ============================================
    // 权限边界测试
    // ============================================

    @Test
    @Order(80)
    @DisplayName("9.1 管理员可以访问核心接口")
    void testAdminCanAccessCoreEndpoints() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        // 测试管理员可以访问各模块列表接口
        mockMvc.perform(post("/system/user/list")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/system/role/list")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/system/menu/list")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/system/dept/list")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @Order(81)
    @DisplayName("9.2 普通用户只能访问被授权的接口")
    void testNormalUserCanOnlyAccessAuthorizedEndpoints() throws Exception {
        String token = login(NORMAL_USERNAME, NORMAL_PASSWORD);

        // 普通用户可以查看用户列表 (有 system:user:query 权限)
        mockMvc.perform(post("/system/user/list")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());

        // 普通用户不能新增用户 (没有 system:user:add 权限)
        // 权限拒绝返回 HTTP 200 + code=500
        mockMvc.perform(post("/system/user/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ============================================
    // Token安全测试
    // ============================================

    @Test
    @Order(90)
    @DisplayName("10.1 空Token被拒绝")
    void testEmptyToken() throws Exception {
        mockMvc.perform(post("/system/user/list")
                .header("Authorization", "Bearer "))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(91)
    @DisplayName("10.2 格式错误的Token被拒绝")
    void testMalformedToken() throws Exception {
        mockMvc.perform(post("/system/user/list")
                .header("Authorization", "InvalidFormat"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(92)
    @DisplayName("10.3 Token过期验证")
    void testExpiredToken() throws Exception {
        // 使用一个明显过期的token格式
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYxNjIzOTAyMiwiZXhwIjoxNjE2MjM5MDIyfQ.invalid";

        mockMvc.perform(post("/system/user/list")
                .header("Authorization", "Bearer " + expiredToken))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}

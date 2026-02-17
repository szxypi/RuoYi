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
 * SysPostController 单元测试类
 */
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

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String NORMAL_USERNAME = "normal";
    private static final String NORMAL_PASSWORD = "admin123";

    private static final String CEO_POST_ID = "1";
    private static final String PM_POST_ID = "2";

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
    // 岗位列表查询测试
    // ============================================

    @Test
    @Order(1)
    @DisplayName("1.1 管理员查询岗位列表成功")
    void testList_WithAdmin() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/post/list")
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
    @DisplayName("1.2 按条件查询岗位列表")
    void testList_WithCondition() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/post/list")
                .header("Authorization", "Bearer " + token)
                .param("postName", "董事长"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());
    }

    @Test
    @Order(3)
    @DisplayName("1.3 按岗位编码查询")
    void testList_ByPostCode() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/post/list")
                .header("Authorization", "Bearer " + token)
                .param("postCode", "ceo"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray());
    }

    @Test
    @Order(4)
    @DisplayName("1.4 未认证访问岗位列表 - 返回401")
    void testList_Unauthorized() throws Exception {
        mockMvc.perform(post("/system/post/list"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // ============================================
    // 岗位新增测试
    // ============================================

    @Test
    @Order(10)
    @DisplayName("2.1 管理员新增岗位成功")
    void testAdd_Success() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> post = new HashMap<>();
        post.put("postName", "测试新增岗位");
        post.put("postCode", "test_post");
        post.put("postSort", "99");
        post.put("status", "0");

        mockMvc.perform(post("/system/post/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(11)
    @DisplayName("2.2 新增岗位 - 岗位名称已存在")
    void testAdd_DuplicatePostName() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> post = new HashMap<>();
        post.put("postName", "董事长"); // 已存在的岗位名
        post.put("postCode", "test_dup_name");
        post.put("postSort", "99");

        mockMvc.perform(post("/system/post/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(12)
    @DisplayName("2.3 新增岗位 - 岗位编码已存在")
    void testAdd_DuplicatePostCode() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> post = new HashMap<>();
        post.put("postName", "测试岗位编码重复");
        post.put("postCode", "ceo"); // 已存在的岗位编码
        post.put("postSort", "99");

        mockMvc.perform(post("/system/post/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(13)
    @DisplayName("2.4 普通用户新增岗位 - 无权限")
    void testAdd_NoPermission() throws Exception {
        String token = login(NORMAL_USERNAME, NORMAL_PASSWORD);

        Map<String, Object> post = new HashMap<>();
        post.put("postName", "无权限新增岗位");
        post.put("postCode", "no_perm_post");
        post.put("postSort", "99");

        mockMvc.perform(post("/system/post/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ============================================
    // 岗位修改测试
    // ============================================

    @Test
    @Order(20)
    @DisplayName("3.1 管理员修改岗位成功")
    void testEdit_Success() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> post = new HashMap<>();
        post.put("id", PM_POST_ID);
        post.put("postName", "修改后的项目经理");
        post.put("postCode", "se");
        post.put("postSort", "2");
        post.put("status", "0");

        mockMvc.perform(post("/system/post/edit")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(21)
    @DisplayName("3.2 修改岗位 - 岗位名称重复")
    void testEdit_DuplicatePostName() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> post = new HashMap<>();
        post.put("id", PM_POST_ID);
        post.put("postName", "董事长"); // 与现有岗位名重复
        post.put("postCode", "se");
        post.put("postSort", "2");

        mockMvc.perform(post("/system/post/edit")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(22)
    @DisplayName("3.3 修改岗位 - 岗位编码重复")
    void testEdit_DuplicatePostCode() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> post = new HashMap<>();
        post.put("id", PM_POST_ID);
        post.put("postName", "项目经理");
        post.put("postCode", "ceo"); // 与现有岗位编码重复
        post.put("postSort", "2");

        mockMvc.perform(post("/system/post/edit")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(23)
    @DisplayName("3.4 修改岗位状态成功")
    void testEdit_Status() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> post = new HashMap<>();
        post.put("id", PM_POST_ID);
        post.put("postName", "项目经理");
        post.put("postCode", "se");
        post.put("postSort", "2");
        post.put("status", "1"); // 停用

        mockMvc.perform(post("/system/post/edit")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    // ============================================
    // 岗位删除测试
    // ============================================

    @Test
    @Order(30)
    @DisplayName("4.1 管理员删除岗位成功")
    void testRemove_Success() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        // 先新增一个岗位用于删除
        Map<String, Object> post = new HashMap<>();
        post.put("postName", "待删除岗位");
        post.put("postCode", "to_be_deleted");
        post.put("postSort", "99");

        mockMvc.perform(post("/system/post/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isOk());

        // 查询岗位ID
        MvcResult result = mockMvc.perform(post("/system/post/list")
                .header("Authorization", "Bearer " + token)
                .param("postName", "待删除岗位"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(responseBody);
        String postId = root.path("rows").get(0).path("id").asText();

        // 删除岗位
        mockMvc.perform(post("/system/post/remove")
                .header("Authorization", "Bearer " + token)
                .param("ids", postId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(31)
    @DisplayName("4.2 删除已分配用户的岗位 - 不允许")
    void testRemove_Allocated() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        // 董事长岗位已分配给用户
        mockMvc.perform(post("/system/post/remove")
                .header("Authorization", "Bearer " + token)
                .param("ids", CEO_POST_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(32)
    @DisplayName("4.3 批量删除岗位")
    void testRemove_Batch() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        // 先新增两个岗位用于删除
        Map<String, Object> post1 = new HashMap<>();
        post1.put("postName", "批量删除岗位1");
        post1.put("postCode", "batch_delete_1");
        post1.put("postSort", "99");

        Map<String, Object> post2 = new HashMap<>();
        post2.put("postName", "批量删除岗位2");
        post2.put("postCode", "batch_delete_2");
        post2.put("postSort", "99");

        mockMvc.perform(post("/system/post/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/system/post/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post2)))
                .andExpect(status().isOk());

        // 查询岗位ID
        MvcResult result = mockMvc.perform(post("/system/post/list")
                .header("Authorization", "Bearer " + token))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(responseBody);

        String id1 = null, id2 = null;
        for (JsonNode node : root.path("rows")) {
            String name = node.path("postName").asText();
            if ("批量删除岗位1".equals(name)) {
                id1 = node.path("id").asText();
            } else if ("批量删除岗位2".equals(name)) {
                id2 = node.path("id").asText();
            }
        }

        assertNotNull(id1);
        assertNotNull(id2);

        // 批量删除
        mockMvc.perform(post("/system/post/remove")
                .header("Authorization", "Bearer " + token)
                .param("ids", id1 + "," + id2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    // ============================================
    // 唯一性校验测试
    // ============================================

    @Test
    @Order(40)
    @DisplayName("5.1 校验岗位名称唯一性 - 唯一")
    void testCheckPostNameUnique_Unique() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/post/checkPostNameUnique")
                .header("Authorization", "Bearer " + token)
                .param("postName", "唯一岗位名测试"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @Order(41)
    @DisplayName("5.2 校验岗位名称唯一性 - 不唯一")
    void testCheckPostNameUnique_NotUnique() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/post/checkPostNameUnique")
                .header("Authorization", "Bearer " + token)
                .param("postName", "董事长"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @Order(42)
    @DisplayName("5.3 校验岗位编码唯一性 - 唯一")
    void testCheckPostCodeUnique_Unique() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/post/checkPostCodeUnique")
                .header("Authorization", "Bearer " + token)
                .param("postCode", "unique_post_code"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @Order(43)
    @DisplayName("5.4 校验岗位编码唯一性 - 不唯一")
    void testCheckPostCodeUnique_NotUnique() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/post/checkPostCodeUnique")
                .header("Authorization", "Bearer " + token)
                .param("postCode", "ceo"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // ============================================
    // 权限测试
    // ============================================

    @Test
    @Order(50)
    @DisplayName("6.1 普通用户查询岗位列表 - 有权限")
    void testList_WithNormalUser() throws Exception {
        String token = login(NORMAL_USERNAME, NORMAL_PASSWORD);

        mockMvc.perform(post("/system/post/list")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(51)
    @DisplayName("6.2 普通用户删除岗位 - 无权限")
    void testRemove_NoPermission() throws Exception {
        String token = login(NORMAL_USERNAME, NORMAL_PASSWORD);

        mockMvc.perform(post("/system/post/remove")
                .header("Authorization", "Bearer " + token)
                .param("ids", "4"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}

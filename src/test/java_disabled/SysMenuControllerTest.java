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
 * SysMenuController 单元测试类
 */
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

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String NORMAL_USERNAME = "normal";
    private static final String NORMAL_PASSWORD = "admin123";

    private static final String SYSTEM_MENU_ID = "1";
    private static final String USER_MENU_ID = "2";
    private static final String ROLE_MENU_ID = "3";

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
    // 菜单列表查询测试
    // ============================================

    @Test
    @Order(1)
    @DisplayName("1.1 管理员查询菜单列表成功")
    void testList_WithAdmin() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(get("/system/menu/list")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(2)
    @DisplayName("1.2 按条件查询菜单列表")
    void testList_WithCondition() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(get("/system/menu/list")
                        .header("Authorization", "Bearer " + token)
                        .param("menuName", "系统管理"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(3)
    @DisplayName("1.3 未认证访问菜单列表 - 返回401")
    void testList_Unauthorized() throws Exception {
        mockMvc.perform(get("/system/menu/list"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    // ============================================
    // 菜单新增测试
    // ============================================

    @Test
    @Order(10)
    @DisplayName("2.1 管理员新增菜单成功 - 目录类型")
    void testAdd_Directory() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> menu = new HashMap<>();
        menu.put("menuName", "测试新增目录");
        menu.put("parentId", "0");
        menu.put("orderNum", "99");
        menu.put("menuType", "M");
        menu.put("visible", "0");
        menu.put("url", "#");
        menu.put("icon", "test");

        mockMvc.perform(post("/system/menu/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menu)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(11)
    @DisplayName("2.2 管理员新增菜单成功 - 菜单类型")
    void testAdd_MenuType() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> menu = new HashMap<>();
        menu.put("menuName", "测试新增菜单页面");
        menu.put("parentId", SYSTEM_MENU_ID);
        menu.put("orderNum", "99");
        menu.put("menuType", "C");
        menu.put("visible", "0");
        menu.put("url", "/system/test");
        menu.put("perms", "system:test:list");
        menu.put("icon", "test");

        mockMvc.perform(post("/system/menu/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menu)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(12)
    @DisplayName("2.3 管理员新增菜单成功 - 按钮类型")
    void testAdd_ButtonType() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> menu = new HashMap<>();
        menu.put("menuName", "测试新增按钮");
        menu.put("parentId", USER_MENU_ID);
        menu.put("orderNum", "99");
        menu.put("menuType", "F");
        menu.put("visible", "0");
        menu.put("perms", "system:user:testBtn");

        mockMvc.perform(post("/system/menu/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menu)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(13)
    @DisplayName("2.4 新增菜单 - 菜单名称已存在")
    void testAdd_DuplicateMenuName() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> menu = new HashMap<>();
        menu.put("menuName", "系统管理"); // 已存在的菜单名
        menu.put("parentId", "0");
        menu.put("orderNum", "99");
        menu.put("menuType", "M");

        mockMvc.perform(post("/system/menu/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menu)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(14)
    @DisplayName("2.5 普通用户新增菜单 - 无权限")
    void testAdd_NoPermission() throws Exception {
        String token = login(NORMAL_USERNAME, NORMAL_PASSWORD);

        Map<String, Object> menu = new HashMap<>();
        menu.put("menuName", "无权限新增菜单");
        menu.put("parentId", "0");
        menu.put("orderNum", "99");
        menu.put("menuType", "M");

        mockMvc.perform(post("/system/menu/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menu)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ============================================
    // 菜单修改测试
    // ============================================

    @Test
    @Order(20)
    @DisplayName("3.1 管理员修改菜单成功")
    void testEdit_Success() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> menu = new HashMap<>();
        menu.put("id", ROLE_MENU_ID);
        menu.put("menuName", "修改后的角色管理");
        menu.put("parentId", SYSTEM_MENU_ID);
        menu.put("orderNum", "2");
        menu.put("menuType", "C");
        menu.put("visible", "0");
        menu.put("url", "/system/role");
        menu.put("perms", "system:role:list");

        mockMvc.perform(post("/system/menu/edit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menu)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(21)
    @DisplayName("3.2 修改菜单 - 菜单名称重复")
    void testEdit_DuplicateMenuName() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        Map<String, Object> menu = new HashMap<>();
        menu.put("id", ROLE_MENU_ID);
        menu.put("menuName", "用户管理"); // 与现有菜单名重复
        menu.put("parentId", SYSTEM_MENU_ID);
        menu.put("orderNum", "2");
        menu.put("menuType", "C");

        mockMvc.perform(post("/system/menu/edit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menu)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ============================================
    // 菜单删除测试
    // ============================================

    @Test
    @Order(30)
    @DisplayName("4.1 删除有子菜单的菜单 - 不允许")
    void testRemove_HasChildren() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(delete("/system/menu/" + SYSTEM_MENU_ID)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(31)
    @DisplayName("4.2 删除已分配角色的菜单 - 不允许")
    void testRemove_Allocated() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        // 用户管理菜单已分配给角色
        mockMvc.perform(delete("/system/menu/" + USER_MENU_ID)
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @Order(32)
    @DisplayName("4.3 删除未分配的菜单成功")
    void testRemove_Success() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        // 先新增一个菜单用于删除
        Map<String, Object> menu = new HashMap<>();
        menu.put("menuName", "待删除菜单");
        menu.put("parentId", SYSTEM_MENU_ID);
        menu.put("orderNum", "99");
        menu.put("menuType", "F");
        menu.put("perms", "system:test:delete");

        mockMvc.perform(post("/system/menu/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menu)))
                .andExpect(status().isOk());

        // 查询菜单ID
        MvcResult result = mockMvc.perform(get("/system/menu/list")
                        .header("Authorization", "Bearer " + token)
                        .param("menuName", "待删除菜单"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(responseBody);
        String menuId = root.get(0).path("id").asText();

        // 删除菜单
        mockMvc.perform(delete("/system/menu/" + menuId)
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
    @DisplayName("5.1 校验菜单名称唯一性 - 唯一")
    void testCheckMenuNameUnique_Unique() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/menu/checkMenuNameUnique")
                        .header("Authorization", "Bearer " + token)
                        .param("menuName", "唯一菜单名测试")
                        .param("parentId", SYSTEM_MENU_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @Order(41)
    @DisplayName("5.2 校验菜单名称唯一性 - 不唯一")
    void testCheckMenuNameUnique_NotUnique() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/menu/checkMenuNameUnique")
                        .header("Authorization", "Bearer " + token)
                        .param("menuName", "系统管理")
                        .param("parentId", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // ============================================
    // 菜单排序测试
    // ============================================

    @Test
    @Order(50)
    @DisplayName("6.1 修改菜单排序成功")
    void testUpdateSort() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(post("/system/menu/updateSort")
                        .header("Authorization", "Bearer " + token)
                        .param("menuIds", ROLE_MENU_ID)
                        .param("orderNums", "99"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    // ============================================
    // 菜单树测试
    // ============================================

    @Test
    @Order(60)
    @DisplayName("7.1 获取角色菜单树数据")
    void testRoleMenuTreeData() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(get("/system/menu/roleMenuTreeData")
                        .header("Authorization", "Bearer " + token)
                        .param("id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(61)
    @DisplayName("7.2 获取所有菜单树数据")
    void testMenuTreeData() throws Exception {
        String token = login(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(get("/system/menu/menuTreeData")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

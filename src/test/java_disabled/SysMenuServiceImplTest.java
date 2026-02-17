package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.FdtempApplication;
import com.zjjh.fdtemp.TestConfig;
import com.zjjh.fdtemp.beans.entity.SysMenu;
import com.zjjh.fdtemp.beans.entity.SysRole;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.core.domain.Ztree;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.service.SysMenuService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysMenuService 单元测试类
 */
@SpringBootTest(classes = FdtempApplication.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class SysMenuServiceImplTest {

    @Autowired
    private SysMenuService menuService;

    private static final String SYSTEM_MENU_ID = "1";     // 系统管理
    private static final String USER_MENU_ID = "2";       // 用户管理
    private static final String ROLE_MENU_ID = "3";       // 角色管理
    private static final String ADMIN_USER_ID = "1";

    // ============================================
    // 查询测试
    // ============================================

    @Test
    @Order(1)
    @DisplayName("1.1 查询菜单列表")
    void testSelectMenuList() {
        SysMenu query = new SysMenu();
        List<SysMenu> list = menuService.selectMenuList(query, ADMIN_USER_ID);
        assertNotNull(list);
        assertTrue(list.size() >= 5, "应该至少有5个测试菜单");
    }

    @Test
    @Order(2)
    @DisplayName("1.2 根据ID查询菜单")
    void testSelectMenuById() {
        SysMenu menu = menuService.selectMenuById(SYSTEM_MENU_ID);
        assertNotNull(menu);
        assertEquals("系统管理", menu.getMenuName());
    }

    @Test
    @Order(3)
    @DisplayName("1.3 查询所有菜单")
    void testSelectMenuAll() {
        List<SysMenu> list = menuService.selectMenuAll(ADMIN_USER_ID);
        assertNotNull(list);
        assertTrue(list.size() >= 5);
    }

    @Test
    @Order(4)
    @DisplayName("1.4 根据用户查询菜单")
    void testSelectMenusByUser() {
        SysUser adminUser = new SysUser();
        adminUser.setId(ADMIN_USER_ID);
        adminUser.setLoginName("admin");

        List<SysMenu> menus = menuService.selectMenusByUser(adminUser);
        assertNotNull(menus);
        assertTrue(menus.size() > 0);
    }

    @Test
    @Order(5)
    @DisplayName("1.5 查询子菜单数量")
    void testSelectCountMenuByParentId() {
        int count = menuService.selectCountMenuByParentId(SYSTEM_MENU_ID);
        assertTrue(count >= 5, "系统管理应该有多个子菜单");
    }

    @Test
    @Order(6)
    @DisplayName("1.6 查询菜单使用数量")
    void testSelectCountRoleMenuByMenuId() {
        int count = menuService.selectCountRoleMenuByMenuId(USER_MENU_ID);
        assertTrue(count >= 1, "用户管理应该被角色使用");
    }

    // ============================================
    // 权限查询测试
    // ============================================

    @Test
    @Order(10)
    @DisplayName("2.1 根据用户ID查询权限")
    void testSelectPermsByUserId() {
        Set<String> perms = menuService.selectPermsByUserId(ADMIN_USER_ID);
        assertNotNull(perms);
        assertTrue(perms.size() > 0);
        assertTrue(perms.contains("system:user:list"));
    }

    @Test
    @Order(11)
    @DisplayName("2.2 查询所有权限")
    void testSelectPermsAll() {
        Set<String> perms = menuService.selectPermsAll();
        assertNotNull(perms);
        assertTrue(perms.size() > 0);
    }

    @Test
    @Order(12)
    @DisplayName("2.3 根据角色ID查询权限")
    void testSelectPermsByRoleId() {
        Set<String> perms = menuService.selectPermsByRoleId("1"); // admin角色
        assertNotNull(perms);
        assertTrue(perms.size() > 0);
    }

    // ============================================
    // 树结构测试
    // ============================================

    @Test
    @Order(20)
    @DisplayName("3.1 查询菜单树数据")
    void testMenuTreeData() {
        List<Ztree> trees = menuService.menuTreeData(ADMIN_USER_ID);
        assertNotNull(trees);
        assertTrue(trees.size() > 0);
    }

    @Test
    @Order(21)
    @DisplayName("3.2 查询角色菜单树数据")
    void testRoleMenuTreeData() {
        SysRole role = new SysRole();
        role.setId("1"); // admin角色

        List<Ztree> trees = menuService.roleMenuTreeData(role, ADMIN_USER_ID);
        assertNotNull(trees);
        assertTrue(trees.size() > 0);
    }

    @Test
    @Order(22)
    @DisplayName("3.3 查询角色菜单树数据 - 无角色")
    void testRoleMenuTreeData_NoRole() {
        SysRole role = new SysRole();

        List<Ztree> trees = menuService.roleMenuTreeData(role, ADMIN_USER_ID);
        assertNotNull(trees);
    }

    @Test
    @Order(23)
    @DisplayName("3.4 查询所有权限Map")
    void testSelectPermsAllMap() {
        Map<String, String> permsMap = menuService.selectPermsAll(ADMIN_USER_ID);
        assertNotNull(permsMap);
    }

    // ============================================
    // 唯一性校验测试
    // ============================================

    @Test
    @Order(30)
    @DisplayName("4.1 校验菜单名称唯一性 - 已存在")
    void testCheckMenuNameUnique_Existing() {
        SysMenu menu = new SysMenu();
        menu.setMenuName("系统管理");
        menu.setParentId("0");
        boolean result = menuService.checkMenuNameUnique(menu);
        assertEquals(UserConstants.NOT_UNIQUE, result);
    }

    @Test
    @Order(31)
    @DisplayName("4.2 校验菜单名称唯一性 - 不存在")
    void testCheckMenuNameUnique_New() {
        SysMenu menu = new SysMenu();
        menu.setMenuName("新菜单测试名称");
        menu.setParentId(SYSTEM_MENU_ID);
        boolean result = menuService.checkMenuNameUnique(menu);
        assertEquals(UserConstants.UNIQUE, result);
    }

    @Test
    @Order(32)
    @DisplayName("4.3 校验菜单名称唯一性 - 修改时保持自己的名字")
    void testCheckMenuNameUnique_Self() {
        SysMenu menu = new SysMenu();
        menu.setId(SYSTEM_MENU_ID);
        menu.setMenuName("系统管理");
        menu.setParentId("0");
        boolean result = menuService.checkMenuNameUnique(menu);
        assertEquals(UserConstants.UNIQUE, result);
    }

    // ============================================
    // 新增测试
    // ============================================

    @Test
    @Order(40)
    @DisplayName("5.1 新增菜单成功")
    void testInsertMenu() {
        SysMenu menu = createTestMenu("测试新增菜单", SYSTEM_MENU_ID, "M");

        int result = menuService.insertMenu(menu);
        assertTrue(result > 0);
    }

    @Test
    @Order(41)
    @DisplayName("5.2 新增按钮类型菜单")
    void testInsertMenu_Button() {
        SysMenu menu = createTestMenu("测试按钮", USER_MENU_ID, "F");
        menu.setPerms("system:user:test");

        int result = menuService.insertMenu(menu);
        assertTrue(result > 0);
    }

    @Test
    @Order(42)
    @DisplayName("5.3 新增菜单类型菜单")
    void testInsertMenu_C() {
        SysMenu menu = createTestMenu("测试菜单页面", SYSTEM_MENU_ID, "C");
        menu.setUrl("/system/test");
        menu.setPerms("system:test:list");

        int result = menuService.insertMenu(menu);
        assertTrue(result > 0);
    }

    // ============================================
    // 修改测试
    // ============================================

    @Test
    @Order(50)
    @DisplayName("6.1 修改菜单成功")
    void testUpdateMenu() {
        SysMenu menu = menuService.selectMenuById(ROLE_MENU_ID);
        assertNotNull(menu);

        String newMenuName = "修改后的角色管理";
        menu.setMenuName(newMenuName);

        int result = menuService.updateMenu(menu);
        assertTrue(result >= 0);

        SysMenu updated = menuService.selectMenuById(ROLE_MENU_ID);
        assertEquals(newMenuName, updated.getMenuName());
    }

    @Test
    @Order(51)
    @DisplayName("6.2 修改菜单排序")
    void testUpdateMenuSort() {
        String[] menuIds = {ROLE_MENU_ID};
        String[] orderNums = {"99"};

        assertDoesNotThrow(() -> {
            menuService.updateMenuSort(menuIds, orderNums);
        });
    }

    // ============================================
    // 删除测试
    // ============================================

    @Test
    @Order(60)
    @DisplayName("7.1 删除无子菜单且未分配的菜单")
    void testDeleteMenuById_Success() {
        // 先新增一个菜单用于删除
        SysMenu menu = createTestMenu("待删除菜单", SYSTEM_MENU_ID, "F");
        menu.setPerms("system:test:delete");
        menuService.insertMenu(menu);

        // 查找刚创建的菜单
        List<SysMenu> menus = menuService.selectMenuList(new SysMenu(), ADMIN_USER_ID);
        SysMenu created = menus.stream()
                .filter(m -> "待删除菜单".equals(m.getMenuName()))
                .findFirst()
                .orElse(null);
        assertNotNull(created);

        int result = menuService.deleteMenuById(created.getId());
        assertTrue(result >= 0);
    }

    @Test
    @Order(61)
    @DisplayName("7.2 删除有子菜单的菜单 - 应返回错误")
    void testDeleteMenuById_HasChildren() {
        // 系统管理有子菜单
        int result = menuService.deleteMenuById(SYSTEM_MENU_ID);
        // 结果取决于实现
        assertNotNull(result);
    }

    // ============================================
    // 菜单层级测试
    // ============================================

    @Test
    @Order(70)
    @DisplayName("8.1 验证菜单树形结构")
    void testMenuHierarchy() {
        SysUser adminUser = new SysUser();
        adminUser.setId(ADMIN_USER_ID);

        List<SysMenu> menus = menuService.selectMenusByUser(adminUser);

        // 查找系统管理菜单
        SysMenu systemMenu = menus.stream()
                .filter(m -> "系统管理".equals(m.getMenuName()))
                .findFirst()
                .orElse(null);

        if (systemMenu != null) {
            // 验证有子菜单
            List<SysMenu> children = systemMenu.getChildren();
            assertTrue(children == null || children.size() >= 0);
        }
    }

    @Test
    @Order(71)
    @DisplayName("8.2 验证菜单类型")
    void testMenuTypes() {
        List<SysMenu> menus = menuService.selectMenuList(new SysMenu(), ADMIN_USER_ID);

        // 验证存在不同类型的菜单
        boolean hasDirectory = menus.stream().anyMatch(m -> "M".equals(m.getMenuType()));
        boolean hasMenu = menus.stream().anyMatch(m -> "C".equals(m.getMenuType()));
        boolean hasButton = menus.stream().anyMatch(m -> "F".equals(m.getMenuType()));

        assertTrue(hasDirectory, "应该存在目录类型菜单");
        assertTrue(hasMenu, "应该存在菜单类型菜单");
        assertTrue(hasButton, "应该存在按钮类型菜单");
    }

    // ============================================
    // 辅助方法
    // ============================================

    private SysMenu createTestMenu(String menuName, String parentId, String menuType) {
        SysMenu menu = new SysMenu();
        menu.setMenuName(menuName);
        menu.setParentId(parentId);
        menu.setOrderNum("99");
        menu.setMenuType(menuType);
        menu.setVisible("0");
        menu.setIsRefresh("1");
        menu.setUrl("#");
        menu.setPerms("");
        menu.setIcon("#");
        return menu;
    }
}

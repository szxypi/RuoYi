package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.FdtempApplication;
import com.zjjh.fdtemp.TestConfig;
import com.zjjh.fdtemp.beans.LoginUser;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FdtempApplication.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class SysMenuServiceImplTest {

    @Autowired
    private SysMenuService menuService;

    private static final String SYSTEM_MENU_ID = "1";
    private static final String USER_MENU_ID = "2";
    private static final String ROLE_MENU_ID = "3";
    private static final String ADMIN_USER_ID = "1";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        SysUser adminUser = new SysUser();
        adminUser.setId("1");
        adminUser.setLoginName("admin");
        adminUser.setUserName("超级管理员");
        adminUser.setStatus("0");
        SysRole adminRole = new SysRole();
        adminRole.setRoleKey("admin");
        adminUser.setRoles(List.of(adminRole));

        Set<String> permissions = new HashSet<>();
        permissions.add("*:*:*");
        LoginUser loginUser = new LoginUser(adminUser, permissions);

        Authentication auth = new UsernamePasswordAuthenticationToken(
            loginUser, null, loginUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @Order(1)
    @DisplayName("1.1 查询菜单列表")
    void testSelectMenuList() {
        SysMenu query = new SysMenu();
        List<SysMenu> list = menuService.selectMenuList(query, ADMIN_USER_ID);
        assertNotNull(list);
        assertTrue(list.size() >= 5);
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
        assertTrue(count >= 5);
    }

    @Test
    @Order(10)
    @DisplayName("2.1 根据用户ID查询权限")
    void testSelectPermsByUserId() {
        Set<String> perms = menuService.selectPermsByUserId(ADMIN_USER_ID);
        assertNotNull(perms);
        assertTrue(perms.size() > 0);
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
        role.setId("1");
        List<Ztree> trees = menuService.roleMenuTreeData(role, ADMIN_USER_ID);
        assertNotNull(trees);
        assertTrue(trees.size() > 0);
    }

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
    @Order(40)
    @DisplayName("5.1 新增菜单成功")
    @Disabled("H2 不支持 MySQL 的 sysdate() 函数")
    void testInsertMenu() {
        SysMenu menu = createTestMenu("测试新增菜单", SYSTEM_MENU_ID, "M");
        int result = menuService.insertMenu(menu);
        assertTrue(result > 0);
    }

    @Test
    @Order(50)
    @DisplayName("6.1 修改菜单成功")
    @Disabled("H2 不支持 MySQL 的 sysdate() 函数")
    void testUpdateMenu() {
        SysMenu menu = menuService.selectMenuById(ROLE_MENU_ID);
        assertNotNull(menu);
        String newMenuName = "修改后的角色管理";
        menu.setMenuName(newMenuName);
        int result = menuService.updateMenu(menu);
        assertTrue(result >= 0);
    }

    @Test
    @Order(70)
    @DisplayName("8.1 验证菜单类型")
    void testMenuTypes() {
        List<SysMenu> menus = menuService.selectMenuList(new SysMenu(), ADMIN_USER_ID);
        boolean hasDirectory = menus.stream().anyMatch(m -> "M".equals(m.getMenuType()));
        boolean hasMenu = menus.stream().anyMatch(m -> "C".equals(m.getMenuType()));
        boolean hasButton = menus.stream().anyMatch(m -> "F".equals(m.getMenuType()));
        assertTrue(hasDirectory);
        assertTrue(hasMenu);
        assertTrue(hasButton);
    }

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

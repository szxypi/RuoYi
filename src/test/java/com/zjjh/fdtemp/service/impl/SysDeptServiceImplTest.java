package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.FdtempApplication;
import com.zjjh.fdtemp.TestConfig;
import com.zjjh.fdtemp.beans.LoginUser;
import com.zjjh.fdtemp.beans.entity.SysDept;
import com.zjjh.fdtemp.beans.entity.SysRole;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.core.domain.Ztree;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.service.SysDeptService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FdtempApplication.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class SysDeptServiceImplTest {

    @Autowired
    private SysDeptService deptService;

    private static final String ROOT_DEPT_ID = "1";
    private static final String SHENZHEN_DEPT_ID = "2";
    private static final String RD_DEPT_ID = "3";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        SysUser adminUser = new SysUser();
        adminUser.setId("1");
        adminUser.setLoginName("admin");
        adminUser.setUserName("超级管理员");
        adminUser.setStatus("0");

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
    @DisplayName("1.1 查询所有部门列表")
    void testSelectDeptList() {
        SysDept query = new SysDept();
        List<SysDept> list = deptService.selectDeptList(query);
        assertNotNull(list);
        assertTrue(list.size() >= 4);
    }

    @Test
    @Order(2)
    @DisplayName("1.2 根据ID查询部门")
    void testSelectDeptById() {
        SysDept dept = deptService.selectDeptById(ROOT_DEPT_ID);
        assertNotNull(dept);
        assertEquals("总公司", dept.getDeptName());
    }

    @Test
    @Order(3)
    @DisplayName("1.3 查询部门树结构")
    void testSelectDeptTree() {
        List<Ztree> trees = deptService.selectDeptTree(new SysDept());
        assertNotNull(trees);
        assertTrue(trees.size() > 0);
    }

    @Test
    @Order(4)
    @DisplayName("1.4 查询角色部门树")
    void testRoleDeptTreeData() {
        SysRole role = new SysRole();
        role.setId("2");
        List<Ztree> trees = deptService.roleDeptTreeData(role);
        assertNotNull(trees);
    }

    @Test
    @Order(5)
    @DisplayName("1.5 查询下级部门数量")
    void testSelectDeptCount() {
        int count = deptService.selectDeptCount(ROOT_DEPT_ID);
        assertTrue(count >= 1);
    }

    @Test
    @Order(6)
    @DisplayName("1.6 检查部门是否存在用户")
    void testCheckDeptExistUser() {
        boolean exists = deptService.checkDeptExistUser(ROOT_DEPT_ID);
        assertTrue(exists);
    }

    @Test
    @Order(10)
    @DisplayName("2.1 校验部门名称唯一性 - 已存在")
    void testCheckDeptNameUnique_Existing() {
        SysDept dept = new SysDept();
        dept.setDeptName("总公司");
        dept.setParentId("0");
        boolean result = deptService.checkDeptNameUnique(dept);
        assertEquals(UserConstants.NOT_UNIQUE, result);
    }

    @Test
    @Order(11)
    @DisplayName("2.2 校验部门名称唯一性 - 不存在")
    void testCheckDeptNameUnique_New() {
        SysDept dept = new SysDept();
        dept.setDeptName("新部门测试名称");
        dept.setParentId(ROOT_DEPT_ID);
        boolean result = deptService.checkDeptNameUnique(dept);
        assertEquals(UserConstants.UNIQUE, result);
    }

    @Test
    @Order(20)
    @DisplayName("3.1 新增部门成功")
    @Disabled("H2 不支持 MySQL 的 sysdate() 函数")
    void testInsertDept() {
        SysDept dept = createTestDept("测试新增部门", SHENZHEN_DEPT_ID);
        int result = deptService.insertDept(dept);
        assertTrue(result > 0);
    }

    @Test
    @Order(30)
    @DisplayName("4.1 修改部门成功")
    @Disabled("H2 不支持 MySQL 的 sysdate() 函数")
    void testUpdateDept() {
        SysDept dept = deptService.selectDeptById(RD_DEPT_ID);
        assertNotNull(dept);
        String newDeptName = "修改后的研发部门";
        dept.setDeptName(newDeptName);
        int result = deptService.updateDept(dept);
        assertTrue(result >= 0);
    }

    @Test
    @Order(40)
    @DisplayName("5.1 删除空部门成功")
    void testDeleteDeptById_Success() {
        int result = deptService.deleteDeptById("4");
        assertTrue(result >= 0);
    }

    private SysDept createTestDept(String deptName, String parentId) {
        SysDept dept = new SysDept();
        dept.setDeptName(deptName);
        dept.setParentId(parentId);
        dept.setOrderNum(99);
        dept.setLeader("test");
        dept.setPhone("13800000000");
        dept.setEmail("test@test.com");
        dept.setStatus("0");
        return dept;
    }
}

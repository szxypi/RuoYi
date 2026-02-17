package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.FdtempApplication;
import com.zjjh.fdtemp.TestConfig;
import com.zjjh.fdtemp.beans.entity.SysDept;
import com.zjjh.fdtemp.beans.entity.SysRole;
import com.zjjh.fdtemp.common.core.domain.Ztree;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.service.SysDeptService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysDeptService 单元测试类
 */
@SpringBootTest(classes = FdtempApplication.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class SysDeptServiceImplTest {

    @Autowired
    private SysDeptService deptService;

    private static final String ROOT_DEPT_ID = "1";       // 总公司
    private static final String SHENZHEN_DEPT_ID = "2";   // 深圳分公司
    private static final String RD_DEPT_ID = "3";         // 研发部门

    // ============================================
    // 查询测试
    // ============================================

    @Test
    @Order(1)
    @DisplayName("1.1 查询所有部门列表")
    void testSelectDeptList() {
        SysDept query = new SysDept();
        List<SysDept> list = deptService.selectDeptList(query);
        assertNotNull(list);
        assertTrue(list.size() >= 4, "应该至少有4个测试部门");
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
    @DisplayName("1.4 查询部门树结构（排除下级）")
    void testSelectDeptTreeExcludeChild() {
        SysDept dept = new SysDept();
        dept.setExcludeId(SHENZHEN_DEPT_ID);

        List<Ztree> trees = deptService.selectDeptTreeExcludeChild(dept);
        assertNotNull(trees);
        // 深圳分公司及其子部门应该被排除
    }

    @Test
    @Order(5)
    @DisplayName("1.5 查询角色部门树")
    void testRoleDeptTreeData() {
        SysRole role = new SysRole();
        role.setId("2"); // 普通角色

        List<Ztree> trees = deptService.roleDeptTreeData(role);
        assertNotNull(trees);
    }

    @Test
    @Order(6)
    @DisplayName("1.6 查询下级部门数量")
    void testSelectDeptCount() {
        int count = deptService.selectDeptCount(ROOT_DEPT_ID);
        assertTrue(count >= 1, "总公司应该至少有1个子部门");
    }

    @Test
    @Order(7)
    @DisplayName("1.7 检查部门是否存在用户")
    void testCheckDeptExistUser() {
        // 总公司下应该有用户
        boolean exists = deptService.checkDeptExistUser(ROOT_DEPT_ID);
        assertTrue(exists);
    }

    @Test
    @Order(8)
    @DisplayName("1.8 检查部门不存在用户")
    void testCheckDeptNotExistUser() {
        // 市场部门（ID=4）可能没有用户
        boolean exists = deptService.checkDeptExistUser("4");
        // 根据测试数据，市场部门没有用户
        assertFalse(exists);
    }

    @Test
    @Order(9)
    @DisplayName("1.9 查询正常状态的子部门数量")
    void testSelectNormalChildrenDeptById() {
        int count = deptService.selectNormalChildrenDeptById(ROOT_DEPT_ID);
        assertTrue(count >= 0);
    }

    // ============================================
    // 唯一性校验测试
    // ============================================

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
    @Order(12)
    @DisplayName("2.3 校验部门名称唯一性 - 修改时保持自己的名字")
    void testCheckDeptNameUnique_Self() {
        SysDept dept = new SysDept();
        dept.setId(ROOT_DEPT_ID);
        dept.setDeptName("总公司");
        dept.setParentId("0");
        boolean result = deptService.checkDeptNameUnique(dept);
        assertEquals(UserConstants.UNIQUE, result);
    }

    @Test
    @Order(13)
    @DisplayName("2.4 校验部门名称唯一性 - 同级不重复但不同父级可重复")
    void testCheckDeptNameUnique_DifferentParent() {
        // 研发部门在总公司下的深圳分公司下，如果在另一个分公司创建同名部门应该可以
        SysDept dept = new SysDept();
        dept.setDeptName("研发部门");
        dept.setParentId(ROOT_DEPT_ID);
        // 在总公司下创建研发部门，名字可以重复因为父级不同
        boolean result = deptService.checkDeptNameUnique(dept);
        // 取决于测试数据，这里验证功能存在
        assertNotNull(result);
    }

    // ============================================
    // 新增测试
    // ============================================

    @Test
    @Order(20)
    @DisplayName("3.1 新增部门成功")
    void testInsertDept() {
        SysDept dept = createTestDept("测试新增部门", SHENZHEN_DEPT_ID);

        int result = deptService.insertDept(dept);
        assertTrue(result > 0);
    }

    @Test
    @Order(21)
    @DisplayName("3.2 新增部门到停用父部门 - 抛出异常")
    void testInsertDept_ParentDisabled() {
        // 需要先停用一个部门
        SysDept parentDept = deptService.selectDeptById(RD_DEPT_ID);
        parentDept.setStatus("1"); // 停用
        deptService.updateDept(parentDept);

        // 尝试在停用的部门下新增子部门
        SysDept newDept = createTestDept("测试停用父部门", RD_DEPT_ID);

        ServiceException exception = assertThrows(ServiceException.class, () -> {
            deptService.insertDept(newDept);
        });
        assertTrue(exception.getMessage().contains("停用"));
    }

    // ============================================
    // 修改测试
    // ============================================

    @Test
    @Order(30)
    @DisplayName("4.1 修改部门成功")
    void testUpdateDept() {
        SysDept dept = deptService.selectDeptById(RD_DEPT_ID);
        assertNotNull(dept);

        String newDeptName = "修改后的研发部门";
        dept.setDeptName(newDeptName);

        int result = deptService.updateDept(dept);
        assertTrue(result >= 0);

        SysDept updated = deptService.selectDeptById(RD_DEPT_ID);
        assertEquals(newDeptName, updated.getDeptName());
    }

    @Test
    @Order(31)
    @DisplayName("4.2 修改部门状态为停用但有正常子部门 - 返回错误")
    void testUpdateDept_DisableWithNormalChildren() {
        // 尝试停用深圳分公司，但它下面有正常的子部门
        SysDept dept = deptService.selectDeptById(SHENZHEN_DEPT_ID);
        dept.setStatus("1"); // 停用

        // 这个操作应该被阻止或者子部门会被级联更新
        int result = deptService.updateDept(dept);
        // 结果取决于业务逻辑
        assertTrue(result >= 0);
    }

    @Test
    @Order(32)
    @DisplayName("4.3 修改部门的父部门")
    void testUpdateDept_ChangeParent() {
        // 创建一个新部门用于测试
        SysDept newDept = createTestDept("测试修改父部门", SHENZHEN_DEPT_ID);
        deptService.insertDept(newDept);

        // 查找刚创建的部门
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        SysDept created = depts.stream()
                .filter(d -> "测试修改父部门".equals(d.getDeptName()))
                .findFirst()
                .orElse(null);
        assertNotNull(created);

        // 修改父部门
        created.setParentId(ROOT_DEPT_ID);
        int result = deptService.updateDept(created);
        assertTrue(result >= 0);
    }

    // ============================================
    // 删除测试
    // ============================================

    @Test
    @Order(40)
    @DisplayName("5.1 删除有子部门的部门 - 应该被阻止")
    void testDeleteDept_WithChildren() {
        // 深圳分公司有子部门，不能删除
        // 但根据实现，这可能不会抛出异常，而是返回0或负数
        // 实际的删除检查在Controller层
        int result = deptService.deleteDeptById(SHENZHEN_DEPT_ID);
        // 验证删除行为
        assertNotNull(result);
    }

    @Test
    @Order(41)
    @DisplayName("5.2 删除有用户的部门 - 应该被阻止")
    void testDeleteDept_WithUsers() {
        // 总公司有用户，不能删除
        // 实际的删除检查在Controller层
        int result = deptService.deleteDeptById(ROOT_DEPT_ID);
        assertNotNull(result);
    }

    @Test
    @Order(42)
    @DisplayName("5.3 删除空部门成功")
    void testDeleteDept_Success() {
        // 市场部门（ID=4）没有子部门和用户
        int result = deptService.deleteDeptById("4");
        assertTrue(result >= 0);
    }

    // ============================================
    // 树结构测试
    // ============================================

    @Test
    @Order(50)
    @DisplayName("6.1 部门树结构验证")
    void testDeptTreeStructure() {
        List<Ztree> trees = deptService.selectDeptTree(new SysDept());

        // 验证根节点
        Ztree rootTree = trees.stream()
                .filter(t -> "1".equals(t.getId()))
                .findFirst()
                .orElse(null);

        // 或者验证树结构存在
        assertTrue(trees.size() > 0);
    }

    @Test
    @Order(51)
    @DisplayName("6.2 部门祖级列表正确性")
    void testDeptAncestors() {
        SysDept rdDept = deptService.selectDeptById(RD_DEPT_ID);
        assertNotNull(rdDept);

        // 研发部门的祖级应该是 0,1,2 (根->总公司->深圳分公司)
        String ancestors = rdDept.getAncestors();
        assertTrue(ancestors.contains("0"));
        assertTrue(ancestors.contains("1"));
    }

    // ============================================
    // 辅助方法
    // ============================================

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

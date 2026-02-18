package com.zjjh.fdtemp.beans.entity;

import org.junit.jupiter.api.*;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysDictType 实体类单元测试
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SysDictTypeTest {

    private SysDictType dictType;

    @BeforeEach
    void setUp() {
        dictType = new SysDictType();
    }

    @Test
    @Order(1)
    @DisplayName("设置和获取ID")
    void testId() {
        dictType.setId("test-id-123");
        assertEquals("test-id-123", dictType.getId());
    }

    @Test
    @Order(2)
    @DisplayName("设置和获取字典名称")
    void testDictName() {
        dictType.setDictName("用户性别");
        assertEquals("用户性别", dictType.getDictName());
    }

    @Test
    @Order(3)
    @DisplayName("设置和获取字典类型")
    void testDictType() {
        dictType.setDictType("sys_user_sex");
        assertEquals("sys_user_sex", dictType.getDictType());
    }

    @Test
    @Order(4)
    @DisplayName("设置和获取状态")
    void testStatus() {
        dictType.setStatus("0");
        assertEquals("0", dictType.getStatus());
        dictType.setStatus("1");
        assertEquals("1", dictType.getStatus());
    }

    @Test
    @Order(10)
    @DisplayName("设置和获取创建用户")
    void testCreateUser() {
        dictType.setCreateUser("admin");
        assertEquals("admin", dictType.getCreateUser());
    }

    @Test
    @Order(11)
    @DisplayName("设置和获取创建时间")
    void testCreateTime() {
        Date now = new Date();
        dictType.setCreateTime(now);
        assertEquals(now, dictType.getCreateTime());
    }

    @Test
    @Order(20)
    @DisplayName("toString方法包含所有属性")
    void testToString() {
        dictType.setId("1");
        dictType.setDictName("用户性别");
        dictType.setDictType("sys_user_sex");
        dictType.setStatus("0");
        String str = dictType.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("用户性别"));
        assertTrue(str.contains("sys_user_sex"));
    }

    @Test
    @Order(30)
    @DisplayName("空值测试-null字典名称")
    void testNullDictName() {
        dictType.setDictName(null);
        assertNull(dictType.getDictName());
    }

    @Test
    @Order(31)
    @DisplayName("空值测试-null字典类型")
    void testNullDictType() {
        dictType.setDictType(null);
        assertNull(dictType.getDictType());
    }

    @Test
    @Order(32)
    @DisplayName("特殊字符测试")
    void testSpecialCharacters() {
        String specialChars = "<script>alert('xss')</script>&\"'";
        dictType.setDictName(specialChars);
        dictType.setDictType(specialChars);
        assertEquals(specialChars, dictType.getDictName());
        assertEquals(specialChars, dictType.getDictType());
    }

    @Test
    @Order(40)
    @DisplayName("字典类型-包含下划线")
    void testDictTypeWithUnderscore() {
        dictType.setDictType("sys_show_hide");
        assertEquals("sys_show_hide", dictType.getDictType());
    }

    @Test
    @Order(41)
    @DisplayName("字典类型-包含数字")
    void testDictTypeWithNumber() {
        dictType.setDictType("sys_type_1");
        assertEquals("sys_type_1", dictType.getDictType());
    }

    @Test
    @Order(50)
    @DisplayName("params参数初始化测试")
    void testParamsInitialization() {
        assertNotNull(dictType.getParams());
        assertTrue(dictType.getParams().isEmpty());
    }
}

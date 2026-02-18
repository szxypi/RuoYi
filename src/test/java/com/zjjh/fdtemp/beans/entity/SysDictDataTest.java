package com.zjjh.fdtemp.beans.entity;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysDictData 实体类单元测试
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SysDictDataTest {

    private SysDictData dictData;

    @BeforeEach
    void setUp() {
        dictData = new SysDictData();
    }

    @Test
    @Order(1)
    @DisplayName("设置和获取ID")
    void testId() {
        dictData.setId("test-id-123");
        assertEquals("test-id-123", dictData.getId());
    }

    @Test
    @Order(2)
    @DisplayName("设置和获取字典排序")
    void testDictSort() {
        dictData.setDictSort(1L);
        assertEquals(1L, dictData.getDictSort());
        dictData.setDictSort(100L);
        assertEquals(100L, dictData.getDictSort());
    }

    @Test
    @Order(3)
    @DisplayName("设置和获取字典标签")
    void testDictLabel() {
        dictData.setDictLabel("男");
        assertEquals("男", dictData.getDictLabel());
    }

    @Test
    @Order(4)
    @DisplayName("设置和获取字典键值")
    void testDictValue() {
        dictData.setDictValue("0");
        assertEquals("0", dictData.getDictValue());
    }

    @Test
    @Order(5)
    @DisplayName("设置和获取字典类型")
    void testDictType() {
        dictData.setDictType("sys_user_sex");
        assertEquals("sys_user_sex", dictData.getDictType());
    }

    @Test
    @Order(6)
    @DisplayName("设置和获取CSS类名")
    void testCssClass() {
        dictData.setCssClass("badge-primary");
        assertEquals("badge-primary", dictData.getCssClass());
    }

    @Test
    @Order(7)
    @DisplayName("设置和获取列表样式类")
    void testListClass() {
        dictData.setListClass("primary");
        assertEquals("primary", dictData.getListClass());
    }

    @Test
    @Order(8)
    @DisplayName("设置和获取是否默认")
    void testIsDefault() {
        dictData.setIsDefault("Y");
        assertEquals("Y", dictData.getIsDefault());
        dictData.setIsDefault("N");
        assertEquals("N", dictData.getIsDefault());
    }

    @Test
    @Order(9)
    @DisplayName("设置和获取状态")
    void testStatus() {
        dictData.setStatus("0");
        assertEquals("0", dictData.getStatus());
        dictData.setStatus("1");
        assertEquals("1", dictData.getStatus());
    }

    @Test
    @Order(20)
    @DisplayName("getDefault方法-是默认值")
    void testGetDefault_Yes() {
        dictData.setIsDefault("Y");
        assertTrue(dictData.getDefault());
    }

    @Test
    @Order(21)
    @DisplayName("getDefault方法-不是默认值")
    void testGetDefault_No() {
        dictData.setIsDefault("N");
        assertFalse(dictData.getDefault());
    }

    @Test
    @Order(22)
    @DisplayName("getDefault方法-null值")
    void testGetDefault_Null() {
        dictData.setIsDefault(null);
        assertFalse(dictData.getDefault());
    }

    @Test
    @Order(30)
    @DisplayName("排序值边界测试-0")
    void testDictSortZero() {
        dictData.setDictSort(0L);
        assertEquals(0L, dictData.getDictSort());
    }

    @Test
    @Order(31)
    @DisplayName("排序值边界测试-负数")
    void testDictSortNegative() {
        dictData.setDictSort(-1L);
        assertEquals(-1L, dictData.getDictSort());
    }

    @Test
    @Order(32)
    @DisplayName("排序值边界测试-大数值")
    void testDictSortLargeValue() {
        dictData.setDictSort(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, dictData.getDictSort());
    }

    @Test
    @Order(40)
    @DisplayName("特殊字符测试-字典标签")
    void testSpecialCharactersInLabel() {
        String specialChars = "<script>alert('xss')</script>&\"'";
        dictData.setDictLabel(specialChars);
        assertEquals(specialChars, dictData.getDictLabel());
    }

    @Test
    @Order(50)
    @DisplayName("CSS类名-primary")
    void testListClassPrimary() {
        dictData.setListClass("primary");
        assertEquals("primary", dictData.getListClass());
    }

    @Test
    @Order(51)
    @DisplayName("CSS类名-danger")
    void testListClassDanger() {
        dictData.setListClass("danger");
        assertEquals("danger", dictData.getListClass());
    }

    @Test
    @Order(60)
    @DisplayName("params参数初始化测试")
    void testParamsInitialization() {
        assertNotNull(dictData.getParams());
        assertTrue(dictData.getParams().isEmpty());
    }
}

package com.zjjh.fdtemp.beans.entity;

import org.junit.jupiter.api.*;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

/**
 * SysConfig 实体类单元测试
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SysConfigTest {

    private SysConfig config;

    @BeforeEach
    void setUp() {
        config = new SysConfig();
    }

    @Test
    @Order(1)
    @DisplayName("设置和获取ID")
    void testId() {
        config.setId("test-id-123");
        assertEquals("test-id-123", config.getId());
    }

    @Test
    @Order(2)
    @DisplayName("设置和获取配置名称")
    void testConfigName() {
        config.setConfigName("测试配置");
        assertEquals("测试配置", config.getConfigName());
    }

    @Test
    @Order(3)
    @DisplayName("设置和获取配置键名")
    void testConfigKey() {
        config.setConfigKey("test.config.key");
        assertEquals("test.config.key", config.getConfigKey());
    }

    @Test
    @Order(4)
    @DisplayName("设置和获取配置键值")
    void testConfigValue() {
        config.setConfigValue("配置值");
        assertEquals("配置值", config.getConfigValue());
    }

    @Test
    @Order(5)
    @DisplayName("设置和获取配置类型")
    void testConfigType() {
        config.setConfigType("Y");
        assertEquals("Y", config.getConfigType());
        config.setConfigType("N");
        assertEquals("N", config.getConfigType());
    }

    @Test
    @Order(10)
    @DisplayName("设置和获取创建用户")
    void testCreateUser() {
        config.setCreateUser("admin");
        assertEquals("admin", config.getCreateUser());
    }

    @Test
    @Order(11)
    @DisplayName("设置和获取创建时间")
    void testCreateTime() {
        Date now = new Date();
        config.setCreateTime(now);
        assertEquals(now, config.getCreateTime());
    }

    @Test
    @Order(20)
    @DisplayName("toString方法包含所有属性")
    void testToString() {
        config.setId("1");
        config.setConfigName("测试配置");
        config.setConfigKey("test.key");
        config.setConfigValue("testValue");
        config.setConfigType("N");
        String str = config.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("测试配置"));
        assertTrue(str.contains("test.key"));
    }

    @Test
    @Order(30)
    @DisplayName("空值测试-null配置名称")
    void testNullConfigName() {
        config.setConfigName(null);
        assertNull(config.getConfigName());
    }

    @Test
    @Order(31)
    @DisplayName("空值测试-null配置键名")
    void testNullConfigKey() {
        config.setConfigKey(null);
        assertNull(config.getConfigKey());
    }

    @Test
    @Order(32)
    @DisplayName("空字符串测试")
    void testEmptyStrings() {
        config.setConfigName("");
        config.setConfigKey("");
        config.setConfigValue("");
        assertEquals("", config.getConfigName());
        assertEquals("", config.getConfigKey());
        assertEquals("", config.getConfigValue());
    }

    @Test
    @Order(33)
    @DisplayName("特殊字符测试")
    void testSpecialCharacters() {
        String specialChars = "<script>alert('xss')</script>&\"'";
        config.setConfigName(specialChars);
        config.setConfigKey(specialChars);
        config.setConfigValue(specialChars);
        assertEquals(specialChars, config.getConfigName());
        assertEquals(specialChars, config.getConfigKey());
        assertEquals(specialChars, config.getConfigValue());
    }

    @Test
    @Order(40)
    @DisplayName("params参数初始化测试")
    void testParamsInitialization() {
        assertNotNull(config.getParams());
        assertTrue(config.getParams().isEmpty());
    }
}

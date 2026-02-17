package com.zjjh.fdtemp.beans.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SysLogininfor 实体类单元测试
 */
@DisplayName("登录日志实体类测试")
class SysLogininforTest {

    private SysLogininfor logininfor;

    @BeforeEach
    void setUp() {
        logininfor = new SysLogininfor();
    }

    @Test
    @DisplayName("测试设置和获取loginName")
    void testLoginName() {
        logininfor.setLoginName("admin");
        assertEquals("admin", logininfor.getLoginName());
    }

    @Test
    @DisplayName("测试设置和获取status")
    void testStatus() {
        logininfor.setStatus("0");
        assertEquals("0", logininfor.getStatus());
    }

    @Test
    @DisplayName("测试设置和获取ipaddr")
    void testIpaddr() {
        logininfor.setIpaddr("192.168.1.100");
        assertEquals("192.168.1.100", logininfor.getIpaddr());
    }

    @Test
    @DisplayName("测试设置和获取loginLocation")
    void testLoginLocation() {
        logininfor.setLoginLocation("北京市 朝阳区");
        assertEquals("北京市 朝阳区", logininfor.getLoginLocation());
    }

    @Test
    @DisplayName("测试设置和获取browser")
    void testBrowser() {
        logininfor.setBrowser("Chrome 100.0");
        assertEquals("Chrome 100.0", logininfor.getBrowser());
    }

    @Test
    @DisplayName("测试设置和获取os")
    void testOs() {
        logininfor.setOs("Windows 10");
        assertEquals("Windows 10", logininfor.getOs());
    }

    @Test
    @DisplayName("测试设置和获取msg")
    void testMsg() {
        logininfor.setMsg("登录成功");
        assertEquals("登录成功", logininfor.getMsg());
    }

    @Test
    @DisplayName("测试设置和获取loginTime")
    void testLoginTime() {
        Date now = new Date();
        logininfor.setLoginTime(now);
        assertEquals(now, logininfor.getLoginTime());
    }

    @Test
    @DisplayName("测试继承自BaseEntity的属性")
    void testBaseEntityProperties() {
        logininfor.setId("test-id-456");
        logininfor.setCreateUser("system");
        logininfor.setCreateTime(new Date());

        assertEquals("test-id-456", logininfor.getId());
        assertEquals("system", logininfor.getCreateUser());
        assertNotNull(logininfor.getCreateTime());
    }

    @Test
    @DisplayName("测试空值处理")
    void testNullValues() {
        SysLogininfor empty = new SysLogininfor();

        assertNull(empty.getLoginName());
        assertNull(empty.getStatus());
        assertNull(empty.getIpaddr());
        assertNull(empty.getLoginLocation());
        assertNull(empty.getBrowser());
        assertNull(empty.getOs());
        assertNull(empty.getMsg());
        assertNull(empty.getLoginTime());
    }

    @Test
    @DisplayName("测试登录状态值")
    void testLoginStatusValues() {
        logininfor.setStatus("0");
        assertEquals("0", logininfor.getStatus());

        logininfor.setStatus("1");
        assertEquals("1", logininfor.getStatus());
    }

    @Test
    @DisplayName("测试IP地址格式")
    void testIpAddressFormats() {
        logininfor.setIpaddr("192.168.1.1");
        assertEquals("192.168.1.1", logininfor.getIpaddr());

        logininfor.setIpaddr("::1");
        assertEquals("::1", logininfor.getIpaddr());

        logininfor.setIpaddr("127.0.0.1");
        assertEquals("127.0.0.1", logininfor.getIpaddr());
    }

    @Test
    @DisplayName("测试常见浏览器类型")
    void testBrowserTypes() {
        logininfor.setBrowser("Chrome 100.0.4896.127");
        assertEquals("Chrome 100.0.4896.127", logininfor.getBrowser());

        logininfor.setBrowser("Firefox 99.0");
        assertEquals("Firefox 99.0", logininfor.getBrowser());
    }

    @Test
    @DisplayName("测试常见操作系统类型")
    void testOsTypes() {
        logininfor.setOs("Windows 10");
        assertEquals("Windows 10", logininfor.getOs());

        logininfor.setOs("Mac OS X 12.3");
        assertEquals("Mac OS X 12.3", logininfor.getOs());

        logininfor.setOs("Linux Ubuntu 20.04");
        assertEquals("Linux Ubuntu 20.04", logininfor.getOs());
    }

    @Test
    @DisplayName("测试登录消息类型")
    void testMessageTypes() {
        logininfor.setMsg("登录成功");
        assertEquals("登录成功", logininfor.getMsg());

        logininfor.setMsg("用户名或密码错误");
        assertEquals("用户名或密码错误", logininfor.getMsg());

        logininfor.setMsg("退出成功");
        assertEquals("退出成功", logininfor.getMsg());
    }

    @Test
    @DisplayName("测试特殊字符处理")
    void testSpecialCharacters() {
        logininfor.setLoginName("user@test");
        assertEquals("user@test", logininfor.getLoginName());

        logininfor.setLoginLocation("北京市（朝阳区）");
        assertEquals("北京市（朝阳区）", logininfor.getLoginLocation());
    }

    @Test
    @DisplayName("测试长字符串")
    void testLongStrings() {
        String longLocation = "北京市".repeat(50);
        logininfor.setLoginLocation(longLocation);
        assertEquals(longLocation, logininfor.getLoginLocation());
    }

    @Test
    @DisplayName("测试params属性继承")
    void testParamsProperty() {
        logininfor.getParams().put("key", "value");
        assertTrue(logininfor.getParams().containsKey("key"));
        assertEquals("value", logininfor.getParams().get("key"));
    }

    @Test
    @DisplayName("测试searchValue属性继承")
    void testSearchValueProperty() {
        logininfor.setSearchValue("test");
        assertEquals("test", logininfor.getSearchValue());
    }
}

package com.zjjh.fdtemp.common.web.domain.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("系统信息测试")
class SysTest {

    private Sys sys;

    @BeforeEach
    void setUp() {
        sys = new Sys();
    }

    @Test
    @DisplayName("测试设置和获取服务器名称")
    void testComputerName() {
        sys.setComputerName("DESKTOP-ABC123");
        assertEquals("DESKTOP-ABC123", sys.getComputerName());
    }

    @Test
    @DisplayName("测试设置和获取服务器IP")
    void testComputerIp() {
        sys.setComputerIp("192.168.1.100");
        assertEquals("192.168.1.100", sys.getComputerIp());
    }

    @Test
    @DisplayName("测试设置和获取项目路径")
    void testUserDir() {
        sys.setUserDir("/opt/app/myproject");
        assertEquals("/opt/app/myproject", sys.getUserDir());
    }

    @Test
    @DisplayName("测试设置和获取操作系统名称")
    void testOsName() {
        sys.setOsName("Windows 10");
        assertEquals("Windows 10", sys.getOsName());
    }

    @Test
    @DisplayName("测试设置和获取系统架构")
    void testOsArch() {
        sys.setOsArch("amd64");
        assertEquals("amd64", sys.getOsArch());
    }

    @Test
    @DisplayName("测试初始值")
    void testDefaultValues() {
        Sys newSys = new Sys();
        assertNull(newSys.getComputerName());
        assertNull(newSys.getComputerIp());
        assertNull(newSys.getUserDir());
        assertNull(newSys.getOsName());
        assertNull(newSys.getOsArch());
    }

    @Test
    @DisplayName("测试常见CPU架构")
    void testCommonArchitectures() {
        sys.setOsArch("x86_64");
        assertEquals("x86_64", sys.getOsArch());
        sys.setOsArch("amd64");
        assertEquals("amd64", sys.getOsArch());
        sys.setOsArch("aarch64");
        assertEquals("aarch64", sys.getOsArch());
    }
}

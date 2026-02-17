package com.zjjh.fdtemp.common.web.domain.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JVM信息测试")
class JvmTest {

    private Jvm jvm;

    @BeforeEach
    void setUp() {
        jvm = new Jvm();
    }

    @Test
    @DisplayName("测试设置和获取JVM总内存(MB)")
    void testTotal() {
        jvm.setTotal(512L * 1024 * 1024);
        assertEquals(512.0, jvm.getTotal(), 1.0);
    }

    @Test
    @DisplayName("测试设置和获取JVM最大内存(MB)")
    void testMax() {
        jvm.setMax(1024L * 1024 * 1024);
        assertEquals(1024.0, jvm.getMax(), 1.0);
    }

    @Test
    @DisplayName("测试设置和获取JVM空闲内存(MB)")
    void testFree() {
        jvm.setFree(256L * 1024 * 1024);
        assertEquals(256.0, jvm.getFree(), 1.0);
    }

    @Test
    @DisplayName("测试获取JVM已用内存(MB)")
    void testUsed() {
        jvm.setTotal(512L * 1024 * 1024);
        jvm.setFree(256L * 1024 * 1024);
        assertEquals(256.0, jvm.getUsed(), 1.0);
    }

    @Test
    @DisplayName("测试JVM内存使用率")
    void testUsage() {
        jvm.setTotal(512L * 1024 * 1024);
        jvm.setFree(256L * 1024 * 1024);
        assertEquals(50.0, jvm.getUsage(), 1.0);
    }

    @Test
    @DisplayName("测试设置和获取JDK版本")
    void testVersion() {
        jvm.setVersion("17.0.1");
        assertEquals("17.0.1", jvm.getVersion());
    }

    @Test
    @DisplayName("测试设置和获取JDK路径")
    void testHome() {
        String home = "/usr/lib/jvm/java-17-openjdk";
        jvm.setHome(home);
        assertEquals(home, jvm.getHome());
    }

    @Test
    @DisplayName("测试获取JDK名称")
    void testGetName() {
        String name = jvm.getName();
        assertNotNull(name);
        assertFalse(name.isEmpty());
    }

    @Test
    @DisplayName("测试初始值")
    void testDefaultValues() {
        Jvm newJvm = new Jvm();
        assertEquals(0.0, newJvm.getTotal());
        assertEquals(0.0, newJvm.getMax());
        assertEquals(0.0, newJvm.getFree());
        assertNull(newJvm.getVersion());
        assertNull(newJvm.getHome());
    }
}

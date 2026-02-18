package com.zjjh.fdtemp.common.web.domain.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("内存信息测试")
class MemTest {

    private Mem mem;

    @BeforeEach
    void setUp() {
        mem = new Mem();
    }

    @Test
    @DisplayName("测试设置和获取内存总量(GB)")
    void testTotal() {
        long totalBytes = 16L * 1024 * 1024 * 1024;
        mem.setTotal(totalBytes);
        assertTrue(mem.getTotal() > 0);
    }

    @Test
    @DisplayName("测试设置和获取已用内存(GB)")
    void testUsed() {
        long usedBytes = 8L * 1024 * 1024 * 1024;
        mem.setUsed(usedBytes);
        assertTrue(mem.getUsed() > 0);
    }

    @Test
    @DisplayName("测试设置和获取空闲内存(GB)")
    void testFree() {
        long freeBytes = 4L * 1024 * 1024 * 1024;
        mem.setFree(freeBytes);
        assertTrue(mem.getFree() > 0);
    }

    @Test
    @DisplayName("测试内存使用率计算")
    void testUsage() {
        mem.setTotal(16L * 1024 * 1024 * 1024);
        mem.setUsed(8L * 1024 * 1024 * 1024);
        double usage = mem.getUsage();
        assertEquals(50.0, usage, 1.0);
    }

    @Test
    @DisplayName("测试内存使用率为0")
    void testUsageZero() {
        mem.setTotal(16L * 1024 * 1024 * 1024);
        mem.setUsed(0);
        assertEquals(0.0, mem.getUsage(), 0.1);
    }

    @Test
    @DisplayName("测试初始值")
    void testDefaultValues() {
        Mem newMem = new Mem();
        assertEquals(0.0, newMem.getTotal());
        assertEquals(0.0, newMem.getUsed());
        assertEquals(0.0, newMem.getFree());
    }
}

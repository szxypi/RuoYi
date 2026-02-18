package com.zjjh.fdtemp.common.web.domain.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CPU信息测试")
class CpuTest {

    private Cpu cpu;

    @BeforeEach
    void setUp() {
        cpu = new Cpu();
    }

    @Test
    @DisplayName("测试设置和获取CPU核心数")
    void testCpuNum() {
        cpu.setCpuNum(8);
        assertEquals(8, cpu.getCpuNum());
    }

    @Test
    @DisplayName("测试CPU核心数为0")
    void testCpuNumZero() {
        cpu.setCpuNum(0);
        assertEquals(0, cpu.getCpuNum());
    }

    @Test
    @DisplayName("测试CPU核心数为1")
    void testCpuNumOne() {
        cpu.setCpuNum(1);
        assertEquals(1, cpu.getCpuNum());
    }

    @Test
    @DisplayName("测试CPU核心数为大数值")
    void testCpuNumLarge() {
        cpu.setCpuNum(128);
        assertEquals(128, cpu.getCpuNum());
    }

    @Test
    @DisplayName("测试各字段初始值")
    void testDefaultValues() {
        Cpu newCpu = new Cpu();
        assertEquals(0, newCpu.getCpuNum());
        assertEquals(0.0, newCpu.getTotal());
    }

    @Test
    @DisplayName("测试典型使用场景")
    void testTypicalUsage() {
        cpu.setCpuNum(8);
        cpu.setTotal(1000);
        cpu.setSys(150);
        cpu.setUsed(350);
        cpu.setWait(50);
        cpu.setFree(450);

        assertEquals(8, cpu.getCpuNum());
        assertEquals(100000.0, cpu.getTotal());
    }
}

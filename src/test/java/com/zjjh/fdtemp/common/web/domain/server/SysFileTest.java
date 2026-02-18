package com.zjjh.fdtemp.common.web.domain.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("系统文件信息测试")
class SysFileTest {

    private SysFile sysFile;

    @BeforeEach
    void setUp() {
        sysFile = new SysFile();
    }

    @Test
    @DisplayName("测试设置和获取盘符路径")
    void testDirName() {
        sysFile.setDirName("C:\\");
        assertEquals("C:\\", sysFile.getDirName());
    }

    @Test
    @DisplayName("测试设置和获取盘符类型")
    void testSysTypeName() {
        sysFile.setSysTypeName("NTFS");
        assertEquals("NTFS", sysFile.getSysTypeName());
    }

    @Test
    @DisplayName("测试设置和获取文件类型")
    void testTypeName() {
        sysFile.setTypeName("本地固定磁盘");
        assertEquals("本地固定磁盘", sysFile.getTypeName());
    }

    @Test
    @DisplayName("测试设置和获取总大小")
    void testTotal() {
        sysFile.setTotal("500 GB");
        assertEquals("500 GB", sysFile.getTotal());
    }

    @Test
    @DisplayName("测试设置和获取剩余大小")
    void testFree() {
        sysFile.setFree("200 GB");
        assertEquals("200 GB", sysFile.getFree());
    }

    @Test
    @DisplayName("测试设置和获取已使用量")
    void testUsed() {
        sysFile.setUsed("300 GB");
        assertEquals("300 GB", sysFile.getUsed());
    }

    @Test
    @DisplayName("测试设置和获取使用率")
    void testUsage() {
        sysFile.setUsage(60.0);
        assertEquals(60.0, sysFile.getUsage(), 0.1);
    }

    @Test
    @DisplayName("测试初始值")
    void testDefaultValues() {
        SysFile newFile = new SysFile();
        assertNull(newFile.getDirName());
        assertNull(newFile.getSysTypeName());
        assertNull(newFile.getTypeName());
        assertNull(newFile.getTotal());
        assertNull(newFile.getFree());
        assertNull(newFile.getUsed());
        assertEquals(0.0, newFile.getUsage(), 0.1);
    }

    @Test
    @DisplayName("测试使用率为0")
    void testUsageZero() {
        sysFile.setUsage(0.0);
        assertEquals(0.0, sysFile.getUsage(), 0.1);
    }

    @Test
    @DisplayName("测试使用率为100%")
    void testUsageFull() {
        sysFile.setUsage(100.0);
        assertEquals(100.0, sysFile.getUsage(), 0.1);
    }
}

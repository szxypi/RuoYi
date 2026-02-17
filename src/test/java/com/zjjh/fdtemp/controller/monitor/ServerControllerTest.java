package com.zjjh.fdtemp.controller.monitor;

import com.zjjh.fdtemp.common.web.domain.Server;
import com.zjjh.fdtemp.common.web.domain.server.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("服务器监控控制器测试")
class ServerControllerTest {

    @InjectMocks
    private ServerController controller;

    @Test
    @DisplayName("测试Server的convertFileSize方法 - 字节")
    void testConvertFileSizeBytes() {
        Server server = new Server();
        String result = server.convertFileSize(100);
        assertEquals("100 B", result);
    }

    @Test
    @DisplayName("测试Server的convertFileSize方法 - KB")
    void testConvertFileSizeKB() {
        Server server = new Server();
        String result = server.convertFileSize(1024);
        assertTrue(result.contains("KB"));
    }

    @Test
    @DisplayName("测试Server的convertFileSize方法 - MB")
    void testConvertFileSizeMB() {
        Server server = new Server();
        String result = server.convertFileSize(1024 * 1024);
        assertTrue(result.contains("MB"));
    }

    @Test
    @DisplayName("测试Server的convertFileSize方法 - GB")
    void testConvertFileSizeGB() {
        Server server = new Server();
        String result = server.convertFileSize(1024L * 1024 * 1024);
        assertTrue(result.contains("GB"));
    }

    @Test
    @DisplayName("测试Server的setter方法")
    void testServerSetters() {
        Server server = new Server();
        Cpu cpu = new Cpu();
        cpu.setCpuNum(8);
        server.setCpu(cpu);
        assertEquals(8, server.getCpu().getCpuNum());

        Mem mem = new Mem();
        server.setMem(mem);
        assertNotNull(server.getMem());

        Jvm jvm = new Jvm();
        server.setJvm(jvm);
        assertNotNull(server.getJvm());

        Sys sys = new Sys();
        server.setSys(sys);
        assertNotNull(server.getSys());
    }

    @Test
    @DisplayName("测试Server初始值")
    void testServerDefaultValues() {
        Server server = new Server();
        assertNotNull(server.getCpu());
        assertNotNull(server.getMem());
        assertNotNull(server.getJvm());
        assertNotNull(server.getSys());
        assertNotNull(server.getSysFiles());
        assertTrue(server.getSysFiles().isEmpty());
    }

    @Test
    @DisplayName("测试Cpu类getter和setter")
    void testCpuGettersSetters() {
        Cpu cpu = new Cpu();
        cpu.setCpuNum(8);
        cpu.setTotal(1000);
        assertEquals(8, cpu.getCpuNum());
        assertTrue(cpu.getTotal() >= 0);
    }

    @Test
    @DisplayName("测试Mem类getter和setter")
    void testMemGettersSetters() {
        Mem mem = new Mem();
        mem.setTotal(16L * 1024 * 1024 * 1024);
        mem.setUsed(8L * 1024 * 1024 * 1024);
        mem.setFree(8L * 1024 * 1024 * 1024);
        assertTrue(mem.getTotal() > 0);
        assertTrue(mem.getUsed() > 0);
        assertTrue(mem.getFree() > 0);
    }

    @Test
    @DisplayName("测试Jvm类getter和setter")
    void testJvmGettersSetters() {
        Jvm jvm = new Jvm();
        jvm.setTotal(1024L * 1024 * 1024);
        jvm.setMax(2048L * 1024 * 1024);
        jvm.setFree(512L * 1024 * 1024);
        jvm.setVersion("17.0.1");
        jvm.setHome("/usr/lib/jvm/java-17");
        assertTrue(jvm.getTotal() > 0);
        assertTrue(jvm.getMax() > 0);
        assertEquals("17.0.1", jvm.getVersion());
    }

    @Test
    @DisplayName("测试Sys类getter和setter")
    void testSysGettersSetters() {
        Sys sys = new Sys();
        sys.setComputerName("SERVER-001");
        sys.setComputerIp("192.168.1.1");
        sys.setOsName("Linux");
        sys.setOsArch("amd64");
        sys.setUserDir("/opt/app");
        assertEquals("SERVER-001", sys.getComputerName());
        assertEquals("Linux", sys.getOsName());
    }

    @Test
    @DisplayName("测试SysFile类getter和setter")
    void testSysFileGettersSetters() {
        SysFile sysFile = new SysFile();
        sysFile.setDirName("/");
        sysFile.setSysTypeName("ext4");
        sysFile.setTypeName("Local Disk");
        sysFile.setTotal("100 GB");
        sysFile.setFree("50 GB");
        sysFile.setUsed("50 GB");
        sysFile.setUsage(50.0);
        assertEquals("/", sysFile.getDirName());
        assertEquals("ext4", sysFile.getSysTypeName());
        assertEquals(50.0, sysFile.getUsage(), 0.1);
    }
}

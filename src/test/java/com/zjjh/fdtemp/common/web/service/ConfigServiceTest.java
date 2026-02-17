package com.zjjh.fdtemp.common.web.service;

import com.zjjh.fdtemp.service.SysConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("配置服务测试")
class ConfigServiceTest {

    @Mock
    private SysConfigService configService;

    @InjectMocks
    private ConfigService config;

    @Test
    @DisplayName("测试获取配置值")
    void testGetKey() {
        String configKey = "sys.config.key";
        String expectedValue = "config-value";
        when(configService.selectConfigByKey(configKey)).thenReturn(expectedValue);
        String result = config.getKey(configKey);
        assertEquals(expectedValue, result);
        verify(configService, times(1)).selectConfigByKey(configKey);
    }

    @Test
    @DisplayName("测试获取配置值 - 空值")
    void testGetKeyEmpty() {
        String configKey = "nonexistent.key";
        when(configService.selectConfigByKey(configKey)).thenReturn("");
        String result = config.getKey(configKey);
        assertEquals("", result);
    }

    @Test
    @DisplayName("测试获取配置值 - null值")
    void testGetKeyNull() {
        String configKey = "nonexistent.key";
        when(configService.selectConfigByKey(configKey)).thenReturn(null);
        String result = config.getKey(configKey);
        assertNull(result);
    }

    @Test
    @DisplayName("测试获取配置值 - 数字类型")
    void testGetKeyNumeric() {
        String configKey = "sys.config.timeout";
        when(configService.selectConfigByKey(configKey)).thenReturn("30000");
        String result = config.getKey(configKey);
        assertEquals("30000", result);
    }

    @Test
    @DisplayName("测试多次获取同一配置")
    void testGetKeyMultipleTimes() {
        String configKey = "sys.config.key";
        when(configService.selectConfigByKey(configKey)).thenReturn("value");
        config.getKey(configKey);
        config.getKey(configKey);
        config.getKey(configKey);
        verify(configService, times(3)).selectConfigByKey(configKey);
    }

    @Test
    @DisplayName("测试获取不同配置")
    void testGetDifferentKeys() {
        when(configService.selectConfigByKey("key1")).thenReturn("value1");
        when(configService.selectConfigByKey("key2")).thenReturn("value2");
        assertEquals("value1", config.getKey("key1"));
        assertEquals("value2", config.getKey("key2"));
    }

    @Test
    @DisplayName("测试特殊字符键名")
    void testGetKeySpecialCharacters() {
        String specialKey = "sys.config.special-key_123.test";
        when(configService.selectConfigByKey(specialKey)).thenReturn("special-value");
        String result = config.getKey(specialKey);
        assertEquals("special-value", result);
    }
}

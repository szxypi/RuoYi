package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysConfig;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.common.utils.CacheUtils;
import com.zjjh.fdtemp.constants.Constants;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.dao.SysConfigDao;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SysConfigService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SysConfigServiceImplTest {

    @Mock
    private SysConfigDao configMapper;

    @InjectMocks
    private SysConfigServiceImpl configService;

    private MockedStatic<CacheUtils> cacheUtilsMock;

    @BeforeEach
    void setUp() {
        cacheUtilsMock = mockStatic(CacheUtils.class);
    }

    @AfterEach
    void tearDown() {
        cacheUtilsMock.close();
    }

    private SysConfig createTestConfig(String id, String configName, String configKey, String configValue, String configType) {
        SysConfig config = new SysConfig();
        config.setId(id);
        config.setConfigName(configName);
        config.setConfigKey(configKey);
        config.setConfigValue(configValue);
        config.setConfigType(configType);
        return config;
    }

    // ==================== selectConfigById 测试 ====================

    @Test
    @Order(1)
    @DisplayName("根据ID查询配置-正常情况")
    void testSelectConfigById_Success() {
        SysConfig expectedConfig = createTestConfig("1", "测试配置", "test.key", "testValue", "N");
        when(configMapper.selectConfig(any(SysConfig.class))).thenReturn(expectedConfig);

        SysConfig result = configService.selectConfigById("1");

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("测试配置", result.getConfigName());
    }

    @Test
    @Order(2)
    @DisplayName("根据ID查询配置-不存在返回null")
    void testSelectConfigById_NotFound() {
        when(configMapper.selectConfig(any(SysConfig.class))).thenReturn(null);

        SysConfig result = configService.selectConfigById("nonexistent");

        assertNull(result);
    }

    // ==================== selectConfigByKey 测试 ====================

    @Test
    @Order(10)
    @DisplayName("根据Key查询配置-从缓存获取")
    void testSelectConfigByKey_FromCache() {
        String configKey = "test.key";
        String configValue = "cachedValue";
        cacheUtilsMock.when(() -> CacheUtils.get(Constants.SYS_CONFIG_CACHE, Constants.SYS_CONFIG_KEY + configKey))
                .thenReturn(configValue);

        String result = configService.selectConfigByKey(configKey);

        assertEquals(configValue, result);
        verify(configMapper, never()).selectConfig(any());
    }

    @Test
    @Order(11)
    @DisplayName("根据Key查询配置-从数据库获取并缓存")
    void testSelectConfigByKey_FromDatabase() {
        String configKey = "test.key";
        String configValue = "dbValue";
        SysConfig config = createTestConfig("1", "测试", configKey, configValue, "N");

        cacheUtilsMock.when(() -> CacheUtils.get(Constants.SYS_CONFIG_CACHE, Constants.SYS_CONFIG_KEY + configKey))
                .thenReturn(null);
        when(configMapper.selectConfig(any(SysConfig.class))).thenReturn(config);

        String result = configService.selectConfigByKey(configKey);

        assertEquals(configValue, result);
    }

    @Test
    @Order(12)
    @DisplayName("根据Key查询配置-不存在返回空字符串")
    void testSelectConfigByKey_NotFound() {
        String configKey = "nonexistent.key";
        cacheUtilsMock.when(() -> CacheUtils.get(Constants.SYS_CONFIG_CACHE, Constants.SYS_CONFIG_KEY + configKey))
                .thenReturn(null);
        when(configMapper.selectConfig(any(SysConfig.class))).thenReturn(null);

        String result = configService.selectConfigByKey(configKey);

        assertEquals("", result);
    }

    // ==================== selectConfigList 测试 ====================

    @Test
    @Order(20)
    @DisplayName("查询配置列表-正常情况")
    void testSelectConfigList_Success() {
        List<SysConfig> expectedList = new ArrayList<>();
        expectedList.add(createTestConfig("1", "配置1", "key1", "value1", "N"));
        expectedList.add(createTestConfig("2", "配置2", "key2", "value2", "Y"));

        when(configMapper.selectConfigList(any(SysConfig.class))).thenReturn(expectedList);

        List<SysConfig> result = configService.selectConfigList(new SysConfig());

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // ==================== insertConfig 测试 ====================

    @Test
    @Order(30)
    @DisplayName("新增配置-成功并更新缓存")
    void testInsertConfig_Success() {
        SysConfig config = createTestConfig(null, "新配置", "new.key", "newValue", "N");
        when(configMapper.insertConfig(any(SysConfig.class))).thenReturn(1);

        int result = configService.insertConfig(config);

        assertEquals(1, result);
    }

    // ==================== updateConfig 测试 ====================

    @Test
    @Order(40)
    @DisplayName("更新配置-成功且key改变时删除旧缓存")
    void testUpdateConfig_Success_KeyChanged() {
        SysConfig oldConfig = createTestConfig("1", "旧配置", "old.key", "oldValue", "N");
        SysConfig newConfig = createTestConfig("1", "新配置", "new.key", "newValue", "N");

        when(configMapper.selectConfigById("1")).thenReturn(oldConfig);
        when(configMapper.updateConfig(any(SysConfig.class))).thenReturn(1);

        int result = configService.updateConfig(newConfig);

        assertEquals(1, result);
    }

    // ==================== deleteConfigByIds 测试 ====================

    @Test
    @Order(50)
    @DisplayName("删除配置-成功删除非内置配置")
    void testDeleteConfigByIds_Success() {
        SysConfig config = createTestConfig("1", "测试配置", "test.key", "value", "N");
        when(configMapper.selectConfig(any(SysConfig.class))).thenReturn(config);
        when(configMapper.deleteConfigById("1")).thenReturn(1);

        assertDoesNotThrow(() -> configService.deleteConfigByIds("1"));
        verify(configMapper).deleteConfigById("1");
    }

    @Test
    @Order(51)
    @DisplayName("删除配置-内置配置抛出异常")
    void testDeleteConfigByIds_BuiltInConfig() {
        SysConfig config = createTestConfig("1", "内置配置", "builtin.key", "value", UserConstants.YES);
        when(configMapper.selectConfig(any(SysConfig.class))).thenReturn(config);

        ServiceException exception = assertThrows(ServiceException.class, () -> configService.deleteConfigByIds("1"));
        assertTrue(exception.getMessage().contains("内置参数"));
        verify(configMapper, never()).deleteConfigById(anyString());
    }

    @Test
    @Order(52)
    @DisplayName("删除配置-批量删除多个")
    void testDeleteConfigByIds_Multiple() {
        SysConfig config1 = createTestConfig("1", "配置1", "key1", "value1", "N");
        SysConfig config2 = createTestConfig("2", "配置2", "key2", "value2", "N");

        when(configMapper.selectConfig(any(SysConfig.class)))
            .thenReturn(config1)
            .thenReturn(config2);
        when(configMapper.deleteConfigById(anyString())).thenReturn(1);

        assertDoesNotThrow(() -> configService.deleteConfigByIds("1,2"));
        verify(configMapper, times(2)).deleteConfigById(anyString());
    }

    // ==================== loadingConfigCache 测试 ====================

    @Test
    @Order(60)
    @DisplayName("加载配置缓存-成功")
    void testLoadingConfigCache_Success() {
        List<SysConfig> configs = new ArrayList<>();
        configs.add(createTestConfig("1", "配置1", "key1", "value1", "N"));
        configs.add(createTestConfig("2", "配置2", "key2", "value2", "Y"));

        when(configMapper.selectConfigList(any(SysConfig.class))).thenReturn(configs);

        configService.loadingConfigCache();

        verify(configMapper).selectConfigList(any(SysConfig.class));
    }

    // ==================== clearConfigCache 测试 ====================

    @Test
    @Order(70)
    @DisplayName("清空配置缓存-成功")
    void testClearConfigCache_Success() {
        configService.clearConfigCache();
        cacheUtilsMock.verify(() -> CacheUtils.removeAll(Constants.SYS_CONFIG_CACHE));
    }

    // ==================== resetConfigCache 测试 ====================

    @Test
    @Order(80)
    @DisplayName("重置配置缓存-成功")
    void testResetConfigCache_Success() {
        when(configMapper.selectConfigList(any(SysConfig.class))).thenReturn(new ArrayList<>());

        configService.resetConfigCache();

        cacheUtilsMock.verify(() -> CacheUtils.removeAll(Constants.SYS_CONFIG_CACHE));
        verify(configMapper).selectConfigList(any(SysConfig.class));
    }

    // ==================== checkConfigKeyUnique 测试 ====================

    @Test
    @Order(90)
    @DisplayName("校验配置Key唯一性-唯一")
    void testCheckConfigKeyUnique_Unique() {
        SysConfig config = createTestConfig(null, "测试", "unique.key", "value", "N");
        when(configMapper.checkConfigKeyUnique("unique.key")).thenReturn(null);

        boolean result = configService.checkConfigKeyUnique(config);

        assertTrue(result);
    }

    @Test
    @Order(91)
    @DisplayName("校验配置Key唯一性-不唯一(新配置)")
    void testCheckConfigKeyUnique_NotUnique_New() {
        SysConfig config = createTestConfig(null, "测试", "existing.key", "value", "N");
        SysConfig existingConfig = createTestConfig("1", "已存在", "existing.key", "value", "N");
        when(configMapper.checkConfigKeyUnique("existing.key")).thenReturn(existingConfig);

        boolean result = configService.checkConfigKeyUnique(config);

        assertFalse(result);
    }

    @Test
    @Order(92)
    @DisplayName("校验配置Key唯一性-唯一(更新自己的配置)")
    void testCheckConfigKeyUnique_Unique_UpdateSelf() {
        SysConfig config = createTestConfig("1", "测试", "existing.key", "value", "N");
        SysConfig existingConfig = createTestConfig("1", "已存在", "existing.key", "value", "N");
        when(configMapper.checkConfigKeyUnique("existing.key")).thenReturn(existingConfig);

        boolean result = configService.checkConfigKeyUnique(config);

        assertTrue(result);
    }
}

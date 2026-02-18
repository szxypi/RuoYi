package com.zjjh.fdtemp.controller.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjjh.fdtemp.beans.entity.SysConfig;
import com.zjjh.fdtemp.service.SysConfigService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SysConfigController 单元测试
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SysConfigControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SysConfigService configService;

    @InjectMocks
    private SysConfigController configController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(configController).build();
        objectMapper = new ObjectMapper();
    }

    private SysConfig createTestConfig(String id, String configName, String configKey, String configValue) {
        SysConfig config = new SysConfig();
        config.setId(id);
        config.setConfigName(configName);
        config.setConfigKey(configKey);
        config.setConfigValue(configValue);
        config.setConfigType("N");
        return config;
    }

    // ==================== list 测试 ====================

    @Test
    @Order(1)
    @DisplayName("查询配置列表-成功")
    void testList_Success() throws Exception {
        // Arrange
        List<SysConfig> configs = new ArrayList<>();
        configs.add(createTestConfig("1", "配置1", "key1", "value1"));
        configs.add(createTestConfig("2", "配置2", "key2", "value2"));

        when(configService.selectConfigList(org.mockito.ArgumentMatchers.any(SysConfig.class))).thenReturn(configs);

        // Act & Assert
        mockMvc.perform(post("/system/config/list")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("configName", "配置"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows").value(org.hamcrest.Matchers.hasSize(2)));
    }

    @Test
    @Order(2)
    @DisplayName("查询配置列表-空列表")
    void testList_Empty() throws Exception {
        // Arrange
        when(configService.selectConfigList(org.mockito.ArgumentMatchers.any(SysConfig.class))).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(post("/system/config/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows").value(org.hamcrest.Matchers.hasSize(0)));
    }

    // ==================== addSave 测试 ====================

    @Test
    @Order(10)
    @DisplayName("新增配置-成功")
    void testAddSave_Success() throws Exception {
        // Arrange
        when(configService.checkConfigKeyUnique(org.mockito.ArgumentMatchers.any(SysConfig.class))).thenReturn(true);
        when(configService.insertConfig(org.mockito.ArgumentMatchers.any(SysConfig.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/config/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("configName", "新配置")
                        .param("configKey", "new.key")
                        .param("configValue", "newValue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(11)
    @DisplayName("新增配置-Key已存在")
    void testAddSave_KeyExists() throws Exception {
        // Arrange
        when(configService.checkConfigKeyUnique(org.mockito.ArgumentMatchers.any(SysConfig.class))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/system/config/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("configName", "新配置")
                        .param("configKey", "existing.key")
                        .param("configValue", "newValue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.containsString("参数键名已存在")));
    }

    // ==================== editSave 测试 ====================

    @Test
    @Order(20)
    @DisplayName("修改配置-成功")
    void testEditSave_Success() throws Exception {
        // Arrange
        when(configService.checkConfigKeyUnique(org.mockito.ArgumentMatchers.any(SysConfig.class))).thenReturn(true);
        when(configService.updateConfig(org.mockito.ArgumentMatchers.any(SysConfig.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/config/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("configName", "修改后的配置")
                        .param("configKey", "updated.key")
                        .param("configValue", "updatedValue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(21)
    @DisplayName("修改配置-Key已存在")
    void testEditSave_KeyExists() throws Exception {
        // Arrange
        when(configService.checkConfigKeyUnique(org.mockito.ArgumentMatchers.any(SysConfig.class))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/system/config/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("configName", "修改后的配置")
                        .param("configKey", "existing.key")
                        .param("configValue", "updatedValue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ==================== remove 测试 ====================

    @Test
    @Order(30)
    @DisplayName("删除配置-成功")
    void testRemove_Success() throws Exception {
        // Arrange
        doNothing().when(configService).deleteConfigByIds("1,2");

        // Act & Assert
        mockMvc.perform(post("/system/config/remove")
                        .param("ids", "1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(configService).deleteConfigByIds("1,2");
    }

    // ==================== refreshCache 测试 ====================

    @Test
    @Order(40)
    @DisplayName("刷新缓存-成功")
    void testRefreshCache_Success() throws Exception {
        // Arrange
        doNothing().when(configService).resetConfigCache();

        // Act & Assert
        mockMvc.perform(get("/system/config/refreshCache"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(configService).resetConfigCache();
    }

    // ==================== checkConfigKeyUnique 测试 ====================

    @Test
    @Order(50)
    @DisplayName("校验配置Key唯一性-唯一")
    void testCheckConfigKeyUnique_Unique() throws Exception {
        // Arrange
        when(configService.checkConfigKeyUnique(org.mockito.ArgumentMatchers.any(SysConfig.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/system/config/checkConfigKeyUnique")
                        .param("configKey", "unique.key"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @Order(51)
    @DisplayName("校验配置Key唯一性-不唯一")
    void testCheckConfigKeyUnique_NotUnique() throws Exception {
        // Arrange
        when(configService.checkConfigKeyUnique(org.mockito.ArgumentMatchers.any(SysConfig.class))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/system/config/checkConfigKeyUnique")
                        .param("configKey", "existing.key"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}

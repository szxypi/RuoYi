package com.zjjh.fdtemp.controller.system;

import com.zjjh.fdtemp.beans.entity.SysDictData;
import com.zjjh.fdtemp.service.SysDictDataService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SysDictDataController 单元测试
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SysDictDataControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SysDictDataService dictDataService;

    @InjectMocks
    private SysDictDataController dictDataController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dictDataController).build();
    }

    private SysDictData createTestDictData(String id, String dictType, String dictLabel, String dictValue) {
        SysDictData data = new SysDictData();
        data.setId(id);
        data.setDictType(dictType);
        data.setDictLabel(dictLabel);
        data.setDictValue(dictValue);
        data.setDictSort(1L);
        data.setStatus("0");
        return data;
    }

    // ==================== list 测试 ====================

    @Test
    @Order(1)
    @DisplayName("查询字典数据列表-成功")
    void testList_Success() throws Exception {
        // Arrange
        List<SysDictData> dictDataList = new ArrayList<>();
        dictDataList.add(createTestDictData("1", "sys_user_sex", "男", "0"));
        dictDataList.add(createTestDictData("2", "sys_user_sex", "女", "1"));

        when(dictDataService.selectDictDataList(org.mockito.ArgumentMatchers.any(SysDictData.class))).thenReturn(dictDataList);

        // Act & Assert
        mockMvc.perform(post("/system/dict/data/list")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows").value(org.hamcrest.Matchers.hasSize(2)));
    }

    @Test
    @Order(2)
    @DisplayName("查询字典数据列表-带条件")
    void testList_WithCondition() throws Exception {
        // Arrange
        List<SysDictData> dictDataList = new ArrayList<>();
        dictDataList.add(createTestDictData("1", "sys_user_sex", "男", "0"));

        when(dictDataService.selectDictDataList(org.mockito.ArgumentMatchers.any(SysDictData.class))).thenReturn(dictDataList);

        // Act & Assert
        mockMvc.perform(post("/system/dict/data/list")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dictType", "sys_user_sex")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows").value(org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    @Order(3)
    @DisplayName("查询字典数据列表-空列表")
    void testList_Empty() throws Exception {
        // Arrange
        when(dictDataService.selectDictDataList(org.mockito.ArgumentMatchers.any(SysDictData.class))).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(post("/system/dict/data/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows").value(org.hamcrest.Matchers.hasSize(0)));
    }

    // ==================== addSave 测试 ====================

    @Test
    @Order(10)
    @DisplayName("新增字典数据-成功")
    void testAddSave_Success() throws Exception {
        // Arrange
        when(dictDataService.insertDictData(org.mockito.ArgumentMatchers.any(SysDictData.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/dict/data/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dictType", "new_dict")
                        .param("dictLabel", "新标签")
                        .param("dictValue", "1")
                        .param("dictSort", "1")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(11)
    @DisplayName("新增字典数据-插入失败")
    void testAddSave_InsertFailed() throws Exception {
        // Arrange
        when(dictDataService.insertDictData(org.mockito.ArgumentMatchers.any(SysDictData.class))).thenReturn(0);

        // Act & Assert
        mockMvc.perform(post("/system/dict/data/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dictType", "new_dict")
                        .param("dictLabel", "新标签")
                        .param("dictValue", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ==================== editSave 测试 ====================

    @Test
    @Order(20)
    @DisplayName("修改字典数据-成功")
    void testEditSave_Success() throws Exception {
        // Arrange
        when(dictDataService.updateDictData(org.mockito.ArgumentMatchers.any(SysDictData.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/dict/data/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("dictType", "sys_user_sex")
                        .param("dictLabel", "修改后的标签")
                        .param("dictValue", "0")
                        .param("dictSort", "1")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(21)
    @DisplayName("修改字典数据-更新失败")
    void testEditSave_UpdateFailed() throws Exception {
        // Arrange
        when(dictDataService.updateDictData(org.mockito.ArgumentMatchers.any(SysDictData.class))).thenReturn(0);

        // Act & Assert
        mockMvc.perform(post("/system/dict/data/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "999")
                        .param("dictType", "sys_user_sex")
                        .param("dictLabel", "修改后的标签")
                        .param("dictValue", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ==================== remove 测试 ====================

    @Test
    @Order(30)
    @DisplayName("删除字典数据-成功")
    void testRemove_Success() throws Exception {
        // Arrange
        doNothing().when(dictDataService).deleteDictDataByIds("1,2");

        // Act & Assert
        mockMvc.perform(post("/system/dict/data/remove")
                        .param("ids", "1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(dictDataService).deleteDictDataByIds("1,2");
    }

    @Test
    @Order(31)
    @DisplayName("删除字典数据-单个ID")
    void testRemove_SingleId() throws Exception {
        // Arrange
        doNothing().when(dictDataService).deleteDictDataByIds("1");

        // Act & Assert
        mockMvc.perform(post("/system/dict/data/remove")
                        .param("ids", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    // ==================== 边界条件测试 ====================

    @Test
    @Order(40)
    @DisplayName("边界条件-字典标签包含特殊字符")
    void testAddSave_SpecialCharacters() throws Exception {
        // Arrange
        when(dictDataService.insertDictData(org.mockito.ArgumentMatchers.any(SysDictData.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/dict/data/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dictType", "test_dict")
                        .param("dictLabel", "<script>alert('xss')</script>")
                        .param("dictValue", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(41)
    @DisplayName("边界条件-设置默认值")
    void testAddSave_SetDefault() throws Exception {
        // Arrange
        when(dictDataService.insertDictData(org.mockito.ArgumentMatchers.any(SysDictData.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/dict/data/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dictType", "test_dict")
                        .param("dictLabel", "默认标签")
                        .param("dictValue", "1")
                        .param("isDefault", "Y"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}

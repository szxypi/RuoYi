package com.zjjh.fdtemp.controller.system;

import com.zjjh.fdtemp.beans.entity.SysDictType;
import com.zjjh.fdtemp.common.core.domain.Ztree;
import com.zjjh.fdtemp.service.SysDictTypeService;
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
 * SysDictTypeController 单元测试
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SysDictTypeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SysDictTypeService dictTypeService;

    @InjectMocks
    private SysDictTypeController dictTypeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dictTypeController).build();
    }

    private SysDictType createTestDictType(String id, String dictName, String dictType, String status) {
        SysDictType type = new SysDictType();
        type.setId(id);
        type.setDictName(dictName);
        type.setDictType(dictType);
        type.setStatus(status);
        return type;
    }

    private Ztree createTestZtree(String id, String name, String title) {
        Ztree ztree = new Ztree();
        ztree.setId(id);
        ztree.setName(name);
        ztree.setTitle(title);
        return ztree;
    }

    // ==================== list 测试 ====================

    @Test
    @Order(1)
    @DisplayName("查询字典类型列表-成功")
    void testList_Success() throws Exception {
        // Arrange
        List<SysDictType> dictTypes = new ArrayList<>();
        dictTypes.add(createTestDictType("1", "用户性别", "sys_user_sex", "0"));
        dictTypes.add(createTestDictType("2", "菜单状态", "sys_show_hide", "0"));

        when(dictTypeService.selectDictTypeList(org.mockito.ArgumentMatchers.any(SysDictType.class))).thenReturn(dictTypes);

        // Act & Assert
        mockMvc.perform(post("/system/dict/list")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows").value(org.hamcrest.Matchers.hasSize(2)));
    }

    @Test
    @Order(2)
    @DisplayName("查询字典类型列表-空列表")
    void testList_Empty() throws Exception {
        // Arrange
        when(dictTypeService.selectDictTypeList(org.mockito.ArgumentMatchers.any(SysDictType.class))).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(post("/system/dict/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows").value(org.hamcrest.Matchers.hasSize(0)));
    }

    // ==================== addSave 测试 ====================

    @Test
    @Order(10)
    @DisplayName("新增字典类型-成功")
    void testAddSave_Success() throws Exception {
        // Arrange
        when(dictTypeService.checkDictTypeUnique(org.mockito.ArgumentMatchers.any(SysDictType.class))).thenReturn(true);
        when(dictTypeService.insertDictType(org.mockito.ArgumentMatchers.any(SysDictType.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/dict/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dictName", "新字典")
                        .param("dictType", "new_dict")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @Order(11)
    @DisplayName("新增字典类型-类型已存在")
    void testAddSave_TypeExists() throws Exception {
        // Arrange
        when(dictTypeService.checkDictTypeUnique(org.mockito.ArgumentMatchers.any(SysDictType.class))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/system/dict/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("dictName", "新字典")
                        .param("dictType", "existing_dict")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ==================== editSave 测试 ====================

    @Test
    @Order(20)
    @DisplayName("修改字典类型-成功")
    void testEditSave_Success() throws Exception {
        // Arrange
        when(dictTypeService.checkDictTypeUnique(org.mockito.ArgumentMatchers.any(SysDictType.class))).thenReturn(true);
        when(dictTypeService.updateDictType(org.mockito.ArgumentMatchers.any(SysDictType.class))).thenReturn(1);

        // Act & Assert
        mockMvc.perform(post("/system/dict/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "1")
                        .param("dictName", "修改后的字典")
                        .param("dictType", "updated_dict")
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    // ==================== remove 测试 ====================

    @Test
    @Order(30)
    @DisplayName("删除字典类型-成功")
    void testRemove_Success() throws Exception {
        // Arrange
        doNothing().when(dictTypeService).deleteDictTypeByIds("1,2");

        // Act & Assert
        mockMvc.perform(post("/system/dict/remove")
                        .param("ids", "1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(dictTypeService).deleteDictTypeByIds("1,2");
    }

    // ==================== refreshCache 测试 ====================

    @Test
    @Order(40)
    @DisplayName("刷新字典缓存-成功")
    void testRefreshCache_Success() throws Exception {
        // Arrange
        doNothing().when(dictTypeService).resetDictCache();

        // Act & Assert
        mockMvc.perform(get("/system/dict/refreshCache"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(dictTypeService).resetDictCache();
    }

    // ==================== checkDictTypeUnique 测试 ====================

    @Test
    @Order(50)
    @DisplayName("校验字典类型唯一性-唯一")
    void testCheckDictTypeUnique_Unique() throws Exception {
        // Arrange
        when(dictTypeService.checkDictTypeUnique(org.mockito.ArgumentMatchers.any(SysDictType.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/system/dict/checkDictTypeUnique")
                        .param("dictType", "unique_dict"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @Order(51)
    @DisplayName("校验字典类型唯一性-不唯一")
    void testCheckDictTypeUnique_NotUnique() throws Exception {
        // Arrange
        when(dictTypeService.checkDictTypeUnique(org.mockito.ArgumentMatchers.any(SysDictType.class))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/system/dict/checkDictTypeUnique")
                        .param("dictType", "existing_dict"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // ==================== treeData 测试 ====================

    @Test
    @Order(60)
    @DisplayName("获取字典树-成功")
    void testTreeData_Success() throws Exception {
        // Arrange
        List<Ztree> ztrees = new ArrayList<>();
        ztrees.add(createTestZtree("1", "(用户性别)&nbsp;&nbsp;&nbsp;sys_user_sex", "sys_user_sex"));
        ztrees.add(createTestZtree("2", "(菜单状态)&nbsp;&nbsp;&nbsp;sys_show_hide", "sys_show_hide"));

        when(dictTypeService.selectDictTree(org.mockito.ArgumentMatchers.any(SysDictType.class))).thenReturn(ztrees);

        // Act & Assert
        mockMvc.perform(get("/system/dict/treeData"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].title").value("sys_user_sex"));
    }

    @Test
    @Order(61)
    @DisplayName("获取字典树-空列表")
    void testTreeData_Empty() throws Exception {
        // Arrange
        when(dictTypeService.selectDictTree(org.mockito.ArgumentMatchers.any(SysDictType.class))).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get("/system/dict/treeData"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.hasSize(0)));
    }
}

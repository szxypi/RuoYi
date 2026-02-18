package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysDictData;
import com.zjjh.fdtemp.beans.entity.SysDictType;
import com.zjjh.fdtemp.common.core.domain.Ztree;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.common.utils.DictUtils;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.dao.SysDictDataDao;
import com.zjjh.fdtemp.dao.SysDictTypeDao;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * SysDictTypeService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SysDictTypeServiceImplTest {

    @Mock
    private SysDictTypeDao dictTypeMapper;

    @Mock
    private SysDictDataDao dictDataMapper;

    @InjectMocks
    private SysDictTypeServiceImpl dictTypeService;

    private MockedStatic<DictUtils> dictUtilsMock;

    @BeforeEach
    void setUp() {
        dictUtilsMock = mockStatic(DictUtils.class);
    }

    @AfterEach
    void tearDown() {
        dictUtilsMock.close();
    }

    private SysDictType createTestDictType(String id, String dictName, String dictType, String status) {
        SysDictType type = new SysDictType();
        type.setId(id);
        type.setDictName(dictName);
        type.setDictType(dictType);
        type.setStatus(status);
        return type;
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

    // ==================== selectDictTypeList 测试 ====================

    @Test
    @Order(1)
    @DisplayName("查询字典类型列表-正常情况")
    void testSelectDictTypeList_Success() {
        List<SysDictType> expectedList = new ArrayList<>();
        expectedList.add(createTestDictType("1", "用户性别", "sys_user_sex", "0"));
        expectedList.add(createTestDictType("2", "菜单状态", "sys_show_hide", "0"));

        when(dictTypeMapper.selectDictTypeList(any(SysDictType.class))).thenReturn(expectedList);

        List<SysDictType> result = dictTypeService.selectDictTypeList(new SysDictType());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("sys_user_sex", result.get(0).getDictType());
    }

    // ==================== selectDictTypeAll 测试 ====================

    @Test
    @Order(10)
    @DisplayName("查询所有字典类型-正常情况")
    void testSelectDictTypeAll_Success() {
        List<SysDictType> expectedList = new ArrayList<>();
        expectedList.add(createTestDictType("1", "用户性别", "sys_user_sex", "0"));

        when(dictTypeMapper.selectDictTypeAll()).thenReturn(expectedList);

        List<SysDictType> result = dictTypeService.selectDictTypeAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ==================== selectDictDataByType 测试 ====================

    @Test
    @Order(20)
    @DisplayName("根据类型查询字典数据-从缓存获取")
    void testSelectDictDataByType_FromCache() {
        String dictType = "sys_user_sex";
        List<SysDictData> cachedData = new ArrayList<>();
        cachedData.add(createTestDictData("1", dictType, "男", "0"));

        dictUtilsMock.when(() -> DictUtils.getDictCache(dictType)).thenReturn(cachedData);

        List<SysDictData> result = dictTypeService.selectDictDataByType(dictType);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(dictDataMapper, never()).selectDictDataByType(anyString());
    }

    @Test
    @Order(21)
    @DisplayName("根据类型查询字典数据-从数据库获取并缓存")
    void testSelectDictDataByType_FromDatabase() {
        String dictType = "sys_user_sex";
        List<SysDictData> dbData = new ArrayList<>();
        dbData.add(createTestDictData("1", dictType, "男", "0"));

        dictUtilsMock.when(() -> DictUtils.getDictCache(dictType)).thenReturn(null);
        when(dictDataMapper.selectDictDataByType(dictType)).thenReturn(dbData);

        List<SysDictData> result = dictTypeService.selectDictDataByType(dictType);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @Order(22)
    @DisplayName("根据类型查询字典数据-不存在返回null")
    void testSelectDictDataByType_NotFound() {
        String dictType = "nonexistent_type";
        dictUtilsMock.when(() -> DictUtils.getDictCache(dictType)).thenReturn(null);
        when(dictDataMapper.selectDictDataByType(dictType)).thenReturn(null);

        List<SysDictData> result = dictTypeService.selectDictDataByType(dictType);

        assertNull(result);
    }

    // ==================== selectDictTypeById 测试 ====================

    @Test
    @Order(30)
    @DisplayName("根据ID查询字典类型-正常情况")
    void testSelectDictTypeById_Success() {
        SysDictType expected = createTestDictType("1", "用户性别", "sys_user_sex", "0");
        when(dictTypeMapper.selectDictTypeById("1")).thenReturn(expected);

        SysDictType result = dictTypeService.selectDictTypeById("1");

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("sys_user_sex", result.getDictType());
    }

    // ==================== deleteDictTypeByIds 测试 ====================

    @Test
    @Order(40)
    @DisplayName("删除字典类型-成功")
    void testDeleteDictTypeByIds_Success() {
        SysDictType dictType = createTestDictType("1", "测试类型", "test_type", "0");
        when(dictTypeMapper.selectDictTypeById("1")).thenReturn(dictType);
        when(dictDataMapper.countDictDataByType("test_type")).thenReturn(0);
        when(dictTypeMapper.deleteDictTypeById("1")).thenReturn(1);

        assertDoesNotThrow(() -> dictTypeService.deleteDictTypeByIds("1"));
        verify(dictTypeMapper).deleteDictTypeById("1");
    }

    @Test
    @Order(41)
    @DisplayName("删除字典类型-已分配数据不能删除")
    void testDeleteDictTypeByIds_HasData() {
        SysDictType dictType = createTestDictType("1", "用户性别", "sys_user_sex", "0");
        when(dictTypeMapper.selectDictTypeById("1")).thenReturn(dictType);
        when(dictDataMapper.countDictDataByType("sys_user_sex")).thenReturn(3);

        ServiceException exception = assertThrows(ServiceException.class, () -> dictTypeService.deleteDictTypeByIds("1"));
        assertTrue(exception.getMessage().contains("已分配"));
        verify(dictTypeMapper, never()).deleteDictTypeById(anyString());
    }

    // ==================== insertDictType 测试 ====================

    @Test
    @Order(50)
    @DisplayName("新增字典类型-成功")
    void testInsertDictType_Success() {
        SysDictType dictType = createTestDictType(null, "新类型", "new_type", "0");
        when(dictTypeMapper.insertDictType(any(SysDictType.class))).thenReturn(1);

        int result = dictTypeService.insertDictType(dictType);

        assertEquals(1, result);
    }

    // ==================== updateDictType 测试 ====================

    @Test
    @Order(60)
    @DisplayName("更新字典类型-成功")
    void testUpdateDictType_Success() {
        SysDictType oldDict = createTestDictType("1", "旧类型", "old_type", "0");
        SysDictType newDict = createTestDictType("1", "新类型", "new_type", "0");
        List<SysDictData> dictDataList = new ArrayList<>();
        dictDataList.add(createTestDictData("1", "new_type", "数据1", "1"));

        when(dictTypeMapper.selectDictTypeById("1")).thenReturn(oldDict);
        when(dictDataMapper.updateDictDataType("old_type", "new_type")).thenReturn(1);
        when(dictTypeMapper.updateDictType(any(SysDictType.class))).thenReturn(1);
        when(dictDataMapper.selectDictDataByType("new_type")).thenReturn(dictDataList);

        int result = dictTypeService.updateDictType(newDict);

        assertEquals(1, result);
        verify(dictDataMapper).updateDictDataType("old_type", "new_type");
    }

    // ==================== checkDictTypeUnique 测试 ====================

    @Test
    @Order(70)
    @DisplayName("校验字典类型唯一性-唯一")
    void testCheckDictTypeUnique_Unique() {
        SysDictType dictType = createTestDictType(null, "新类型", "unique_type", "0");
        when(dictTypeMapper.checkDictTypeUnique("unique_type")).thenReturn(null);

        boolean result = dictTypeService.checkDictTypeUnique(dictType);

        assertTrue(result);
    }

    @Test
    @Order(71)
    @DisplayName("校验字典类型唯一性-不唯一(新增)")
    void testCheckDictTypeUnique_NotUnique_New() {
        SysDictType dictType = createTestDictType(null, "新类型", "existing_type", "0");
        SysDictType existingType = createTestDictType("1", "已存在", "existing_type", "0");
        when(dictTypeMapper.checkDictTypeUnique("existing_type")).thenReturn(existingType);

        boolean result = dictTypeService.checkDictTypeUnique(dictType);

        assertFalse(result);
    }

    @Test
    @Order(72)
    @DisplayName("校验字典类型唯一性-唯一(更新自己)")
    void testCheckDictTypeUnique_Unique_UpdateSelf() {
        SysDictType dictType = createTestDictType("1", "类型", "existing_type", "0");
        SysDictType existingType = createTestDictType("1", "已存在", "existing_type", "0");
        when(dictTypeMapper.checkDictTypeUnique("existing_type")).thenReturn(existingType);

        boolean result = dictTypeService.checkDictTypeUnique(dictType);

        assertTrue(result);
    }

    // ==================== selectDictTree 测试 ====================

    @Test
    @Order(80)
    @DisplayName("查询字典树-正常状态")
    void testSelectDictTree_Success() {
        List<SysDictType> dictTypes = new ArrayList<>();
        dictTypes.add(createTestDictType("1", "用户性别", "sys_user_sex", UserConstants.DICT_NORMAL));
        dictTypes.add(createTestDictType("2", "停用类型", "disabled_type", "1"));

        when(dictTypeMapper.selectDictTypeList(any(SysDictType.class))).thenReturn(dictTypes);

        List<Ztree> result = dictTypeService.selectDictTree(new SysDictType());

        assertNotNull(result);
        assertEquals(1, result.size()); // 只有正常状态的
        assertEquals("1", result.get(0).getId());
        assertTrue(result.get(0).getName().contains("用户性别"));
        assertEquals("sys_user_sex", result.get(0).getTitle());
    }

    // ==================== clearDictCache 测试 ====================

    @Test
    @Order(90)
    @DisplayName("清空字典缓存-成功")
    void testClearDictCache_Success() {
        dictTypeService.clearDictCache();
        dictUtilsMock.verify(DictUtils::clearDictCache);
    }
}

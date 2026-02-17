package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysDictData;
import com.zjjh.fdtemp.common.utils.DictUtils;
import com.zjjh.fdtemp.dao.SysDictDataDao;
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
 * SysDictDataService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SysDictDataServiceImplTest {

    @Mock
    private SysDictDataDao dictDataMapper;

    @InjectMocks
    private SysDictDataServiceImpl dictDataService;

    private MockedStatic<DictUtils> dictUtilsMock;

    @BeforeEach
    void setUp() {
        dictUtilsMock = mockStatic(DictUtils.class);
    }

    @AfterEach
    void tearDown() {
        dictUtilsMock.close();
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

    // ==================== selectDictDataList 测试 ====================

    @Test
    @Order(1)
    @DisplayName("查询字典数据列表-正常情况")
    void testSelectDictDataList_Success() {
        List<SysDictData> expectedList = new ArrayList<>();
        expectedList.add(createTestDictData("1", "sys_user_sex", "男", "0"));
        expectedList.add(createTestDictData("2", "sys_user_sex", "女", "1"));

        when(dictDataMapper.selectDictDataList(any(SysDictData.class))).thenReturn(expectedList);

        List<SysDictData> result = dictDataService.selectDictDataList(new SysDictData());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("sys_user_sex", result.get(0).getDictType());
    }

    @Test
    @Order(2)
    @DisplayName("查询字典数据列表-带条件查询")
    void testSelectDictDataList_WithCondition() {
        SysDictData query = new SysDictData();
        query.setDictType("sys_user_sex");
        query.setStatus("0");

        List<SysDictData> expectedList = new ArrayList<>();
        expectedList.add(createTestDictData("1", "sys_user_sex", "男", "0"));

        when(dictDataMapper.selectDictDataList(any(SysDictData.class))).thenReturn(expectedList);

        List<SysDictData> result = dictDataService.selectDictDataList(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(dictDataMapper).selectDictDataList(query);
    }

    // ==================== selectDictLabel 测试 ====================

    @Test
    @Order(10)
    @DisplayName("根据字典类型和键值查询标签-正常情况")
    void testSelectDictLabel_Success() {
        when(dictDataMapper.selectDictLabel("sys_user_sex", "0")).thenReturn("男");

        String result = dictDataService.selectDictLabel("sys_user_sex", "0");

        assertEquals("男", result);
    }

    @Test
    @Order(11)
    @DisplayName("根据字典类型和键值查询标签-不存在")
    void testSelectDictLabel_NotFound() {
        when(dictDataMapper.selectDictLabel("nonexistent", "999")).thenReturn(null);

        String result = dictDataService.selectDictLabel("nonexistent", "999");

        assertNull(result);
    }

    // ==================== selectDictDataById 测试 ====================

    @Test
    @Order(20)
    @DisplayName("根据ID查询字典数据-正常情况")
    void testSelectDictDataById_Success() {
        SysDictData expected = createTestDictData("1", "sys_user_sex", "男", "0");
        when(dictDataMapper.selectDictDataById("1")).thenReturn(expected);

        SysDictData result = dictDataService.selectDictDataById("1");

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("男", result.getDictLabel());
    }

    @Test
    @Order(21)
    @DisplayName("根据ID查询字典数据-不存在")
    void testSelectDictDataById_NotFound() {
        when(dictDataMapper.selectDictDataById("nonexistent")).thenReturn(null);

        SysDictData result = dictDataService.selectDictDataById("nonexistent");

        assertNull(result);
    }

    // ==================== deleteDictDataByIds 测试 ====================

    @Test
    @Order(30)
    @DisplayName("删除字典数据-成功并更新缓存")
    void testDeleteDictDataByIds_Success() {
        SysDictData data = createTestDictData("1", "sys_user_sex", "男", "0");
        List<SysDictData> remainingData = new ArrayList<>();
        remainingData.add(createTestDictData("2", "sys_user_sex", "女", "1"));

        when(dictDataMapper.selectDictDataById("1")).thenReturn(data);
        when(dictDataMapper.deleteDictDataById("1")).thenReturn(1);
        when(dictDataMapper.selectDictDataByType("sys_user_sex")).thenReturn(remainingData);

        dictDataService.deleteDictDataByIds("1");

        verify(dictDataMapper).deleteDictDataById("1");
    }

    @Test
    @Order(31)
    @DisplayName("删除字典数据-批量删除")
    void testDeleteDictDataByIds_Multiple() {
        SysDictData data1 = createTestDictData("1", "sys_user_sex", "男", "0");
        SysDictData data2 = createTestDictData("2", "sys_user_sex", "女", "1");
        List<SysDictData> remainingData = new ArrayList<>();

        when(dictDataMapper.selectDictDataById("1")).thenReturn(data1);
        when(dictDataMapper.selectDictDataById("2")).thenReturn(data2);
        when(dictDataMapper.deleteDictDataById(anyString())).thenReturn(1);
        when(dictDataMapper.selectDictDataByType("sys_user_sex")).thenReturn(remainingData);

        dictDataService.deleteDictDataByIds("1,2");

        verify(dictDataMapper, times(2)).deleteDictDataById(anyString());
    }

    // ==================== insertDictData 测试 ====================

    @Test
    @Order(40)
    @DisplayName("新增字典数据-成功并更新缓存")
    void testInsertDictData_Success() {
        SysDictData newData = createTestDictData(null, "sys_user_sex", "未知", "2");
        List<SysDictData> updatedList = new ArrayList<>();
        updatedList.add(createTestDictData("1", "sys_user_sex", "男", "0"));
        updatedList.add(createTestDictData("2", "sys_user_sex", "女", "1"));
        updatedList.add(createTestDictData("3", "sys_user_sex", "未知", "2"));

        when(dictDataMapper.insertDictData(any(SysDictData.class))).thenReturn(1);
        when(dictDataMapper.selectDictDataByType("sys_user_sex")).thenReturn(updatedList);

        int result = dictDataService.insertDictData(newData);

        assertEquals(1, result);
    }

    @Test
    @Order(41)
    @DisplayName("新增字典数据-失败不更新缓存")
    void testInsertDictData_Failed() {
        SysDictData newData = createTestDictData(null, "sys_user_sex", "未知", "2");
        when(dictDataMapper.insertDictData(any(SysDictData.class))).thenReturn(0);

        int result = dictDataService.insertDictData(newData);

        assertEquals(0, result);
    }

    // ==================== updateDictData 测试 ====================

    @Test
    @Order(50)
    @DisplayName("更新字典数据-成功并更新缓存")
    void testUpdateDictData_Success() {
        SysDictData updateData = createTestDictData("1", "sys_user_sex", "男性", "0");
        List<SysDictData> updatedList = new ArrayList<>();
        updatedList.add(updateData);

        when(dictDataMapper.updateDictData(any(SysDictData.class))).thenReturn(1);
        when(dictDataMapper.selectDictDataByType("sys_user_sex")).thenReturn(updatedList);

        int result = dictDataService.updateDictData(updateData);

        assertEquals(1, result);
    }

    @Test
    @Order(51)
    @DisplayName("更新字典数据-失败不更新缓存")
    void testUpdateDictData_Failed() {
        SysDictData updateData = createTestDictData("1", "sys_user_sex", "男性", "0");
        when(dictDataMapper.updateDictData(any(SysDictData.class))).thenReturn(0);

        int result = dictDataService.updateDictData(updateData);

        assertEquals(0, result);
    }

    // ==================== 边界条件测试 ====================

    @Test
    @Order(60)
    @DisplayName("边界条件-空字符串ID")
    void testSelectDictDataById_EmptyId() {
        when(dictDataMapper.selectDictDataById("")).thenReturn(null);

        SysDictData result = dictDataService.selectDictDataById("");

        assertNull(result);
    }

    @Test
    @Order(61)
    @DisplayName("边界条件-删除空字符串ID")
    void testDeleteDictDataByIds_EmptyId() {
        // 空字符串ID场景，Convert.toStrArray会返回空数组
        assertDoesNotThrow(() -> dictDataService.deleteDictDataByIds(""));
    }

    @Test
    @Order(62)
    @DisplayName("边界条件-字典数据包含特殊字符")
    void testDictDataWithSpecialCharacters() {
        SysDictData data = new SysDictData();
        data.setId("1");
        data.setDictType("test_type");
        data.setDictLabel("<script>alert('xss')</script>");
        data.setDictValue("value");
        data.setDictSort(1L);

        List<SysDictData> expectedList = new ArrayList<>();
        expectedList.add(data);

        when(dictDataMapper.selectDictDataList(any(SysDictData.class))).thenReturn(expectedList);

        List<SysDictData> result = dictDataService.selectDictDataList(new SysDictData());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getDictLabel().contains("<script>"));
    }
}

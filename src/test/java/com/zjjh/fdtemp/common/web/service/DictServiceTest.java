package com.zjjh.fdtemp.common.web.service;

import com.zjjh.fdtemp.beans.entity.SysDictData;
import com.zjjh.fdtemp.service.SysDictDataService;
import com.zjjh.fdtemp.service.SysDictTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("字典服务测试")
class DictServiceTest {

    @Mock
    private SysDictTypeService dictTypeService;

    @Mock
    private SysDictDataService dictDataService;

    @InjectMocks
    private DictService dictService;

    private SysDictData testDictData;

    @BeforeEach
    void setUp() {
        testDictData = new SysDictData();
        testDictData.setDictType("sys_normal_disable");
        testDictData.setDictValue("0");
        testDictData.setDictLabel("正常");
    }

    @Test
    @DisplayName("测试根据字典类型查询字典数据")
    void testGetType() {
        List<SysDictData> expectedList = Arrays.asList(testDictData);
        when(dictTypeService.selectDictDataByType("sys_normal_disable")).thenReturn(expectedList);
        List<SysDictData> result = dictService.getType("sys_normal_disable");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("正常", result.get(0).getDictLabel());
        verify(dictTypeService, times(1)).selectDictDataByType("sys_normal_disable");
    }

    @Test
    @DisplayName("测试根据字典类型查询字典数据 - 空列表")
    void testGetTypeEmpty() {
        when(dictTypeService.selectDictDataByType("nonexistent")).thenReturn(Collections.emptyList());
        List<SysDictData> result = dictService.getType("nonexistent");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试根据字典类型和键值查询字典标签")
    void testGetLabel() {
        when(dictDataService.selectDictLabel("sys_normal_disable", "0")).thenReturn("正常");
        String result = dictService.getLabel("sys_normal_disable", "0");
        assertEquals("正常", result);
        verify(dictDataService, times(1)).selectDictLabel("sys_normal_disable", "0");
    }

    @Test
    @DisplayName("测试根据字典类型和键值查询字典标签 - 不存在")
    void testGetLabelNotFound() {
        when(dictDataService.selectDictLabel("nonexistent", "999")).thenReturn("");
        String result = dictService.getLabel("nonexistent", "999");
        assertEquals("", result);
    }

    @Test
    @DisplayName("测试查询多个字典项")
    void testGetTypeMultiple() {
        SysDictData data1 = new SysDictData();
        data1.setDictValue("0");
        data1.setDictLabel("正常");

        SysDictData data2 = new SysDictData();
        data2.setDictValue("1");
        data2.setDictLabel("停用");

        List<SysDictData> expectedList = Arrays.asList(data1, data2);
        when(dictTypeService.selectDictDataByType("sys_normal_disable")).thenReturn(expectedList);
        List<SysDictData> result = dictService.getType("sys_normal_disable");
        assertEquals(2, result.size());
        assertEquals("正常", result.get(0).getDictLabel());
        assertEquals("停用", result.get(1).getDictLabel());
    }

    @Test
    @DisplayName("测试查询不同字典类型")
    void testGetDifferentTypes() {
        when(dictTypeService.selectDictDataByType("type1")).thenReturn(Arrays.asList(testDictData));
        when(dictTypeService.selectDictDataByType("type2")).thenReturn(Collections.emptyList());
        List<SysDictData> result1 = dictService.getType("type1");
        List<SysDictData> result2 = dictService.getType("type2");
        assertEquals(1, result1.size());
        assertTrue(result2.isEmpty());
    }

    @Test
    @DisplayName("测试查询不同字典标签")
    void testGetDifferentLabels() {
        when(dictDataService.selectDictLabel("sys_normal_disable", "0")).thenReturn("正常");
        when(dictDataService.selectDictLabel("sys_normal_disable", "1")).thenReturn("停用");
        assertEquals("正常", dictService.getLabel("sys_normal_disable", "0"));
        assertEquals("停用", dictService.getLabel("sys_normal_disable", "1"));
    }

    @Test
    @DisplayName("测试空字典类型")
    void testGetTypeEmptyString() {
        when(dictTypeService.selectDictDataByType("")).thenReturn(Collections.emptyList());
        List<SysDictData> result = dictService.getType("");
        assertTrue(result.isEmpty());
    }
}

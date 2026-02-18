package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysDictData;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.common.utils.DictUtils;
import com.zjjh.fdtemp.dao.SysDictDataDao;
import com.zjjh.fdtemp.service.SysDictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典 业务层处理
 *
 * @author szx
 */
@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl implements SysDictDataService {

    private final SysDictDataDao dictDataMapper;

    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        return dictDataMapper.selectDictDataList(dictData);
    }

    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        return dictDataMapper.selectDictLabel(dictType, dictValue);
    }

    @Override
    public SysDictData selectDictDataById(String dictCode) {
        return dictDataMapper.selectDictDataById(dictCode);
    }

    @Override
    public void deleteDictDataByIds(String ids) {
        for (String dictCode : Convert.toStrArray(ids)) {
            SysDictData data = selectDictDataById(dictCode);
            dictDataMapper.deleteDictDataById(dictCode);
            refreshDictCache(data.getDictType());
        }
    }

    @Override
    public int insertDictData(SysDictData data) {
        int row = dictDataMapper.insertDictData(data);
        if (row > 0) {
            refreshDictCache(data.getDictType());
        }
        return row;
    }

    @Override
    public int updateDictData(SysDictData data) {
        int row = dictDataMapper.updateDictData(data);
        if (row > 0) {
            refreshDictCache(data.getDictType());
        }
        return row;
    }

    /**
     * 刷新指定字典类型的缓存
     */
    private void refreshDictCache(String dictType) {
        List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dictType);
        DictUtils.setDictCache(dictType, dictDatas);
    }
}

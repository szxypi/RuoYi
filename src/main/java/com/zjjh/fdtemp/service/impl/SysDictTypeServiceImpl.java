package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysDictData;
import com.zjjh.fdtemp.beans.entity.SysDictType;
import com.zjjh.fdtemp.common.core.domain.Ztree;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.common.utils.DictUtils;
import com.zjjh.fdtemp.common.utils.StringUtils;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.dao.SysDictDataDao;
import com.zjjh.fdtemp.dao.SysDictTypeDao;
import com.zjjh.fdtemp.service.SysDictTypeService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典 业务层处理
 *
 * @author szx
 */
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl implements SysDictTypeService {

    private final SysDictTypeDao dictTypeMapper;
    private final SysDictDataDao dictDataMapper;

    private static final String DICT_STATUS_NORMAL = "0";

    /**
     * 项目启动时，初始化字典到缓存
     */
    @PostConstruct
    public void init() {
        loadingDictCache();
    }

    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        return dictTypeMapper.selectDictTypeList(dictType);
    }

    @Override
    public List<SysDictType> selectDictTypeAll() {
        return dictTypeMapper.selectDictTypeAll();
    }

    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        List<SysDictData> dictDatas = DictUtils.getDictCache(dictType);
        if (StringUtils.isNotEmpty(dictDatas)) {
            return dictDatas;
        }

        dictDatas = dictDataMapper.selectDictDataByType(dictType);
        if (StringUtils.isEmpty(dictDatas)) {
            return null;
        }
        DictUtils.setDictCache(dictType, dictDatas);
        return dictDatas;
    }

    @Override
    public SysDictType selectDictTypeById(String dictId) {
        return dictTypeMapper.selectDictTypeById(dictId);
    }

    @Override
    public SysDictType selectDictTypeByType(String dictType) {
        return dictTypeMapper.selectDictTypeByType(dictType);
    }

    @Override
    public void deleteDictTypeByIds(String ids) {
        for (String dictId : Convert.toStrArray(ids)) {
            SysDictType dictType = selectDictTypeById(dictId);
            if (dictDataMapper.countDictDataByType(dictType.getDictType()) > 0) {
                throw new ServiceException(String.format("%s已分配,不能删除", dictType.getDictName()));
            }
            dictTypeMapper.deleteDictTypeById(dictId);
            DictUtils.removeDictCache(dictType.getDictType());
        }
    }

    @Override
    public void loadingDictCache() {
        SysDictData query = new SysDictData();
        query.setStatus(DICT_STATUS_NORMAL);
        Map<String, List<SysDictData>> dictDataMap = dictDataMapper.selectDictDataList(query)
                .stream()
                .collect(Collectors.groupingBy(SysDictData::getDictType));

        dictDataMap.forEach((dictType, dataList) -> {
            List<SysDictData> sorted = dataList.stream()
                    .sorted(Comparator.comparing(SysDictData::getDictSort))
                    .collect(Collectors.toList());
            DictUtils.setDictCache(dictType, sorted);
        });
    }

    @Override
    public void clearDictCache() {
        DictUtils.clearDictCache();
    }

    @Override
    public void resetDictCache() {
        clearDictCache();
        loadingDictCache();
    }

    @Override
    public int insertDictType(SysDictType dict) {
        int row = dictTypeMapper.insertDictType(dict);
        if (row > 0) {
            DictUtils.setDictCache(dict.getDictType(), null);
        }
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDictType(SysDictType dict) {
        SysDictType oldDict = dictTypeMapper.selectDictTypeById(dict.getId());
        dictDataMapper.updateDictDataType(oldDict.getDictType(), dict.getDictType());
        int row = dictTypeMapper.updateDictType(dict);
        if (row > 0) {
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dict.getDictType());
            DictUtils.setDictCache(dict.getDictType(), dictDatas);
        }
        return row;
    }

    @Override
    public boolean checkDictTypeUnique(SysDictType dict) {
        String dictId = StringUtils.defaultString(dict.getId());
        SysDictType dictType = dictTypeMapper.checkDictTypeUnique(dict.getDictType());
        if (StringUtils.isNull(dictType)) {
            return UserConstants.UNIQUE;
        }
        return dictId.equals(dictType.getId()) ? UserConstants.UNIQUE : UserConstants.NOT_UNIQUE;
    }

    @Override
    public List<Ztree> selectDictTree(SysDictType dictType) {
        List<SysDictType> dictList = dictTypeMapper.selectDictTypeList(dictType);
        return dictList.stream()
                .filter(dict -> UserConstants.DICT_NORMAL.equals(dict.getStatus()))
                .map(this::toZtree)
                .collect(Collectors.toList());
    }

    private Ztree toZtree(SysDictType dict) {
        Ztree ztree = new Ztree();
        ztree.setId(dict.getId());
        ztree.setName(formatDictName(dict));
        ztree.setTitle(dict.getDictType());
        return ztree;
    }

    private String formatDictName(SysDictType dictType) {
        return "(" + dictType.getDictName() + ")&nbsp;&nbsp;&nbsp;" + dictType.getDictType();
    }
}

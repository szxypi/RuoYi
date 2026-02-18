package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.GenTableColumn;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.dao.GenTableColumnDao;
import com.zjjh.fdtemp.service.GenTableColumnService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 业务字段 服务层实现
 *
 * @author szx
 */
@Service
@RequiredArgsConstructor
public class GenTableColumnServiceImpl implements GenTableColumnService {

    private final GenTableColumnDao genTableColumnDao;

    @Override
    public List<GenTableColumn> selectGenTableColumnListByTableId(GenTableColumn genTableColumn) {
        return genTableColumnDao.selectGenTableColumnListByTableId(genTableColumn);
    }

    @Override
    public int insertGenTableColumn(GenTableColumn genTableColumn) {
        return genTableColumnDao.insertGenTableColumn(genTableColumn);
    }

    @Override
    public int updateGenTableColumn(GenTableColumn genTableColumn) {
        return genTableColumnDao.updateGenTableColumn(genTableColumn);
    }

    @Override
    public int deleteGenTableColumnByIds(String ids) {
        return genTableColumnDao.deleteGenTableColumnByIds(Convert.toStrArray(ids));
    }
}

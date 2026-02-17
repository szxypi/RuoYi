package com.zjjh.fdtemp.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.beans.entity.GenTableColumn;
import com.zjjh.fdtemp.dao.GenTableColumnDao;
import com.zjjh.fdtemp.service.GenTableColumnService;

/**
 * 业务字段 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class GenTableColumnServiceImpl implements GenTableColumnService
{
    @Autowired
    private GenTableColumnDao genTableColumnDao;

    /**
     * 查询业务字段列表
     * 
     * @param genTableColumn 业务字段信息
     * @return 业务字段集合
     */
    @Override
    public List<GenTableColumn> selectGenTableColumnListByTableId(GenTableColumn genTableColumn)
    {
        return genTableColumnDao.selectGenTableColumnListByTableId(genTableColumn);
    }

    /**
     * 新增业务字段
     * 
     * @param genTableColumn 业务字段信息
     * @return 结果
     */
    @Override
    public int insertGenTableColumn(GenTableColumn genTableColumn)
    {
        return genTableColumnDao.insertGenTableColumn(genTableColumn);
    }

    /**
     * 修改业务字段
     * 
     * @param genTableColumn 业务字段信息
     * @return 结果
     */
    @Override
    public int updateGenTableColumn(GenTableColumn genTableColumn)
    {
        return genTableColumnDao.updateGenTableColumn(genTableColumn);
    }

    /**
     * 删除业务字段对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteGenTableColumnByIds(String ids)
    {
        return genTableColumnDao.deleteGenTableColumnByIds(Convert.toStrArray(ids));
    }
}
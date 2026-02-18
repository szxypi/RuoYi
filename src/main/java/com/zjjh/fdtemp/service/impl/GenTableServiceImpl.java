package com.zjjh.fdtemp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zjjh.fdtemp.beans.entity.GenTable;
import com.zjjh.fdtemp.beans.entity.GenTableColumn;
import com.zjjh.fdtemp.common.core.text.CharsetKit;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.common.utils.GenUtils;
import com.zjjh.fdtemp.common.utils.StringUtils;
import com.zjjh.fdtemp.common.utils.VelocityInitializer;
import com.zjjh.fdtemp.common.utils.VelocityUtils;
import com.zjjh.fdtemp.constants.Constants;
import com.zjjh.fdtemp.constants.GenConstants;
import com.zjjh.fdtemp.dao.GenTableColumnDao;
import com.zjjh.fdtemp.dao.GenTableDao;
import com.zjjh.fdtemp.service.GenTableService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 业务 服务层实现
 *
 * @author szx
 */
@Service
@RequiredArgsConstructor
public class GenTableServiceImpl implements GenTableService {
    private static final Logger log = LoggerFactory.getLogger(GenTableServiceImpl.class);

    private static final String SQL_TEMPLATE = "sql.vm";

    private final GenTableDao genTableDao;
    private final GenTableColumnDao genTableColumnDao;

    @Override
    public GenTable selectGenTableById(String id) {
        GenTable genTable = genTableDao.selectGenTableById(id);
        setTableFromOptions(genTable);
        return genTable;
    }

    @Override
    public List<GenTable> selectGenTableList(GenTable genTable) {
        return genTableDao.selectGenTableList(genTable);
    }

    @Override
    public List<GenTable> selectDbTableList(GenTable genTable) {
        return genTableDao.selectDbTableList(genTable);
    }

    @Override
    public List<GenTable> selectDbTableListByNames(String[] tableNames) {
        return genTableDao.selectDbTableListByNames(tableNames);
    }

    @Override
    public List<GenTable> selectGenTableAll() {
        return genTableDao.selectGenTableAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGenTable(GenTable genTable) {
        String options = JSON.toJSONString(genTable.getParams());
        genTable.setOptions(options);
        int row = genTableDao.updateGenTable(genTable);
        if (row > 0) {
            genTable.getColumns().forEach(genTableColumnDao::updateGenTableColumn);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGenTableByIds(String ids) {
        String[] idArray = Convert.toStrArray(ids);
        genTableDao.deleteGenTableByIds(idArray);
        genTableColumnDao.deleteGenTableColumnByIds(idArray);
    }

    @Override
    public boolean createTable(String sql) {
        return genTableDao.createTable(sql) == 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importGenTable(List<GenTable> tableList, String operName) {
        try {
            for (GenTable table : tableList) {
                String tableName = table.getTableName();
                GenUtils.initTable(table, operName);
                int row = genTableDao.insertGenTable(table);
                if (row > 0) {
                    List<GenTableColumn> genTableColumns = genTableColumnDao.selectDbTableColumnsByName(tableName);
                    for (GenTableColumn column : genTableColumns) {
                        GenUtils.initColumnField(column, table);
                        genTableColumnDao.insertGenTableColumn(column);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServiceException("导入失败：" + e.getMessage());
        }
    }

    @Override
    public Map<String, String> previewCode(String tableId) {
        Map<String, String> dataMap = new LinkedHashMap<>();
        GenTable table = genTableDao.selectGenTableById(tableId);
        initTableForGeneration(table);
        VelocityContext context = VelocityUtils.prepareContext(table);

        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory());
        for (String template : templates) {
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, sw);
            dataMap.put(template, sw.toString());
        }
        return dataMap;
    }

    @Override
    public byte[] downloadCode(String tableName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        generatorCode(tableName, zip);
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    @Override
    public void generatorCode(String tableName) {
        GenTable table = genTableDao.selectGenTableByName(tableName);
        initTableForGeneration(table);
        VelocityContext context = VelocityUtils.prepareContext(table);

        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory());
        for (String template : templates) {
            if (StringUtils.contains(template, SQL_TEMPLATE)) {
                continue;
            }
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, sw);
            try {
                String path = getGenPath(table, template);
                FileUtils.writeStringToFile(new File(path), sw.toString(), CharsetKit.UTF_8);
            } catch (IOException e) {
                throw new ServiceException("渲染模板失败，表名：" + table.getTableName());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchDb(String tableName) {
        GenTable table = genTableDao.selectGenTableByName(tableName);
        List<GenTableColumn> tableColumns = table.getColumns();
        Map<String, GenTableColumn> tableColumnMap = tableColumns.stream()
                .collect(Collectors.toMap(GenTableColumn::getColumnName, Function.identity()));

        List<GenTableColumn> dbTableColumns = genTableColumnDao.selectDbTableColumnsByName(tableName);
        if (StringUtils.isEmpty(dbTableColumns)) {
            throw new ServiceException("同步数据失败，原表结构不存在");
        }
        List<String> dbTableColumnNames = dbTableColumns.stream()
                .map(GenTableColumn::getColumnName)
                .collect(Collectors.toList());

        for (GenTableColumn column : dbTableColumns) {
            GenUtils.initColumnField(column, table);
            GenTableColumn prevColumn = tableColumnMap.get(column.getColumnName());
            if (prevColumn != null) {
                updateColumnFromPrev(column, prevColumn);
                genTableColumnDao.updateGenTableColumn(column);
            } else {
                genTableColumnDao.insertGenTableColumn(column);
            }
        }

        List<GenTableColumn> delColumns = tableColumns.stream()
                .filter(column -> !dbTableColumnNames.contains(column.getColumnName()))
                .collect(Collectors.toList());
        if (StringUtils.isNotEmpty(delColumns)) {
            genTableColumnDao.deleteGenTableColumns(delColumns);
        }
    }

    @Override
    public byte[] downloadCode(String[] tableNames) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (String tableName : tableNames) {
            generatorCode(tableName, zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    @Override
    public void validateEdit(GenTable genTable) {
        String tplCategory = genTable.getTplCategory();
        if (GenConstants.TPL_TREE.equals(tplCategory)) {
            validateTreeTemplate(genTable);
        } else if (GenConstants.TPL_SUB.equals(tplCategory)) {
            validateSubTemplate(genTable);
        }
    }

    /**
     * 查询表信息并生成代码到zip
     */
    private void generatorCode(String tableName, ZipOutputStream zip) {
        GenTable table = genTableDao.selectGenTableByName(tableName);
        initTableForGeneration(table);
        VelocityContext context = VelocityUtils.prepareContext(table);

        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory());
        for (String template : templates) {
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, sw);
            try {
                zip.putNextEntry(new ZipEntry(VelocityUtils.getFileName(template, table)));
                IOUtils.write(sw.toString(), zip, Constants.UTF8);
                IOUtils.closeQuietly(sw);
                zip.flush();
                zip.closeEntry();
            } catch (IOException e) {
                log.error("渲染模板失败，表名：" + table.getTableName(), e);
            }
        }
    }

    /**
     * 验证树模板参数
     */
    private void validateTreeTemplate(GenTable genTable) {
        String options = JSON.toJSONString(genTable.getParams());
        JSONObject paramsObj = JSONObject.parseObject(options);
        if (StringUtils.isEmpty(paramsObj.getString(GenConstants.TREE_CODE))) {
            throw new ServiceException("树编码字段不能为空");
        }
        if (StringUtils.isEmpty(paramsObj.getString(GenConstants.TREE_PARENT_CODE))) {
            throw new ServiceException("树父编码字段不能为空");
        }
        if (StringUtils.isEmpty(paramsObj.getString(GenConstants.TREE_NAME))) {
            throw new ServiceException("树名称字段不能为空");
        }
    }

    /**
     * 验证主子表模板参数
     */
    private void validateSubTemplate(GenTable genTable) {
        if (StringUtils.isEmpty(genTable.getSubTableName())) {
            throw new ServiceException("关联子表的表名不能为空");
        }
        if (StringUtils.isEmpty(genTable.getSubTableFkName())) {
            throw new ServiceException("子表关联的外键名不能为空");
        }
    }

    /**
     * 从前置列更新当前列的属性
     */
    private void updateColumnFromPrev(GenTableColumn column, GenTableColumn prevColumn) {
        column.setId(prevColumn.getId());
        if (column.isList()) {
            column.setDictType(prevColumn.getDictType());
            column.setQueryType(prevColumn.getQueryType());
        }
        if (shouldPreserveColumnOptions(column, prevColumn)) {
            column.setIsRequired(prevColumn.getIsRequired());
            column.setHtmlType(prevColumn.getHtmlType());
        }
    }

    /**
     * 判断是否需要保留列选项
     */
    private boolean shouldPreserveColumnOptions(GenTableColumn column, GenTableColumn prevColumn) {
        return StringUtils.isNotEmpty(prevColumn.getIsRequired())
                && !column.isPk()
                && (column.isInsert() || column.isEdit())
                && (column.isUsableColumn() || !column.isSuperColumn());
    }

    /**
     * 初始化表的生成信息（设置主子表、主键列、Velocity引擎）
     */
    private void initTableForGeneration(GenTable table) {
        setSubTable(table);
        setPkColumn(table);
        VelocityInitializer.initVelocity();
    }

    /**
     * 设置主键列信息
     */
    public void setPkColumn(GenTable table) {
        assignPkColumn(table);
        if (GenConstants.TPL_SUB.equals(table.getTplCategory())) {
            assignPkColumn(table.getSubTable());
        }
    }

    /**
     * 为指定表查找并设置主键列，未找到则默认使用第一列
     */
    private void assignPkColumn(GenTable table) {
        for (GenTableColumn column : table.getColumns()) {
            if (column.isPk()) {
                table.setPkColumn(column);
                return;
            }
        }
        table.setPkColumn(table.getColumns().get(0));
    }

    /**
     * 设置主子表信息
     */
    public void setSubTable(GenTable table) {
        String subTableName = table.getSubTableName();
        if (StringUtils.isNotEmpty(subTableName)) {
            table.setSubTable(genTableDao.selectGenTableByName(subTableName));
        }
    }

    /**
     * 设置代码生成其他选项值
     */
    public void setTableFromOptions(GenTable genTable) {
        JSONObject paramsObj = JSONObject.parseObject(genTable.getOptions());
        if (StringUtils.isNull(paramsObj)) {
            return;
        }
        genTable.setTreeCode(paramsObj.getString(GenConstants.TREE_CODE));
        genTable.setTreeParentCode(paramsObj.getString(GenConstants.TREE_PARENT_CODE));
        genTable.setTreeName(paramsObj.getString(GenConstants.TREE_NAME));
        genTable.setParentMenuId(paramsObj.getString(GenConstants.PARENT_MENU_ID));
        genTable.setParentMenuName(paramsObj.getString(GenConstants.PARENT_MENU_NAME));
    }

    /**
     * 获取代码生成地址
     */
    public static String getGenPath(GenTable table, String template) {
        String genPath = table.getGenPath();
        if (StringUtils.equals(genPath, "/")) {
            return System.getProperty("user.dir") + File.separator + "src" + File.separator + VelocityUtils.getFileName(template, table);
        }
        return genPath + File.separator + VelocityUtils.getFileName(template, table);
    }
}

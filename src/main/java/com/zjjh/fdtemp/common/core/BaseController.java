package com.zjjh.fdtemp.common.core;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.common.core.domain.AjaxResult.Type;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.core.page.PageDomain;
import com.zjjh.fdtemp.common.core.page.TableDataInfo;
import com.zjjh.fdtemp.common.core.page.TableSupport;
import com.zjjh.fdtemp.common.utils.DateUtils;
import com.zjjh.fdtemp.common.utils.PageUtils;
import com.zjjh.fdtemp.common.utils.ServletUtils;
import com.zjjh.fdtemp.common.utils.security.SecurityUtils;
import com.zjjh.fdtemp.common.utils.StringUtils;
import com.zjjh.fdtemp.common.utils.sql.SqlUtil;

public class BaseController
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    protected void startPage()
    {
        PageUtils.startPage();
    }

    protected void startOrderBy()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        if (StringUtils.isNotEmpty(pageDomain.getOrderBy()))
        {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.orderBy(orderBy);
        }
    }

    protected void clearPage()
    {
        PageUtils.clearPage();
    }

    public HttpServletRequest getRequest()
    {
        return ServletUtils.getRequest();
    }

    public HttpServletResponse getResponse()
    {
        return ServletUtils.getResponse();
    }

    public HttpSession getSession()
    {
        return getRequest().getSession();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableDataInfo getDataTable(List<?> list)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    protected AjaxResult toAjax(int rows)
    {
        return rows > 0 ? success() : error();
    }

    protected AjaxResult toAjax(boolean result)
    {
        return result ? success() : error();
    }

    public AjaxResult success()
    {
        return AjaxResult.success();
    }

    public AjaxResult error()
    {
        return AjaxResult.error();
    }

    public AjaxResult success(String message)
    {
        return AjaxResult.success(message);
    }

    public static AjaxResult success(Object data)
    {
        return AjaxResult.success("操作成功", data);
    }

    public AjaxResult error(String message)
    {
        return AjaxResult.error(message);
    }

    public AjaxResult error(Type type, String message)
    {
        return new AjaxResult(type, message);
    }

    public SysUser getSysUser()
    {
        return SecurityUtils.getSysUser();
    }

    public String getUserId()
    {
        return getSysUser().getId();
    }

    public String getLoginName()
    {
        return getSysUser().getLoginName();
    }
}

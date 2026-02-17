package com.zjjh.fdtemp.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Entity基类
 *
 * @author ruoyi
 */
public class BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 主键ID varchar(32) UUID */
    private String id;

    /** 是否删除: "1"=存在, "0"=删除 */
    private String yn;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /** 创建者 */
    private String createUser;

    /** 创建者昵称 */
    private String createUserNickname;

    /** 更新者 */
    private String updateUser;

    /** 更新者昵称 */
    private String updateUserNickname;

    /** 附加字段1 */
    private String attr1;

    /** 搜索值 */
    @JsonIgnore
    private String searchValue;

    /** 请求参数 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getYn()
    {
        return yn;
    }

    public void setYn(String yn)
    {
        this.yn = yn;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public String getCreateUser()
    {
        return createUser;
    }

    public void setCreateUser(String createUser)
    {
        this.createUser = createUser;
    }

    public String getCreateUserNickname()
    {
        return createUserNickname;
    }

    public void setCreateUserNickname(String createUserNickname)
    {
        this.createUserNickname = createUserNickname;
    }

    public String getUpdateUser()
    {
        return updateUser;
    }

    public void setUpdateUser(String updateUser)
    {
        this.updateUser = updateUser;
    }

    public String getUpdateUserNickname()
    {
        return updateUserNickname;
    }

    public void setUpdateUserNickname(String updateUserNickname)
    {
        this.updateUserNickname = updateUserNickname;
    }

    public String getAttr1()
    {
        return attr1;
    }

    public void setAttr1(String attr1)
    {
        this.attr1 = attr1;
    }

    public String getSearchValue()
    {
        return searchValue;
    }

    public void setSearchValue(String searchValue)
    {
        this.searchValue = searchValue;
    }

    public Map<String, Object> getParams()
    {
        if (params == null)
        {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParams(Map<String, Object> params)
    {
        this.params = params;
    }
}

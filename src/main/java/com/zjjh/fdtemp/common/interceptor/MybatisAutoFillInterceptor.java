package com.zjjh.fdtemp.common.interceptor;

import com.zjjh.fdtemp.beans.BaseEntity;
import com.zjjh.fdtemp.common.utils.IdGenerator;
import com.zjjh.fdtemp.common.utils.StringUtils;
import com.zjjh.fdtemp.common.utils.security.SecurityUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * MyBatis 全局拦截器 - 自动填充公共字段
 *
 * INSERT 时自动填充: id, yn, createUser, createUserNickname, createTime, updateTime
 * UPDATE 时自动填充: updateUser, updateUserNickname, updateTime
 */
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class MybatisAutoFillInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(MybatisAutoFillInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        SqlCommandType sqlCommandType = ms.getSqlCommandType();

        if (parameter == null) {
            return invocation.proceed();
        }

        if (sqlCommandType == SqlCommandType.INSERT) {
            handleInsert(parameter);
        } else if (sqlCommandType == SqlCommandType.UPDATE) {
            handleUpdate(parameter);
        }

        return invocation.proceed();
    }

    private void handleInsert(Object parameter) {
        if (parameter instanceof BaseEntity entity) {
            fillInsert(entity);
        } else if (parameter instanceof Map<?, ?> map) {
            for (Object value : map.values()) {
                if (value instanceof BaseEntity entity) {
                    fillInsert(entity);
                } else if (value instanceof Collection<?> collection) {
                    for (Object item : collection) {
                        if (item instanceof BaseEntity entity) {
                            fillInsert(entity);
                        }
                    }
                }
            }
        }
    }

    private void handleUpdate(Object parameter) {
        if (parameter instanceof BaseEntity entity) {
            fillUpdate(entity);
        } else if (parameter instanceof Map<?, ?> map) {
            for (Object value : map.values()) {
                if (value instanceof BaseEntity entity) {
                    fillUpdate(entity);
                } else if (value instanceof Collection<?> collection) {
                    for (Object item : collection) {
                        if (item instanceof BaseEntity entity) {
                            fillUpdate(entity);
                        }
                    }
                }
            }
        }
    }

    private void fillInsert(BaseEntity entity) {
        Date now = new Date();
        // 自动生成 UUID 主键
        if (StringUtils.isEmpty(entity.getId())) {
            entity.setId(IdGenerator.nextId());
        }
        // 设置 yn 为存在
        if (StringUtils.isEmpty(entity.getYn())) {
            entity.setYn("1");
        }
        // 设置时间
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(now);
        }
        entity.setUpdateTime(now);
        // 设置用户信息（忽略未登录场景，如系统自动记录日志）
        try {
            String loginName = SecurityUtils.getLoginName();
            String userName = SecurityUtils.getUserName();
            if (StringUtils.isEmpty(entity.getCreateUser())) {
                entity.setCreateUser(loginName);
            }
            if (StringUtils.isEmpty(entity.getCreateUserNickname())) {
                entity.setCreateUserNickname(userName);
            }
            if (StringUtils.isEmpty(entity.getUpdateUser())) {
                entity.setUpdateUser(loginName);
            }
            if (StringUtils.isEmpty(entity.getUpdateUserNickname())) {
                entity.setUpdateUserNickname(userName);
            }
        } catch (Exception e) {
            log.debug("自动填充用户信息失败（可能未登录）: {}", e.getMessage());
        }
    }

    private void fillUpdate(BaseEntity entity) {
        entity.setUpdateTime(new Date());
        try {
            String loginName = SecurityUtils.getLoginName();
            String userName = SecurityUtils.getUserName();
            if (StringUtils.isEmpty(entity.getUpdateUser())) {
                entity.setUpdateUser(loginName);
            }
            if (StringUtils.isEmpty(entity.getUpdateUserNickname())) {
                entity.setUpdateUserNickname(userName);
            }
        } catch (Exception e) {
            log.debug("自动填充用户信息失败（可能未登录）: {}", e.getMessage());
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
}

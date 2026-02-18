package com.zjjh.fdtemp.config.security;

import com.zjjh.fdtemp.beans.LoginUser;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.enums.UserStatus;
import com.zjjh.fdtemp.service.SysMenuService;
import com.zjjh.fdtemp.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysMenuService menuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.selectUserByLoginName(username);
        if (user == null) {
            log.info("登录用户：{} 不存在.", username);
            throw new UsernameNotFoundException("登录用户：" + username + " 不存在");
        }
        if (UserStatus.DELETED.getCode().equals(user.getYn())) {
            log.info("登录用户：{} 已被删除.", username);
            throw new ServiceException("对不起，您的账号：" + username + " 已被删除");
        }
        if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new ServiceException("对不起，您的账号：" + username + " 已停用");
        }

        Set<String> permissions = new HashSet<>();
        if (user.isAdmin()) {
            // 管理员获取所有菜单权限
            permissions.addAll(menuService.selectPermsAll());
        } else {
            permissions.addAll(menuService.selectPermsByUserId(user.getId()));
        }

        return new LoginUser(user, permissions);
    }
}

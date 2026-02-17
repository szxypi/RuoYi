package com.zjjh.fdtemp.common.utils.security;

import com.zjjh.fdtemp.beans.LoginUser;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.exception.ServiceException;
import com.zjjh.fdtemp.common.utils.spring.SpringUtils;
import com.zjjh.fdtemp.service.SysUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecurityUtils {

    /**
     * BCryptPasswordEncoder 静态单例，避免重复创建实例
     */
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static LoginUser getLoginUser() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        throw new ServiceException("获取用户信息异常");
    }

    public static SysUser getSysUser() {
        return getLoginUser().getUser();
    }

    public static String getUserId() {
        return getSysUser().getId();
    }

    public static String getLoginName() {
        return getSysUser().getLoginName();
    }

    public static String getUserName() {
        return getSysUser().getUserName();
    }

    public static boolean isAdmin() {
        try {
            return isAdminUser(getSysUser());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断指定 userId 的用户是否为管理员
     * 兼容旧调用：如果传入的 userId 等于当前登录用户的 ID，则直接判断当前用户
     * 否则通过查询数据库来判断
     */
    public static boolean isAdmin(String userId) {
        try {
            SysUser currentUser = getSysUser();
            if (currentUser != null && currentUser.getId() != null && currentUser.getId().equals(userId)) {
                return isAdminUser(currentUser);
            }
            // 如果不是当前用户，则通过数据库查询
            return isAdminById(userId);
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * 通过用户ID查询用户信息，判断是否为管理员
     * 使用 SpringUtils 获取 SysUserService Bean 进行查询
     *
     * @param userId 用户ID
     * @return true 如果是管理员，否则返回 false
     */
    public static boolean isAdminById(String userId) {
        try {
            SysUserService userService = SpringUtils.getBean(SysUserService.class);
            SysUser user = userService.selectUserById(userId);
            return isAdminUser(user);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isAdminUser(SysUser user) {
        return user != null && "admin".equals(user.getLoginName());
    }

    public static String getIp() {
        try {
            return getLoginUser().getIpaddr();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    public static String encryptPassword(String password) {
        return PASSWORD_ENCODER.encode(password);
    }

    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }
}

package com.zjjh.fdtemp.controller.system;

import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.annotation.Log;
import com.zjjh.fdtemp.common.core.BaseController;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.common.utils.StringUtils;
import com.zjjh.fdtemp.common.utils.file.FileUploadUtils;
import com.zjjh.fdtemp.common.utils.file.FileUtils;
import com.zjjh.fdtemp.common.utils.file.MimeTypeUtils;
import com.zjjh.fdtemp.common.utils.security.SecurityUtils;
import com.zjjh.fdtemp.config.AppConfig;
import com.zjjh.fdtemp.enums.BusinessType;
import com.zjjh.fdtemp.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 个人信息 业务处理
 *
 * @author szx
 */
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(SysProfileController.class);

    @Autowired
    private SysUserService userService;

    /**
     * 个人信息
     */
    @GetMapping()
    public AjaxResult profile() {
        SysUser user = getSysUser();
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("roleGroup", userService.selectUserRoleGroup(user.getId()));
        result.put("postGroup", userService.selectUserPostGroup(user.getId()));
        return success(result);
    }

    @PreAuthorize("hasAuthority('system:user:profile')")
    @GetMapping("/checkPassword")
    public boolean checkPassword(String password) {
        SysUser user = getSysUser();
        return SecurityUtils.matchesPassword(password, user.getPassword());
    }

    @Log(title = "重置密码", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    public AjaxResult resetPwd(String oldPassword, String newPassword) {
        SysUser user = getSysUser();
        if (!SecurityUtils.matchesPassword(oldPassword, user.getPassword())) {
            return error("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, user.getPassword())) {
            return error("新密码不能与旧密码相同");
        }
        user.setPassword(SecurityUtils.encryptPassword(newPassword));
        if (userService.resetUserPwd(user) > 0) {
            return success();
        }
        return error("修改密码异常，请联系管理员");
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public AjaxResult update(SysUser user) {
        SysUser currentUser = getSysUser();
        currentUser.setUserName(user.getUserName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(currentUser)) {
            return error("修改用户'" + currentUser.getLoginName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(currentUser)) {
            return error("修改用户'" + currentUser.getLoginName() + "'失败，邮箱账号已存在");
        }
        if (userService.updateUserInfo(currentUser) > 0) {
            return success();
        }
        return error();
    }

    /**
     * 保存头像
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PostMapping("/updateAvatar")
    public AjaxResult updateAvatar(@RequestParam("avatarfile") MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                SysUser currentUser = getSysUser();
                String avatar = FileUploadUtils.upload(AppConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION, true);
                if (userService.updateUserAvatar(currentUser.getId(), avatar)) {
                    String oldAvatar = currentUser.getAvatar();
                    if (StringUtils.isNotEmpty(oldAvatar)) {
                        FileUtils.deleteFile(AppConfig.getProfile() + FileUtils.stripPrefix(oldAvatar));
                    }
                    currentUser.setAvatar(avatar);
                    return success();
                }
            }
            return error();
        } catch (Exception e) {
            log.error("修改头像失败！", e);
            return error(e.getMessage());
        }
    }
}

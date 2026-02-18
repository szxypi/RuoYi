package com.zjjh.fdtemp.controller.system;

import com.zjjh.fdtemp.beans.entity.SysDept;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.annotation.Log;
import com.zjjh.fdtemp.common.core.BaseController;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.common.core.domain.Ztree;
import com.zjjh.fdtemp.common.core.page.TableDataInfo;
import com.zjjh.fdtemp.common.core.text.Convert;
import com.zjjh.fdtemp.common.utils.DateUtils;
import com.zjjh.fdtemp.common.utils.StringUtils;
import com.zjjh.fdtemp.common.utils.poi.ExcelUtil;
import com.zjjh.fdtemp.common.utils.security.SecurityUtils;
import com.zjjh.fdtemp.enums.BusinessType;
import com.zjjh.fdtemp.service.SysDeptService;
import com.zjjh.fdtemp.service.SysPostService;
import com.zjjh.fdtemp.service.SysRoleService;
import com.zjjh.fdtemp.service.SysUserService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户信息
 *
 * @author szx
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {
    @Autowired
    private SysUserService userService;

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysDeptService deptService;

    @Autowired
    private SysPostService postService;

    @PreAuthorize("hasAuthority('system:user:list')")
    @PostMapping("/list")
    public TableDataInfo list(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("hasAuthority('system:user:export')")
    @PostMapping("/export")
    public AjaxResult export(SysUser user) {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        return util.exportExcel(list, "用户数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("hasAuthority('system:user:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String message = userService.importUser(userList, updateSupport, getLoginName());
        return AjaxResult.success(message);
    }

    @PreAuthorize("hasAuthority('system:user:view')")
    @GetMapping("/importTemplate")
    public AjaxResult importTemplate() {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        return util.importTemplateExcel("用户数据");
    }

    /**
     * 新增保存用户
     */
    @PreAuthorize("hasAuthority('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult addSave(@Validated SysUser user) {
        deptService.checkDeptDataScope(user.getDeptId());
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!userService.checkLoginNameUnique(user)) {
            return error("新增用户'" + user.getLoginName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return error("新增用户'" + user.getLoginName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return error("新增用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
        }
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setPwdUpdateDate(DateUtils.getNowDate());
        user.setCreateUser(getLoginName());
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改保存用户
     */
    @PreAuthorize("hasAuthority('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult editSave(@Validated SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getId());
        deptService.checkDeptDataScope(user.getDeptId());
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!userService.checkLoginNameUnique(user)) {
            return error("修改用户'" + user.getLoginName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return error("修改用户'" + user.getLoginName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return error("修改用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateUser(getLoginName());
        return toAjax(userService.updateUser(user));
    }

    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    @Log(title = "重置密码", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    public AjaxResult resetPwdSave(SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.resetUserPwd(user));
    }

    /**
     * 用户授权角色
     */
    @PreAuthorize("hasAuthority('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PostMapping("/authRole/insertAuthRole")
    public AjaxResult insertAuthRole(String userId, String[] roleIds) {
        userService.checkUserDataScope(userId);
        roleService.checkRoleDataScope(roleIds);
        userService.insertUserAuth(userId, roleIds);
        return success();
    }

    @PreAuthorize("hasAuthority('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    public AjaxResult remove(String ids) {
        if (ArrayUtils.contains(Convert.toLongArray(ids), getUserId())) {
            return error("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(ids));
    }

    /**
     * 校验用户名
     */
    @PostMapping("/checkLoginNameUnique")
    public boolean checkLoginNameUnique(SysUser user) {
        return userService.checkLoginNameUnique(user);
    }

    /**
     * 校验手机号码
     */
    @PostMapping("/checkPhoneUnique")
    public boolean checkPhoneUnique(SysUser user) {
        return userService.checkPhoneUnique(user);
    }

    /**
     * 校验email邮箱
     */
    @PostMapping("/checkEmailUnique")
    public boolean checkEmailUnique(SysUser user) {
        return userService.checkEmailUnique(user);
    }

    /**
     * 用户状态修改
     */
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PostMapping("/changeStatus")
    public AjaxResult changeStatus(SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getId());
        return toAjax(userService.changeStatus(user));
    }

    /**
     * 加载部门列表树
     */
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/deptTreeData")
    public List<Ztree> deptTreeData() {
        return deptService.selectDeptTree(new SysDept());
    }
}

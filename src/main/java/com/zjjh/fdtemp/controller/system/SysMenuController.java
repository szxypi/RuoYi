package com.zjjh.fdtemp.controller.system;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.zjjh.fdtemp.common.annotation.Log;
import com.zjjh.fdtemp.common.core.BaseController;
import com.zjjh.fdtemp.common.core.domain.AjaxResult;
import com.zjjh.fdtemp.common.core.domain.Ztree;
import com.zjjh.fdtemp.beans.entity.SysMenu;
import com.zjjh.fdtemp.beans.entity.SysRole;
import com.zjjh.fdtemp.enums.BusinessType;
import com.zjjh.fdtemp.common.utils.security.SecurityUtils;
import com.zjjh.fdtemp.service.SysMenuService;

/**
 * 菜单信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController
{
    @Autowired
    private SysMenuService menuService;

    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/list")
    public List<SysMenu> list(SysMenu menu)
    {
        String userId = SecurityUtils.getUserId();
        List<SysMenu> menuList = menuService.selectMenuList(menu, userId);
        return menuList;
    }

    /**
     * 删除菜单
     */
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:menu:remove')")
    @DeleteMapping("/{menuId}")
    public AjaxResult remove(@PathVariable("menuId") String menuId)
    {
        if (menuService.selectCountMenuByParentId(menuId) > 0)
        {
            return AjaxResult.warn("存在子菜单,不允许删除");
        }
        if (menuService.selectCountRoleMenuByMenuId(menuId) > 0)
        {
            return AjaxResult.warn("菜单已分配,不允许删除");
        }
        return toAjax(menuService.deleteMenuById(menuId));
    }

    /**
     * 新增保存菜单
     */
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:menu:add')")
    @PostMapping("/add")
    public AjaxResult addSave(@Validated SysMenu menu)
    {
        if (!menuService.checkMenuNameUnique(menu))
        {
            return error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        return toAjax(menuService.insertMenu(menu));
    }

    /**
     * 修改保存菜单
     */
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @PostMapping("/edit")
    public AjaxResult editSave(@Validated SysMenu menu)
    {
        if (!menuService.checkMenuNameUnique(menu))
        {
            return error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        return toAjax(menuService.updateMenu(menu));
    }

    /**
     * 保存菜单排序
     */
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @PostMapping("/updateSort")
    public AjaxResult updateSort(@RequestParam String[] menuIds, @RequestParam String[] orderNums)
    {
        menuService.updateMenuSort(menuIds, orderNums);
        return success();
    }

    /**
     * 校验菜单名称
     */
    @PostMapping("/checkMenuNameUnique")
    public boolean checkMenuNameUnique(SysMenu menu)
    {
        return menuService.checkMenuNameUnique(menu);
    }

    /**
     * 加载角色菜单列表树
     */
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/roleMenuTreeData")
    public List<Ztree> roleMenuTreeData(SysRole role)
    {
        String userId = SecurityUtils.getUserId();
        List<Ztree> ztrees = menuService.roleMenuTreeData(role, userId);
        return ztrees;
    }

    /**
     * 加载所有菜单列表树
     */
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/menuTreeData")
    public List<Ztree> menuTreeData()
    {
        String userId = SecurityUtils.getUserId();
        List<Ztree> ztrees = menuService.menuTreeData(userId);
        return ztrees;
    }
}

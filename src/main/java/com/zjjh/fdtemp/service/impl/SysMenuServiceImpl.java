package com.zjjh.fdtemp.service.impl;

import com.zjjh.fdtemp.beans.entity.SysMenu;
import com.zjjh.fdtemp.beans.entity.SysRole;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.core.domain.Ztree;
import com.zjjh.fdtemp.common.utils.StringUtils;
import com.zjjh.fdtemp.common.utils.security.SecurityUtils;
import com.zjjh.fdtemp.constants.UserConstants;
import com.zjjh.fdtemp.dao.SysMenuDao;
import com.zjjh.fdtemp.dao.SysRoleMenuDao;
import com.zjjh.fdtemp.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;

/**
 * 菜单 业务层处理
 *
 * @author szx
 */
@Service
public class SysMenuServiceImpl implements SysMenuService {
    private static final String PERMISSION_FORMAT = "perms[\"{0}\"]";

    @Autowired
    private SysMenuDao menuMapper;

    @Autowired
    private SysRoleMenuDao roleMenuMapper;

    @Override
    public List<SysMenu> selectMenusByUser(SysUser user) {
        List<SysMenu> menus = user.isAdmin()
                ? menuMapper.selectMenuNormalAll()
                : menuMapper.selectMenusByUserId(user.getId());
        return getChildPerms(menus, "0");
    }

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu, String userId) {
        if (SecurityUtils.isAdmin(userId)) {
            return menuMapper.selectMenuList(menu);
        }
        menu.getParams().put("userId", userId);
        return menuMapper.selectMenuListByUserId(menu);
    }

    @Override
    public List<SysMenu> selectMenuAll(String userId) {
        return SecurityUtils.isAdmin(userId)
                ? menuMapper.selectMenuAll()
                : menuMapper.selectMenuAllByUserId(userId);
    }

    @Override
    public Set<String> selectPermsByUserId(String userId) {
        return parsePerms(menuMapper.selectPermsByUserId(userId));
    }

    @Override
    public Set<String> selectPermsAll() {
        return parsePerms(menuMapper.selectPermsAll());
    }

    @Override
    public Set<String> selectPermsByRoleId(String roleId) {
        return parsePerms(menuMapper.selectPermsByRoleId(roleId));
    }

    @Override
    public List<Ztree> roleMenuTreeData(SysRole role, String userId) {
        List<SysMenu> menuList = selectMenuAll(userId);
        if (StringUtils.isNull(role.getId())) {
            return initZtree(menuList, null, true);
        }
        return initZtree(menuList, menuMapper.selectMenuTree(role.getId()), true);
    }

    @Override
    public List<Ztree> menuTreeData(String userId) {
        return initZtree(selectMenuAll(userId));
    }

    @Override
    public LinkedHashMap<String, String> selectPermsAll(String userId) {
        LinkedHashMap<String, String> section = new LinkedHashMap<>();
        List<SysMenu> permissions = selectMenuAll(userId);
        for (SysMenu menu : permissions) {
            section.put(menu.getUrl(), MessageFormat.format(PERMISSION_FORMAT, menu.getPerms()));
        }
        return section;
    }

    @Override
    public int deleteMenuById(String menuId) {
        return menuMapper.deleteMenuById(menuId);
    }

    @Override
    public SysMenu selectMenuById(String menuId) {
        return menuMapper.selectMenuById(menuId);
    }

    @Override
    public int selectCountMenuByParentId(String parentId) {
        return menuMapper.selectCountMenuByParentId(parentId);
    }

    @Override
    public int selectCountRoleMenuByMenuId(String menuId) {
        return roleMenuMapper.selectCountRoleMenuByMenuId(menuId);
    }

    @Override
    public int insertMenu(SysMenu menu) {
        return menuMapper.insertMenu(menu);
    }

    @Override
    public int updateMenu(SysMenu menu) {
        return menuMapper.updateMenu(menu);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMenuSort(String[] menuIds, String[] orderNums) {
        for (int i = 0; i < menuIds.length; i++) {
            SysMenu menu = new SysMenu();
            menu.setId(menuIds[i]);
            menu.setOrderNum(orderNums[i]);
            menuMapper.updateMenuSort(menu);
        }
    }

    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        SysMenu info = menuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId());
        return isUnique(menu.getId(), info);
    }

    /**
     * 对象转菜单树
     */
    private List<Ztree> initZtree(List<SysMenu> menuList) {
        return initZtree(menuList, null, false);
    }

    /**
     * 对象转菜单树
     *
     * @param roleMenuList 角色已存在菜单列表
     * @param permsFlag    是否需要显示权限标识
     */
    private List<Ztree> initZtree(List<SysMenu> menuList, List<String> roleMenuList, boolean permsFlag) {
        List<Ztree> ztrees = new ArrayList<>();
        boolean isCheck = StringUtils.isNotNull(roleMenuList);
        for (SysMenu menu : menuList) {
            Ztree ztree = new Ztree();
            ztree.setId(menu.getId());
            ztree.setpId(menu.getParentId());
            ztree.setName(transMenuName(menu, permsFlag));
            ztree.setTitle(menu.getMenuName());
            if (isCheck) {
                ztree.setChecked(roleMenuList.contains(menu.getId() + menu.getPerms()));
            }
            ztrees.add(ztree);
        }
        return ztrees;
    }

    /**
     * 解析权限字符串列表，将逗号分隔的权限标识拆分并去重
     */
    private Set<String> parsePerms(List<String> perms) {
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 转换菜单名称（带权限标识）
     */
    private String transMenuName(SysMenu menu, boolean permsFlag) {
        StringBuilder sb = new StringBuilder(menu.getMenuName());
        if (permsFlag) {
            sb.append("<font color=\"#888\">&nbsp;&nbsp;&nbsp;").append(menu.getPerms()).append("</font>");
        }
        return sb.toString();
    }

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list     分类表
     * @param parentId 传入的父节点ID
     */
    private List<SysMenu> getChildPerms(List<SysMenu> list, String parentId) {
        List<SysMenu> returnList = new ArrayList<>();
        for (SysMenu menu : list) {
            if (parentId.equals(menu.getParentId())) {
                recursionFn(list, menu);
                returnList.add(menu);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysMenu> list, SysMenu t) {
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu child : childList) {
            if (hasChild(list, child)) {
                recursionFn(list, child);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> childList = new ArrayList<>();
        for (SysMenu menu : list) {
            if (StringUtils.isNotNull(menu.getParentId()) && menu.getParentId().equals(t.getId())) {
                childList.add(menu);
            }
        }
        return childList;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return !getChildList(list, t).isEmpty();
    }

    /**
     * 判断是否唯一
     */
    private boolean isUnique(String currentId, SysMenu existing) {
        String menuId = StringUtils.isNull(currentId) ? "" : currentId;
        return StringUtils.isNull(existing) || existing.getId().equals(menuId)
                ? UserConstants.UNIQUE
                : UserConstants.NOT_UNIQUE;
    }
}

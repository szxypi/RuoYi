package com.zjjh.fdtemp.service;

import com.zjjh.fdtemp.beans.entity.SysMenu;
import com.zjjh.fdtemp.beans.entity.SysRole;
import com.zjjh.fdtemp.beans.entity.SysUser;
import com.zjjh.fdtemp.common.core.domain.Ztree;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 菜单 业务层
 *
 * @author szx
 */
public interface SysMenuService {
    /**
     * 根据用户ID查询菜单
     *
     * @param user 用户信息
     * @return 菜单列表
     */
    List<SysMenu> selectMenusByUser(SysUser user);

    /**
     * 查询系统菜单列表
     *
     * @param menu   菜单信息
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu, String userId);

    /**
     * 查询菜单集合
     *
     * @param userId 用户ID
     * @return 所有菜单信息
     */
    List<SysMenu> selectMenuAll(String userId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> selectPermsByUserId(String userId);

    /**
     * 查询所有菜单权限标识（用于管理员）
     *
     * @return 权限列表
     */
    Set<String> selectPermsAll();

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    Set<String> selectPermsByRoleId(String roleId);

    /**
     * 根据角色ID查询菜单
     *
     * @param role   角色对象
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<Ztree> roleMenuTreeData(SysRole role, String userId);

    /**
     * 查询所有菜单信息
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<Ztree> menuTreeData(String userId);

    /**
     * 查询系统所有权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Map<String, String> selectPermsAll(String userId);

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    int deleteMenuById(String menuId);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    SysMenu selectMenuById(String menuId);

    /**
     * 查询菜单数量
     *
     * @param parentId 菜单父ID
     * @return 结果
     */
    int selectCountMenuByParentId(String parentId);

    /**
     * 查询菜单使用数量
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    int selectCountRoleMenuByMenuId(String menuId);

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int insertMenu(SysMenu menu);

    /**
     * 修改保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int updateMenu(SysMenu menu);

    /**
     * 保存菜单排序
     *
     * @param menuIds   菜单ID
     * @param orderNums 排序ID
     */
    void updateMenuSort(String[] menuIds, String[] orderNums);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    boolean checkMenuNameUnique(SysMenu menu);
}

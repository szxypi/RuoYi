package com.zjjh.fdtemp.dao;

import com.zjjh.fdtemp.beans.entity.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单表 数据层
 *
 * @author szx
 */
public interface SysMenuDao {
    /**
     * 查询系统所有菜单（含按钮）
     *
     * @return 菜单列表
     */
    List<SysMenu> selectMenuAll();

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenuAllByUserId(String userId);

    /**
     * 查询系统正常显示菜单（不含按钮）
     *
     * @return 菜单列表
     */
    List<SysMenu> selectMenuNormalAll();

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenusByUserId(String userId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> selectPermsByUserId(String userId);

    /**
     * 查询所有菜单权限标识（用于管理员）
     *
     * @return 权限列表
     */
    List<String> selectPermsAll();

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<String> selectPermsByRoleId(String roleId);

    /**
     * 根据角色ID查询菜单
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<String> selectMenuTree(String roleId);

    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    List<SysMenu> selectMenuListByUserId(SysMenu menu);

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
     * 新增菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int insertMenu(SysMenu menu);

    /**
     * 修改菜单信息
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
    void updateMenuSort(SysMenu menu);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menuName 菜单名称
     * @param parentId 父菜单ID
     * @return 结果
     */
    SysMenu checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") String parentId);
}

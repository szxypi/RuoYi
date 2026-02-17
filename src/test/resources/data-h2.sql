-- 测试数据

-- 部门数据
INSERT INTO sys_dept (ID, parent_id, ancestors, dept_name, order_num, leader, status, YN, CREATE_TIME) VALUES
('1', '0', '0', '总公司', 0, 'admin', '0', '1', NOW()),
('2', '1', '0,1', '深圳分公司', 1, 'test', '0', '1', NOW()),
('3', '2', '0,1,2', '研发部门', 1, 'test', '0', '1', NOW()),
('4', '2', '0,1,2', '市场部门', 2, 'test', '0', '1', NOW());

-- 角色数据
INSERT INTO sys_role (ID, role_name, role_key, role_sort, data_scope, status, YN, CREATE_TIME) VALUES
('1', '超级管理员', 'admin', 1, '1', '0', '1', NOW()),
('2', '普通角色', 'common', 2, '2', '0', '1', NOW()),
('3', '测试角色', 'test', 3, '3', '0', '1', NOW());

-- 菜单数据 (核心菜单)
INSERT INTO sys_menu (ID, menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, CREATE_TIME) VALUES
('1', '系统管理', '0', '1', '#', 'M', '0', '', 'system', NOW()),
('2', '用户管理', '1', '1', '/system/user', 'C', '0', 'system:user:list', 'user', NOW()),
('3', '角色管理', '1', '2', '/system/role', 'C', '0', 'system:role:list', 'peoples', NOW()),
('4', '菜单管理', '1', '3', '/system/menu', 'C', '0', 'system:menu:list', 'tree-table', NOW()),
('5', '部门管理', '1', '4', '/system/dept', 'C', '0', 'system:dept:list', 'tree', NOW()),
('6', '字典管理', '1', '7', '/system/dict', 'C', '0', 'system:dict:list', 'dict', NOW()),
('7', '参数设置', '1', '8', '/system/config', 'C', '0', 'system:config:list', 'edit', NOW()),
-- 用户管理按钮
('100', '用户查询', '2', '1', '#', 'F', '0', 'system:user:query', '#', NOW()),
('101', '用户新增', '2', '2', '#', 'F', '0', 'system:user:add', '#', NOW()),
('102', '用户修改', '2', '3', '#', 'F', '0', 'system:user:edit', '#', NOW()),
('103', '用户删除', '2', '4', '#', 'F', '0', 'system:user:remove', '#', NOW()),
('104', '用户导出', '2', '5', '#', 'F', '0', 'system:user:export', '#', NOW()),
-- 角色管理按钮
('105', '角色查询', '3', '1', '#', 'F', '0', 'system:role:query', '#', NOW()),
('106', '角色新增', '3', '2', '#', 'F', '0', 'system:role:add', '#', NOW()),
('107', '角色修改', '3', '3', '#', 'F', '0', 'system:role:edit', '#', NOW()),
('108', '角色删除', '3', '4', '#', 'F', '0', 'system:role:remove', '#', NOW());

-- 岗位数据
INSERT INTO sys_post (ID, post_code, post_name, post_sort, status, YN, CREATE_TIME) VALUES
('1', 'ceo', '董事长', 1, '0', '1', NOW()),
('2', 'se', '项目经理', 2, '0', '1', NOW()),
('3', 'hr', '人力资源', 3, '0', '1', NOW()),
('4', 'user', '普通员工', 4, '0', '1', NOW());

-- 用户数据 (密码为 BCrypt 加密的 'admin123' 和 'test123')
-- $2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2 = admin123
-- $2a$10$5OpF3T4xP0fE5XL5gC9L5.LnK9nQ3D8m1O2JxYvR3sT6uW9zA1B2C = test123
INSERT INTO sys_user (ID, dept_id, login_name, user_name, user_type, email, phonenumber, sex, password, status, YN, CREATE_TIME) VALUES
('1', '1', 'admin', '超级管理员', '00', 'admin@fdtemp.com', '15888888888', '0', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '1', NOW()),
('2', '2', 'test', '测试用户', '00', 'test@fdtemp.com', '15666666666', '1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '1', NOW()),
('3', '3', 'normal', '普通用户', '00', 'normal@fdtemp.com', '15666666667', '0', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '1', NOW());

-- 用户与角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
('1', '1'),
('2', '2'),
('3', '2');

-- 角色与菜单关联 (admin拥有所有权限, 普通角色只有查看权限)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
-- 管理员角色 (角色1) 拥有所有菜单权限
('1', '1'),
('1', '2'),
('1', '3'),
('1', '4'),
('1', '5'),
('1', '6'),
('1', '7'),
('1', '100'),
('1', '101'),
('1', '102'),
('1', '103'),
('1', '104'),
('1', '105'),
('1', '106'),
('1', '107'),
('1', '108'),
-- 普通角色 (角色2) 只有查看权限
('2', '1'),
('2', '2'),
('2', '3'),
('2', '4'),
('2', '5'),
('2', '6'),
('2', '7'),
('2', '100'),
('2', '105');

-- 角色与部门关联
INSERT INTO sys_role_dept (role_id, dept_id) VALUES
('2', '2'),
('2', '3'),
('2', '4');

-- 用户与岗位关联
INSERT INTO sys_user_post (user_id, post_id) VALUES
('1', '1'),
('2', '2'),
('3', '4');

-- 字典类型
INSERT INTO sys_dict_type (ID, dict_name, dict_type, status, YN, CREATE_TIME) VALUES
('1', '用户性别', 'sys_user_sex', '0', '1', NOW()),
('2', '菜单状态', 'sys_show_hide', '0', '1', NOW()),
('3', '开关状态', 'sys_normal_disable', '0', '1', NOW());

-- 字典数据
INSERT INTO sys_dict_data (ID, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, YN, CREATE_TIME) VALUES
('1', 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', '1', NOW()),
('2', 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', '1', NOW()),
('3', 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', '1', NOW()),
('4', 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', '1', NOW()),
('5', 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', '1', NOW()),
('6', 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', '1', NOW()),
('7', 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', '1', NOW());

-- 参数配置
INSERT INTO sys_config (ID, config_name, config_key, config_value, config_type, YN, CREATE_TIME) VALUES
('1', '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', '1', NOW()),
('2', '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', '1', NOW()),
('3', '是否开启验证码', 'sys.account.captchaEnabled', 'false', 'Y', '1', NOW());

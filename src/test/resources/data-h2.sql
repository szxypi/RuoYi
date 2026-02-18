-- 测试数据

-- 部门数据
INSERT INTO sys_dept (ID, parent_id, ancestors, dept_name, order_num, leader, status, YN, CREATE_TIME)
VALUES ('1', '0', '0', '总公司', 0, 'admin', '0', '1', CURRENT_TIMESTAMP),
       ('2', '1', '0,1', '深圳分公司', 1, 'test', '0', '1', CURRENT_TIMESTAMP),
       ('3', '2', '0,1,2', '研发部门', 1, 'test', '0', '1', CURRENT_TIMESTAMP),
       ('4', '2', '0,1,2', '市场部门', 2, 'test', '0', '1', CURRENT_TIMESTAMP);

-- 角色数据
INSERT INTO sys_role (ID, role_name, role_key, role_sort, data_scope, status, YN, CREATE_TIME)
VALUES ('1', '超级管理员', 'admin', 1, '1', '0', '1', CURRENT_TIMESTAMP),
       ('2', '普通角色', 'common', 2, '2', '0', '1', CURRENT_TIMESTAMP),
       ('3', '测试角色', 'test', 3, '3', '0', '1', CURRENT_TIMESTAMP);

-- 菜单数据 (核心菜单)
INSERT INTO sys_menu (ID, menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, CREATE_TIME)
VALUES ('1', '系统管理', '0', '1', '#', 'M', '0', '', 'system', CURRENT_TIMESTAMP),
       ('2', '用户管理', '1', '1', '/system/user', 'C', '0', 'system:user:list', 'user', CURRENT_TIMESTAMP),
       ('3', '角色管理', '1', '2', '/system/role', 'C', '0', 'system:role:list', 'peoples', CURRENT_TIMESTAMP),
       ('4', '菜单管理', '1', '3', '/system/menu', 'C', '0', 'system:menu:list', 'tree-table', CURRENT_TIMESTAMP),
       ('5', '部门管理', '1', '4', '/system/dept', 'C', '0', 'system:dept:list', 'tree', CURRENT_TIMESTAMP),
       ('6', '字典管理', '1', '7', '/system/dict', 'C', '0', 'system:dict:list', 'dict', CURRENT_TIMESTAMP),
       ('7', '参数设置', '1', '8', '/system/config', 'C', '0', 'system:config:list', 'edit', CURRENT_TIMESTAMP),
-- 用户管理按钮
       ('100', '用户查询', '2', '1', '#', 'F', '0', 'system:user:query', '#', CURRENT_TIMESTAMP),
       ('101', '用户新增', '2', '2', '#', 'F', '0', 'system:user:add', '#', CURRENT_TIMESTAMP),
       ('102', '用户修改', '2', '3', '#', 'F', '0', 'system:user:edit', '#', CURRENT_TIMESTAMP),
       ('103', '用户删除', '2', '4', '#', 'F', '0', 'system:user:remove', '#', CURRENT_TIMESTAMP),
       ('104', '用户导出', '2', '5', '#', 'F', '0', 'system:user:export', '#', CURRENT_TIMESTAMP),
-- 角色管理按钮
       ('105', '角色查询', '3', '1', '#', 'F', '0', 'system:role:query', '#', CURRENT_TIMESTAMP),
       ('106', '角色新增', '3', '2', '#', 'F', '0', 'system:role:add', '#', CURRENT_TIMESTAMP),
       ('107', '角色修改', '3', '3', '#', 'F', '0', 'system:role:edit', '#', CURRENT_TIMESTAMP),
       ('108', '角色删除', '3', '4', '#', 'F', '0', 'system:role:remove', '#', CURRENT_TIMESTAMP),
-- 菜单管理按钮
       ('109', '菜单查询', '4', '1', '#', 'F', '0', 'system:menu:query', '#', CURRENT_TIMESTAMP),
       ('110', '菜单新增', '4', '2', '#', 'F', '0', 'system:menu:add', '#', CURRENT_TIMESTAMP),
       ('111', '菜单修改', '4', '3', '#', 'F', '0', 'system:menu:edit', '#', CURRENT_TIMESTAMP),
       ('112', '菜单删除', '4', '4', '#', 'F', '0', 'system:menu:remove', '#', CURRENT_TIMESTAMP),
-- 部门管理按钮
       ('113', '部门查询', '5', '1', '#', 'F', '0', 'system:dept:query', '#', CURRENT_TIMESTAMP),
       ('114', '部门新增', '5', '2', '#', 'F', '0', 'system:dept:add', '#', CURRENT_TIMESTAMP),
       ('115', '部门修改', '5', '3', '#', 'F', '0', 'system:dept:edit', '#', CURRENT_TIMESTAMP),
       ('116', '部门删除', '5', '4', '#', 'F', '0', 'system:dept:remove', '#', CURRENT_TIMESTAMP),
-- 字典管理按钮
       ('117', '字典查询', '6', '1', '#', 'F', '0', 'system:dict:query', '#', CURRENT_TIMESTAMP),
       ('118', '字典新增', '6', '2', '#', 'F', '0', 'system:dict:add', '#', CURRENT_TIMESTAMP),
       ('119', '字典修改', '6', '3', '#', 'F', '0', 'system:dict:edit', '#', CURRENT_TIMESTAMP),
       ('120', '字典删除', '6', '4', '#', 'F', '0', 'system:dict:remove', '#', CURRENT_TIMESTAMP),
-- 参数设置按钮
       ('121', '参数查询', '7', '1', '#', 'F', '0', 'system:config:query', '#', CURRENT_TIMESTAMP),
       ('122', '参数新增', '7', '2', '#', 'F', '0', 'system:config:add', '#', CURRENT_TIMESTAMP),
       ('123', '参数修改', '7', '3', '#', 'F', '0', 'system:config:edit', '#', CURRENT_TIMESTAMP),
       ('124', '参数删除', '7', '4', '#', 'F', '0', 'system:config:remove', '#', CURRENT_TIMESTAMP),
-- 岗位管理
       ('8', '岗位管理', '1', '5', '/system/post', 'C', '0', 'system:post:list', 'post', CURRENT_TIMESTAMP),
       ('125', '岗位查询', '8', '1', '#', 'F', '0', 'system:post:query', '#', CURRENT_TIMESTAMP),
       ('126', '岗位新增', '8', '2', '#', 'F', '0', 'system:post:add', '#', CURRENT_TIMESTAMP),
       ('127', '岗位修改', '8', '3', '#', 'F', '0', 'system:post:edit', '#', CURRENT_TIMESTAMP),
       ('128', '岗位删除', '8', '4', '#', 'F', '0', 'system:post:remove', '#', CURRENT_TIMESTAMP),
       ('129', '岗位导出', '8', '5', '#', 'F', '0', 'system:post:export', '#', CURRENT_TIMESTAMP),
-- 通知公告
       ('9', '通知公告', '1', '9', '/system/notice', 'C', '0', 'system:notice:list', 'message', CURRENT_TIMESTAMP),
       ('130', '公告查询', '9', '1', '#', 'F', '0', 'system:notice:query', '#', CURRENT_TIMESTAMP),
       ('131', '公告新增', '9', '2', '#', 'F', '0', 'system:notice:add', '#', CURRENT_TIMESTAMP),
       ('132', '公告修改', '9', '3', '#', 'F', '0', 'system:notice:edit', '#', CURRENT_TIMESTAMP),
       ('133', '公告删除', '9', '4', '#', 'F', '0', 'system:notice:remove', '#', CURRENT_TIMESTAMP),
-- 系统监控
       ('10', '系统监控', '0', '2', '#', 'M', '0', '', 'monitor', CURRENT_TIMESTAMP),
       ('11', '在线用户', '10', '1', '/monitor/online', 'C', '0', 'monitor:online:list', 'online', CURRENT_TIMESTAMP),
       ('143', '服务监控', '10', '2', '/monitor/server', 'C', '0', 'monitor:server:view', 'server', CURRENT_TIMESTAMP),
       ('12', '操作日志', '10', '4', '/monitor/operlog', 'C', '0', 'monitor:operlog:list', 'form', CURRENT_TIMESTAMP),
       ('13', '登录日志', '10', '5', '/monitor/logininfor', 'C', '0', 'monitor:logininfor:list', 'logininfor',
        CURRENT_TIMESTAMP),
       ('150', '系统工具', '0', '3', '#', 'M', '0', '', 'tool', CURRENT_TIMESTAMP),
       ('151', '代码生成', '150', '1', '/tool/gen', 'C', '0', 'tool:gen:view', 'code', CURRENT_TIMESTAMP),
       ('152', '生成查询', '151', '1', '#', 'F', '0', 'tool:gen:list', '#', CURRENT_TIMESTAMP),
       ('134', '操作查询', '12', '1', '#', 'F', '0', 'monitor:operlog:query', '#', CURRENT_TIMESTAMP),
       ('135', '操作删除', '12', '2', '#', 'F', '0', 'monitor:operlog:remove', '#', CURRENT_TIMESTAMP),
       ('136', '操作导出', '12', '3', '#', 'F', '0', 'monitor:operlog:export', '#', CURRENT_TIMESTAMP),
       ('137', '登录查询', '13', '1', '#', 'F', '0', 'monitor:logininfor:query', '#', CURRENT_TIMESTAMP),
       ('138', '登录删除', '13', '2', '#', 'F', '0', 'monitor:logininfor:remove', '#', CURRENT_TIMESTAMP),
       ('139', '登录导出', '13', '3', '#', 'F', '0', 'monitor:logininfor:export', '#', CURRENT_TIMESTAMP),
       ('140', '用户重置密码', '2', '6', '#', 'F', '0', 'system:user:resetPwd', '#', CURRENT_TIMESTAMP),
       ('141', '用户状态修改', '2', '7', '#', 'F', '0', 'system:user:changeStatus', '#', CURRENT_TIMESTAMP),
       ('142', '用户导入', '2', '8', '#', 'F', '0', 'system:user:import', '#', CURRENT_TIMESTAMP);

-- 岗位数据
INSERT INTO sys_post (ID, post_code, post_name, post_sort, status, YN, CREATE_TIME)
VALUES ('1', 'ceo', '董事长', 1, '0', '1', CURRENT_TIMESTAMP),
       ('2', 'se', '项目经理', 2, '0', '1', CURRENT_TIMESTAMP),
       ('3', 'hr', '人力资源', 3, '0', '1', CURRENT_TIMESTAMP),
       ('4', 'user', '普通员工', 4, '0', '1', CURRENT_TIMESTAMP);

-- 用户数据 (密码为 BCrypt 加密的 'admin123' 和 'test123')
-- $2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2 = admin123
-- $2a$10$5OpF3T4xP0fE5XL5gC9L5.LnK9nQ3D8m1O2JxYvR3sT6uW9zA1B2C = test123
INSERT INTO sys_user (ID, dept_id, login_name, user_name, user_type, email, phonenumber, sex, password, status, YN,
                      CREATE_TIME)
VALUES ('1', '1', 'admin', '超级管理员', '00', 'admin@fdtemp.com', '15888888888', '0',
        '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '1', CURRENT_TIMESTAMP),
       ('2', '2', 'test', '测试用户', '00', 'test@fdtemp.com', '15666666666', '1',
        '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '1', CURRENT_TIMESTAMP),
       ('3', '3', 'normal', '普通用户', '00', 'normal@fdtemp.com', '15666666667', '0',
        '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '1', CURRENT_TIMESTAMP);

-- 用户与角色关联
INSERT INTO sys_user_role (user_id, role_id)
VALUES ('1', '1'),
       ('2', '2'),
       ('3', '2');

-- 角色与菜单关联 (admin拥有所有权限, 普通角色只有查看权限)
INSERT INTO sys_role_menu (role_id, menu_id)
VALUES
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
('1', '109'),
('1', '110'),
('1', '111'),
('1', '112'),
('1', '113'),
('1', '114'),
('1', '115'),
('1', '116'),
('1', '117'),
('1', '118'),
('1', '119'),
('1', '120'),
('1', '121'),
('1', '122'),
('1', '123'),
('1', '124'),
('1', '8'),
('1', '125'),
('1', '126'),
('1', '127'),
('1', '128'),
('1', '129'),
('1', '9'),
('1', '130'),
('1', '131'),
('1', '132'),
('1', '133'),
('1', '10'),
('1', '11'),
('1', '143'),
('1', '12'),
('1', '13'),
('1', '134'),
('1', '135'),
('1', '136'),
('1', '137'),
('1', '138'),
('1', '139'),
('1', '140'),
('1', '141'),
('1', '142'),
('1', '150'),
('1', '151'),
('1', '152'),
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
INSERT INTO sys_role_dept (role_id, dept_id)
VALUES ('2', '2'),
       ('2', '3'),
       ('2', '4');

-- 用户与岗位关联
INSERT INTO sys_user_post (user_id, post_id)
VALUES ('1', '1'),
       ('2', '2'),
       ('3', '4');

-- 字典类型
INSERT INTO sys_dict_type (ID, dict_name, dict_type, status, YN, CREATE_TIME)
VALUES ('1', '用户性别', 'sys_user_sex', '0', '1', CURRENT_TIMESTAMP),
       ('2', '菜单状态', 'sys_show_hide', '0', '1', CURRENT_TIMESTAMP),
       ('3', '开关状态', 'sys_normal_disable', '0', '1', CURRENT_TIMESTAMP);

-- 字典数据
INSERT INTO sys_dict_data (ID, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status,
                           YN, CREATE_TIME)
VALUES ('1', 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', '1', CURRENT_TIMESTAMP),
       ('2', 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', '1', CURRENT_TIMESTAMP),
       ('3', 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', '1', CURRENT_TIMESTAMP),
       ('4', 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', '1', CURRENT_TIMESTAMP),
       ('5', 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', '1', CURRENT_TIMESTAMP),
       ('6', 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', '1', CURRENT_TIMESTAMP),
       ('7', 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', '1', CURRENT_TIMESTAMP);

-- 参数配置
INSERT INTO sys_config (ID, config_name, config_key, config_value, config_type, YN, CREATE_TIME)
VALUES ('1', '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', '1', CURRENT_TIMESTAMP),
       ('2', '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', '1', CURRENT_TIMESTAMP),
       ('3', '是否开启验证码', 'sys.account.captchaEnabled', 'false', 'Y', '1', CURRENT_TIMESTAMP);

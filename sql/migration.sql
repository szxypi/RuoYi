-- ============================================================
-- RuoYi 数据库规范迁移脚本
-- 1. 主键改造: bigint auto_increment -> varchar(32) UUID (32位小写无横线)
-- 2. 公共字段替换: del_flag->YN, create_by->CREATE_USER等
-- 3. 删除 sys_user_online 表
-- ============================================================

-- 注意: 执行前请备份数据库!
-- 注意: 请在事务中执行，确保数据一致性
-- UUID格式: LOWER(REPLACE(UUID(), '-', '')) 生成32位小写无横线UUID

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- A. sys_dept
-- ============================================================
ALTER TABLE sys_dept ADD COLUMN ID varchar(32) NULL;
UPDATE sys_dept SET ID = LOWER(REPLACE(UUID(), '-', ''));
ALTER TABLE sys_dept ADD COLUMN YN char(1) DEFAULT '1';
UPDATE sys_dept SET YN = CASE WHEN del_flag = '0' THEN '1' ELSE '0' END;
ALTER TABLE sys_dept ADD COLUMN CREATE_USER varchar(64) NULL;
ALTER TABLE sys_dept ADD COLUMN CREATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_dept ADD COLUMN UPDATE_USER varchar(64) NULL;
ALTER TABLE sys_dept ADD COLUMN UPDATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_dept ADD COLUMN ATTR1 varchar(500) NULL;
UPDATE sys_dept SET CREATE_USER = create_by, UPDATE_USER = update_by;
RENAME TABLE sys_dept TO sys_dept_bak;
CREATE TABLE sys_dept AS SELECT * FROM sys_dept_bak;
-- 保存旧ID到新ID的映射
CREATE TEMPORARY TABLE tmp_dept_map AS SELECT dept_id AS old_id, ID AS new_id FROM sys_dept_bak;
ALTER TABLE sys_dept DROP COLUMN dept_id;
ALTER TABLE sys_dept DROP COLUMN del_flag;
ALTER TABLE sys_dept DROP COLUMN create_by;
ALTER TABLE sys_dept DROP COLUMN update_by;
ALTER TABLE sys_dept DROP COLUMN remark;
ALTER TABLE sys_dept MODIFY COLUMN ID varchar(32) NOT NULL;
ALTER TABLE sys_dept ADD PRIMARY KEY (ID);
-- 更新 parent_id 引用
UPDATE sys_dept d
  JOIN tmp_dept_map m ON d.parent_id = m.old_id
  SET d.parent_id = m.new_id;
-- 更新 ancestors
-- (ancestors 是逗号分隔的部门ID路径，需要逐个替换)
DROP TABLE sys_dept_bak;

-- ============================================================
-- B. sys_user
-- ============================================================
ALTER TABLE sys_user ADD COLUMN ID varchar(32) NULL;
UPDATE sys_user SET ID = LOWER(REPLACE(UUID(), '-', ''));
ALTER TABLE sys_user ADD COLUMN YN char(1) DEFAULT '1';
UPDATE sys_user SET YN = CASE WHEN del_flag = '0' THEN '1' ELSE '0' END;
ALTER TABLE sys_user ADD COLUMN CREATE_USER varchar(64) NULL;
ALTER TABLE sys_user ADD COLUMN CREATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_user ADD COLUMN UPDATE_USER varchar(64) NULL;
ALTER TABLE sys_user ADD COLUMN UPDATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_user ADD COLUMN ATTR1 varchar(500) NULL;
UPDATE sys_user SET CREATE_USER = create_by, UPDATE_USER = update_by;
ALTER TABLE sys_user MODIFY COLUMN password varchar(100);
CREATE TEMPORARY TABLE tmp_user_map AS SELECT user_id AS old_id, ID AS new_id FROM sys_user;
-- 更新 dept_id FK
UPDATE sys_user u
  JOIN tmp_dept_map dm ON u.dept_id = dm.old_id
  SET u.dept_id = dm.new_id;
ALTER TABLE sys_user DROP PRIMARY KEY;
ALTER TABLE sys_user DROP COLUMN user_id;
ALTER TABLE sys_user DROP COLUMN salt;
ALTER TABLE sys_user DROP COLUMN del_flag;
ALTER TABLE sys_user DROP COLUMN create_by;
ALTER TABLE sys_user DROP COLUMN update_by;
ALTER TABLE sys_user DROP COLUMN remark;
ALTER TABLE sys_user MODIFY COLUMN ID varchar(32) NOT NULL;
ALTER TABLE sys_user ADD PRIMARY KEY (ID);
ALTER TABLE sys_user MODIFY COLUMN dept_id varchar(32) NULL;

-- ============================================================
-- C. sys_role
-- ============================================================
ALTER TABLE sys_role ADD COLUMN ID varchar(32) NULL;
UPDATE sys_role SET ID = LOWER(REPLACE(UUID(), '-', ''));
ALTER TABLE sys_role ADD COLUMN YN char(1) DEFAULT '1';
UPDATE sys_role SET YN = CASE WHEN del_flag = '0' THEN '1' ELSE '0' END;
ALTER TABLE sys_role ADD COLUMN CREATE_USER varchar(64) NULL;
ALTER TABLE sys_role ADD COLUMN CREATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_role ADD COLUMN UPDATE_USER varchar(64) NULL;
ALTER TABLE sys_role ADD COLUMN UPDATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_role ADD COLUMN ATTR1 varchar(500) NULL;
UPDATE sys_role SET CREATE_USER = create_by, UPDATE_USER = update_by;
CREATE TEMPORARY TABLE tmp_role_map AS SELECT role_id AS old_id, ID AS new_id FROM sys_role;
ALTER TABLE sys_role DROP PRIMARY KEY;
ALTER TABLE sys_role DROP COLUMN role_id;
ALTER TABLE sys_role DROP COLUMN del_flag;
ALTER TABLE sys_role DROP COLUMN create_by;
ALTER TABLE sys_role DROP COLUMN update_by;
ALTER TABLE sys_role DROP COLUMN remark;
ALTER TABLE sys_role MODIFY COLUMN ID varchar(32) NOT NULL;
ALTER TABLE sys_role ADD PRIMARY KEY (ID);

-- ============================================================
-- D. sys_menu
-- ============================================================
ALTER TABLE sys_menu ADD COLUMN ID varchar(32) NULL;
UPDATE sys_menu SET ID = LOWER(REPLACE(UUID(), '-', ''));
ALTER TABLE sys_menu ADD COLUMN YN char(1) DEFAULT '1';
UPDATE sys_menu SET YN = '1';
ALTER TABLE sys_menu ADD COLUMN CREATE_USER varchar(64) NULL;
ALTER TABLE sys_menu ADD COLUMN CREATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_menu ADD COLUMN UPDATE_USER varchar(64) NULL;
ALTER TABLE sys_menu ADD COLUMN UPDATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_menu ADD COLUMN ATTR1 varchar(500) NULL;
UPDATE sys_menu SET CREATE_USER = create_by, UPDATE_USER = update_by, ATTR1 = remark;
CREATE TEMPORARY TABLE tmp_menu_map AS SELECT menu_id AS old_id, ID AS new_id FROM sys_menu;
-- 更新 parent_id
UPDATE sys_menu m
  JOIN tmp_menu_map mm ON m.parent_id = mm.old_id
  SET m.parent_id = mm.new_id;
ALTER TABLE sys_menu DROP PRIMARY KEY;
ALTER TABLE sys_menu DROP COLUMN menu_id;
ALTER TABLE sys_menu DROP COLUMN create_by;
ALTER TABLE sys_menu DROP COLUMN update_by;
ALTER TABLE sys_menu DROP COLUMN remark;
ALTER TABLE sys_menu MODIFY COLUMN ID varchar(32) NOT NULL;
ALTER TABLE sys_menu ADD PRIMARY KEY (ID);
ALTER TABLE sys_menu MODIFY COLUMN parent_id varchar(32) NULL;

-- ============================================================
-- E. sys_post
-- ============================================================
ALTER TABLE sys_post ADD COLUMN ID varchar(32) NULL;
UPDATE sys_post SET ID = LOWER(REPLACE(UUID(), '-', ''));
ALTER TABLE sys_post ADD COLUMN YN char(1) DEFAULT '1';
UPDATE sys_post SET YN = '1';
ALTER TABLE sys_post ADD COLUMN CREATE_USER varchar(64) NULL;
ALTER TABLE sys_post ADD COLUMN CREATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_post ADD COLUMN UPDATE_USER varchar(64) NULL;
ALTER TABLE sys_post ADD COLUMN UPDATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_post ADD COLUMN ATTR1 varchar(500) NULL;
UPDATE sys_post SET CREATE_USER = create_by, UPDATE_USER = update_by, ATTR1 = remark;
CREATE TEMPORARY TABLE tmp_post_map AS SELECT post_id AS old_id, ID AS new_id FROM sys_post;
ALTER TABLE sys_post DROP PRIMARY KEY;
ALTER TABLE sys_post DROP COLUMN post_id;
ALTER TABLE sys_post DROP COLUMN create_by;
ALTER TABLE sys_post DROP COLUMN update_by;
ALTER TABLE sys_post DROP COLUMN remark;
ALTER TABLE sys_post MODIFY COLUMN ID varchar(32) NOT NULL;
ALTER TABLE sys_post ADD PRIMARY KEY (ID);

-- ============================================================
-- F. sys_config
-- ============================================================
ALTER TABLE sys_config ADD COLUMN ID varchar(32) NULL;
UPDATE sys_config SET ID = LOWER(REPLACE(UUID(), '-', ''));
ALTER TABLE sys_config ADD COLUMN YN char(1) DEFAULT '1';
UPDATE sys_config SET YN = '1';
ALTER TABLE sys_config ADD COLUMN CREATE_USER varchar(64) NULL;
ALTER TABLE sys_config ADD COLUMN CREATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_config ADD COLUMN UPDATE_USER varchar(64) NULL;
ALTER TABLE sys_config ADD COLUMN UPDATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_config ADD COLUMN ATTR1 varchar(500) NULL;
UPDATE sys_config SET CREATE_USER = create_by, UPDATE_USER = update_by, ATTR1 = remark;
ALTER TABLE sys_config DROP PRIMARY KEY;
ALTER TABLE sys_config DROP COLUMN config_id;
ALTER TABLE sys_config DROP COLUMN create_by;
ALTER TABLE sys_config DROP COLUMN update_by;
ALTER TABLE sys_config DROP COLUMN remark;
ALTER TABLE sys_config MODIFY COLUMN ID varchar(32) NOT NULL;
ALTER TABLE sys_config ADD PRIMARY KEY (ID);

-- ============================================================
-- G. sys_dict_type
-- ============================================================
ALTER TABLE sys_dict_type ADD COLUMN ID varchar(32) NULL;
UPDATE sys_dict_type SET ID = LOWER(REPLACE(UUID(), '-', ''));
ALTER TABLE sys_dict_type ADD COLUMN YN char(1) DEFAULT '1';
UPDATE sys_dict_type SET YN = '1';
ALTER TABLE sys_dict_type ADD COLUMN CREATE_USER varchar(64) NULL;
ALTER TABLE sys_dict_type ADD COLUMN CREATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_dict_type ADD COLUMN UPDATE_USER varchar(64) NULL;
ALTER TABLE sys_dict_type ADD COLUMN UPDATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_dict_type ADD COLUMN ATTR1 varchar(500) NULL;
UPDATE sys_dict_type SET CREATE_USER = create_by, UPDATE_USER = update_by, ATTR1 = remark;
ALTER TABLE sys_dict_type DROP PRIMARY KEY;
ALTER TABLE sys_dict_type DROP COLUMN dict_id;
ALTER TABLE sys_dict_type DROP COLUMN create_by;
ALTER TABLE sys_dict_type DROP COLUMN update_by;
ALTER TABLE sys_dict_type DROP COLUMN remark;
ALTER TABLE sys_dict_type MODIFY COLUMN ID varchar(32) NOT NULL;
ALTER TABLE sys_dict_type ADD PRIMARY KEY (ID);

-- ============================================================
-- H. sys_dict_data
-- ============================================================
ALTER TABLE sys_dict_data ADD COLUMN ID varchar(32) NULL;
UPDATE sys_dict_data SET ID = LOWER(REPLACE(UUID(), '-', ''));
ALTER TABLE sys_dict_data ADD COLUMN YN char(1) DEFAULT '1';
UPDATE sys_dict_data SET YN = '1';
ALTER TABLE sys_dict_data ADD COLUMN CREATE_USER varchar(64) NULL;
ALTER TABLE sys_dict_data ADD COLUMN CREATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_dict_data ADD COLUMN UPDATE_USER varchar(64) NULL;
ALTER TABLE sys_dict_data ADD COLUMN UPDATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_dict_data ADD COLUMN ATTR1 varchar(500) NULL;
UPDATE sys_dict_data SET CREATE_USER = create_by, UPDATE_USER = update_by, ATTR1 = remark;
ALTER TABLE sys_dict_data DROP PRIMARY KEY;
ALTER TABLE sys_dict_data DROP COLUMN dict_code;
ALTER TABLE sys_dict_data DROP COLUMN create_by;
ALTER TABLE sys_dict_data DROP COLUMN update_by;
ALTER TABLE sys_dict_data DROP COLUMN remark;
ALTER TABLE sys_dict_data MODIFY COLUMN ID varchar(32) NOT NULL;
ALTER TABLE sys_dict_data ADD PRIMARY KEY (ID);

-- ============================================================
-- I. sys_notice
-- ============================================================
ALTER TABLE sys_notice ADD COLUMN ID varchar(32) NULL;
UPDATE sys_notice SET ID = LOWER(REPLACE(UUID(), '-', ''));
ALTER TABLE sys_notice ADD COLUMN YN char(1) DEFAULT '1';
UPDATE sys_notice SET YN = '1';
ALTER TABLE sys_notice ADD COLUMN CREATE_USER varchar(64) NULL;
ALTER TABLE sys_notice ADD COLUMN CREATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_notice ADD COLUMN UPDATE_USER varchar(64) NULL;
ALTER TABLE sys_notice ADD COLUMN UPDATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_notice ADD COLUMN ATTR1 varchar(500) NULL;
UPDATE sys_notice SET CREATE_USER = create_by, UPDATE_USER = update_by, ATTR1 = remark;
ALTER TABLE sys_notice DROP PRIMARY KEY;
ALTER TABLE sys_notice DROP COLUMN notice_id;
ALTER TABLE sys_notice DROP COLUMN create_by;
ALTER TABLE sys_notice DROP COLUMN update_by;
ALTER TABLE sys_notice DROP COLUMN remark;
ALTER TABLE sys_notice MODIFY COLUMN ID varchar(32) NOT NULL;
ALTER TABLE sys_notice ADD PRIMARY KEY (ID);

-- ============================================================
-- J. sys_oper_log
-- ============================================================
ALTER TABLE sys_oper_log ADD COLUMN ID varchar(32) NULL;
UPDATE sys_oper_log SET ID = LOWER(REPLACE(UUID(), '-', ''));
ALTER TABLE sys_oper_log DROP PRIMARY KEY;
ALTER TABLE sys_oper_log DROP COLUMN oper_id;
ALTER TABLE sys_oper_log MODIFY COLUMN ID varchar(32) NOT NULL;
ALTER TABLE sys_oper_log ADD PRIMARY KEY (ID);

-- ============================================================
-- K. sys_logininfor
-- ============================================================
ALTER TABLE sys_logininfor ADD COLUMN ID varchar(32) NULL;
UPDATE sys_logininfor SET ID = LOWER(REPLACE(UUID(), '-', ''));
ALTER TABLE sys_logininfor DROP PRIMARY KEY;
ALTER TABLE sys_logininfor DROP COLUMN info_id;
ALTER TABLE sys_logininfor MODIFY COLUMN ID varchar(32) NOT NULL;
ALTER TABLE sys_logininfor ADD PRIMARY KEY (ID);

-- ============================================================
-- L. sys_job
-- ============================================================
ALTER TABLE sys_job ADD COLUMN ID varchar(32) NULL;
UPDATE sys_job SET ID = LOWER(REPLACE(UUID(), '-', ''));
ALTER TABLE sys_job ADD COLUMN YN char(1) DEFAULT '1';
UPDATE sys_job SET YN = '1';
ALTER TABLE sys_job ADD COLUMN CREATE_USER varchar(64) NULL;
ALTER TABLE sys_job ADD COLUMN CREATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_job ADD COLUMN UPDATE_USER varchar(64) NULL;
ALTER TABLE sys_job ADD COLUMN UPDATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE sys_job ADD COLUMN ATTR1 varchar(500) NULL;
UPDATE sys_job SET CREATE_USER = create_by, UPDATE_USER = update_by, ATTR1 = remark;
CREATE TEMPORARY TABLE tmp_job_map AS SELECT job_id AS old_id, ID AS new_id FROM sys_job;
ALTER TABLE sys_job DROP PRIMARY KEY;
ALTER TABLE sys_job DROP COLUMN job_id;
ALTER TABLE sys_job DROP COLUMN create_by;
ALTER TABLE sys_job DROP COLUMN update_by;
ALTER TABLE sys_job DROP COLUMN remark;
ALTER TABLE sys_job MODIFY COLUMN ID varchar(32) NOT NULL;
ALTER TABLE sys_job ADD PRIMARY KEY (ID);

-- ============================================================
-- M. sys_job_log
-- ============================================================
ALTER TABLE sys_job_log ADD COLUMN ID varchar(32) NULL;
UPDATE sys_job_log SET ID = LOWER(REPLACE(UUID(), '-', ''));
-- 更新 job_id FK
ALTER TABLE sys_job_log MODIFY COLUMN job_id varchar(32) NULL;
UPDATE sys_job_log jl
  JOIN tmp_job_map jm ON jl.job_id = jm.old_id
  SET jl.job_id = jm.new_id;
ALTER TABLE sys_job_log DROP PRIMARY KEY;
ALTER TABLE sys_job_log DROP COLUMN job_log_id;
ALTER TABLE sys_job_log MODIFY COLUMN ID varchar(32) NOT NULL;
ALTER TABLE sys_job_log ADD PRIMARY KEY (ID);

-- ============================================================
-- N. gen_table
-- ============================================================
ALTER TABLE gen_table ADD COLUMN ID varchar(32) NULL;
UPDATE gen_table SET ID = LOWER(REPLACE(UUID(), '-', ''));
ALTER TABLE gen_table ADD COLUMN YN char(1) DEFAULT '1';
UPDATE gen_table SET YN = '1';
ALTER TABLE gen_table ADD COLUMN CREATE_USER varchar(64) NULL;
ALTER TABLE gen_table ADD COLUMN CREATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE gen_table ADD COLUMN UPDATE_USER varchar(64) NULL;
ALTER TABLE gen_table ADD COLUMN UPDATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE gen_table ADD COLUMN ATTR1 varchar(500) NULL;
UPDATE gen_table SET CREATE_USER = create_by, UPDATE_USER = update_by, ATTR1 = remark;
CREATE TEMPORARY TABLE tmp_gentable_map AS SELECT table_id AS old_id, ID AS new_id FROM gen_table;
ALTER TABLE gen_table DROP PRIMARY KEY;
ALTER TABLE gen_table DROP COLUMN table_id;
ALTER TABLE gen_table DROP COLUMN create_by;
ALTER TABLE gen_table DROP COLUMN update_by;
ALTER TABLE gen_table DROP COLUMN remark;
ALTER TABLE gen_table MODIFY COLUMN ID varchar(32) NOT NULL;
ALTER TABLE gen_table ADD PRIMARY KEY (ID);

-- ============================================================
-- O. gen_table_column
-- ============================================================
ALTER TABLE gen_table_column ADD COLUMN ID varchar(32) NULL;
UPDATE gen_table_column SET ID = LOWER(REPLACE(UUID(), '-', ''));
-- 更新 table_id FK
ALTER TABLE gen_table_column MODIFY COLUMN table_id varchar(32) NULL;
UPDATE gen_table_column gc
  JOIN tmp_gentable_map gm ON gc.table_id = gm.old_id
  SET gc.table_id = gm.new_id;
ALTER TABLE gen_table_column DROP PRIMARY KEY;
ALTER TABLE gen_table_column DROP COLUMN column_id;
ALTER TABLE gen_table_column ADD COLUMN CREATE_USER varchar(64) NULL;
ALTER TABLE gen_table_column ADD COLUMN CREATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE gen_table_column ADD COLUMN UPDATE_USER varchar(64) NULL;
ALTER TABLE gen_table_column ADD COLUMN UPDATE_USER_NICKNAME varchar(64) NULL;
ALTER TABLE gen_table_column ADD COLUMN ATTR1 varchar(500) NULL;
UPDATE gen_table_column SET CREATE_USER = create_by, UPDATE_USER = update_by;
ALTER TABLE gen_table_column DROP COLUMN create_by;
ALTER TABLE gen_table_column DROP COLUMN update_by;
ALTER TABLE gen_table_column MODIFY COLUMN ID varchar(32) NOT NULL;
ALTER TABLE gen_table_column ADD PRIMARY KEY (ID);

-- ============================================================
-- P. 关联表 - sys_user_role
-- ============================================================
ALTER TABLE sys_user_role MODIFY COLUMN user_id varchar(32) NOT NULL;
ALTER TABLE sys_user_role MODIFY COLUMN role_id varchar(32) NOT NULL;
UPDATE sys_user_role ur
  JOIN tmp_user_map um ON ur.user_id = um.old_id
  SET ur.user_id = um.new_id;
UPDATE sys_user_role ur
  JOIN tmp_role_map rm ON ur.role_id = rm.old_id
  SET ur.role_id = rm.new_id;

-- ============================================================
-- Q. 关联表 - sys_role_menu
-- ============================================================
ALTER TABLE sys_role_menu MODIFY COLUMN role_id varchar(32) NOT NULL;
ALTER TABLE sys_role_menu MODIFY COLUMN menu_id varchar(32) NOT NULL;
UPDATE sys_role_menu rm2
  JOIN tmp_role_map rmap ON rm2.role_id = rmap.old_id
  SET rm2.role_id = rmap.new_id;
UPDATE sys_role_menu rm2
  JOIN tmp_menu_map mmap ON rm2.menu_id = mmap.old_id
  SET rm2.menu_id = mmap.new_id;

-- ============================================================
-- R. 关联表 - sys_role_dept
-- ============================================================
ALTER TABLE sys_role_dept MODIFY COLUMN role_id varchar(32) NOT NULL;
ALTER TABLE sys_role_dept MODIFY COLUMN dept_id varchar(32) NOT NULL;
UPDATE sys_role_dept rd
  JOIN tmp_role_map rmap ON rd.role_id = rmap.old_id
  SET rd.role_id = rmap.new_id;
UPDATE sys_role_dept rd
  JOIN tmp_dept_map dmap ON rd.dept_id = dmap.old_id
  SET rd.dept_id = dmap.new_id;

-- ============================================================
-- S. 关联表 - sys_user_post
-- ============================================================
ALTER TABLE sys_user_post MODIFY COLUMN user_id varchar(32) NOT NULL;
ALTER TABLE sys_user_post MODIFY COLUMN post_id varchar(32) NOT NULL;
UPDATE sys_user_post up
  JOIN tmp_user_map umap ON up.user_id = umap.old_id
  SET up.user_id = umap.new_id;
UPDATE sys_user_post up
  JOIN tmp_post_map pmap ON up.post_id = pmap.old_id
  SET up.post_id = pmap.new_id;

-- ============================================================
-- T. 删除 sys_user_online 表 (JWT不需要)
-- ============================================================
DROP TABLE IF EXISTS sys_user_online;

-- ============================================================
-- U. 重命名时间列 (如果列名不同)
-- ============================================================
-- 注意: 如果原表列名已经是 create_time/update_time，
-- 且你希望改为大写 CREATE_TIME/UPDATE_TIME，
-- MySQL列名不区分大小写，所以无需修改。
-- 如果需要显式重命名:
-- ALTER TABLE sys_user CHANGE create_time CREATE_TIME datetime NULL;
-- ALTER TABLE sys_user CHANGE update_time UPDATE_TIME datetime NULL;
-- (对所有表重复)

-- ============================================================
-- 清理临时表
-- ============================================================
DROP TEMPORARY TABLE IF EXISTS tmp_dept_map;
DROP TEMPORARY TABLE IF EXISTS tmp_user_map;
DROP TEMPORARY TABLE IF EXISTS tmp_role_map;
DROP TEMPORARY TABLE IF EXISTS tmp_menu_map;
DROP TEMPORARY TABLE IF EXISTS tmp_post_map;
DROP TEMPORARY TABLE IF EXISTS tmp_job_map;
DROP TEMPORARY TABLE IF EXISTS tmp_gentable_map;

SET FOREIGN_KEY_CHECKS = 1;

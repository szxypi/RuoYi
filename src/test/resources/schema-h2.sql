-- H2 Test Schema for fdtemp

-- 清理旧数据，确保多上下文场景下不会主键冲突
DROP ALL OBJECTS;

-- 创建 find_in_set 函数别名以兼容 MySQL
CREATE ALIAS find_in_set AS 'int findInSet(String needle, String haystack) { if (needle == null || haystack == null) return 0; String[] parts = haystack.split(","); for (int i = 0; i < parts.length; i++) { if (needle.equals(parts[i].trim())) return i + 1; } return 0; }';

-- 部门表
CREATE TABLE IF NOT EXISTS sys_dept (
    ID VARCHAR(32) PRIMARY KEY,
    parent_id VARCHAR(32) DEFAULT '0',
    ancestors VARCHAR(255) DEFAULT '',
    dept_name VARCHAR(50) DEFAULT '',
    order_num INT DEFAULT 0,
    leader VARCHAR(20) DEFAULT NULL,
    phone VARCHAR(11) DEFAULT NULL,
    email VARCHAR(50) DEFAULT NULL,
    status CHAR(1) DEFAULT '0',
    YN CHAR(1) DEFAULT '1',
    CREATE_USER VARCHAR(64) DEFAULT '',
    CREATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    CREATE_TIME DATETIME,
    UPDATE_USER VARCHAR(64) DEFAULT '',
    UPDATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    UPDATE_TIME DATETIME,
    ATTR1 VARCHAR(500) DEFAULT NULL
);

-- 用户信息表
CREATE TABLE IF NOT EXISTS sys_user (
    ID VARCHAR(32) PRIMARY KEY,
    dept_id VARCHAR(32) DEFAULT NULL,
    login_name VARCHAR(50) NOT NULL,
    user_name VARCHAR(50) NOT NULL,
    user_type VARCHAR(10) DEFAULT '00',
    email VARCHAR(100) DEFAULT '',
    phonenumber VARCHAR(11) DEFAULT '',
    sex CHAR(1) DEFAULT '0',
    avatar VARCHAR(200) DEFAULT '',
    password VARCHAR(200) DEFAULT '',
    status CHAR(1) DEFAULT '0',
    YN CHAR(1) DEFAULT '1',
    login_ip VARCHAR(128) DEFAULT '',
    login_date DATETIME,
    pwd_update_date DATETIME,
    CREATE_USER VARCHAR(64) DEFAULT '',
    CREATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    CREATE_TIME DATETIME,
    UPDATE_USER VARCHAR(64) DEFAULT '',
    UPDATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    UPDATE_TIME DATETIME,
    ATTR1 VARCHAR(500) DEFAULT NULL
);

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    ID VARCHAR(32) PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    role_key VARCHAR(100) NOT NULL,
    role_sort INT DEFAULT 0,
    data_scope CHAR(1) DEFAULT '1',
    status CHAR(1) DEFAULT '0',
    YN CHAR(1) DEFAULT '1',
    CREATE_USER VARCHAR(64) DEFAULT '',
    CREATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    CREATE_TIME DATETIME,
    UPDATE_USER VARCHAR(64) DEFAULT '',
    UPDATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    UPDATE_TIME DATETIME,
    ATTR1 VARCHAR(500) DEFAULT NULL
);

-- 菜单权限表
CREATE TABLE IF NOT EXISTS sys_menu (
    ID VARCHAR(32) PRIMARY KEY,
    menu_name VARCHAR(50) NOT NULL,
    parent_id VARCHAR(32) DEFAULT '0',
    order_num VARCHAR(10) DEFAULT '0',
    url VARCHAR(200) DEFAULT '#',
    target VARCHAR(20) DEFAULT '',
    menu_type CHAR(1) DEFAULT '',
    visible CHAR(1) DEFAULT '0',
    is_refresh CHAR(1) DEFAULT '1',
    perms VARCHAR(100) DEFAULT '',
    icon VARCHAR(100) DEFAULT '#',
    CREATE_USER VARCHAR(64) DEFAULT '',
    CREATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    CREATE_TIME DATETIME,
    UPDATE_USER VARCHAR(64) DEFAULT '',
    UPDATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    UPDATE_TIME DATETIME,
    ATTR1 VARCHAR(500) DEFAULT NULL
);

-- 用户和角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id VARCHAR(32) NOT NULL,
    role_id VARCHAR(32) NOT NULL,
    PRIMARY KEY(user_id, role_id)
);

-- 角色和菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    role_id VARCHAR(32) NOT NULL,
    menu_id VARCHAR(32) NOT NULL,
    PRIMARY KEY(role_id, menu_id)
);

-- 角色和部门关联表
CREATE TABLE IF NOT EXISTS sys_role_dept (
    role_id VARCHAR(32) NOT NULL,
    dept_id VARCHAR(32) NOT NULL,
    PRIMARY KEY(role_id, dept_id)
);

-- 岗位信息表
CREATE TABLE IF NOT EXISTS sys_post (
    ID VARCHAR(32) PRIMARY KEY,
    post_code VARCHAR(64) NOT NULL,
    post_name VARCHAR(50) NOT NULL,
    post_sort INT DEFAULT 0,
    status CHAR(1) DEFAULT '0',
    YN CHAR(1) DEFAULT '1',
    CREATE_USER VARCHAR(64) DEFAULT '',
    CREATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    CREATE_TIME DATETIME,
    UPDATE_USER VARCHAR(64) DEFAULT '',
    UPDATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    UPDATE_TIME DATETIME,
    ATTR1 VARCHAR(500) DEFAULT NULL
);

-- 用户与岗位关联表
CREATE TABLE IF NOT EXISTS sys_user_post (
    user_id VARCHAR(32) NOT NULL,
    post_id VARCHAR(32) NOT NULL,
    PRIMARY KEY (user_id, post_id)
);

-- 字典类型表
CREATE TABLE IF NOT EXISTS sys_dict_type (
    ID VARCHAR(32) PRIMARY KEY,
    dict_name VARCHAR(100) DEFAULT '',
    dict_type VARCHAR(100) DEFAULT '',
    status CHAR(1) DEFAULT '0',
    YN CHAR(1) DEFAULT '1',
    CREATE_USER VARCHAR(64) DEFAULT '',
    CREATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    CREATE_TIME DATETIME,
    UPDATE_USER VARCHAR(64) DEFAULT '',
    UPDATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    UPDATE_TIME DATETIME,
    ATTR1 VARCHAR(500) DEFAULT NULL
);

-- 字典数据表
CREATE TABLE IF NOT EXISTS sys_dict_data (
    ID VARCHAR(32) PRIMARY KEY,
    dict_sort INT DEFAULT 0,
    dict_label VARCHAR(100) DEFAULT '',
    dict_value VARCHAR(100) DEFAULT '',
    dict_type VARCHAR(100) DEFAULT '',
    css_class VARCHAR(100) DEFAULT NULL,
    list_class VARCHAR(100) DEFAULT NULL,
    is_default CHAR(1) DEFAULT 'N',
    status CHAR(1) DEFAULT '0',
    YN CHAR(1) DEFAULT '1',
    CREATE_USER VARCHAR(64) DEFAULT '',
    CREATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    CREATE_TIME DATETIME,
    UPDATE_USER VARCHAR(64) DEFAULT '',
    UPDATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    UPDATE_TIME DATETIME,
    ATTR1 VARCHAR(500) DEFAULT NULL
);

-- 参数配置表
CREATE TABLE IF NOT EXISTS sys_config (
    ID VARCHAR(32) PRIMARY KEY,
    config_name VARCHAR(100) DEFAULT '',
    config_key VARCHAR(100) DEFAULT '',
    config_value VARCHAR(500) DEFAULT '',
    config_type CHAR(1) DEFAULT 'N',
    YN CHAR(1) DEFAULT '1',
    CREATE_USER VARCHAR(64) DEFAULT '',
    CREATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    CREATE_TIME DATETIME,
    UPDATE_USER VARCHAR(64) DEFAULT '',
    UPDATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    UPDATE_TIME DATETIME,
    ATTR1 VARCHAR(500) DEFAULT NULL
);

-- 系统访问记录
CREATE TABLE IF NOT EXISTS sys_logininfor (
    ID VARCHAR(32) PRIMARY KEY,
    login_name VARCHAR(50) DEFAULT '',
    ipaddr VARCHAR(128) DEFAULT '',
    login_location VARCHAR(255) DEFAULT '',
    browser VARCHAR(50) DEFAULT '',
    os VARCHAR(50) DEFAULT '',
    status CHAR(1) DEFAULT '0',
    msg VARCHAR(255) DEFAULT '',
    login_time DATETIME
);

-- 操作日志记录
CREATE TABLE IF NOT EXISTS sys_oper_log (
    ID VARCHAR(32) PRIMARY KEY,
    title VARCHAR(50) DEFAULT '',
    business_type INT DEFAULT 0,
    method VARCHAR(200) DEFAULT '',
    request_method VARCHAR(10) DEFAULT '',
    operator_type INT DEFAULT 0,
    oper_name VARCHAR(50) DEFAULT '',
    dept_name VARCHAR(50) DEFAULT '',
    oper_url VARCHAR(255) DEFAULT '',
    oper_ip VARCHAR(128) DEFAULT '',
    oper_location VARCHAR(255) DEFAULT '',
    oper_param VARCHAR(2000) DEFAULT '',
    json_result VARCHAR(2000) DEFAULT '',
    status INT DEFAULT 0,
    error_msg VARCHAR(2000) DEFAULT '',
    cost_time BIGINT DEFAULT 0,
    oper_time DATETIME
);

-- 通知公告表
CREATE TABLE IF NOT EXISTS sys_notice (
    ID VARCHAR(32) PRIMARY KEY,
    notice_title VARCHAR(50) NOT NULL,
    notice_type CHAR(1) NOT NULL,
    notice_content CLOB,
    status CHAR(1) DEFAULT '0',
    CREATE_USER VARCHAR(64) DEFAULT '',
    CREATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    CREATE_TIME DATETIME,
    UPDATE_USER VARCHAR(64) DEFAULT '',
    UPDATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    UPDATE_TIME DATETIME,
    ATTR1 VARCHAR(500) DEFAULT NULL
);

-- 定时任务调度表
CREATE TABLE IF NOT EXISTS sys_job (
    ID VARCHAR(32) PRIMARY KEY,
    job_name VARCHAR(64) DEFAULT '',
    job_group VARCHAR(64) DEFAULT 'DEFAULT',
    invoke_target VARCHAR(500) NOT NULL,
    cron_expression VARCHAR(255) DEFAULT '',
    misfire_policy VARCHAR(20) DEFAULT '3',
    concurrent CHAR(1) DEFAULT '1',
    status CHAR(1) DEFAULT '0',
    YN CHAR(1) DEFAULT '1',
    CREATE_USER VARCHAR(64) DEFAULT '',
    CREATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    CREATE_TIME DATETIME,
    UPDATE_USER VARCHAR(64) DEFAULT '',
    UPDATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    UPDATE_TIME DATETIME,
    ATTR1 VARCHAR(500) DEFAULT NULL
);

-- 定时任务调度日志表
CREATE TABLE IF NOT EXISTS sys_job_log (
    ID VARCHAR(32) PRIMARY KEY,
    job_name VARCHAR(64) DEFAULT '',
    job_group VARCHAR(64) DEFAULT 'DEFAULT',
    invoke_target VARCHAR(500) DEFAULT '',
    job_message VARCHAR(500) DEFAULT '',
    status CHAR(1) DEFAULT '0',
    exception_info VARCHAR(2000) DEFAULT '',
    create_time DATETIME
);

-- 代码生成业务表
CREATE TABLE IF NOT EXISTS gen_table (
    ID VARCHAR(32) PRIMARY KEY,
    table_name VARCHAR(200) DEFAULT '',
    table_comment VARCHAR(500) DEFAULT '',
    sub_table_name VARCHAR(64) DEFAULT NULL,
    sub_table_fk_name VARCHAR(64) DEFAULT NULL,
    class_name VARCHAR(100) DEFAULT '',
    tpl_category VARCHAR(200) DEFAULT 'crud',
    tpl_web_type VARCHAR(30) DEFAULT '',
    package_name VARCHAR(100) DEFAULT '',
    module_name VARCHAR(30) DEFAULT '',
    business_name VARCHAR(30) DEFAULT '',
    function_name VARCHAR(50) DEFAULT '',
    function_author VARCHAR(50) DEFAULT '',
    gen_type CHAR(1) DEFAULT '0',
    gen_path VARCHAR(200) DEFAULT '/',
    pk_column VARCHAR(200) DEFAULT NULL,
    options VARCHAR(1000) DEFAULT NULL,
    tree_code VARCHAR(200) DEFAULT NULL,
    tree_parent_code VARCHAR(200) DEFAULT NULL,
    tree_name VARCHAR(200) DEFAULT NULL,
    parent_menu_id VARCHAR(32) DEFAULT NULL,
    parent_menu_name VARCHAR(50) DEFAULT NULL,
    CREATE_USER VARCHAR(64) DEFAULT '',
    CREATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    CREATE_TIME DATETIME,
    UPDATE_USER VARCHAR(64) DEFAULT '',
    UPDATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    UPDATE_TIME DATETIME,
    ATTR1 VARCHAR(500) DEFAULT NULL
);

-- 代码生成业务表字段
CREATE TABLE IF NOT EXISTS gen_table_column (
    ID VARCHAR(32) PRIMARY KEY,
    table_id VARCHAR(32) DEFAULT NULL,
    column_name VARCHAR(200) DEFAULT NULL,
    column_comment VARCHAR(500) DEFAULT NULL,
    column_type VARCHAR(100) DEFAULT NULL,
    java_type VARCHAR(500) DEFAULT NULL,
    java_field VARCHAR(200) DEFAULT NULL,
    is_pk CHAR(1) DEFAULT NULL,
    is_increment CHAR(1) DEFAULT NULL,
    is_required CHAR(1) DEFAULT NULL,
    is_insert CHAR(1) DEFAULT NULL,
    is_edit CHAR(1) DEFAULT NULL,
    is_list CHAR(1) DEFAULT NULL,
    is_query CHAR(1) DEFAULT NULL,
    query_type VARCHAR(200) DEFAULT 'EQ',
    html_type VARCHAR(200) DEFAULT 'input',
    dict_type VARCHAR(200) DEFAULT '',
    sort INT DEFAULT NULL,
    CREATE_USER VARCHAR(64) DEFAULT '',
    CREATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    CREATE_TIME DATETIME,
    UPDATE_USER VARCHAR(64) DEFAULT '',
    UPDATE_USER_NICKNAME VARCHAR(64) DEFAULT '',
    UPDATE_TIME DATETIME
);

-- Quartz相关表 (简化版)
CREATE TABLE IF NOT EXISTS qrtz_job_details (
    sched_name VARCHAR(120) NOT NULL,
    job_name VARCHAR(200) NOT NULL,
    job_group VARCHAR(200) NOT NULL,
    description VARCHAR(250) NULL,
    job_class_name VARCHAR(250) NOT NULL,
    is_durable VARCHAR(1) NOT NULL,
    is_nonconcurrent VARCHAR(1) NOT NULL,
    is_update_data VARCHAR(1) NOT NULL,
    requests_recovery VARCHAR(1) NOT NULL,
    job_data BLOB NULL,
    PRIMARY KEY (sched_name, job_name, job_group)
);

CREATE TABLE IF NOT EXISTS qrtz_triggers (
    sched_name VARCHAR(120) NOT NULL,
    trigger_name VARCHAR(200) NOT NULL,
    trigger_group VARCHAR(200) NOT NULL,
    job_name VARCHAR(200) NOT NULL,
    job_group VARCHAR(200) NOT NULL,
    description VARCHAR(250) NULL,
    next_fire_time BIGINT NULL,
    prev_fire_time BIGINT NULL,
    priority INTEGER NULL,
    trigger_state VARCHAR(16) NOT NULL,
    trigger_type VARCHAR(8) NOT NULL,
    start_time BIGINT NOT NULL,
    end_time BIGINT NULL,
    calendar_name VARCHAR(200) NULL,
    misfire_instr SMALLINT NULL,
    job_data BLOB NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, job_name, job_group)
        REFERENCES qrtz_job_details(sched_name, job_name, job_group)
);

CREATE TABLE IF NOT EXISTS qrtz_cron_triggers (
    sched_name VARCHAR(120) NOT NULL,
    trigger_name VARCHAR(200) NOT NULL,
    trigger_group VARCHAR(200) NOT NULL,
    cron_expression VARCHAR(200) NOT NULL,
    time_zone_id VARCHAR(80),
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    FOREIGN KEY (sched_name, trigger_name, trigger_group)
        REFERENCES qrtz_triggers(sched_name, trigger_name, trigger_group) ON DELETE CASCADE
);

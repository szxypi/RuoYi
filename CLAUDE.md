# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

基于 RuoYi v4.8.2 深度定制的 Spring Boot 3 后台管理系统。包名 `com.zjjh.fdtemp`，构件名 `fdtemp`。**纯 REST API 后端**，无前端模板（除代码生成器的 Velocity 模板外）。

- Java 17 + Spring Boot 3.5.8 + Spring Security + JWT + MyBatis + Druid + MySQL
- 认证：JWT Bearer Token（access token + refresh token），token 黑名单支持 memory/db 两种模式
- Swagger UI 可访问：`/swagger-ui.html`

## 常用命令

```bash
# 启动应用（需本地 MySQL，默认连接 localhost:3306/ry）
mvn spring-boot:run

# 运行所有测试（H2 内存数据库，无需 MySQL）
mvn test

# 运行单个测试类
mvn test -Dtest=SysUserServiceImplTest

# 运行单个测试方法
mvn test -Dtest=SysUserServiceImplTest#testSelectUserByLoginName

# 打包（跳过测试）
mvn clean package -DskipTests

# 生成覆盖率报告（运行测试后在 target/site/jacoco/ 查看）
mvn test
```

### 环境变量

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `DB_USERNAME` | `root` | MySQL 用户名 |
| `DB_PASSWORD` | `password` | MySQL 密码 |
| `JWT_SECRET` | `defaultDevSecretKeyPleaseReplaceInProduction123456` | JWT 密钥 |
| `DRUID_USERNAME` | `admin` | Druid 监控台用户名 |
| `DRUID_PASSWORD` | _(空)_ | Druid 监控台密码 |

## 代码架构

### 包结构

```
com.zjjh.fdtemp
├── beans/                   # 实体层
│   ├── BaseEntity.java      # 所有实体基类（见下方说明）
│   ├── LoginUser.java       # Spring Security UserDetails 实现
│   └── entity/              # 业务实体（SysUser, SysRole, SysDept 等）
├── common/
│   ├── annotation/          # 自定义注解（@Log, @DataScope, @Anonymous 等）
│   ├── aspectj/             # AOP 切面（日志、数据权限、动态数据源）
│   ├── core/
│   │   ├── BaseController.java          # 控制器基类（分页、文件下载等）
│   │   └── domain/AjaxResult.java       # 统一响应格式（code/msg/data）
│   ├── interceptor/
│   │   └── MybatisAutoFillInterceptor   # 自动填充公共字段（见下方说明）
│   ├── utils/security/      # JwtUtils, SecurityUtils, TokenBlacklist
│   └── web/
│       ├── exception/GlobalExceptionHandler.java  # 全局异常处理
│       └── service/         # CacheService, ConfigService, DictService, PermissionService
├── config/
│   ├── SecurityConfig.java  # Spring Security 配置（JWT 无状态）
│   └── security/UserDetailsServiceImpl.java  # 用户认证实现
├── controller/              # REST 控制器（auth/, system/, monitor/, quartz/, generator/）
├── dao/                     # MyBatis Mapper 接口（命名规范：*Dao.java）
├── filter/                  # JwtAuthenticationFilter, XssFilter
├── service/                 # 服务接口 + impl/ 实现
└── enums/                   # 枚举（BusinessType, UserStatus 等）
```

MyBatis XML 映射文件位于 `src/main/resources/mapper/**/*Dao.xml`。

### BaseEntity（所有实体的基类）

所有业务实体继承 `BaseEntity`，包含以下字段，**由 `MybatisAutoFillInterceptor` 自动填充**，无需手动设置：

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `String` | 32 位小写 UUID（无横线），由 `IdGenerator.nextId()` 生成 |
| `yn` | `String` | 软删除标记：`"1"` = 存在，`"0"` = 已删除 |
| `createTime` / `updateTime` | `Date` | 由拦截器自动设置 |
| `createUser` / `createUserNickname` | `String` | 由拦截器从 SecurityContext 自动填充 |
| `updateUser` / `updateUserNickname` | `String` | 同上 |

### 认证流程

1. `POST /auth/login` → 返回 `token`（access token）和 `refreshToken`
2. 后续请求在 `Authorization` 头携带 `Bearer <token>`
3. `JwtAuthenticationFilter` 验证 token、检查黑名单、拒绝使用 refresh token 访问受保护资源
4. `POST /auth/refresh` → 使用 refresh token 换取新的 token 对（旧 refresh token 加入黑名单）
5. `POST /auth/logout` → 将 access token 和 refresh token 均加入黑名单

公开端点（无需认证）：`/auth/login`、`/auth/register`、`/auth/refresh`、`/captcha/**`、Swagger 相关路径。

### 关键注解

| 注解 | 用途 |
|------|------|
| `@Log(title="...", businessType=BusinessType.INSERT)` | 记录操作日志（AOP 切面处理） |
| `@DataScope(deptAlias="d", userAlias="u")` | 数据权限过滤（将 SQL 片段注入 `entity.params["dataScope"]`） |
| `@Anonymous` | 标记无需认证的端点 |
| `@Sensitive(strategy=DesensitizedType.PHONE)` | 响应脱敏 |
| `@RepeatSubmit` | 防重复提交 |

### 统一响应格式

```java
// 成功
AjaxResult.success(data)          // code=0
AjaxResult.success("消息", data)

// 失败
AjaxResult.error("错误消息")       // code=500

// 分页列表（继承自 BaseController）
getDataTable(list)                 // 返回 TableDataInfo
```

### 数据权限（DataScope）

`@DataScope` 注解通过 AOP 将权限 SQL 片段写入 `entity.getParams().get("dataScope")`。Mapper XML 中使用 `${params.dataScope}` 引用。角色数据权限类型：全部(1)、自定义(2)、本部门(3)、本部门及子部门(4)、仅本人(5)。

### 测试配置

测试使用 H2 内存数据库，Profile 为 `test`：
- 配置文件：`src/test/resources/application-test.yml`
- Schema 初始化：`src/test/resources/schema-h2.sql`
- 测试数据：`src/test/resources/data-h2.sql`
- `TestConfig.java` 提供内存模式的 Quartz Scheduler

标准测试类写法：
```java
@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class MyServiceTest { ... }
```

### 数据库初始化

SQL 文件在 `sql/` 目录：
- `ry_20250416.sql` — 完整建库脚本
- `quartz.sql` — Quartz 定时任务相关表
- `migration.sql` — 增量迁移脚本

Token 黑名单（DB 模式）使用表 `sys_token_blacklist`，字段：`token_hash`（SHA-256）、`expiration_time`、`create_time`。

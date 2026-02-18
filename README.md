# fdtemp 管理系统

基于 Spring Boot 3 构建的轻量级后台管理系统，提供用户权限、字典参数、定时任务、日志监控等通用能力，适合作为企业中台的 REST API 后端脚手架使用。

## 技术栈

| 类别 | 技术 |
|------|------|
| 核心框架 | Spring Boot 3.5.8 / Spring Security |
| 认证 | JWT（access token + refresh token + 黑名单） |
| 持久层 | MyBatis 3 + Druid 连接池 + PageHelper |
| 数据库 | MySQL 8 |
| 定时任务 | Quartz 2.5 |
| API 文档 | SpringDoc OpenAPI 3（Swagger UI） |
| 其他 | Apache POI（Excel 导出）、Velocity（代码生成）、OSHI（服务器监控） |

## 内置模块

- **用户权限体系**：用户 / 角色 / 菜单 / 部门 / 岗位，支持按部门范围过滤数据（`@DataScope`）
- **字典 & 参数管理**：系统级键值配置，支持运行时读取
- **操作日志 & 登录日志**：AOP 注解驱动，自动记录请求与响应
- **定时任务**：Quartz 在线管理，支持 Cron 表达式，记录执行日志
- **代码生成器**：根据数据库表结构生成 Controller / Service / Mapper / XML 骨架
- **服务监控**：CPU / 内存 / 磁盘 / JVM 实时数据
- **缓存监控**：系统配置缓存和字典缓存的查看与清理

## 快速启动

### 前置依赖

- Java 17+
- Maven 3.8+
- MySQL 8

### 初始化数据库

```bash
mysql -u root -p < sql/ry_20250416.sql
```

### 配置数据源

编辑 `src/main/resources/application-druid.yml`，或通过环境变量覆盖：

```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET=your_256bit_secret_key_here
```

### 启动

```bash
mvn spring-boot:run
```

默认监听 `80` 端口，可在 `application.yml` 中修改 `server.port`。

### API 文档

启动后访问：`http://localhost/swagger-ui.html`

## 认证方式

```http
POST /auth/login
Content-Type: application/json

{"username": "admin", "password": "admin123"}
```

响应返回 `token` 和 `refreshToken`，后续请求在 `Authorization` 头携带：

```
Authorization: Bearer <token>
```

token 过期后使用 `POST /auth/refresh` 换取新的 token 对；`POST /auth/logout` 使 token 立即失效。

## API 路径规范

| 操作 | 路径 |
|------|------|
| 列表查询 | `GET /xxx/list` |
| 新增 | `POST /xxx/add` |
| 编辑 | `POST /xxx/edit` |
| 删除 | `POST /xxx/remove` |

统一响应格式：HTTP 状态码始终为 `200`，业务状态通过 `code` 字段区分（`0` = 成功，`500` = 错误）。

## 运行测试

测试使用 H2 内存数据库，无需本地 MySQL：

```bash
mvn test
```

## 许可证

[MIT](LICENSE)

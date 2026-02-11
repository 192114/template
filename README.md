# Template

Spring Boot 模板工程，用于统一技术栈与开发规范。本文档为**开发规范**说明，包含目录职责、命名约定、配置参数及构建与质量要求。

---

## 1. 项目简介与技术栈

- **项目**：Spring Boot 4.0.2 + Java 21，包名 `com.shadow.template`。
- **技术栈**：
  - Web：Spring WebMVC、Validation
  - 安全：Spring Security、JWT（jjwt）
  - 数据：MyBatis-Plus、MySQL、Flyway、Redis
  - 其他：Spring Mail、Actuator、SpringDoc（OpenAPI/Scalar）
- **质量**：Checkstyle、SpotBugs（含 FindSecBugs），在 `mvn verify` 阶段执行。

---

## 2. 快速开始

### 2.1 环境准备

- 复制 `.env.example` 为 `.env`，填写所需环境变量
- 确保 MySQL、Redis 已启动

### 2.2 开发环境启动

| 方式 | 说明 |
|------|------|
| VS Code 调试 | 使用 `.vscode/launch.json` 中的 `TemplateApplication`，自动加载 `.env` 并设置 `SPRING_PROFILES_ACTIVE=dev` |
| Maven 命令行 | `mvn spring-boot:run`（默认 dev profile） |
| 显式指定 profile | `mvn spring-boot:run -Dspring-boot.run.profiles=dev` |

开发环境特性（来自 `application-dev.yml`）：
- 接口文档（Scalar）：`http://localhost:8023/api/scalar`（详见 2.5 接口文档）
- 日志级别：DEBUG
- Actuator 暴露：health、info、metrics、env
- 数据库连接池：10/2，开启泄漏检测

### 2.3 生产环境启动

| 配置项 | 说明 |
|--------|------|
| Profile 激活 | 部署时设置环境变量 `SPRING_PROFILES_ACTIVE=prod` |
| 配置来源 | `application-prod.yml` 覆盖 base 配置 |

生产环境特性（来自 `application-prod.yml`）：
- Scalar 文档关闭
- 日志级别：INFO/WARN，路径 `/var/log/template`
- Actuator 仅暴露 health、info，`show-details: never`
- 数据库连接池：20/5，关闭泄漏检测
- Flyway `clean-disabled: true`

### 2.4 Profile 配置文件说明

| 文件 | 用途 |
|------|------|
| `application.yml` | 基础配置，默认 profile 为 dev |
| `application-dev.yml` | 开发环境覆盖 |
| `application-prod.yml` | 生产环境覆盖 |

### 2.5 接口文档

开发环境（dev profile）下提供交互式 API 文档与 OpenAPI 规范，便于浏览与调试接口。

| 用途 | 地址 |
|------|------|
| Scalar 文档页（浏览、调试接口） | `http://localhost:8023/api/scalar`（或 `/api/scalar.html`，以实际重定向为准） |
| OpenAPI JSON（规范，供工具/导入） | `http://localhost:8023/api/v3/api-docs` |

---

## 3. 目录结构与职责

根包：`src/main/java/com/shadow/template/`。

```
com.shadow.template
├── TemplateApplication.java

├── config                    # 应用与基础设施配置
│   ├── AppProperties.java
│   ├── InfraProperties.java
│   ├── SecurityConfig.java
│   └── TimeMetaObjectHandler.java

├── common                    # 跨模块通用组件
│   ├── exception             # 全局异常
│   │   ├── BizException.java
│   │   ├── GlobalExceptionHandler.java
│   │   └── HttpStatusMapper.java
│   ├── filter
│   │   └── TraceIdFilter.java
│   ├── result                # 统一返回
│   │   ├── FieldErrorDetail.java
│   │   ├── Result.java
│   │   └── ResultCode.java
│   └── util
│       ├── CookieUtils.java
│       ├── EmailCodeGenerator.java
│       └── RequestUtils.java

├── modules                   # 按业务域拆分的模块
│   ├── auth
│   │   ├── constants
│   │   ├── controller
│   │   ├── dto
│   │   ├── entity
│   │   ├── enums
│   │   ├── mapper
│   │   ├── service
│   │   │   └── impl
│   │   ├── validation
│   │   └── vo
│   └── user
│       ├── dto
│       ├── entity
│       ├── enums
│       ├── mapper
│       └── service
│           └── impl
└── security                  # 认证授权（不放在 modules 下）
    ├── JwtAuthenticationFilter.java
    ├── JwtTokenProvider.java
    ├── RestAccessDeniedHandler.java
    └── RestAuthenticationEntryPoint.java
```

### 根包下职责

| 包/目录 | 职责 |
|--------|------|
| `config` | 应用与基础设施配置：`*Properties`（绑定 yaml）、`*Config`（Bean/安全等）、`TimeMetaObjectHandler` 等 |
| `common` | 跨模块通用：`exception`（BizException、GlobalExceptionHandler、HttpStatusMapper）、`result`（Result、ResultCode、FieldErrorDetail）、`filter`（TraceIdFilter）、`util` |
| `modules` | 按业务域拆分的模块，每个子包为一个领域（如 auth、user） |
| `security` | 与认证授权相关的过滤器、Token 提供者、401/403 处理器 |

### 模块内职责（以 `modules.auth` 为例）

| 子包 | 职责 | 命名示例 |
|------|------|----------|
| `controller` | 对外 HTTP 接口，仅做参数校验与调用 Service | `AuthController` |
| `service` / `service.impl` | 业务逻辑；接口放 `service`，实现放 `impl` | `AuthService`, `AuthServiceImpl` |
| `mapper` | MyBatis-Plus Mapper，对应表 | `UserSessionMapper` |
| `entity` | 与数据库表一一对应的实体 | `UserSessionEntity` |
| `dto` | 入参/内部传输对象（请求体、命令等） | `UserLoginDto`, `SendEmailDto`, `UserRegisterDto` |
| `vo` | 仅用于响应的视图对象 | `TokenResponseVo` |
| `enums` | 本模块枚举 | `LoginTypeEnum`, `EmailUsageEnum` |
| `constants` | 本模块常量（如正则） | `AuthRegex` |
| `validation` | 自定义校验注解与 Validator | `LoginConstraint`, `LoginConstraintValidator` |

**约定**：入参/命令统一使用 `*Dto` 或 `*Command` 一种命名（本工程中两者并存，新代码可任选其一并保持模块内一致）；VO 仅用于 HTTP 响应体。

---

## 4. 命名规范

- **包名**：全小写，单词连写；模块用 `modules.<领域>`，如 `modules.auth`、`modules.user`。
- **类名**：
  - Controller：`*Controller`，REST 使用 `@RestController`。
  - Service：接口 `*Service`，实现类 `*ServiceImpl`，放在 `service.impl`。
  - Mapper：`*Mapper`，继承 MyBatis-Plus `BaseMapper<*Entity>`。
  - 实体：`*Entity`，与表名对应（表名 snake_case，如 `user_session`）。
  - 配置属性类：`*Properties`（`@ConfigurationProperties`）；配置类：`*Config`。
  - 异常：业务异常 `BizException`；全局处理 `GlobalExceptionHandler`。
- **接口与 URL**：REST，路径小写、短横线可选；统一前缀在 `server.servlet.context-path`（当前为 `/api`），模块路径如 `/auth`、`/user`。
- **配置文件**：主配置 `application.yml`；敏感信息使用环境变量占位符 `${ENV_VAR}`，不写死。

---

## 5. 配置与参数说明

主配置：`src/main/resources/application.yml`。自定义配置类：`config/AppProperties.java`（前缀 `app`）。

主配置中 `spring.profiles.active` 默认 `dev`，可通过环境变量 `SPRING_PROFILES_ACTIVE` 覆盖。配置文件 `application-{profile}.yml` 会覆盖 base 配置中的同名项。

### 5.1 Spring

| 配置项 | 说明 |
|--------|------|
| `spring.application.name` | 应用名，如 `template`。 |
| `spring.flyway.enabled` | 是否启用 Flyway。 |
| `spring.flyway.validate-on-migrate` | 迁移时是否校验。 |
| `spring.mvc.async.request-timeout` | 异步请求超时，如 `30s`。 |
| `spring.datasource.url` / `username` / `password` | 数据源，建议用环境变量。 |
| `spring.datasource.hikari.*` | 连接池：`connection-timeout`（取连接等待 ms）、`validation-timeout`、`idle-timeout`、`max-lifetime`、`maximum-pool-size`、`minimum-idle`、`leak-detection-threshold`（泄漏检测阈值 ms）。 |
| `spring.data.redis.host` / `port` / `username` / `password` | Redis 连接。 |
| `spring.mail.*` | 发件：host、port、username、password、protocol（如 smtps）、ssl、smtp 超时与 starttls。 |

### 5.2 Server

| 配置项 | 说明 |
|--------|------|
| `server.port` | 服务端口，如 `8023`。 |
| `server.servlet.context-path` | 上下文路径，如 `/api`，所有接口以此为前缀。 |
| `server.tomcat.connection-timeout` | 连接超时。 |
| `server.tomcat.keep-alive-timeout` | Keep-Alive 超时。 |
| `server.tomcat.max-keep-alive-requests` | 单连接最大请求数。 |
| `server.servlet.session.timeout` | Session 超时，如 `30m`。 |

### 5.3 app（自定义）

| 配置项 | 说明 |
|--------|------|
| `app.jwt.secret` | JWT 签名密钥，UTF-8 字节长度至少 32。 |
| `app.jwt.issuer` | JWT 签发者。 |
| `app.jwt.expire-seconds` | Access Token 有效期（秒），建议 ≥60。 |
| `app.refresh.expire-days` | Refresh Token 有效天数，≥1。 |
| `app.security.permit-all` | 逗号分隔的放行路径，如 `/auth/**`、`/actuator/health`。 |
| `app.security.cors.allowed-origins` | CORS 允许的源。 |
| `app.security.cors.allowed-methods` | CORS 允许的 HTTP 方法。 |
| `app.security.cors.allowed-headers` | CORS 允许的请求头。 |
| `app.security.cors.allow-credentials` | 是否允许携带凭证。 |

### 5.4 其他

| 配置项 | 说明 |
|--------|------|
| `mybatis-plus.configuration.map-underscore-to-camel-case` | 下划线转驼峰。 |
| `mybatis-plus.configuration.log-impl` | SQL 日志实现，如 Slf4j。 |
| `logging.file.path` | 日志目录，如 `logs`。 |
| `logging.level.*` | 各包日志级别（root、com.shadow.template、org.mybatis 等）。 |
| `management.endpoints.web.exposure.include` | 暴露的 Actuator 端点，如 `health,info,metrics`。 |

### 5.5 环境变量汇总

以下为 `application.yml` 中使用的环境变量，敏感信息请通过环境变量或外部配置注入。

| 变量名 | 用途 | 必填 | 示例（脱敏） |
|--------|------|------|--------------|
| `SPRING_PROFILES_ACTIVE` | 激活的 profile（dev/prod） | 否 | `dev`（默认）、`prod` |
| `DATABASE_URL` | MySQL JDBC URL | 是 | `jdbc:mysql://localhost:3306/db` |
| `DATABASE_USERNAME` | 数据库用户名 | 是 | - |
| `DATABASE_PASSWORD` | 数据库密码 | 是 | - |
| `REDIS_HOST` | Redis 主机 | 是 | `localhost` |
| `REDIS_PORT` | Redis 端口 | 是 | `6379` |
| `REDIS_USERNAME` | Redis 用户名 | 否 | - |
| `REDIS_PASSWORD` | Redis 密码 | 否 | - |
| `MAIL_HOST` | SMTP 主机 | 是 | `smtp.example.com` |
| `MAIL_PORT` | SMTP 端口 | 是 | `465` |
| `MAIL_USERNAME` | 发件邮箱 | 是 | - |
| `MAIL_PASSWORD` | 发件密码/授权码 | 是 | - |
| `JWT_SECRET` | JWT 密钥（≥32 字节 UTF-8） | 是 | - |
| `JWT_ISSUER` | JWT 签发者 | 是 | `template` |
| `JWT_EXPIRE_SECONDS` | Access Token 有效期（秒） | 是 | `3600` |
| `REFRESH_EXPIRE_DAYS` | Refresh Token 有效天数 | 是 | `7` |
| `CORS_ALLOWED_ORIGINS` | CORS 允许的源，逗号分隔 | 是 | `http://localhost:3000` |
| `CORS_ALLOWED_METHODS` | CORS 允许的方法 | 是 | `GET,POST,PUT,DELETE,OPTIONS` |
| `CORS_ALLOWED_HEADERS` | CORS 允许的请求头 | 是 | `*` 或具体列表 |
| `CORS_ALLOW_CREDENTIALS` | 是否允许携带 Cookie | 是 | `true` |

---

## 6. 统一返回与异常

- **成功/失败**：统一使用 `Result<T>`（code、message、data）。成功：`Result.success(data)` 或 `Result.success()`；失败：`Result.failure(ResultCode)` 或 `Result.failure(code, message)`。
- **业务异常**：抛出 `BizException(ResultCode)`，由 `GlobalExceptionHandler` 转换为 HTTP 状态码与 Result 体；状态码与错误码映射见 `HttpStatusMapper`。
- **校验**：Controller 入参使用 `@RequestBody @Valid XxxDto`；自定义校验使用 `@XxxConstraint` 及对应 `ConstraintValidator`。

---

## 7. 构建与质量

- **编译与验证**：`mvn clean verify`（含 Checkstyle、SpotBugs）。
- **Checkstyle**：配置在 `config/checkstyle/checkstyle.xml`（如行宽 120、忽略 import 等）；新代码需通过 `mvn checkstyle:check`。
- **SpotBugs**：含 FindSecBugs；排除项在 `config/spotbugs/excludeFilter.xml`。

---

## 8. 其他约定

- **数据库变更**：仅通过 Flyway 脚本，放在 `src/main/resources/db/migration/`，命名 `V<序号>__<描述>.sql`。
- **日志**：使用 Slf4j；TraceId 由 `TraceIdFilter` 注入 MDC（请求头 `X-Trace-Id` 或自动生成），便于链路追踪。
- **认证**：Access Token 放在请求头 `Authorization: Bearer <token>`；Refresh Token 按当前实现（如 Cookie）传递，具体见各接口说明。

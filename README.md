### 目录结构

```
src/main/java/com/shadow/template/
├── config/                 // 安全、Web、MyBatis、Swagger
├── controller/             // Web入口
├── service/                // 业务接口
│   └── impl/               // 业务实现
├── mapper/                 // MyBatis Mapper 接口
├── model/                  // 领域模型
│   ├── entity/             // 与数据库映射
│   ├── dto/                // 请求参数
│   └── vo/                 // 返回结构
├── security/               // JWT、鉴权逻辑、UserDetails
├── common/                 // 统一响应、常量、错误码、枚举
├── exception/              // 业务异常 + 全局异常处理
├── utils/                  // 常用工具
└── TemplateApplication.java
```

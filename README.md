### 目录结构

```
com.example.demo
├── DemoApplication.java

├── config              # 配置类
│   ├── MybatisPlusConfig.java
│   ├── WebConfig.java
│   └── SwaggerConfig.java

├── common              # 通用组件
│   ├── result          # 统一返回
│   │   ├── Result.java
│   │   └── ResultCode.java
│   ├── exception       # 全局异常
│   │   ├── BizException.java
│   │   └── GlobalExceptionHandler.java
│   └── util

├── module              # 按业务拆包（强烈推荐）
│   └── user
│       ├── controller
│       │   └── UserController.java
│       ├── service
│       │   ├── UserService.java
│       │   └── impl
│       │       └── UserServiceImpl.java
│       ├── mapper
│       │   └── UserMapper.java
│       ├── entity
│       │   └── User.java
│       ├── dto
│       │   └── UserCreateDTO.java
│       └── vo
│           └── UserVO.java

```

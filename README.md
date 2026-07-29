# 具身智能机器人主题邮局原型系统

基于带灵巧手人形机器人的邮政主题网点原型系统，聚焦 **业务中台后端 + 后台管理前端 + 邮政系统对接适配层**。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Java 17+, Spring Boot 3.3.x, Spring MVC, Spring Security |
| ORM | MyBatis-Plus 3.5.x |
| 数据库 | MySQL 8.x + Flyway 版本管理 |
| 缓存 | Redis 7 (库存原子操作 / 分布式锁) |
| 鉴权 | Spring Security + JWT (RBAC) |
| API 文档 | Knife4j (OpenAPI 3.0) |
| 前端 | Vue 3 + Vite + Element Plus + Pinia + Vue Router |
| 基础设施 | Docker Compose |

## 项目结构

```
RobotDemo/
├── backend/                          # Spring Boot 后端
│   └── src/main/java/com/postal/robotdemo/
│       ├── controller/               # 接口层 (RESTful API)
│       ├── service/                  # 业务服务层
│       │   └── impl/                 # 服务实现
│       ├── mapper/                   # MyBatis-Plus Mapper
│       ├── entity/                   # 数据库实体
│       ├── dto/                      # 请求 DTO
│       ├── vo/                       # 响应 VO
│       ├── adapter/                  # 邮政系统对接适配层
│       │   ├── client/               # 邮政接口客户端
│       │   └── mock/                 # Mock 桩实现
│       ├── config/                   # Spring 配置类
│       ├── common/                   # 通用工具 (统一响应/异常处理)
│       └── security/                 # Spring Security 配置
├── frontend/                         # Vue 3 前端
│   └── src/
│       ├── views/                    # 页面组件
│       ├── router/                   # 路由配置
│       ├── stores/                   # Pinia 状态管理
│       ├── api/                      # API 请求封装
│       └── components/               # 公共组件
├── docker-compose.yml                # MySQL + Redis 一键启动
└── README.md
```

## 快速启动

### 1. 环境要求
- JDK 17+
- Maven 3.6+
- Node.js 18+
- Docker Desktop

### 2. 启动 MySQL + Redis

```bash
docker-compose up -d
```

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端启动后访问: http://localhost:8080
API 文档: http://localhost:8080/doc.html

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端启动后访问: http://localhost:5173

### 5. 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 运营 | operator | admin123 |
| 维护 | maintainer | admin123 |

## 邮政对接适配层

### Mock 模式切换

在 `application.yml` 中配置:

```yaml
postal:
  mock-enabled: true    # true=Mock模式, false=真实邮政接口
```

或通过环境变量:

```bash
export POSTAL_MOCK_ENABLED=true
```

### 已封装的邮政接口

| 接口 | 说明 | 服务编码 |
|------|------|----------|
| 邮件资费查询 | 根据收发地址/重量/尺寸查询邮费 | F8 |
| 邮件号码生成 | 生成运单号 | F8 |
| 收寄订单提交 | 提交完整寄件订单 | F8 |
| 生成收款二维码 | 生成支付二维码 | F8 |
| 支付状态查询 | 查询支付结果 | F8 |

### 签名算法

```
DigitalSign = BASE64(MD5(ServiceCode + Version + ActionCode + TransactionID 
               + SrcSysID + DstSysID + ReqTime + SessionBody内容 + 秘钥))
```

### Mock 支持的场景
- 正常返回 (按接口文档结构)
- 支付状态流转: 支付中(00) → 支付成功(01)
- 签名错误 (错误码 1002)
- 超时模拟
- 报文错误 (错误码 9009)

## 核心功能模块

- [x] 项目骨架搭建
- [ ] 商品管理 (CRUD/上下架/标签/陈列点位/抓取标识)
- [ ] 库存管理 (实时库存/锁定释放/低库存告警/视觉校验回写)
- [ ] 订单状态机 (待支付→支付中→成功/失败/取消/超时/人工)
- [ ] 任务调度状态机 (CREATED→QUEUED→RUNNING→SUCCEEDED/FAILED/...)
- [ ] 邮政对接适配层 (5个接口 + Mock)
- [ ] 用户认证与 RBAC 权限
- [ ] 审计日志
- [ ] 后台管理界面 (8个页面)
- [ ] 断网降级机制
- [ ] 集成测试

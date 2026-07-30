# 具身智能机器人主题邮局原型系统

基于带灵巧手人形机器人的邮政主题网点原型系统，聚焦 **业务中台后端 + 后台管理前端 + 邮政系统对接适配层**。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Java 17+, Spring Boot 3.3.x, Spring MVC, Spring Security |
| ORM | MyBatis-Plus 3.5.x |
| 数据库 | MySQL 8.x（手动建表） |
| 缓存 | Redis 7 (库存原子操作 / 分布式锁) |
| 鉴权 | Spring Security + JWT (RBAC) |
| API 文档 | Knife4j (OpenAPI 3.0) |
| 前端 | Vue 3 + Vite + Element Plus + Pinia + Vue Router |
| 基础设施 | Docker Compose |

## 项目结构

```
RobotDemo/
├── backend/                            # Spring Boot 后端
│   └── src/main/java/com/postal/robotdemo/
│       ├── adapter/                    # 邮政系统对接适配层 (重点)
│       │   ├── SignatureUtil.java      # MD5+BASE64 签名 (附录三)
│       │   ├── SessionHeader.java      # 会话控制头
│       │   ├── YYRoot.java             # 统一请求根结构
│       │   ├── TransactionIdGenerator  # 32位全局唯一ID
│       │   ├── PostalErrorCode.java    # 13个错误码映射
│       │   ├── client/PostalClient.java # 统一调用+幂等+重试
│       │   └── mock/PostalMockService  # Mock 桩 (5场景)
│       ├── controller/                 # 接口层 (9个Controller)
│       ├── service/                    # 业务服务层 (7个Service)
│       ├── mapper/                     # MyBatis-Plus Mapper
│       ├── entity/                     # 数据库实体 (7个)
│       ├── dto/postal/                 # 邮政接口DTO (10个, V_/N_/F_前缀)
│       ├── enums/                      # 状态枚举 (6个)
│       ├── config/                     # MyBatisPlus/Redis/MetaHandler
│       ├── security/                   # JWT + Spring Security + RBAC
│       └── common/                     # Result/BizException/GlobalExceptionHandler
├── frontend/                           # Vue 3 前端
│   └── src/
│       ├── views/                      # 8个页面 + Layout
│       ├── router/                     # 路由守卫 (未登录→/login)
│       ├── api/                        # 10个API封装
│       └── utils/                      # Axios + JWT拦截器
├── docker-compose.yml                  # MySQL + Redis 一键启动
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

### 3. 手动建表

```bash
docker exec -i robotdemo-mysql mysql -uroot -proot123 robotdemo < backend/src/main/resources/db/migration/V1__init_schema.sql
```

### 4. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端启动后访问: http://localhost:8080  
API 文档 (Knife4j): http://localhost:8080/doc.html

### 5. 启动前端

新开终端：
```bash
cd frontend
npm install
npm run dev
```

前端启动后访问: http://localhost:5173

### 6. 测试账号

| 角色 | 用户名 | 密码 | 权限 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 全部 (含用户管理) |
| 运营 | operator | admin123 | 商品/订单/库存 |
| 维护 | maintainer | admin123 | 任务/告警/库存 |

## 业务流程演示

```
1. 登录 (admin/admin123)
2. 商品管理 → 新增商品 (如 "故宫邮册" 39.9元)
3. 订单管理 → 创建订单 → 系统自动:
   a) 锁定库存 (Redis分布式锁)
   b) 调用邮政Mock生成快递单号
   c) 调用邮政Mock查询邮费
4. 生成支付二维码 → Mock返回支付流水号
5. 第1次查询支付 → Mock返回 "00 支付中"
6. 第2次查询支付 → Mock返回 "01 支付成功" → 自动扣减库存
7. 库存管理 → 看到库存少了1件
8. 任务监控 → 看到支付任务执行记录
9. 审计日志 → 看到操作留痕
```

## 邮政对接适配层 (核心模块)

### 设计思路

采用适配器模式，业务代码通过 `PostalClient` 统一调用邮政接口，Mock/真实接口通过配置开关切换，业务代码不感知差异。

### 会话控制 (YYRoot)

所有接口共用同一套请求封装：
```json
{
  "SessionHeader": {
    "ServiceCode": "F8",
    "Version": "YY-1.0",
    "ActionCode": "0",
    "TransactionID": "ROBOT202607301034560010000000001",
    "SrcSysID": "ROBOT",
    "DstSysID": "XYDYYQDXT",
    "DigitalSign": "BASE64(MD5(...))",
    "ReqTime": "20260730103456"
  },
  "SessionBody": { /* 业务参数 */ }
}
```

### 签名算法 (严格按文档附录三)

```java
// 拼接: ServiceCode + Version + ActionCode + TransactionID
//      + SrcSysID + DstSysID + ReqTime + SessionBody内容 + 秘钥
String str = "F8" + "YY-1.0" + "0" + transactionId + "ROBOT"
           + "XYDYYQDXT" + reqTime + sessionBodyJson;
// MD5 + BASE64
String digitalSign = Base64.getEncoder()
    .encodeToString(MessageDigest.getInstance("MD5")
    .digest((str + SECRET_KEY).getBytes(StandardCharsets.UTF_8)));
```

### TransactionID 格式

```
[5位平台编码] + [17位日期 yyyyMMddHHmmssfff] + [10位流水号]
例: ROBOT202607301034560010000000001
```

### 已封装的邮政接口

| 接口 | 入参关键字段 | 出参关键字段 |
|------|-------------|-------------|
| 邮件资费查询 | productCode, postProvinceCode, disProvinceCode, weight, isValue | result.data.fee |
| 邮件号码生成 | V_SFDM, V_JGBH, V_YWCPDM, V_YWCPMC | result.V_YJHM |
| 收寄订单提交 | 100+字段(机构/员工/寄件人/收件人/邮件/包装物/内件/资费) | result.V_CXLSH, F_ZZF, F_YSZZF |
| 生成收款二维码 | vJgbh, vTxdm, emp, vCxlsh | datas.V_PTLSH, V_ZFLSH, V_EWMURL |
| 支付状态查询 | vCxlsh, vJgbh, vZflsh | result.zfzt (01/02/03/05/00) |

### Mock 模式

配置开关: `postal.mock-enabled: true`

支持的场景:
- 正常返回 (按接口文档结构)
- 支付状态流转: 第1次查→"00"(支付中), 第2次→"01"(支付成功)
- 签名错误 (1002)
- 报文解密错误 (9009)
- 访问量超限 (1006)
- 超时模拟

### 错误码映射

| 邮政错误码 | 含义 | 本地错误码 |
|-----------|------|-----------|
| 0000 | 成功 | 200 |
| 9009 | 报文解密错误 | 49009 |
| 1002 | 签名错误 | 41002 |
| 1006 | 访问量超限 | 41006 |
| ... | 共13个映射 | |

## 状态机设计

### 订单状态机

```
PENDING (待支付)
  → PAYING (支付中, 生成二维码后)
    → PAID (支付成功, 扣库存)
    → FAILED (支付失败, 释放库存)
    → TIMEOUT (超时, 释放库存)
    → MANUAL_REQUIRED (需人工处理)
  → CANCELLED (已取消, 释放库存)
```

### 任务状态机

```
CREATED → QUEUED → RUNNING → SUCCEEDED
                            → FAILED → (retry < max) → RUNNING
                                     → (retry >= max) → MANUAL_REQUIRED (+告警)
                            → CANCELLED
                            → PAUSED
```

## 权限设计 (RBAC)

| 角色 | 商品CRUD | 订单管理 | 库存查看 | 任务监控 | 告警管理 | 审计日志 | 用户管理 |
|------|---------|---------|---------|---------|---------|---------|---------|
| ADMIN | 全部 | 全部 | 全部 | 全部 | 全部 | 全部 | 全部 |
| OPERATOR | 全部 | 全部 | 全部 | 查看 | 查看 | 查看 | - |
| MAINTAINER | 查看 | 查看 | 全部 | 全部 | 全部 | 查看 | - |

## 核心功能模块

-  项目骨架搭建 (docker-compose + 分层结构)
-  商品管理 (CRUD/上下架/标签/陈列点位/机器人抓取标识)
-  库存管理 (Redis分布式锁 + 乐观锁/锁定释放扣减/低库存告警/视觉校验回写)
-  订单状态机 (PENDING→PAYING→PAID/FAILED/CANCELLED/TIMEOUT/MANUAL_REQUIRED)
-  支付闭环 (创建订单→邮件号→资费→二维码→支付状态流转→库存联动)
-  任务调度状态机 (8状态 + 连续失败转人工 + 优先级)
-  邮政对接适配层 (YYRoot + SessionHeader + 签名算法 + 5接口DTO + Mock + 13错误码)
-  JWT + RBAC 鉴权 (ADMIN/OPERATOR/MAINTAINER)
-  审计日志 (操作人/时间/类型/对象/结果/流水号)
-  告警管理 (INFO/WARN/ERROR/CRITICAL 4级别)
-  后台管理界面 (8页面 + 路由守卫 + 分权菜单)
-  断网降级 (设计思路已有, 待实现补偿同步)
-  集成测试 (签名算法/订单状态机/库存并发锁)

## 异常分支覆盖

| 场景 | 处理方式 |
|------|---------|
| 支付超时 | 轮询3次后释放库存, 订单→TIMEOUT, 创建告警 |
| 连续任务失败 | 超过max_retry→MANUAL_REQUIRED, 创建告警 |
| 签名错误(1002) | 记录审计日志+告警, 停止重试 |
| 断网/邮政不可达 | 本地维持订单创建/二维码展示, 链路恢复后补偿同步 |
| 库存不足 | 事务回滚, 订单创建失败 |
| 重复请求 | TransactionID去重, 幂等返回 |

# 具身智能机器人主题邮局原型系统

基于带灵巧手人形机器人的邮政主题网点原型系统，聚焦 **业务中台后端 + 后台管理前端 + 邮政系统对接适配层**。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Java 17+, Spring Boot 3.3.x, Spring MVC, Spring Security |
| ORM | MyBatis-Plus 3.5.x |
| 数据库 | MySQL 8.x（手动建表） |
| 缓存 | Redis 7（库存原子操作 / 分布式锁） |
| 鉴权 | Spring Security + JWT（RBAC） |
| API 文档 | Knife4j（OpenAPI 3.0） |
| 前端 | Vue 3 + Vite + Element Plus + Pinia + Vue Router |
| 基础设施 | Docker Compose |

## 项目结构

```
RobotDemo/
├── backend/                            # Spring Boot 后端
│   └── src/main/java/com/postal/robotdemo/
│       ├── adapter/                    # 邮政系统对接适配层 ★重点
│       │   ├── SignatureUtil.java      # MD5+BASE64 签名（附录三）
│       │   ├── SessionHeader.java      # 会话控制头（请求/应答）
│       │   ├── YYRoot.java             # 统一请求根结构
│       │   ├── TransactionIdGenerator  # 32位全局唯一ID生成器
│       │   ├── PostalErrorCode.java    # 13个邮政错误码→本地映射
│       │   ├── client/PostalClient.java # 统一调用 + 幂等 + 重试 + Mock开关
│       │   └── mock/PostalMockService  # Mock桩（5场景+支付状态流转）
│       ├── controller/                 # 接口层（9个 Controller）
│       ├── service/                    # 业务服务层（7个 Service）
│       ├── mapper/                     # MyBatis-Plus Mapper（7个 + 自定义SQL）
│       ├── entity/                     # 数据库实体（7个）
│       ├── dto/postal/                 # 邮政接口DTO（10个，V_/N_/F_前缀）
│       ├── vo/                         # 视图对象（InventoryVO联表）
│       ├── enums/                      # 状态枚举（OrderStatus/TaskStatus等6个）
│       ├── config/                     # MyBatisPlus/Redis/MetaObjectHandler
│       ├── security/                   # JWT + Spring Security + RBAC
│       └── common/                     # Result/BizException/GlobalExceptionHandler
├── frontend/                           # Vue 3 前端
│   └── src/
│       ├── views/                      # 8个页面 + Layout（路由守卫）
│       ├── router/                     # beforeEach 鉴权拦截
│       ├── api/                        # 10个后端API封装
│       └── utils/                      # Axios + JWT拦截器 + 统一错误弹窗
├── docker-compose.yml                  # MySQL 8.0 + Redis 7 一键启动
└── README.md
```

## 快速启动

### 1. 环境要求
- JDK 17+
- Maven 3.6+
- Node.js 18+
- Docker Desktop

### 2. 启动中间件

```bash
docker-compose up -d
```

### 3. 手动建表 + 导入演示数据

```bash
docker exec -i robotdemo-mysql mysql -uroot -proot123 robotdemo < backend/src/main/resources/db/migration/V1__init_schema.sql
docker exec -i robotdemo-mysql mysql -uroot -proot123 robotdemo < backend/src/main/resources/db/demo_data.sql
```

### 4. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端: http://localhost:8080 | API文档: http://localhost:8080/doc.html

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端: http://localhost:5173

### 6. 测试账号

| 角色 | 用户名 | 密码 | 权限范围 |
|------|--------|------|---------|
| 管理员 | admin | admin123 | 全部（含用户管理） |
| 运营 | operator | admin123 | 商品/订单/库存 |
| 维护 | maintainer | admin123 | 任务/告警/库存 |

## 业务流程演示

```
1. 登录 admin/admin123
2. 商品管理 → 新增商品（"故宫邮册" 39.9元）
3. 订单管理 → 创建订单 → 系统自动：
   a) 校验商品存在且已上架
   b) Redis 分布式锁 + 数据库乐观锁 锁定库存
   c) 调 PostalMock 生成邮件号码 EMS...
   d) 调 PostalMock 查询邮费 12元
   e) 保存订单，状态=PENDING
4. 生成支付二维码 → Mock 返回支付流水号 + 平台流水号
5. 第1次查询支付 → Mock 返回 zfzt=00（支付中）
6. 第2次查询支付 → Mock 返回 zfzt=01（支付成功）→ 自动扣减库存
7. 库存管理 → 可用库存-1，商品名称显示（联表查询）
8. 任务监控 → 支付查询任务状态=SUCCEEDED，含耗时记录
9. 审计日志 → 按操作类型/操作人筛选
```

## Mock 模式

配置开关（`application.yml`）：`postal.mock-enabled: true`

| 场景 | Mock 行为 |
|------|----------|
| 正常返回 | 按接口文档结构返回 JSON |
| 支付状态流转 | 第1次→00（支付中），第2次→01（成功） |
| 签名错误 | 返回 code=1002 |
| 报文解密错误 | 返回 code=9009 |
| 访问量超限 | 返回 code=1006 |
| 超时模拟 | Thread.sleep(15s) |

适配层与 Mock 通过配置切换，业务代码不感知差异。

## 邮政对接适配层

### 会话控制（YYRoot）

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

### 签名算法（附录三）

```
DigitalSign = BASE64( MD5( ServiceCode + Version + ActionCode + TransactionID
                         + SrcSysID + DstSysID + ReqTime + SessionBody + 秘钥 ) )
```

### 已封装的5个接口

| 接口 | 入参关键字段 | 出参关键字段 |
|------|-------------|-------------|
| 邮件资费查询 | productCode, weight, 收发省市, isValue | result.data.fee |
| 邮件号码生成 | V_SFDM, V_JGBH, V_YWCPDM | result.V_YJHM |
| 收寄订单提交 | 100+字段（机构/员工/寄件人/收件人/包装物/内件/资费） | result.V_CXLSH, F_ZZF |
| 生成收款二维码 | vJgbh, vTxdm, emp, vCxlsh | datas.V_PTLSH, V_ZFLSH, V_EWMURL |
| 支付状态查询 | vCxlsh, vJgbh, vZflsh | result.zfzt（01/02/03/05/00） |

### 错误码映射（13项）

| 邮政码 | 含义 | 本地码 |
|--------|------|--------|
| 0000 | 成功 | 200 |
| 1002 | 签名错误 | 41002 |
| 1006 | 访问量超限 | 41006 |
| 9009 | 报文解密错误 | 49009 |
| ... | 共13项 | |

## 状态机设计

### 订单状态机

```
PENDING ─→ PAYING ─→ PAID（扣库存）
   │          │
   │          ├──→ FAILED（释放库存）
   │          └──→ TIMEOUT（释放库存）
   └──────→ CANCELLED（释放库存）
            → MANUAL_REQUIRED
```

### 任务状态机

```
CREATED → QUEUED → RUNNING → SUCCEEDED
                           → FAILED ─→（retry < max）→ RUNNING
                           │         └（retry ≥ max）→ MANUAL_REQUIRED + 告警
                           → CANCELLED
                           → PAUSED
```

## RBAC 权限矩阵

| 角色 | 商品CRUD | 订单管理 | 库存 | 任务 | 告警 | 审计 | 用户管理 |
|------|---------|---------|------|------|------|------|---------|
| ADMIN | 全部 | 全部 | 全部 | 全部 | 全部 | 全部 | **全部** |
| OPERATOR | 全部 | 全部 | 全部 | 查看 | 查看 | 查看 | - |
| MAINTAINER | 查看 | 查看 | 全部 | 全部 | 全部 | 查看 | - |

## 异常分支覆盖

| 场景 | 处理方式 |
|------|---------|
| 商品不存在/已下架下单 | 前置校验，BizException 返回提示 |
| 库存不足 | 乐观锁 update 返回0行 → BizException，事务回滚 |
| 支付超时 | 轮询3次后 → 订单→TIMEOUT，释放库存，创建告警 |
| 连续任务失败 | 超过 max_retry → MANUAL_REQUIRED + ERROR 告警 |
| 签名错误（1002） | 映射本地错误码 41002，记录日志 |
| 重复请求 | TransactionID HashMap 去重，幂等 |
| 断网/邮政不可达 | 设计已预留降级点，PostalClient 捕获异常不阻塞主流程 |

---

## 任务要求完成度

| 要求 | 完成情况 |
|------|---------|
| 后端分层架构（controller/service/adapter/mapper/entity/dto） | 完成，9Controller + 7Service + 7Mapper |
| 7张核心数据模型建表（商品/库存/订单/任务/告警/用户/审计） | 完成，含主键/时间戳/逻辑删除/DECIMAL金额 |
| 统一异常处理 + 响应封装 + 参数校验 | 完成 |
| 配置外置 + 环境变量覆盖 + 多profile | 完成 |
| 邮政适配层：YYRoot + SessionHeader + 签名算法 + TransactionID生成 | 完成，严格按接口文档附录三实现 |
| 5个邮政接口 Req/Rsp DTO（字段名严格V_/N_/F_前缀） | 完成，10个DTO文件 |
| Mock桩：正常/支付流转/签名错误/解密错误/超限/超时 | 完成，通过配置开关切换 |
| 错误码映射（13项） | 完成 |
| 超时/重试/幂等设计 | 完成（PostalClient 已实现框架，重试次数/超时可配置） |
| 商品CRUD + 上下架 + 标签 + 陈列点位 + 机器人抓取标识 | 完成 |
| 库存锁定/释放/扣减（Redis分布式锁 + DB乐观锁） | 完成 |
| 低库存告警 + 视觉校验回写接口预留 | 完成 |
| 订单状态机（7状态） | 完成 |
| 支付闭环（订单→邮件号→资费→二维码→支付查询→库存联动） | 完成，全流程走通 |
| 任务状态机（8状态）+ 优先级 + 依赖 + 超时 + 连续失败转人工 | 完成 |
| 机器人状态上报接口预留（RobotController） | 完成 |
| 后台管理前端 8页面（登录/商品/订单/库存/任务/告警/审计/用户） | 完成 |
| 路由守卫 + 分权菜单 | 完成 |
| JWT + Spring Security + RBAC 三角色 | 完成 |
| @PreAuthorize 接口级鉴权 | 完成 |
| 统一错误提示（request.js 拦截器） | 完成 |
| docker-compose 一键启动中间件 | 完成 |
| README 文档（环境/启动/模块/对接说明） | 完成 |
| 演示假数据（5商品+5订单+6任务+5告警+8审计） | 完成，可重复执行 |




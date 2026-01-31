# 铁路购票系统

## 项目概述

基于Spring Cloud微服务架构的铁路购票系统，采用前后端分离模式，实现车次查询、座位选择、订单确认、余票管理等核心功能。系统具备高并发处理能力，支持分布式事务、流量控制、消息队列等企业级特性。

---

## 技术栈

### 核心框架
- **Spring Boot**: 3.0.0
- **Spring Cloud**: 2022.0.0
- **Spring Cloud Alibaba**: 2022.0.0.0-RC1
- **Java**: 17

### 微服务组件
- **Nacos**: 注册中心 + 配置中心 (服务发现、配置管理)
- **Spring Cloud Gateway**: API网关 (路由转发、统一入口)
- **OpenFeign**: 声明式HTTP客户端 (服务间调用)

### 中间件
- **Redis**: 缓存 + 分布式锁
- **Redisson**: Redis分布式锁客户端
- **RocketMQ**: 消息队列 (异步解耦)
- **MySQL**: 关系型数据库

### 分布式技术
- **Seata**: 分布式事务 (AT模式)
- **Sentinel**: 流量控制 + 熔断降级

### 持久层
- **MyBatis-Plus**: ORM框架 (增强CRUD、分页插件)

### 工具库
- **Hutool**: Java工具类库
- **FastJSON**: JSON处理
- **Knife4j**: API文档 (Swagger增强)
- **Quartz**: 定时任务框架
- **SnowFlake**: 分布式ID生成

---

## 项目架构

### 模块划分

```
train (父模块)
├── gateway          # API网关 (端口: 8080)
├── member           # 会员服务 (端口: 8081)
├── business         # 业务服务 (端口: 8082)
├── batch            # 批处理服务
├── common           # 公共模块
└── generator        # 代码生成器
```

### 服务职责

| 服务 | 职责 | 数据库 |
|------|------|--------|
| **gateway** | 统一入口、路由转发、JWT校验 | - |
| **member** | 会员管理、乘客信息、车票查询 | train-member |
| **business** | 车次管理、订单确认、余票计算、座位分配 | train-business |
| **batch** | 定时任务、每日数据生成 | train-batch |
| **common** | 公共工具、异常处理、通用配置 | - |

---

## 核心功能及执行流程

### 一、车票购买流程

#### 1.1 流程概览
```
用户请求 → 网关路由 → 会员服务校验 → 业务服务处理 → 消息队列异步 → 座位分配 → 分布式事务提交
```

#### 1.2 详细执行流程

**步骤1: 网关拦截与JWT校验**
- 用户通过 `/business/confirm-order/do` 发起购票请求
- Gateway的 `AuthGlobalFilter` 拦截请求
- 从Header中提取JWT Token
- 使用 `JwtUtil` 解析Token，验证用户身份
- 将用户ID放入Request上下文（`LoginMemberContext`）

**步骤2: 流量控制 (Sentinel)**

- 请求到达 `BeforeConfirmOrderService.beforeDoConfirmOrder()`
- `@SentinelResource` 注解触发流量控制检查
- Nacos配置的流控规则（`sentinel-business-flow`）生效
- 超过阈值时触发降级，返回"当前购票人数过多,请稍后重试"

**步骤3: 秒杀令牌校验**

- 调用 `SkTokenService.validSkToken(date, trainCode)`
- 令牌生成公式: `令牌数量 = 座位数 × 站点数 × 75%`
- 使用数据库行锁实现令牌扣减（`mapper.decrease(date, trainCode)`）
- **优化点**: 代码注释中预留Redis缓存优化方案
  - 缓存存在: Redis扣减令牌，每5次请求回写数据库
  - 缓存不存在: 查询数据库并初始化缓存
- 令牌不足时抛出 `CONFIRM_ORDER_SK_TOKEN_FAIL` 异常

**步骤4: 保存确认订单**
- 创建 `ConfirmOrder` 实体
- 订单状态: `INIT` (初始状态)
- 使用雪花算法生成订单ID: `SnowUtil.getSnowflakeNextId()`
- 保存到 `confirm_order` 表

**步骤5: 发送RocketMQ消息**

- 构建 `ConfirmOrderMQDto` 对象
- 包含: 日期、车次编码、日志流水号
- 发送到Topic: `CONFIRM_ORDER` (RocketMQTopicEnum)
- **异步解耦**: 用户请求立即返回，后续座位分配异步处理

**步骤6: 消息消费与锁机制**
- 消息被 `ConfirmOrderService.doConfirmOrder()` 消费
- 获取Redisson分布式锁: `lockKey = date + "-" + trainCode`
- `lock.tryLock(0, TimeUnit.SECONDS)`: 非阻塞获取锁
- 获取锁失败时直接返回，不抛异常（避免重复处理）

**步骤7: 批量处理订单**
- 查询状态为 `INIT` 的订单（每次最多5条）
- 按订单ID升序排序（先进先出）
- 循环调用 `sell()` 方法处理每个订单
- 异常处理:
  - `BUSINESS_TICKET_INSUFFICIENT`: 余票不足，订单状态改为 `EMPTY`，继续下一个
  - 其他异常: 直接抛出，中断循环

**步骤8: 座位分配算法**

**8.1 预扣减余票数量**
- 根据 `tickets` 列表中的座位类型（一等座YDZ、二等座EDZ等）
- 预扣减 `daily_train_ticket` 表中的对应余票
- 扣减后为负数时抛出 `BUSINESS_TICKET_INSUFFICIENT`

**8.2 选座逻辑**
- 支持选座和不选座两种模式

**选座模式:**
- 从请求中获取用户指定的座位（如 `A1`, `C1`, `D1`, `F1`）
- 计算绝对座位索引和相对座位索引
- 根据座位类型获取可用的座位列枚举（`SeatColEnum.getColsByType()`）

**不选座模式:**
- 系统自动分配第一个可用座位

**8.3 查找可用座位**
- 遍历该车型的所有车厢（按车厢序号升序）
- 对每节车厢内的座位按序号遍历
- 判断条件:
  1. 座位未被预选
  2. 列号符合要求（选座模式）
  3. 座位在目标区间内可售（`calSell()`）

**8.4 座位可售计算 (calSell)**
- 座位销售状态使用**二进制位图**存储
- 每个bit代表一个站点区间，1=已售，0=可售
- 计算公式:
  ```java
  // 获取目标区间的销售状态
  String sellPart = sell.substring(startIndex, endIndex);
  // 转换为整数，大于0表示已售
  if(Integer.parseInt(sellPart) > 0) {
      return false; // 不可卖
  }
  // 标记该区间为已售
  sellPart = sellPart.replace("0", "1");
  // 二进制位运算
  int newSell = NumberUtil.binaryToInt(sellPart) | NumberUtil.binaryToInt(sell);
  ```
- 使用**前缀和数组**优化区间查询效率

**8.5 偏移座位检查（多人选座）**
- 对于选座模式，需验证所有相关座位是否可用
- 根据相对座位索引计算偏移座位
- 检查偏移座位是否也在目标区间内可售

**步骤9: 分布式事务提交 (Seata)**

- 调用 `afterDoConfirmOrder()` 方法
- `@GlobalTransactional` 注解开启Seata全局事务
- 事务XID: `RootContext.getXID()`
- 批量更新操作:
  1. **每日座位表**: 更新 `daily_train_seat` 的 `sell` 字段（二进制位图）
  2. **每日余票表**: 更新 `daily_train_ticket` 的余票数量
     - 使用前缀和计算受影响的站点区间
     - 仅更新受影响区间的余票
  3. **用户车票表**: 调用Member服务的Feign接口保存车票
     - 远程调用: `memberFeign.saveTicket(ticketReq)`
  4. **确认订单表**: 更新订单状态为 `SUCCESS`

**步骤10: 事务提交与锁释放**
- Seata协调各服务事务分支提交
- finally块释放Redisson分布式锁
- 记录日志: "购票结束,释放锁"

---

### 二、每日数据生成流程 (批处理)

#### 2.1 流程概览
```
Quartz定时任务 → Batch服务 → Feign调用Business服务 → 生成每日车次数据 → 生成站点/车厢/座位/余票/令牌
```

#### 2.2 详细执行流程

**步骤1: Quartz定时触发**
- 任务类: `DailyTrainJob`
- `@DisallowConcurrentExecution`: 不允许并发执行
- 增加日志流水号: `MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3))`
- 计算目标日期: 当前日期 + 14天

**步骤2: 调用Business服务**
- 使用Feign客户端调用 `BusinessFeign.genDaily(offset.toJdkDate())`
- Sentinel监控Feign调用，配置熔断降级规则
- 降级类: `BusinessFeignFallback`

**步骤3: 生成每日车次 (DailyTrainService.genDaily)**
- 查询所有基础车次数据 (`train` 表)
- 为每个车次创建 `DailyTrain` 实体
- 设置日期、生成ID、创建/更新时间
- 删除已存在的该日期数据（幂等性保证）
- 批量保存: `saveBatch(dailyTrainList, 500)`

**步骤4: 生成每日站点 (DailyTrainStationService.genDaily)**
- 从 `train_station` 表复制站点数据
- 设置每日站点序号
- 保存到 `daily_train_station` 表

**步骤5: 生成每日车厢 (DailyTrainCarriageService.genDaily)**
- 从 `train_carriage` 表复制车厢数据
- 保存到 `daily_train_carriage` 表

**步骤6: 生成每日座位 (DailyTrainSeatService.genDaily)**
- 从 `train_seat` 表复制座位数据
- 每个座位生成销售状态二进制位图（初始全为0）
- 保存到 `daily_train_seat` 表

**步骤7: 生成每日余票 (DailyTrainTicketService.genDaily)**
- 根据站点数据生成所有区间余票
- 计算每个区间的余票数量
- 保存到 `daily_train_ticket` 表

**步骤8: 生成秒杀令牌 (SkTokenService.genDaily)**

- 计算令牌数量: `seatCount × trainStationCount × 3/4`
- 删除旧令牌记录
- 创建新令牌记录并保存
- 用于购票时的流量控制

---

### 三、车票查询流程

#### 3.1 流程概览
```
用户请求 → 网关路由 → 会员服务 → 查询车票列表 → 返回分页数据
```

#### 3.2 详细执行流程

**步骤1: JWT校验**
- Gateway拦截 `/ticket/query-list` 请求
- 验证JWT Token
- 用户ID放入上下文

**步骤2: 参数校验**
- `@Valid` 注解触发参数校验
- `TicketQueryReq` 包含: 分页参数、查询条件

**步骤3: 查询车票**
- `TicketService.queryList(req)`
- 从 `ticket` 表查询用户车票
- 支持分页查询
- 返回 `TicketResp` 列表

---

### 四、车次管理流程

#### 4.1 流程概览
```
管理员操作 → 基础车次配置 → 每日数据生成 → 前台查询可用车次
```

#### 4.2 核心功能

**基础车次管理 (Train)**
- 新增/修改/删除车次
- 设置车次编号、类型（高铁/普快等）
- 配置始发站、终点站、发车时间、到站时间

**站点配置 (TrainStation)**
- 配置车次经过的所有站点
- 设置站点序号（1, 2, 3...）
- 设置到达/离开时间

**车厢配置 (TrainCarriage)**
- 配置每节车厢的类型（一等座、二等座等）
- 设置车厢序号

**座位配置 (TrainSeat)**
- 配置每节车厢的座位布局
- 设置座位列号（A, B, C, D, F等）
- 设置座位行号

---

### 五、乘客管理流程

#### 5.1 流程概览
```
用户操作 → 添加/编辑/删除乘客 → 校验数量限制（最多50人） → 保存到数据库
```

#### 5.2 核心功能

**乘客类型枚举 (PassengerTypeEnum)**
- 成人
- 儿童
- 学生
- 残疾人士

**乘客信息**
- 姓名
- 证件类型
- 证件号码
- 手机号
- 类型

---

## 关键技术点

### 1. 高并发购票解决方案

#### 1.1 流量控制 (Sentinel)
- **Nacos动态配置**: 流控规则通过Nacos配置中心动态下发
- **资源级控制**: 对 `confirmOrderDo` 资源进行限流
- **降级策略**: 超过阈值时返回友好提示，避免系统崩溃

#### 1.2 秒杀令牌机制
- **令牌生成**: `令牌数 = 座位数 × 站点数 × 75%`
- **数据库行锁**: 使用 `decrease` 方法实现原子扣减
- **预留Redis缓存优化**: 代码中注释了Redis缓存方案，可进一步提升性能

#### 1.3 分布式锁 (Redisson)
- **锁粒度**: 按日期+车次加锁 (`date-trainCode`)
- **非阻塞获取**: `tryLock(0, TimeUnit.SECONDS)` 避免阻塞
- **锁释放**: finally块确保锁一定释放

### 2. 分布式事务 (Seata AT模式)
- **全局事务**: `@GlobalTransactional` 标注
- **事务传播**: 跨business和member服务
- **回滚机制**: 任一服务失败则全局回滚
- **注册中心**: Seata通过Nacos注册

### 3. 异步解耦 (RocketMQ)
- **消息发送**: 购票请求立即返回，座位分配异步处理
- **顺序保证**: 按订单ID排序消费，保证先进先出
- **削峰填谷**: 缓解瞬时高并发压力

### 4. 座位销售状态优化
- **二进制位图**: 每个bit代表一个站点区间
- **位运算**: 快速判断座位是否可售
- **前缀和数组**: O(1)时间复杂度计算受影响区间
- **空间优化**: 一个字符串存储所有站点状态

### 5. 分布式ID生成 (SnowFlake)
- **算法**: 时间戳 + 机器ID + 序列号
- **优势**: 全局唯一、趋势递增、高性能
- **使用场景**: 订单ID、车票ID、配置ID等

### 6. 缓存策略 (Redis + MyBatis-Plus)
- **缓存配置**: `spring.cache.type=redis`
- **缓存前缀**: `train_cache_`
- **过期时间**: 60秒
- **缓存穿透**: `cache-null-values: false`

### 7. 微服务治理

#### 7.1 服务注册与发现 (Nacos)
- 所有服务注册到Nacos
- 服务间调用使用服务名（`lb://member`）
- 命名空间隔离: `train`

#### 7.2 配置中心 (Nacos)
- 集中配置管理
- 动态配置刷新
- 命名空间: `train`

#### 7.3 网关路由 (Gateway)
- 路径匹配路由
- 负载均衡
- 全局JWT过滤器

### 8. 监控与降级

#### 8.1 Sentinel流控
- 流量阈值配置
- 熔断降级策略
- Dashboard监控面板

#### 8.2 Feign降级
- `BusinessFeignFallback` 实现降级逻辑
- Sentinel监控Feign调用
- `feign.sentinel.enabled=true`

---

## 数据库设计

### 核心表结构

#### Business服务数据库 (train-business)

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `train` | 基础车次 | code, type, start, end |
| `train_station` | 车次站点 | train_code, name, index |
| `train_carriage` | 车次车厢 | train_code, index, seat_type |
| `train_seat` | 车次座位 | train_code, carriage_index, col, row |
| `daily_train` | 每日车次 | date, code |
| `daily_train_station` | 每日站点 | date, train_code, index |
| `daily_train_carriage` | 每日车厢 | date, train_code, index |
| `daily_train_seat` | 每日座位 | date, train_code, sell(二进制位图) |
| `daily_train_ticket` | 每日余票 | date, train_code, ydz, edz, rw, yw |
| `confirm_order` | 确认订单 | id, date, train_code, status, tickets |
| `sk_token` | 秒杀令牌 | date, train_code, count |

#### Member服务数据库 (train-member)

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `member` | 会员 | id, mobile |
| `passenger` | 乘客 | id, member_id, name, id_type, id_no, type |
| `ticket` | 车票 | id, member_id, train_date, train_code, seat_type |

---

## 部署架构

```
┌─────────────────────────────────────────────────────────────┐
│                        Nginx                                │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   Spring Cloud Gateway (8080)               │
│                   - 路由转发                                 │
│                   - JWT校验                                  │
│                   - 统一入口                                 │
└─────────────────────────────────────────────────────────────┘
         │                              │              │
         ▼                              ▼              ▼
┌──────────────┐            ┌──────────────┐  ┌──────────────┐
│   Member     │            │   Business    │  │    Batch      │
│   Service    │            │   Service     │  │   Service     │
│   (8081)     │            │   (8082)      │  │              │
└──────────────┘            └──────────────┘  └──────────────┘
         │                              │              │
         ▼                              ▼              ▼
┌──────────────┐            ┌──────────────┐  ┌──────────────┐
│ train-member │            │train-business │  │ train-business│
│   MySQL      │            │   MySQL       │  │   MySQL       │
└──────────────┘            └──────────────┘  └──────────────┘

中间件层:
├── Nacos (8848) - 注册中心 + 配置中心
├── Redis (6379) - 缓存 + 分布式锁
├── RocketMQ (9876) - 消息队列
├── Sentinel (8090) - 流量控制面板
└── Seata Server - 分布式事务协调器
```

---

## 性能优化点

### 已实现的优化

1. **二进制位图存储座位状态**
   - 空间效率: 每个座位一个字符串存储所有站点状态
   - 时间效率: 位运算快速判断，前缀和数组优化区间查询

2. **批量插入**
   - `saveBatch(dailyTrainList, 500)` 批量保存每日车次
   - 减少数据库交互次数

3. **分布式锁非阻塞获取**
   - `tryLock(0, TimeUnit.SECONDS)` 避免线程阻塞
   - 快速失败，提升系统吞吐量

4. **MyBatis-Plus批量执行器**
   - `default-executor-type: batch`
   - 批量SQL执行

### 待优化点 (代码中预留)

1. **Redis缓存秒杀令牌**
   - 减少数据库压力
   - 提升令牌扣减性能
   - 代码已注释实现方案

2. **Redis缓存热点数据**
   - 车次信息缓存
   - 站点信息缓存
   - 余票信息缓存

3. **数据库读写分离**
   - 读操作走从库
   - 写操作走主库

4. **消息消费批量处理**
   - 当前: 每次最多5条
   - 可根据实际情况调整批量大小

---

## 异常处理

### 异常枚举 (BusinessExceptionEnum)

| 枚举值 | 说明 |
|--------|------|
| `MEMBER_MOBILE_EXIST` | 手机号已注册 |
| `MEMBER_PASSENGER_COUNT_EXCEEDING` | 最多可以存在50个乘车人 |
| `BUSINESS_TICKET_INSUFFICIENT` | 余票不足 |
| `CONFIRM_ORDER_FLOE_ERROR` | 当前购票人数过多,请稍后重试 |
| `CONFIRM_ORDER_LOCK_FAIL` | 当前购票人数过多,请稍后重试 |
| `CONFIRM_ORDER_SK_TOKEN_FAIL` | 票已卖光 |
| `CONFIRM_ORDER_SAVE_ERROR` | 保存购票订单失败 |

### 全局异常处理 (ControllerExceptionHandler)
- 统一捕获异常
- 返回标准格式响应 `CommonResp`
- 记录错误日志

---

## 日志管理

### 日志配置

```yaml
logging:
  level:
    com.szx.train: debug
    com.szx.train.business.mapper: trace
```

### 日志流水号
- 每个请求生成唯一LOG_ID
- MDC (Mapped Diagnostic Context) 传递流水号
- 便于追踪分布式请求链路

---

## API文档

使用 **Knife4j** 生成API文档

访问地址:
- Gateway: `http://localhost:8080/doc.html`
- Member: `http://localhost:8081/member/doc.html`
- Business: `http://localhost:8082/business/doc.html`

---

## 开发规范

### 代码规范
- 使用Lombok简化代码 (`@RequiredArgsConstructor`, `@Slf4j`)
- 统一异常处理
- RESTful API设计
- 统一响应格式 (`CommonResp`)

### 分页规范
- 所有列表查询支持分页
- 统一分页参数: `PageReq`
- 统一响应格式: `PageResp`

### ID生成
- 统一使用雪花算法: `SnowUtil.getSnowflakeNextId()`
- 保证全局唯一性

---

## 测试

### 单元测试
- 各服务独立测试
- Mock外部依赖

### 集成测试
- 服务间调用测试
- 分布式事务测试

### 压力测试
- 并发购票测试
- 令牌扣减压力测试
- RocketMQ消费测试

---

## 项目亮点

1. **完整的高并发购票方案**
   - 流量控制 (Sentinel)
   - 秒杀令牌机制
   - 分布式锁 (Redisson)
   - 异步解耦 (RocketMQ)
   - 分布式事务 (Seata)

2. **创新的座位状态存储方案**
   - 二进制位图存储
   - 位运算快速判断
   - 前缀和数组优化区间查询

3. **完善的微服务治理**
   - 服务注册发现 (Nacos)
   - 配置中心 (Nacos)
   - API网关 (Gateway)
   - 服务调用监控 (Sentinel)

4. **高性能设计**
   - 批量操作
   - 分布式锁非阻塞
   - 缓存策略
   - 预留Redis优化方案

5. **可扩展性**
   - 模块化设计
   - 微服务架构
   - 预留优化接口

---

## 待改进项

1. **引入Redis缓存秒杀令牌** (代码已预留)
2. **实现车票退票功能**
3. **实现车票改签功能**
4. **数据库读写分离**
5. **引入Elasticsearch实现全文搜索**
6. **引入Prometheus + Grafana监控**
7. **引入Zipkin分布式链路追踪**

---

## 贡献指南

欢迎提交Issue和Pull Request！

---

## 联系方式

- 作者: Ferry
- 邮箱: 2028337982@qq.com

---

## 许可证

[待补充]

---

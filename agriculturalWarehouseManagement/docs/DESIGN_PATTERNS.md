# 设计模式应用说明文档

## 项目概述

**项目名称**: 农产品电商仓配管理系统
**技术栈**: Spring Boot 4.0 + MyBatis + MySQL + Vue 3 + 阿里云 OSS
**设计模式数量**: 8 种（GoF 23 种设计模式中选取）

---

## 设计模式应用详情

### 1. 工厂方法模式 (Factory Method Pattern)

**文件位置**: `src/main/java/com/origin/pattern/factory/`

**应用场景**: 创建不同类型的仓库操作单据（入库单、出库单、拣货单、补货单）

**核心类**:
| 类名 | 角色 |
|------|------|
| `WarehouseDocument` | 抽象产品 — 仓库单据基类 |
| `InboundDocument` | 具体产品 — 入库单 |
| `OutboundDocument` | 具体产品 — 出库单 |
| `PickingDocument` | 具体产品 — 拣货单 |
| `ReplenishDocument` | 具体产品 — 补货单 |
| `DocumentFactory` | 工厂类 — 根据类型参数创建对应单据，生成唯一单号 |

**类图关系**:
```
WarehouseDocument (abstract)
  ├── InboundDocument
  ├── OutboundDocument
  ├── PickingDocument
  └── ReplenishDocument

DocumentFactory.createDocument(type, productId, quantity) → WarehouseDocument
```

**业务集成**: `InboundServiceImpl`、`OutboundServiceImpl`、`ReplenishServiceImpl`、`OrderServiceImpl` 中均调用 `DocumentFactory` 创建对应单据。

---

### 2. 策略模式 (Strategy Pattern)

**文件位置**: `src/main/java/com/origin/pattern/strategy/`

**应用场景**:
- 不同品类农产品采用不同存储策略（常温存储 vs 冷链存储）
- 不同业务需求动态切换拣货策略（FIFO / FEFO / 分类优先）

**拣货策略 — 核心类**:
| 类名 | 角色 |
|------|------|
| `PickingStrategy` | 策略接口 — `selectBatch()`, `generatePickingPath()` |
| `FifoPickingStrategy` | 先入先出 — 按入库时间最早优先出库 |
| `ShortestShelfLifeStrategy` | FEFO — 保质期最短优先出库 |
| `CategoryPriorityStrategy` | 分类优先 — 叶菜类优先出库 |
| `PickingContext` | 策略上下文 — 维护当前策略，可在运行时动态切换 |

**存储策略 — 核心类**:
| 类名 | 角色 |
|------|------|
| `StorageStrategy` | 策略接口 — `assignLocation()` |
| `NormalStorageStrategy` | 常温存储 — 适用于叶菜、根茎、水果 |
| `ColdChainStorageStrategy` | 冷链存储 — 适用于冷藏/冷冻品 |
| `StorageContext` | 策略上下文 |

**拣货路径生成**: 策略负责根据选中批次的货架号返回起始路径（如 `C-01-01`），后续由出库流程逐步拼接 `→ 拣货区 → 出口`。

**前端交互**: 仪表盘页面可直接切换拣货策略，API: `PUT /api/inventory/strategy?type=FIFO|FEFO|CATEGORY`

---

### 3. 命令模式 (Command Pattern)

**文件位置**: `src/main/java/com/origin/pattern/command/`

**应用场景**: 仓库操作指令封装 — 支持命令撤销（误操作回滚）、排队执行、操作日志审计

**核心类**:
| 类名 | 角色 |
|------|------|
| `WarehouseCommand` | 命令接口 — `execute()`, `undo()`, `getDescription()` |
| `InboundCommand` | 入库命令 — 确认上架 + 撤销回滚 |
| `OutboundCommand` | 出库命令 — 确认拣货扣库存 + 撤销恢复（含跨批次支持） |
| `CommandInvoker` | 命令调用者 — 维护已执行命令栈 |

**撤销机制**: `CommandInvoker` 使用 `ArrayDeque` 作为栈保存已执行命令，调用 `undoLast()` 时从栈顶弹出最后执行的命令并调用其 `undo()` 方法。单批次拣货使用命令模式，跨批次拣货直接执行扣减。

**业务集成**:
- `InboundServiceImpl` — 入库上架确认为一条命令
- `OutboundServiceImpl` — 出库拣货确认为一条命令
- 前端: 入库/出库页面均提供"撤销"按钮

---

### 4. 状态模式 (State Pattern)

**文件位置**: `src/main/java/com/origin/pattern/state/`

**应用场景**: 仓库 PDA 手持终端设备状态管理 — 防止非法状态转换

**核心类**:
| 类名 | 角色 |
|------|------|
| `DeviceState` | 状态接口 — `startWork()`, `stopWork()`, `reportFault()`, `startCharge()`, `finishCharge()` |
| `IdleState` | 空闲状态 |
| `WorkingState` | 作业中状态 |
| `FaultState` | 故障状态 |
| `ChargingState` | 充电中状态 |
| `DeviceContext` | 设备上下文 — 维护当前状态，封装状态转换逻辑 |

**状态转换规则**:
```
空闲 --startWork()--> 作业中 --stopWork()--> 空闲
空闲 --reportFault()--> 故障 --finishCharge()--> 空闲
空闲 --startCharge()--> 充电中 --finishCharge()--> 空闲
作业中 --reportFault()--> 故障
故障 --任意操作都保持故障--> 故障（防止非法操作）
```

**业务集成**: `PickingServiceImpl` 初始化 3 台 PDA 设备（PDA-001/002/003），分配拣货员时自动分配空闲设备并触发状态转换，完成拣货后释放设备。

---

### 5. 观察者模式 (Observer Pattern)

**文件位置**: `src/main/java/com/origin/pattern/observer/`

**应用场景**: 库存数据变化时，自动通知各订阅模块（预警模块、报表模块）

**核心类**:
| 类名 | 角色 |
|------|------|
| `InventoryObserver` | 观察者接口 — `onInventoryChange(event, inventory)` |
| `InventorySubject` | 被观察者（库存中心） — 维护观察者列表，通知变更 |
| `WarningObserver` | 预警观察者 — 检查临期/低库存并输出预警 |
| `ReportObserver` | 报表观察者 — 库存变化时更新报表缓存 |

**通知触发点**（`InventoryServiceImpl`）:
- `create()` — 新批次入库时通知
- `updateQuantity()` — 库存数量变更时通知
- `updateStatus()` — 库存状态变更时通知

**初始化**: `@PostConstruct` 时将 `WarningObserver` 和 `ReportObserver` 注册到 `InventorySubject`。

---

### 6. 单例模式 (Singleton Pattern)

**文件位置**: `src/main/java/com/origin/pattern/singleton/`

**应用场景**: 全局唯一的系统组件 — 配置管理器、日志管理器

**核心类**:
| 类名 | 实现方式 | 用途 |
|------|----------|------|
| `ConfigManager` | 双重检查锁（DCL） | 管理系统配置项（仓库名称、预警阈值、分页大小等） |
| `LogManager` | 饿汉式（类加载时初始化） | 全局操作日志缓存（内存中驻留最近 200 条日志） |

**ConfigManager 使用示例**:
```java
ConfigManager.getInstance().get("warning.expiry_days");  // 获取配置
ConfigManager.getInstance().set("page.size", "30");       // 修改配置
```

**LogManager 集成**: 所有 Service 实现类均调用 `LogManager.getInstance().addLog()` 记录操作。

---

### 7. 代理模式 (Proxy Pattern)

**文件位置**: `src/main/java/com/origin/pattern/proxy/`

**应用场景**: 仓库操作权限控制 — 不同角色拥有不同操作权限

**核心类**:
| 类名 | 角色 |
|------|------|
| `WarehouseOperation` | 抽象主题接口 |
| `WarehouseOperationImpl` | 真实主题 — 执行实际仓库操作 |
| `WarehouseOperationProxy` | 代理 — 在调用真实操作前进行角色权限校验 |
| `PermissionProxy` | 简化版权限代理 — `check(operation)` 方法供 Controller 直接调用 |

**权限矩阵**:
| 操作 | 管理员 | 仓管员 | 拣货员 |
|------|--------|--------|--------|
| 入库 | ✓ | ✓ | ✗ |
| 出库 | ✓ | ✓ | ✓ |
| 质检 | ✓ | ✓ | ✗ |
| 拣货 | ✓ | ✗ | ✓ |
| 补货 | ✓ | ✓ | ✗ |
| 查看报表 | ✓ | ✓ | ✓ |
| 用户管理 | ✓ | ✗ | ✗ |
| 配置管理 | ✓ | ✗ | ✗ |
| 订单管理 | ✓ | ✗ | ✗ |
| 商品管理 | ✓ | ✗ | ✗ |

**Controller 集成**: 各 Controller 方法中通过 `permissionProxy.check("操作名")` 进行权限校验。

---

### 8. 责任链模式 (Chain of Responsibility Pattern)

**文件位置**: `src/main/java/com/origin/pattern/chain/`

**应用场景**: 订单处理流程 — 库存锁定 → 生成出库单 → 生成拣货任务 → 打印物流单

**核心类**:
| 类名 | 角色 |
|------|------|
| `OrderHandler` | 抽象处理器 — 定义 `handle()` 和 `setNext()` |
| `StockLockHandler` | 锁定库存处理器 |
| `PickingGenerateHandler` | 生成拣货单处理器 |
| `LogisticsPrintHandler` | 打印物流单处理器 |
| `StockUpdateHandler` | 库存更新处理器（链尾） |
| `OrderContext` | 处理上下文 — 携带订单信息在各处理器间传递 |

**处理流程**（`OrderServiceImpl.processOrder()`）:
```
1. FIFO 选择批次 → 锁定库存（状态→"锁定"）
2. 生成出库单 → 设置拣货路径（货架号）
3. 生成拣货任务 → 与出库流程对接
4. 打印物流单 → 记录客户/地址信息
```

处理后出库单走正常的出库拣货流程（移入拣货区 → 分配拣货员 → 拣货确认），拣货确认时自动将关联订单标记为"已完成"并释放锁定库存。

---

## 设计模式与业务模块对应关系

| 业务模块 | 应用的设计模式 |
|----------|----------------|
| 入库管理 (InboundService) | 工厂方法、策略(存储)、命令 |
| 出库拣货 (OutboundService) | 工厂方法、策略(拣货)、命令 |
| 订单处理 (OrderService) | 责任链、工厂方法 |
| 库存管理 (InventoryService) | 观察者、策略 |
| 拣货任务 (PickingService) | 状态 |
| 补货管理 (ReplenishService) | 工厂方法 |
| 报表系统 (ReportService) | 代理（权限）、单例（配置/日志） |
| 用户管理 (UserService) | 单例（日志） |
| 权限控制 (PermissionProxy) | 代理 |

---

## 设计模式亮点总结

1. **策略模式** — 拣货策略可在运行时通过前端界面实时切换（FIFO ↔ FEFO ↔ 分类优先），存储策略根据商品储存条件自动匹配温区货位
2. **命令模式** — 入库/出库操作支持撤销，防止误操作，操作可审计
3. **责任链模式** — 订单处理步骤清晰：锁定 → 出库单 → 拣货任务 → 物流单，出库流程复用现有的出库拣货管线
4. **观察者模式** — 库存变化自动触发预警和报表更新，解耦库存中心与各模块
5. **状态模式** — PDA 设备状态机防止非法操作（如从故障状态直接开始作业）
6. **代理模式** — 权限控制在代理层统一处理，业务代码无需关注权限逻辑
7. **订单-出库联动** — 订单处理生成的出库单走标准出库流程，拣货完成后自动完成订单并释放锁定库存，全链路闭环

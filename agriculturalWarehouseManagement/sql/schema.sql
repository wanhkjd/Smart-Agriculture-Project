-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `warehouse_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `warehouse_db`;

-- 创建用户表
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '管理员/仓管员/拣货员',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint DEFAULT '1' COMMENT '1启用 0禁用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户';

-- 插入用户数据
INSERT INTO `user` (`id`, `username`, `password`, `real_name`, `role`, `phone`, `status`, `created_at`, `updated_at`) VALUES
(4, 'admin', '123456', '系统管理员', '管理员', '13800000001', 1, '2026-05-26 21:05:18', '2026-05-26 21:05:18'),
(5, 'keeper', '123456', '李仓管', '仓管员', '13800000002', 1, '2026-05-26 21:05:18', '2026-05-26 21:05:18'),
(6, 'picker', '123456', '王拣货', '拣货员', '13800000003', 1, '2026-05-26 21:05:18', '2026-05-26 21:05:18');

-- 创建产品表
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '叶菜/根茎/水果/冷链品/粮油',
  `shelf_life_days` int NOT NULL COMMENT '保质期天数',
  `storage_condition` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '常温/冷藏/冷冻',
  `unit` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'kg' COMMENT '计量单位',
  `quantity` decimal(10,2) DEFAULT NULL COMMENT '数量',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deleted` tinyint DEFAULT '0' COMMENT '0正常 1已删除',
  `status` tinyint DEFAULT '1' COMMENT '1上架 0下架',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `image` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品图片URL',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='农产品信息';

-- 插入产品数据
INSERT INTO `product` (`id`, `name`, `category`, `shelf_life_days`, `storage_condition`, `unit`, `quantity`, `description`, `status`, `created_at`, `updated_at`, `image`) VALUES
(1, '小白菜', '叶菜', 3, '常温', 'kg', 0.50, '新鲜小白菜，当日采摘', 1, '2026-05-26 21:05:24', '2026-05-28 21:12:37', 'https://java-web-ai-wsy.oss-cn-beijing.aliyuncs.com/019c6934-b2a3-4605-9965-65e587de1c9f.jpg'),
(2, '菠菜', '叶菜', 2, '常温', 'kg', 0.30, '有机菠菜', 1, '2026-05-26 21:05:24', '2026-05-28 21:13:41', 'https://java-web-ai-wsy.oss-cn-beijing.aliyuncs.com/9e5cf53a-f939-4e13-a1db-dbf01ea37da2.jpg'),
(3, '胡萝卜', '根茎', 30, '常温', 'kg', 1.00, '精选胡萝卜', 1, '2026-05-26 21:05:24', '2026-05-28 21:14:46', 'https://java-web-ai-wsy.oss-cn-beijing.aliyuncs.com/90250261-786d-4e7e-bc08-41b47e130e38.jpg'),
(4, '土豆', '根茎', 60, '常温', 'kg', 1.50, '黄心土豆', 1, '2026-05-26 21:05:24', '2026-05-28 21:15:46', 'https://java-web-ai-wsy.oss-cn-beijing.aliyuncs.com/b9072391-94e4-4470-a6f3-ec11fcada757.jpg'),
(5, '苹果', '水果', 15, '常温', 'kg', 0.80, '红富士苹果', 1, '2026-05-26 21:05:24', '2026-05-28 21:16:24', 'https://java-web-ai-wsy.oss-cn-beijing.aliyuncs.com/9cb0f7de-ba9c-4685-bd29-87b8dc8285ef.jpg'),
(6, '香蕉', '水果', 7, '常温', 'kg', 0.60, '进口香蕉', 1, '2026-05-26 21:05:24', '2026-05-28 21:17:12', 'https://java-web-ai-wsy.oss-cn-beijing.aliyuncs.com/7f7062bb-aff7-46be-a51a-37fd28b56b8b.jpg'),
(7, '鲜牛奶', '冷链品', 7, '冷藏', 'L', 1.00, '巴氏杀菌鲜牛奶', 1, '2026-05-26 21:05:24', '2026-05-28 21:18:06', 'https://java-web-ai-wsy.oss-cn-beijing.aliyuncs.com/8dbb597b-b818-4e2e-a7dc-649a273a3b18.jpg'),
(8, '冷冻鸡胸肉', '冷链品', 90, '冷冻', 'kg', 2.00, '冷冻鸡胸肉', 1, '2026-05-26 21:05:24', '2026-05-28 21:18:38', 'https://java-web-ai-wsy.oss-cn-beijing.aliyuncs.com/936795ec-f54b-4e8a-9805-7e3844d0d451.jpg');


-- 创建货位表
CREATE TABLE `warehouse_location` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '货位编号',
  `zone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '常温区/冷藏区/冷冻区',
  `row_no` int NOT NULL,
  `col_no` int NOT NULL,
  `capacity` decimal(10,2) DEFAULT '0.00',
  `current_load` decimal(10,2) DEFAULT '0.00',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '空闲' COMMENT '空闲/占用/维修',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='货位信息';

-- 插入货位数据
INSERT INTO `warehouse_location` (`id`, `code`, `zone`, `row_no`, `col_no`, `capacity`, `current_load`, `status`, `created_at`) VALUES
(9, 'C-01-01', '常温区', 1, 1, 500.00, 0.00, '空闲', '2026-05-26 21:05:21'),
(10, 'C-02-01', '常温区', 1, 2, 500.00, 0.00, '空闲', '2026-05-26 21:05:21'),
(11, 'C-03-01', '常温区', 1, 3, 500.00, 0.00, '空闲', '2026-05-26 21:05:21'),
(12, 'C-04-01', '常温区', 1, 4, 500.00, 0.00, '空闲', '2026-05-26 21:05:21'),
(13, 'L-05-01', '冷藏区', 2, 1, 500.00, 0.00, '空闲', '2026-05-26 21:05:21'),
(14, 'L-06-01', '冷藏区', 2, 2, 500.00, 0.00, '空闲', '2026-05-26 21:05:21'),
(15, 'F-07-01', '冷冻区', 2, 3, 500.00, 0.00, '空闲', '2026-05-26 21:05:21'),
(16, 'F-08-01', '冷冻区', 2, 4, 500.00, 0.00, '空闲', '2026-05-26 21:05:21');

-- 创建客户订单表
CREATE TABLE `customer_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `product_id` bigint DEFAULT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `customer_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `customer_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delivery_address` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '待处理' COMMENT '待处理/已锁定库存/拣货中/已发货/已取消',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `customer_order_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户订单';

-- 创建入库单表
CREATE TABLE `inbound_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `product_id` bigint DEFAULT NULL,
  `batch_no` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '供应商',
  `quantity` decimal(10,2) NOT NULL,
  `location_id` bigint DEFAULT NULL,
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `quality_check` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '待质检' COMMENT '待质检/合格/不合格',
  `operator` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '待质检' COMMENT '待质检/质检中/已上架/已取消',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`),
  KEY `product_id` (`product_id`),
  KEY `location_id` (`location_id`),
  CONSTRAINT `inbound_order_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `inbound_order_ibfk_2` FOREIGN KEY (`location_id`) REFERENCES `warehouse_location` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='入库单';

-- 创建库存台账表
CREATE TABLE `inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `batch_no` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `quantity` decimal(10,2) NOT NULL DEFAULT '0.00',
  `location_id` bigint DEFAULT NULL,
  `shelf_no` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `production_date` date NOT NULL,
  `expiry_date` date NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '正常' COMMENT '正常/临期/过期/锁定',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  KEY `location_id` (`location_id`),
  CONSTRAINT `inventory_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `inventory_ibfk_2` FOREIGN KEY (`location_id`) REFERENCES `warehouse_location` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存台账';

-- 创建操作日志表
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `operation` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型',
  `target_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目标类型',
  `target_id` bigint DEFAULT NULL COMMENT '目标ID',
  `operator` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `detail` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=230 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志';

-- 创建出库单表
CREATE TABLE `outbound_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `product_id` bigint DEFAULT NULL,
  `batch_no` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `picking_strategy` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'FIFO' COMMENT '拣货策略：FIFO/FEFO/CATEGORY',
  `picking_path` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '拣货路径',
  `operator` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '待拣货' COMMENT '待拣货/拣货中/已完成/已取消',
  `customer_order_id` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `outbound_order_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='出库单';

-- 创建拣货任务表
CREATE TABLE `picking_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_no` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `outbound_order_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `picker` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `picking_path` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '待分配' COMMENT '待分配/已分配/拣货中/已完成',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `task_no` (`task_no`),
  KEY `outbound_order_id` (`outbound_order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `picking_task_ibfk_1` FOREIGN KEY (`outbound_order_id`) REFERENCES `outbound_order` (`id`),
  CONSTRAINT `picking_task_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='拣货任务';

-- 创建补货任务表
CREATE TABLE `replenish_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_no` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `product_id` bigint DEFAULT NULL,
  `current_quantity` decimal(10,2) NOT NULL,
  `replenish_quantity` decimal(10,2) NOT NULL,
  `reason` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '低库存/过期/报损',
  `creator` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '待处理' COMMENT '待处理/补货中/已完成/已取消',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `task_no` (`task_no`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `replenish_task_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='补货任务';
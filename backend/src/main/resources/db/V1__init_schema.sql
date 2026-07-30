-- 建表脚本 V1.0

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password` VARCHAR(256) NOT NULL COMMENT '密码(BCrypt)',
    `real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `role` VARCHAR(32) NOT NULL DEFAULT 'OPERATOR' COMMENT '角色: ADMIN/OPERATOR/MAINTAINER',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` VARCHAR(256) NOT NULL COMMENT '商品名称',
    `description` TEXT COMMENT '商品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `tags` VARCHAR(512) DEFAULT NULL COMMENT '标签,JSON数组',
    `image_url` VARCHAR(512) DEFAULT NULL COMMENT '商品图片URL',
    `display_position` VARCHAR(128) DEFAULT NULL COMMENT '陈列点位',
    `robot_grabbable` TINYINT NOT NULL DEFAULT 0 COMMENT '是否支持机器人抓取展示: 0-否, 1-是',
    `status` VARCHAR(32) NOT NULL DEFAULT 'ON_SHELF' COMMENT '状态: ON_SHELF/OFF_SHELF',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_tags` (`tags`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 库存表
CREATE TABLE IF NOT EXISTS `inventory` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `total_quantity` INT NOT NULL DEFAULT 0 COMMENT '总库存',
    `locked_quantity` INT NOT NULL DEFAULT 0 COMMENT '锁定库存',
    `available_quantity` INT NOT NULL DEFAULT 0 COMMENT '可用库存',
    `low_threshold` INT NOT NULL DEFAULT 5 COMMENT '低库存阈值',
    `sample_missing` TINYINT NOT NULL DEFAULT 0 COMMENT '样品缺失: 0-正常, 1-缺失',
    `sample_misplaced` TINYINT NOT NULL DEFAULT 0 COMMENT '陈列错位: 0-正常, 1-错位',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(256) NOT NULL COMMENT '商品名称',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    `postage` DECIMAL(10,2) DEFAULT NULL COMMENT '邮费',
    `total_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '总金额(商品+邮费)',
    `mail_no` VARCHAR(64) DEFAULT NULL COMMENT '邮件号码',
    `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '订单状态: PENDING/PAYING/PAID/FAILED/CANCELLED/TIMEOUT/MANUAL_REQUIRED',
    `pay_trade_no` VARCHAR(64) DEFAULT NULL COMMENT '支付流水号',
    `pay_platform_no` VARCHAR(64) DEFAULT NULL COMMENT '支付平台流水号',
    `pay_qr_url` VARCHAR(1024) DEFAULT NULL COMMENT '支付二维码链接',
    `postal_trade_no` VARCHAR(64) DEFAULT NULL COMMENT '邮政交易流水号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_status` (`status`),
    KEY `idx_mail_no` (`mail_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 任务表
CREATE TABLE IF NOT EXISTS `task_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `task_no` VARCHAR(64) NOT NULL COMMENT '任务编号',
    `task_type` VARCHAR(32) NOT NULL COMMENT '任务类型: NAV/GRASP/SPEECH/CHECKOUT/INSPECTION/SAFETY',
    `status` VARCHAR(32) NOT NULL DEFAULT 'CREATED' COMMENT '状态: CREATED/QUEUED/RUNNING/PAUSED/SUCCEEDED/FAILED/CANCELLED/MANUAL_REQUIRED',
    `priority` INT NOT NULL DEFAULT 5 COMMENT '优先级: 1(最高)-10(最低)',
    `depend_task_id` BIGINT DEFAULT NULL COMMENT '依赖任务ID',
    `input_data` TEXT COMMENT '输入参数(JSON)',
    `output_data` TEXT COMMENT '输出结果(JSON)',
    `timeout_seconds` INT NOT NULL DEFAULT 300 COMMENT '超时阈值(秒)',
    `retry_count` INT NOT NULL DEFAULT 0 COMMENT '已重试次数',
    `max_retry` INT NOT NULL DEFAULT 3 COMMENT '最大重试次数',
    `fail_reason` TEXT COMMENT '失败原因',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `duration_ms` BIGINT DEFAULT NULL COMMENT '耗时(毫秒)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_no` (`task_no`),
    KEY `idx_status` (`status`),
    KEY `idx_type` (`task_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- 告警表
CREATE TABLE IF NOT EXISTS `alert` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `alert_type` VARCHAR(32) NOT NULL COMMENT '告警类型: NAV_FAIL/GRASP_FAIL/PAY_ERROR/STOCK_LOW/STOCK_MISMATCH/NETWORK_DOWN/SYSTEM_ERROR',
    `level` VARCHAR(16) NOT NULL DEFAULT 'WARN' COMMENT '告警级别: INFO/WARN/ERROR/CRITICAL',
    `source` VARCHAR(64) NOT NULL COMMENT '告警来源',
    `title` VARCHAR(256) NOT NULL COMMENT '告警标题',
    `detail` TEXT COMMENT '告警详情',
    `status` VARCHAR(32) NOT NULL DEFAULT 'OPEN' COMMENT '状态: OPEN/ACKNOWLEDGED/RESOLVED/CLOSED',
    `handler` VARCHAR(64) DEFAULT NULL COMMENT '处理人',
    `handle_record` TEXT COMMENT '处理记录',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_type` (`alert_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警表';

-- 审计日志表
CREATE TABLE IF NOT EXISTS `audit_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `operator` VARCHAR(64) NOT NULL COMMENT '操作人',
    `operate_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `operate_type` VARCHAR(32) NOT NULL COMMENT '操作类型: LOGIN/CONFIG/ORDER/PAY/POSTAL/TASK/ALERT/USER',
    `target` VARCHAR(256) DEFAULT NULL COMMENT '操作对象',
    `target_id` VARCHAR(64) DEFAULT NULL COMMENT '操作对象ID',
    `result` VARCHAR(16) NOT NULL DEFAULT 'SUCCESS' COMMENT '操作结果: SUCCESS/FAIL',
    `detail` TEXT COMMENT '操作详情',
    `trace_id` VARCHAR(64) DEFAULT NULL COMMENT '关联流水号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_operator` (`operator`),
    KEY `idx_operate_time` (`operate_time`),
    KEY `idx_operate_type` (`operate_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';

-- 索引：支持按时间+类型+操作人检索
ALTER TABLE `audit_log` ADD KEY `idx_time_type_operator` (`operate_time`, `operate_type`, `operator`);

-- 初始化管理员账号 (密码均为 admin123)
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`) VALUES
('admin', '$2a$10$ki9cH5VTtwAOyY3R9tzV6ON62s6qJNXbLwJCXqseS.Kfri7RnVO.m', '系统管理员', 'ADMIN'),
('operator', '$2a$10$ki9cH5VTtwAOyY3R9tzV6ON62s6qJNXbLwJCXqseS.Kfri7RnVO.m', '运营人员', 'OPERATOR'),
('maintainer', '$2a$10$ki9cH5VTtwAOyY3R9tzV6ON62s6qJNXbLwJCXqseS.Kfri7RnVO.m', '维护人员', 'MAINTAINER');

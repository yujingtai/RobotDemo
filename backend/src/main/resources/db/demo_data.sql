-- ============================================
-- 演示假数据
-- 使用方式:
-- docker exec -i robotdemo-mysql mysql -uroot -proot123 robotdemo < backend/src/main/resources/db/demo_data.sql
-- ============================================

-- 商品数据 (5个文创产品)
INSERT INTO product (name, description, price, tags, image_url, display_position, robot_grabbable, status) VALUES
('故宫四季邮册', '故宫博物院联名限量邮册，收录春夏秋冬四季主题邮票，精装硬壳，适合收藏送礼', 128.00, '邮册,故宫,收藏,限量', '/images/gugong.jpg', 'A区-01', 1, 'ON_SHELF'),
('大熊猫纪念封', '可爱大熊猫主题纪念信封，含5枚不同姿态熊猫邮票，适合亲子选购', 29.90, '纪念封,熊猫,亲子,萌宠', '/images/panda.jpg', 'A区-02', 1, 'ON_SHELF'),
('黄山风景明信片套装', '安徽黄山四季风光明信片12张套装，含迎客松、云海、日出等经典景观', 19.90, '明信片,黄山,安徽,风景', '/images/huangshan.jpg', 'B区-01', 0, 'ON_SHELF'),
('十二生肖铜章', '纯铜镀金十二生肖纪念章，直径40mm，带收藏证书，可单独选购', 68.00, '铜章,生肖,纪念品,金属', '/images/shengxiao.jpg', 'B区-03', 1, 'ON_SHELF'),
('徽州文创书签', '徽派建筑镂空金属书签，徽州古韵设计，含西递宏村元素', 15.90, '书签,徽州,文创,文具', '/images/shuqian.jpg', 'B区-02', 0, 'ON_SHELF');

-- 库存数据 (与订单状态一致: ORD001已支付扣1件, ORD002支付中锁2件)
INSERT INTO inventory (product_id, total_quantity, locked_quantity, available_quantity, low_threshold, sample_missing, sample_misplaced) VALUES
(1, 19, 0, 19, 5, 0, 0),   -- 故宫邮册: 已售1件
(2, 50, 2, 48, 5, 0, 0),   -- 熊猫封: 支付中锁2件
(3, 100, 0, 100, 5, 0, 0), -- 明信片: 正常
(4, 8, 0, 8, 3, 0, 0),     -- 铜章: 低库存(仅8件)
(5, 200, 0, 200, 5, 0, 0);  -- 书签: 充足

-- 订单数据 (展示不同状态)
INSERT INTO order_info (order_no, product_id, product_name, quantity, amount, postage, total_amount, mail_no, status, pay_trade_no, pay_platform_no, pay_qr_url, postal_trade_no) VALUES
('ORD20260730001', 1, '故宫四季邮册', 1, 128.00, 12.00, 140.00, 'EMS1234567890', 'PAID', 'PAY001', 'PT001', 'https://mock.qrcode/pay1', 'CX20260730001'),
('ORD20260730002', 2, '大熊猫纪念封', 2, 59.80, 12.00, 71.80, 'EMS1234567891', 'PAYING', 'PAY002', 'PT002', 'https://mock.qrcode/pay2', 'CX20260730002'),
('ORD20260730003', 4, '十二生肖铜章', 1, 68.00, 12.00, 80.00, 'EMS1234567892', 'PENDING', NULL, NULL, NULL, NULL),
('ORD20260730004', 3, '黄山风景明信片套装', 1, 19.90, 12.00, 31.90, 'EMS1234567893', 'FAILED', 'PAY003', 'PT003', 'https://mock.qrcode/pay3', 'CX20260730003'),
('ORD20260730005', 5, '徽州文创书签', 3, 47.70, 12.00, 59.70, 'EMS1234567894', 'TIMEOUT', 'PAY004', 'PT004', 'https://mock.qrcode/pay4', 'CX20260730004');

-- 任务数据 (不同状态和类型)
INSERT INTO task_info (task_no, task_type, status, priority, depend_task_id, input_data, output_data, timeout_seconds, retry_count, max_retry, fail_reason, start_time, end_time, duration_ms) VALUES
('TASK001', 'CHECKOUT', 'SUCCEEDED', 3, NULL, '{"orderId":1,"action":"结算"}', '{"result":"支付成功"}', 120, 0, 3, NULL, '2026-07-30 09:00:00', '2026-07-30 09:01:00', 60000),
('TASK002', 'NAV', 'RUNNING', 2, NULL, '{"target":"A区-01","action":"导航到展示点"}', NULL, 60, 0, 3, NULL, '2026-07-30 10:05:00', NULL, NULL),
('TASK003', 'GRASP', 'QUEUED', 4, 2, '{"productId":1,"action":"抓取展示"}', NULL, 180, 0, 2, NULL, NULL, NULL, NULL),
('TASK004', 'SPEECH', 'CREATED', 5, NULL, '{"action":"迎宾","message":"欢迎来到主题邮局"}', NULL, 30, 0, 2, NULL, NULL, NULL, NULL),
('TASK005', 'INSPECTION', 'FAILED', 7, NULL, '{"action":"库存巡检","target":"B区"}', NULL, 300, 3, 3, '视觉识别超时: 相机无响应', '2026-07-30 08:30:00', '2026-07-30 08:35:00', 300000),
('TASK006', 'SAFETY', 'MANUAL_REQUIRED', 1, NULL, '{"action":"急停恢复","reason":"人员异常进入"}', NULL, 10, 3, 3, '连续触发急停3次,自动转人工', '2026-07-30 10:10:00', '2026-07-30 10:10:05', 5000);

-- 告警数据
INSERT INTO alert (alert_type, level, source, title, detail, status) VALUES
('STOCK_LOW', 'WARN', '库存模块', '低库存告警: 十二生肖铜章', '当前可用库存=8, 阈值=3, 请及时补货', 'OPEN'),
('GRASP_FAIL', 'ERROR', '任务模块', '抓取任务连续失败需人工处理', '商品: 故宫四季邮册, 位姿置信度过低, 重试3次失败', 'OPEN'),
('PAY_ERROR', 'ERROR', '支付模块', '支付超时: 徽州文创书签', '支付状态轮询3次超时, 订单已自动取消,库存已释放', 'RESOLVED'),
('NAV_FAIL', 'CRITICAL', '机器人模块', '导航定位丢失', '激光雷达数据异常, 机器人停在B区走廊, 需人工复位', 'OPEN'),
('STOCK_MISMATCH', 'ERROR', '视觉巡检', '账实不一致: 黄山明信片', '系统库存=100, 视觉识别=97, 可能缺失3件', 'ACKNOWLEDGED');

-- 审计日志
INSERT INTO audit_log (operator, operate_time, operate_type, target, target_id, result, detail, trace_id) VALUES
('admin', '2026-07-30 09:00:00', 'LOGIN', '用户登录', 'admin', 'SUCCESS', '管理员登录系统', 'TRACE001'),
('admin', '2026-07-30 09:05:00', 'CONFIG', '商品配置', '1', 'SUCCESS', '新增商品: 故宫四季邮册', 'TRACE002'),
('operator', '2026-07-30 09:30:00', 'ORDER', '订单创建', 'ORD001', 'SUCCESS', '创建订单: 故宫四季邮册 x1', 'TRACE003'),
('operator', '2026-07-30 09:31:00', 'POSTAL', '邮政接口', 'ORD001', 'SUCCESS', '生成邮件号码: EMS1234567890', 'TRACE004'),
('operator', '2026-07-30 09:32:00', 'PAY', '支付', 'ORD001', 'SUCCESS', '支付成功: PAY001, 扣减库存', 'TRACE005'),
('operator', '2026-07-30 10:00:00', 'ORDER', '订单创建', 'ORD003', 'FAIL', '创建订单失败: 库存不足', 'TRACE006'),
('maintainer', '2026-07-30 10:10:00', 'TASK', '任务处理', 'TASK006', 'SUCCESS', '安全任务转人工, 已确认人员异常进入事件', 'TRACE007'),
('maintainer', '2026-07-30 10:15:00', 'ALERT', '告警确认', '2', 'SUCCESS', '确认抓取失败告警, 安排人工复核商品位姿', 'TRACE008');

package com.postal.robotdemo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.postal.robotdemo.entity.OrderInfo;
import com.postal.robotdemo.entity.TaskInfo;
import com.postal.robotdemo.entity.Alert;
import com.postal.robotdemo.enums.AlertLevel;
import com.postal.robotdemo.enums.TaskStatus;
import com.postal.robotdemo.enums.TaskType;
import com.postal.robotdemo.mapper.AlertMapper;
import com.postal.robotdemo.mapper.OrderInfoMapper;
import com.postal.robotdemo.mapper.TaskInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderInfoMapper orderMapper;
    private final InventoryService inventoryService;
    private final TaskInfoMapper taskMapper;
    private final AlertMapper alertMapper;

    /**
     * 轮询支付状态（最多3次，间隔递增）
     */
    @Transactional
    public String pollPayStatus(Long orderId) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null) return null;

        // 创建支付查询任务
        TaskInfo task = new TaskInfo();
        task.setTaskNo("TASK" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        task.setTaskType(TaskType.CHECKOUT);
        task.setStatus(TaskStatus.RUNNING);
        task.setPriority(3);
        task.setMaxRetry(3);
        task.setTimeoutSeconds(120);
        task.setStartTime(LocalDateTime.now());
        taskMapper.insert(task);

        try {
            String zfzt = null;
            for (int i = 0; i < 3; i++) {
                try {
                    // 模拟间隔: 1s, 2s, 3s
                    Thread.sleep((i + 1) * 1000L);
                } catch (InterruptedException ignored) {}

                // Mock 模式下第一次返回00，第二次返回01
                // 这里直接查本地订单状态 (由 OrderService.queryAndUpdatePayStatus 更新)
                OrderInfo latest = orderMapper.selectById(orderId);
                if (latest != null && latest.getStatus() != null) {
                    switch (latest.getStatus()) {
                        case PAID:
                            task.setStatus(TaskStatus.SUCCEEDED);
                            task.setEndTime(LocalDateTime.now());
                            task.setDurationMs(
                                java.time.Duration.between(task.getStartTime(), task.getEndTime()).toMillis());
                            taskMapper.updateById(task);
                            return "01";
                        case FAILED:
                            task.setStatus(TaskStatus.FAILED);
                            task.setFailReason("支付失败");
                            taskMapper.updateById(task);
                            return "02";
                        case TIMEOUT:
                            task.setStatus(TaskStatus.FAILED);
                            task.setFailReason("支付超时");
                            taskMapper.updateById(task);
                            return null;
                        default:
                            break;
                    }
                }
            }

            // 重试耗尽: 订单超时
            order.setStatus(com.postal.robotdemo.enums.OrderStatus.TIMEOUT);
            orderMapper.updateById(order);
            inventoryService.releaseStock(order.getProductId(), order.getQuantity());

            task.setStatus(TaskStatus.FAILED);
            task.setFailReason("支付状态轮询超时(3次)");
            task.setEndTime(LocalDateTime.now());
            taskMapper.updateById(task);

            // 创建告警
            Alert alert = new Alert();
            alert.setAlertType("PAY_ERROR");
            alert.setLevel(AlertLevel.ERROR);
            alert.setSource("支付模块");
            alert.setTitle("支付超时: orderId=" + orderId);
            alert.setDetail("轮询3次仍未获取最终支付结果");
            alertMapper.insert(alert);

            return null;

        } catch (Exception e) {
            log.error("支付状态轮询异常", e);
            task.setStatus(TaskStatus.FAILED);
            task.setFailReason(e.getMessage());
            taskMapper.updateById(task);
            return null;
        }
    }

    /**
     * 支付回调处理（预留）
     */
    @Transactional
    public void handleCallback(String payTradeNo, String status) {
        OrderInfo order = orderMapper.selectOne(
                new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getPayTradeNo, payTradeNo));
        if (order == null) {
            log.warn("支付回调未找到订单: payTradeNo={}", payTradeNo);
            return;
        }
        if ("01".equals(status)) {
            order.setStatus(com.postal.robotdemo.enums.OrderStatus.PAID);
            orderMapper.updateById(order);
            inventoryService.deductStock(order.getProductId(), order.getQuantity());
        }
    }

    public Page<OrderInfo> listOrders(int page, int size, String status) {
        LambdaQueryWrapper<OrderInfo> qw = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            qw.eq(OrderInfo::getStatus,
                    com.postal.robotdemo.enums.OrderStatus.valueOf(status));
        }
        qw.orderByDesc(OrderInfo::getCreateTime);
        return orderMapper.selectPage(new Page<>(page, size), qw);
    }
}

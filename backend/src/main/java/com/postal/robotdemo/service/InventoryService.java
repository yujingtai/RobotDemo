package com.postal.robotdemo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.postal.robotdemo.common.BizException;
import com.postal.robotdemo.entity.Inventory;
import com.postal.robotdemo.entity.Alert;
import com.postal.robotdemo.enums.AlertLevel;
import com.postal.robotdemo.mapper.AlertMapper;
import com.postal.robotdemo.mapper.InventoryMapper;
import com.postal.robotdemo.vo.InventoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryMapper inventoryMapper;
    private final AlertMapper alertMapper;
    private final StringRedisTemplate redisTemplate;

    @Value("${biz.inventory.low-stock-threshold:5}")
    private int lowStockThreshold;

    private static final String LOCK_KEY_PREFIX = "inventory:lock:";

    public List<InventoryVO> listAll() {
        return inventoryMapper.listWithProductName();
    }

    public List<Inventory> listAllRaw() {
        LambdaQueryWrapper<Inventory> qw = new LambdaQueryWrapper<>();
        qw.orderByAsc(Inventory::getAvailableQuantity);
        return inventoryMapper.selectList(qw);
    }

    public Inventory getByProductId(Long productId) {
        return inventoryMapper.selectOne(
                new LambdaQueryWrapper<Inventory>().eq(Inventory::getProductId, productId));
    }

    /**
     * 订单创建时锁定库存
     * 使用 Redis 分布式锁 + 数据库乐观锁双重保障
     */
    @Transactional
    public void lockStock(Long productId, int quantity) {
        String lockKey = LOCK_KEY_PREFIX + productId;
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", Duration.ofSeconds(10));

        if (Boolean.FALSE.equals(locked)) {
            throw new BizException(409, "库存操作繁忙，请稍后重试");
        }

        try {
            int rows = inventoryMapper.lockStock(productId, quantity);
            if (rows == 0) {
                throw new BizException(400, "库存不足");
            }
            checkLowStock(productId);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 支付成功 → 实际扣减库存
     */
    @Transactional
    public void deductStock(Long productId, int quantity) {
        int rows = inventoryMapper.deductStock(productId, quantity);
        if (rows == 0) {
            log.error("扣减库存失败: productId={}, quantity={}", productId, quantity);
            throw new BizException(500, "扣减库存失败");
        }
        checkLowStock(productId);
    }

    /**
     * 支付取消/失败/超时 → 释放锁定库存
     */
    @Transactional
    public void releaseStock(Long productId, int quantity) {
        int rows = inventoryMapper.releaseStock(productId, quantity);
        if (rows == 0) {
            log.warn("释放库存失败(可能已被释放): productId={}, quantity={}", productId, quantity);
        }
    }

    private void checkLowStock(Long productId) {
        Inventory inv = inventoryMapper.selectOne(
                new LambdaQueryWrapper<Inventory>().eq(Inventory::getProductId, productId));
        if (inv != null && inv.getAvailableQuantity() <= inv.getLowThreshold()) {
            Alert alert = new Alert();
            alert.setAlertType("STOCK_LOW");
            alert.setLevel(AlertLevel.WARN);
            alert.setSource("库存模块");
            alert.setTitle("低库存告警: productId=" + productId);
            alert.setDetail("当前可用库存=" + inv.getAvailableQuantity() + ", 阈值=" + inv.getLowThreshold());
            alert.setStatus("OPEN");
            alertMapper.insert(alert);
        }
    }

    /**
     * 机器人视觉库存校验结果回写 (技术规范书3.4.7)
     */
    public void reportVisualCheck(Long productId, Integer sampleMissing, Integer sampleMisplaced, Integer actualQuantity) {
        Inventory inv = getByProductId(productId);
        if (inv == null) {
            log.warn("库存记录不存在: productId={}", productId);
            return;
        }
        if (sampleMissing != null) inv.setSampleMissing(sampleMissing);
        if (sampleMisplaced != null) inv.setSampleMisplaced(sampleMisplaced);
        if (actualQuantity != null && !actualQuantity.equals(inv.getAvailableQuantity())) {
            inv.setAvailableQuantity(actualQuantity);
            // 账实不一致告警
            Alert alert = new Alert();
            alert.setAlertType("STOCK_MISMATCH");
            alert.setLevel(AlertLevel.ERROR);
            alert.setSource("视觉巡检");
            alert.setTitle("账实不一致: productId=" + productId);
            alert.setDetail("系统库存=" + inv.getAvailableQuantity() + ", 实际=" + actualQuantity);
            alert.setStatus("OPEN");
            alertMapper.insert(alert);
        }
        inventoryMapper.updateById(inv);
    }
}

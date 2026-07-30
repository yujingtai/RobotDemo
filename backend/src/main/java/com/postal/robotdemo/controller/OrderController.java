package com.postal.robotdemo.controller;

import com.postal.robotdemo.common.Result;
import com.postal.robotdemo.entity.OrderInfo;
import com.postal.robotdemo.service.OrderService;
import com.postal.robotdemo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 订单与支付 Controller
 * 核心流程: 创建订单 → 生成二维码 → 查询支付状态
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    /** 创建订单 (触发邮政 Mock 全流程) */
    @PostMapping
    public Result<OrderInfo> create(@RequestBody Map<String, Object> body) {
        Long productId = Long.valueOf(body.get("productId").toString());
        String productName = (String) body.getOrDefault("productName", "文创商品");
        int quantity = Integer.parseInt(body.getOrDefault("quantity", "1").toString());
        BigDecimal amount = new BigDecimal(body.getOrDefault("amount", "0").toString());
        BigDecimal postage = body.containsKey("postage") ?
                new BigDecimal(body.get("postage").toString()) : null;
        String receiveName = (String) body.getOrDefault("receiveName", "收件人");
        String receivePhone = (String) body.getOrDefault("receivePhone", "13800000000");
        String receiveAddress = (String) body.getOrDefault("receiveAddress", "北京市朝阳区");

        OrderInfo order = orderService.createOrder(productId, productName, quantity,
                amount, postage, "寄件人", "13900000000",
                receiveName, receivePhone, receiveAddress);
        return Result.ok(order);
    }

    /** 生成支付二维码 */
    @PostMapping("/{id}/pay-qrcode")
    public Result<String> generateQrCode(@PathVariable Long id) {
        String qrUrl = orderService.generatePayQrCode(id);
        return Result.ok(qrUrl);
    }

    /** 查询支付状态 (触发 Mock 流转) */
    @GetMapping("/{id}/pay-status")
    public Result<String> queryPayStatus(@PathVariable Long id) {
        String status = orderService.queryAndUpdatePayStatus(id);
        return Result.ok(status);
    }

    /** 轮询支付 (多次查询) */
    @PostMapping("/{id}/poll")
    public Result<String> pollPay(@PathVariable Long id) {
        String status = paymentService.pollPayStatus(id);
        return Result.ok(status);
    }

    /** 订单列表 */
    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String status) {
        return Result.ok(paymentService.listOrders(page, size, status));
    }

    /** 订单详情 */
    @GetMapping("/{id}")
    public Result<OrderInfo> get(@PathVariable Long id) {
        return Result.ok(orderService.getById(id));
    }
}

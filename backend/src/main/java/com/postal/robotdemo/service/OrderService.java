package com.postal.robotdemo.service;

import com.postal.robotdemo.adapter.client.PostalClient;
import com.postal.robotdemo.common.BizException;
import com.postal.robotdemo.dto.postal.*;
import com.postal.robotdemo.entity.OrderInfo;
import com.postal.robotdemo.entity.Product;
import com.postal.robotdemo.enums.OrderStatus;
import com.postal.robotdemo.mapper.OrderInfoMapper;
import com.postal.robotdemo.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 订单服务 - 核心业务流程:
 * 创建订单 → 锁定库存 → 调邮政生成邮件号 → 资费查询 → 提交订单 → 生成二维码
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderInfoMapper orderMapper;
    private final ProductMapper productMapper;
    private final InventoryService inventoryService;
    private final PostalClient postalClient;

    /**
     * 创建订单完整流程
     */
    @Transactional
    public OrderInfo createOrder(Long productId, String productName, int quantity, BigDecimal amount,
                                  BigDecimal postage, String customerName, String customerPhone,
                                  String receiveName, String receivePhone, String receiveAddress) {

        // 1. 校验商品是否存在且已上架
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BizException(404, "商品不存在");
        }
        if (!"ON_SHELF".equals(product.getStatus())) {
            throw new BizException(400, "商品已下架，无法下单");
        }

        // 2. 锁定库存
        inventoryService.lockStock(productId, quantity);

        // 2. 生成订单号
        String orderNo = "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        // 3. 调邮政生成邮件号码
        PostalMailNoReq mailNoReq = PostalMailNoReq.builder()
                .vSfdm("34")           // 安徽省
                .vJgbh("34100001")     // 机构编号(取自配置)
                .vYwcpdm("400")        // 特快
                .vYwcpmc("特快")
                .vSjlydm("26")
                .build();
        PostalMailNoRsp mailNoRsp = postalClient.generateMailNo(mailNoReq);

        String mailNo = null;
        if (mailNoRsp.getResult() != null && mailNoRsp.getResult().getCode() == 200) {
            mailNo = mailNoRsp.getResult().getVYjhm();
        }

        // 4. 调邮政资费查询
        BigDecimal finalPostage = postage;
        if (finalPostage == null) {
            PostalRateReq rateReq = PostalRateReq.builder()
                    .productCode("400")
                    .postProvinceCode("34").postProvinceName("安徽省")
                    .postCityCode("3406").postCityName("淮北市")
                    .disProvinceCode("11").disProvinceName("北京市")
                    .disCityCode("1101").disCityName("北京市")
                    .weight(500) // 默认重量
                    .isValue("0")
                    .build();
            PostalRateRsp rateRsp = postalClient.queryRate(rateReq);
            if (rateRsp.getResult() != null && rateRsp.getResult().getCode() == 200
                    && rateRsp.getResult().getData() != null) {
                finalPostage = BigDecimal.valueOf(rateRsp.getResult().getData().getFee());
            } else {
                finalPostage = BigDecimal.ZERO;
            }
        }

        // 5. 保存订单
        BigDecimal totalAmount = amount.add(finalPostage != null ? finalPostage : BigDecimal.ZERO);

        OrderInfo order = new OrderInfo();
        order.setOrderNo(orderNo);
        order.setProductId(productId);
        order.setProductName(productName);
        order.setQuantity(quantity);
        order.setAmount(amount);
        order.setPostage(finalPostage);
        order.setTotalAmount(totalAmount);
        order.setMailNo(mailNo);
        order.setStatus(OrderStatus.PENDING);
        orderMapper.insert(order);

        log.info("订单创建成功: orderNo={}, mailNo={}, amount={}", orderNo, mailNo, totalAmount);
        return order;
    }

    /**
     * 生成支付二维码
     * 调用邮政接口获取二维码，更新订单状态为 PAYING
     */
    @Transactional
    public String generatePayQrCode(Long orderId) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null) throw new BizException(404, "订单不存在");

        PostalQrCodeReq qrReq = PostalQrCodeReq.builder()
                .vJgbh("34100001")
                .vTxdm("TS01")
                .emp("EMP001")
                .vCxlsh(order.getOrderNo())
                .build();

        PostalQrCodeRsp qrRsp = postalClient.generateQrCode(qrReq);
        if (qrRsp.getResult() == null || !"200".equals(qrRsp.getResult().getCode())) {
            throw new BizException("生成支付二维码失败: " +
                    (qrRsp.getResult() != null ? qrRsp.getResult().getMsg() : "未知错误"));
        }

        PostalQrCodeRsp.QrCodeDatas datas = qrRsp.getResult().getDatas();
        order.setPayPlatformNo(datas.getVPtlsh());
        order.setPayTradeNo(datas.getVZflsh());
        order.setPayQrUrl(datas.getVEwmurl());
        order.setStatus(OrderStatus.PAYING);
        orderMapper.updateById(order);

        return datas.getVEwmurl();
    }

    /**
     * 查询支付状态并更新订单
     */
    @Transactional
    public String queryAndUpdatePayStatus(Long orderId) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null) throw new BizException(404, "订单不存在");
        if (order.getPayTradeNo() == null) throw new BizException("订单未生成支付二维码");

        PostalPayStatusReq req = PostalPayStatusReq.builder()
                .vCxlsh(order.getOrderNo())
                .vJgbh("34100001")
                .vZflsh(order.getPayTradeNo())
                .build();

        PostalPayStatusRsp rsp = postalClient.queryPayStatus(req);
        String zfzt = rsp.getResult() != null ? rsp.getResult().getZfzt() : null;

        if (zfzt == null) return null;

        switch (zfzt) {
            case "01" -> { // 支付成功
                if (order.getStatus() != OrderStatus.PAID) {
                    order.setStatus(OrderStatus.PAID);
                    orderMapper.updateById(order);
                    inventoryService.deductStock(order.getProductId(), order.getQuantity());
                    log.info("支付成功: orderNo={}", order.getOrderNo());
                }
            }
            case "02" -> { // 支付失败
                order.setStatus(OrderStatus.FAILED);
                orderMapper.updateById(order);
                inventoryService.releaseStock(order.getProductId(), order.getQuantity());
            }
            case "03", "05" -> { // 退款
                order.setStatus(OrderStatus.CANCELLED);
                orderMapper.updateById(order);
            }
            case "00" -> { // 支付中
                order.setStatus(OrderStatus.PAYING);
                orderMapper.updateById(order);
            }
        }
        return zfzt;
    }

    public OrderInfo getById(Long id) {
        return orderMapper.selectById(id);
    }
}

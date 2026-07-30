package com.postal.robotdemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.postal.robotdemo.enums.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_info")
public class OrderInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal amount;
    private BigDecimal postage;
    private BigDecimal totalAmount;
    private String mailNo;              // 邮件号码 V_YJHM
    private OrderStatus status;
    private String payTradeNo;          // 支付流水号 V_ZFLSH
    private String payPlatformNo;       // 平台流水号 V_PTLSH
    private String payQrUrl;            // 二维码链接 V_EWMURL
    private String postalTradeNo;       // 邮政交易流水号 V_CXLSH
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

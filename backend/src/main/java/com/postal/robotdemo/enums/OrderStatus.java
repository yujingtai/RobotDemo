package com.postal.robotdemo.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("待支付"),
    PAYING("支付中"),
    PAID("支付成功"),
    FAILED("支付失败"),
    CANCELLED("已取消"),
    TIMEOUT("已超时"),
    MANUAL_REQUIRED("需人工处理");

    private final String desc;

    OrderStatus(String desc) { this.desc = desc; }
}

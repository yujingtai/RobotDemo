package com.postal.robotdemo.enums;

import lombok.Getter;

/**
 * 邮政支付状态 (技术规范书3.4.7 + 接口文档支付状态查询)
 */
@Getter
public enum PayStatus {
    ZFZ_00("00", "支付中"),
    ZFZ_01("01", "支付成功"),
    ZFZ_02("02", "支付失败"),
    ZFZ_03("03", "已退款"),
    ZFZ_05("05", "部分退款");

    private final String code;
    private final String desc;

    PayStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PayStatus fromCode(String code) {
        for (PayStatus ps : values()) {
            if (ps.code.equals(code)) return ps;
        }
        return null;
    }
}

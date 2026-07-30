package com.postal.robotdemo.adapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会话控制协议 - 请求
 * 严格遵循接口文档 "2.4.4.1 请求" SessionHeader 字段定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionHeader {

    /** 接口协议编码 (固定F8) */
    @JsonProperty("ServiceCode")
    private String serviceCode;

    /** 版本号 (如 YY-1.0) */
    @JsonProperty("Version")
    private String version;

    /** 请求标识 (请求填0) */
    @JsonProperty("ActionCode")
    private String actionCode;

    /** 交易流水号: [5位平台编码]+[17位日期]+[10位流水号] 共32位 */
    @JsonProperty("TransactionID")
    private String transactionId;

    /** 发起方编码 */
    @JsonProperty("SrcSysID")
    private String srcSysId;

    /** 落地方编码 (固定 XYDYYQDXT) */
    @JsonProperty("DstSysID")
    private String dstSysId;

    /** 签名: BASE64(MD5(拼接串+秘钥)) */
    @JsonProperty("DigitalSign")
    private String digitalSign;

    /** 请求时间: yyyyMMddHHmmss */
    @JsonProperty("ReqTime")
    private String reqTime;
}

package com.postal.robotdemo.dto.postal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 生成订单收款二维码 - 响应
 * 严格遵循接口文档 TABLE 18
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostalQrCodeRsp {

    @JsonProperty("result")
    private QrCodeResult result;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QrCodeResult {
        @JsonProperty("code")
        private String code;

        @JsonProperty("msg")
        private String msg;

        @JsonProperty("datas")
        private QrCodeDatas datas;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QrCodeDatas {
        @JsonProperty("V_REMARK")
        private String vRemark;

        @JsonProperty("V_PTLSH")
        private String vPtlsh;          // 平台流水号

        @JsonProperty("V_ZFLSH")
        private String vZflsh;          // 支付流水号

        @JsonProperty("V_EWMURL")
        private String vEwmurl;         // 二维码链接
    }
}

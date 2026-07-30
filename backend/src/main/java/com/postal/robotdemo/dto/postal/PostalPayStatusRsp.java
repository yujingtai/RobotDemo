package com.postal.robotdemo.dto.postal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 支付状态查询 - 响应
 * 严格遵循接口文档 TABLE 21
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostalPayStatusRsp {

    @JsonProperty("result")
    private PayStatusResult result;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PayStatusResult {
        @JsonProperty("code")
        private String code;

        @JsonProperty("msg")
        private String msg;

        /**
         * 支付状态:
         * 01=支付成功, 02=支付失败, 03=已退款, 05=部分退款, 00=支付中
         */
        @JsonProperty("zfzt")
        private String zfzt;
    }
}

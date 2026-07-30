package com.postal.robotdemo.dto.postal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 邮件资费查询 - 响应
 * 严格遵循接口文档 TABLE 9
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostalRateRsp {

    @JsonProperty("result")
    private RateResult result;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RateResult {
        @JsonProperty("code")
        private Integer code;

        @JsonProperty("msg")
        private String msg;

        @JsonProperty("data")
        private RateData data;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RateData {
        @JsonProperty("fee")
        private Double fee;
    }
}

package com.postal.robotdemo.dto.postal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 收寄订单提交 - 响应
 * 严格遵循接口文档 TABLE 15
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostalOrderSubmitRsp {

    @JsonProperty("result")
    private SubmitResult result;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SubmitResult {
        @JsonProperty("code")
        private Integer code;           // 200=成功 500=失败

        @JsonProperty("V_CXLSH")
        private String vCxlsh;          // 交易流水号

        @JsonProperty("F_ZZF")
        private Double fZzf;            // 总资费

        @JsonProperty("F_YSZZF")
        private Double fYszzf;          // 应收总资费

        @JsonProperty("Zfxx")
        private Object zfxx;            // 资费明细
    }
}

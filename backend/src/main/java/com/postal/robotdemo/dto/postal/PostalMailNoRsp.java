package com.postal.robotdemo.dto.postal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 邮件号码生成服务 - 响应
 * 严格遵循接口文档 TABLE 12
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostalMailNoRsp {

    @JsonProperty("result")
    private MailNoResult result;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MailNoResult {
        @JsonProperty("code")
        private Integer code;           // 200=成功 500=失败

        @JsonProperty("V_YJHM")
        private String vYjhm;           // 生成的邮件号码

        @JsonProperty("msg")
        private String msg;
    }
}

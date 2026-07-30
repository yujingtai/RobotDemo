package com.postal.robotdemo.adapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 应答会话控制中的 Response 结构
 * 严格遵循接口文档 "2.4.4.2 应答"
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionResponse {

    /** 应答/错误类型 (F1) */
    @JsonProperty("RspType")
    private String rspType;

    /** 应答/错误代码 (F4): 0000-成功, 9009-解密错误, 1002-签名错误, 1006-超限 */
    @JsonProperty("RspCode")
    private String rspCode;

    /** 应答/错误描述 (V128) */
    @JsonProperty("RspDesc")
    private String rspDesc;
}

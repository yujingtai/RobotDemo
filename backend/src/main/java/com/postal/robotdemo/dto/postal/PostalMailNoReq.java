package com.postal.robotdemo.dto.postal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮件号码生成服务 - 请求
 * 严格遵循接口文档 TABLE 11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostalMailNoReq {

    @JsonProperty("V_SFDM")
    private String vSfdm;           // 省份代码

    @JsonProperty("V_JGBH")
    private String vJgbh;           // 机构编号

    @JsonProperty("V_YWCPDM")
    private String vYwcpdm;         // 业务产品代码

    @JsonProperty("V_YWCPMC")
    private String vYwcpmc;         // 业务产品名称: 107条码平信/200挂信/300普包/400特快

    @JsonProperty("V_BZDM")
    private String vBzdm;           // 备注代码

    @JsonProperty("V_BZMC")
    private String vBzmc;           // 备注名称

    @JsonProperty("V_SJLYDM")
    private String vSjlydm;         // 数据来源代码
}

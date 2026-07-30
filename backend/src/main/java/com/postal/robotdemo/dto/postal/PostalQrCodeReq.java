package com.postal.robotdemo.dto.postal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 生成订单收款二维码 - 请求
 * 严格遵循接口文档 TABLE 17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostalQrCodeReq {

    @JsonProperty("vJgbh")
    private String vJgbh;           // 机构编号

    @JsonProperty("vTxdm")
    private String vTxdm;           // 台席代码

    @JsonProperty("emp")
    private String emp;             // 员工工号

    @JsonProperty("vCxlsh")
    private String vCxlsh;          // 查询流水号
}

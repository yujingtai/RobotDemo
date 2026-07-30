package com.postal.robotdemo.dto.postal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付状态查询 - 请求
 * 严格遵循接口文档 TABLE 20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostalPayStatusReq {

    @JsonProperty("vCxlsh")
    private String vCxlsh;          // 查询流水号

    @JsonProperty("vJgbh")
    private String vJgbh;           // 机构编号

    @JsonProperty("vZflsh")
    private String vZflsh;          // 支付流水号
}

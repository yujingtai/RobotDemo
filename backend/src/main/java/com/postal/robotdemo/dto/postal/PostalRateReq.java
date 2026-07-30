package com.postal.robotdemo.dto.postal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮件资费查询 - 请求
 * 严格遵循接口文档 TABLE 8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostalRateReq {

    @JsonProperty("reqId")
    private String reqId;

    @JsonProperty("reqTime")
    private String reqTime;

    @JsonProperty("productCode")
    private String productCode;

    @JsonProperty("postProvinceCode")
    private String postProvinceCode;

    @JsonProperty("postProvinceName")
    private String postProvinceName;

    @JsonProperty("postCityCode")
    private String postCityCode;

    @JsonProperty("postCityName")
    private String postCityName;

    @JsonProperty("postCountyCode")
    private String postCountyCode;

    @JsonProperty("postCountyName")
    private String postCountyName;

    @JsonProperty("postAddress")
    private String postAddress;

    @JsonProperty("disProvinceCode")
    private String disProvinceCode;

    @JsonProperty("disProvinceName")
    private String disProvinceName;

    @JsonProperty("disCityCode")
    private String disCityCode;

    @JsonProperty("disCityName")
    private String disCityName;

    @JsonProperty("disCountyCode")
    private String disCountyCode;

    @JsonProperty("disCountyName")
    private String disCountyName;

    @JsonProperty("disAddress")
    private String disAddress;

    @JsonProperty("isValue")
    private String isValue;         // 1=是 0=否

    @JsonProperty("valueFee")
    private Double valueFee;

    @JsonProperty("len")
    private String len;             // 单位CM

    @JsonProperty("wide")
    private String wide;

    @JsonProperty("high")
    private String high;

    @JsonProperty("weight")
    private Integer weight;         // 克

    @JsonProperty("costCode")
    private String costCode;

    @JsonProperty("V_BZDM")
    private String vBzdm;

    @JsonProperty("V_BZMC")
    private String vBzmc;

    @JsonProperty("V_SJLYDM")
    private String vSjlydm = "26";

    @JsonProperty("V_SJLYMC")
    private String vSjlymc = "人形机器人";
}

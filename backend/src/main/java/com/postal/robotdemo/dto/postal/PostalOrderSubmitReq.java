package com.postal.robotdemo.dto.postal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 业务办理-收寄订单提交服务 - 请求
 * 严格遵循接口文档 TABLE 14 (核心字段)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostalOrderSubmitReq {

    // --- 机构/员工信息 ---
    @JsonProperty("CUR_V_JGBH")
    private String curVJgbh;

    @JsonProperty("V_JGID")
    private String vJgid;

    @JsonProperty("V_SFDM")
    private String vSfdm;

    @JsonProperty("CUR_V_XSJGID")
    private String curVXsJGID;

    @JsonProperty("CUR_V_DSJGID")
    private String curVDsJGID;

    @JsonProperty("CUR_V_SFJGID")
    private String curVSfJGID;

    @JsonProperty("V_GJJID")
    private String vGjjid;

    @JsonProperty("CUR_V_JGMC")
    private String curVJgmc;

    @JsonProperty("CUR_V_XSJGBH")
    private String curVXsJgbh;

    @JsonProperty("CUR_V_XSJGMC")
    private String curVXsJgmc;

    @JsonProperty("CUR_V_DSJGBH")
    private String curVDsJgbh;

    @JsonProperty("CUR_V_DSJGMC")
    private String curVDsJgmc;

    @JsonProperty("CUR_V_SJJGBH")
    private String curVSjJgbh;

    @JsonProperty("CUR_V_SJJGMC")
    private String curVSjJgmc;

    @JsonProperty("CUR_V_YGID")
    private String curVYgid;

    @JsonProperty("CUR_V_YGGH")
    private String curVYggh;

    @JsonProperty("CUR_V_YGXM")
    private String curVYgxm;

    @JsonProperty("CUR_V_TXDM")
    private String curVTxdm;

    // --- 客户/寄件人信息 ---
    @JsonProperty("V_DZYHBH")
    private String vDzyhbh;

    @JsonProperty("V_DZYHMC")
    private String vDzyhmc;

    @JsonProperty("V_JJKHDH")
    private String vJjkhdh;

    @JsonProperty("V_JJKHXM")
    private String vJjkhxm;

    @JsonProperty("V_JJKHZJLX")
    private String vJjkhzjlx;

    @JsonProperty("V_JJKHZJMC")
    private String vJjkhzjmc;

    @JsonProperty("V_JJKHZJHM")
    private String vJjkhzjhm;

    @JsonProperty("V_JJKHXB")
    private String vJjkhxb;

    @JsonProperty("V_JJKHDZ")
    private String vJjkhdz;

    @JsonProperty("V_JJKHDW")
    private String vJjkhdw;

    @JsonProperty("V_JJKHYB")
    private String vJjkhyb;

    @JsonProperty("V_KH_SFMC")
    private String vKhSfmc;

    @JsonProperty("V_KH_SFXZQH")
    private String vKhSfxzqh;

    @JsonProperty("V_KH_DSMC")
    private String vKhDsmc;

    @JsonProperty("V_KH_DSXZQH")
    private String vKhDsxzqh;

    @JsonProperty("V_KH_XSMC")
    private String vKhXsmc;

    @JsonProperty("V_KH_XSXZQH")
    private String vKhXsxzqh;

    // --- 寄件人信息 ---
    @JsonProperty("V_JJRDH")
    private String vJjrdh;

    @JsonProperty("V_JJRXM")
    private String vJjrxm;

    @JsonProperty("V_JJRXXDZ")
    private String vJjrxxDz;

    @JsonProperty("V_JJRDW")
    private String vJjrdw;

    @JsonProperty("V_JJRYB")
    private String vJjryb;

    @JsonProperty("V_JJR_SFMC")
    private String vJjrSfmc;

    @JsonProperty("V_JJR_SFXZQH")
    private String vJjrSfxzqh;

    @JsonProperty("V_JJR_DSMC")
    private String vJjrDsmc;

    @JsonProperty("V_JJR_DSXZQH")
    private String vJjrDsxzqh;

    @JsonProperty("V_JJR_XSMC")
    private String vJjrXsmc;

    @JsonProperty("V_JJR_XSXZQH")
    private String vJjrXsxzqh;

    @JsonProperty("V_SJJXZQH")
    private String vSjjxzqh;

    // --- 业务产品 ---
    @JsonProperty("V_YWCPDM")
    private String vYwcpdm;

    @JsonProperty("V_YWCPMC")
    private String vYwcpmc;

    @JsonProperty("V_YJTM")
    private String vYjtm;               // 邮件条码

    @JsonProperty("V_SJLYDM")
    private String vSjlydm = "26";

    @JsonProperty("V_SJLYMC")
    private String vSjlymc = "人形机器人";

    // --- 收件人信息 ---
    @JsonProperty("V_SJRDH")
    private String vSjrdh;

    @JsonProperty("V_SJRXM")
    private String vSjrxm;

    @JsonProperty("V_SJRXXDZ")
    private String vSjrxxDz;

    @JsonProperty("V_SJRDW")
    private String vSjrdw;

    @JsonProperty("V_SJRYB")
    private String vSjryb;

    @JsonProperty("V_SJR_SFMC")
    private String vSjrSfmc;

    @JsonProperty("V_SJR_SFXZQH")
    private String vSjrSfxzqh;

    @JsonProperty("V_SJR_DSMC")
    private String vSjrDsmc;

    @JsonProperty("V_SJR_DSXZQH")
    private String vSjrDsxzqh;

    @JsonProperty("V_SJR_XSMC")
    private String vSjrXsmc;

    @JsonProperty("V_SJR_XSXZQH")
    private String vSjrXsxzqh;

    // --- 邮件信息 ---
    @JsonProperty("V_DMDM")
    private String vDmdm;

    @JsonProperty("N_YJZL")
    private String nYjzl;               // 邮件重量

    @JsonProperty("N_JS")
    private String nJs;                 // 件数

    @JsonProperty("V_BZDM")
    private String vBzdm;               // 备注代码

    @JsonProperty("V_BZMC")
    private String vBzmc;               // 备注名称

    @JsonProperty("N_LRFSDM")
    private String nLrfsdm = "1";       // 录入方式代码

    @JsonProperty("N_YJC")
    private String nYjc;                // 长(cm)

    @JsonProperty("N_YJK")
    private String nYjk;                // 宽(cm)

    @JsonProperty("N_YJG")
    private String nYjg;                // 高(cm)

    @JsonProperty("N_YJTJ")
    private String nYjtj;               // 邮件体积

    @JsonProperty("V_QY")
    private String vQy;                 // 0本埠 1外埠

    // --- 资费 ---
    @JsonProperty("F_DBF")
    private String fDbf;                // 打包费

    @JsonProperty("F_TPJE")
    private String fTpje;               // 贴票金额

    @JsonProperty("F_BJJE")
    private String fBjje;               // 保价金额

    @JsonProperty("V_BZW")
    private String vBzw;                // 包装物

    @JsonProperty("V_FFDM")
    private String vFfdm = "XJ";        // 付费代码 XJ=现结

    @JsonProperty("V_FFFS")
    private String vFffs;               // 付费方式

    @JsonProperty("F_ZZF")
    private String fZzf;                // 总资费

    @JsonProperty("F_YSZZF")
    private String fYszzf;              // 应收总资费

    @JsonProperty("N_YHL")
    private String nYhl;                // 优惠率

    @JsonProperty("C_YXBZ")
    private String cYxbz;               // 有效标志

    // --- 包装物列表 ---
    @JsonProperty("BZW_LIST")
    private List<BzwItem> bzwList;

    // --- 内件信息列表 ---
    @JsonProperty("PHGJ_LIST")
    private List<PhgjItem> phgjList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BzwItem {
        @JsonProperty("xh") private String xh;
        @JsonProperty("vSpmc") private String vSpmc;
        @JsonProperty("vSpjm") private String vSpjm;
        @JsonProperty("vSpdm") private String vSpdm;
        @JsonProperty("vDw") private String vDw;
        @JsonProperty("fDj") private Double fDj;
        @JsonProperty("nSpkc") private Integer nSpkc;
        @JsonProperty("nsl") private Integer nsl;
        @JsonProperty("fJe") private Double fJe;
        @JsonProperty("fJhj") private Double fJhj;
        @JsonProperty("fJdfyzk") private Double fJdfyzk;
        @JsonProperty("vFldm") private String vFldm;
        @JsonProperty("vGg") private String vGg;
        @JsonProperty("fSl") private Double fSl;
        @JsonProperty("nSflssp") private Integer nSflssp;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PhgjItem {
        @JsonProperty("id") private String id;
        @JsonProperty("wpbh") private String wpbh;
        @JsonProperty("wpmc") private String wpmc;
        @JsonProperty("wpywmc") private String wpywmc;
        @JsonProperty("djzl") private Integer djzl;
        @JsonProperty("dqnjsbdj") private String dqnjsbdj;
        @JsonProperty("sl") private Integer sl;
        @JsonProperty("jldw") private String jldw;
        @JsonProperty("ycd") private String ycd;
        @JsonProperty("wpsm") private String wpsm;
        @JsonProperty("cfsm") private String cfsm;
        @JsonProperty("myxz") private String myxz;
        @JsonProperty("yyjhm") private String yyjhm;
    }
}

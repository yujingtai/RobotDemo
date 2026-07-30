package com.postal.robotdemo.adapter;

import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * 邮政接口签名工具
 * 算法严格遵循接口文档"附录三：发起方签名算法"
 *
 * 签名规则：
 *   DigitalSign = BASE64( MD5( ServiceCode + Version + ActionCode + TransactionID
 *                 + SrcSysID + DstSysID + ReqTime + SessionBody内容 + 秘钥 ) )
 */
@Slf4j
public class SignatureUtil {

    /** 秘钥，由新一代营业渠道系统授权提供 */
    private static final String SECRET_KEY = "dcff43b0fea660914aAFHElVnrq8";

    /**
     * 计算 DigitalSign
     * @param serviceCode   接口协议编码 (F8)
     * @param version       版本号 (YY-1.0)
     * @param actionCode    请求标识 (0)
     * @param transactionId 交易流水号 (32位)
     * @param srcSysId      发起方编码
     * @param dstSysId      落地方编码 (固定 XYDYYQDXT)
     * @param reqTime       请求时间 (yyyyMMddHHmmss)
     * @param sessionBody   业务数据 JSON 字符串
     * @return DigitalSign (BASE64编码)
     */
    public static String sign(String serviceCode, String version, String actionCode,
                               String transactionId, String srcSysId, String dstSysId,
                               String reqTime, String sessionBody) {
        // 拼接签名字符串: ServiceCode+Version+ActionCode+TransactionID+SrcSysID+DstSysID+ReqTime+SessionBody
        String str = serviceCode + version + actionCode + transactionId
                + srcSysId + dstSysId + reqTime + (sessionBody != null ? sessionBody : "");

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest((str + SECRET_KEY).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            log.error("签名计算失败", e);
            throw new RuntimeException("签名计算失败", e);
        }
    }
}

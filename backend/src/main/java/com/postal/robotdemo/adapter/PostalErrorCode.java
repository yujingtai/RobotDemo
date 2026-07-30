package com.postal.robotdemo.adapter;

import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

/**
 * 邮政接口错误码映射 (文档"附录二：返回编码表")
 * 将邮政侧错误码映射为本地业务错误码
 */
@Getter
public class PostalErrorCode {

    public static final String SUCCESS = "0000";

    // --- 本地错误码 ---
    public static final int BIZ_SIGN_ERROR = 41002;
    public static final int BIZ_DECRYPT_ERROR = 49009;
    public static final int BIZ_SRC_ID_ERROR = 49103;
    public static final int BIZ_DST_ID_ERROR = 49104;
    public static final int BIZ_SESSION_ERROR = 49105;
    public static final int BIZ_PARAM_ERROR = 41000;
    public static final int BIZ_SERVICE_CLOSED = 41001;
    public static final int BIZ_TIME_ERROR = 41004;
    public static final int BIZ_WHITELIST_NOTFOUND = 41005;
    public static final int BIZ_RATE_LIMIT = 41006;
    public static final int BIZ_WHITELIST_FORBIDDEN = 41007;
    public static final int BIZ_SERVICE_NOTFOUND = 41008;
    public static final int BIZ_HTTP_HANDSHAKE_FAIL = 41009;

    /** 邮政错误码 → 本地错误码 */
    private static final Map<String, Integer> MAPPING = new HashMap<>();

    static {
        MAPPING.put("0000", 200);
        MAPPING.put("9009", BIZ_DECRYPT_ERROR);
        MAPPING.put("9102", BIZ_SERVICE_NOTFOUND);
        MAPPING.put("9103", BIZ_SRC_ID_ERROR);
        MAPPING.put("9104", BIZ_DST_ID_ERROR);
        MAPPING.put("9105", BIZ_SESSION_ERROR);
        MAPPING.put("1000", BIZ_PARAM_ERROR);
        MAPPING.put("1001", BIZ_SERVICE_CLOSED);
        MAPPING.put("1002", BIZ_SIGN_ERROR);
        MAPPING.put("1003", 500);
        MAPPING.put("1004", BIZ_TIME_ERROR);
        MAPPING.put("1005", BIZ_WHITELIST_NOTFOUND);
        MAPPING.put("1006", BIZ_RATE_LIMIT);
        MAPPING.put("1007", BIZ_WHITELIST_FORBIDDEN);
        MAPPING.put("1008", BIZ_SERVICE_NOTFOUND);
        MAPPING.put("1009", BIZ_HTTP_HANDSHAKE_FAIL);
    }

    public static int toLocalCode(String postalCode) {
        return MAPPING.getOrDefault(postalCode, 500);
    }

    public static boolean isSuccess(String postalCode) {
        return SUCCESS.equals(postalCode) || "200".equals(postalCode);
    }

    public static String getDesc(String postalCode) {
        return switch (postalCode) {
            case "0000" -> "访问成功";
            case "9009" -> "请求报文解密错误";
            case "9102" -> "协议编码错误";
            case "9103" -> "发起方编码错误";
            case "9104" -> "落地方编码错误";
            case "9105" -> "会话控制编码错误";
            case "1000" -> "请求参数异常";
            case "1001" -> "接口服务已关闭";
            case "1002" -> "签名错误";
            case "1003" -> "系统其他异常";
            case "1004" -> "请求时间异常";
            case "1005" -> "白名单无效或不存在";
            case "1006" -> "访问量超限";
            case "1007" -> "白名单无权访问";
            case "1008" -> "接口服务不存在";
            case "1009" -> "http握手失败";
            default -> "未知错误";
        };
    }
}

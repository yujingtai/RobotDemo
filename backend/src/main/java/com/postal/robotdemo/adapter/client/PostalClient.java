package com.postal.robotdemo.adapter.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postal.robotdemo.adapter.*;
import com.postal.robotdemo.adapter.mock.PostalMockService;
import com.postal.robotdemo.common.BizException;
import com.postal.robotdemo.dto.postal.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 邮政接口统一客户端
 * 通过 postal.mock-enabled 配置切换 Mock / 真实调用
 * 业务代码通过此客户端调用邮政接口，不感知 Mock 差异
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostalClient {

    private final PostalMockService mockService;
    private final ObjectMapper objectMapper;

    @Value("${postal.mock-enabled:true}")
    private boolean mockEnabled;

    @Value("${postal.platform-code:ROBOT}")
    private String platformCode;

    @Value("${postal.src-sys-id:ROBOT}")
    private String srcSysId;

    @Value("${postal.dst-sys-id:XYDYYQDXT}")
    private String dstSysId;

    @Value("${postal.version:YY-1.0}")
    private String version;

    @Value("${postal.timeout-ms:10000}")
    private int timeoutMs;

    @Value("${postal.retry.max-attempts:3}")
    private int maxRetry;

    /** TransactionID 去重缓存 */
    private final ConcurrentHashMap<String, Object> idempotentCache = new ConcurrentHashMap<>();

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 邮件资费查询
     */
    public PostalRateRsp queryRate(PostalRateReq req) {
        if (mockEnabled) {
            return mockService.mockRateQuery(req);
        }
        return callPostalApi(req, PostalRateRsp.class, "rateQuery");
    }

    /**
     * 邮件号码生成
     */
    public PostalMailNoRsp generateMailNo(PostalMailNoReq req) {
        if (mockEnabled) {
            return mockService.mockMailNoGenerate(req);
        }
        return callPostalApi(req, PostalMailNoRsp.class, "mailNoGenerate");
    }

    /**
     * 收寄订单提交
     */
    public PostalOrderSubmitRsp submitOrder(PostalOrderSubmitReq req) {
        if (mockEnabled) {
            return mockService.mockOrderSubmit(req);
        }
        return callPostalApi(req, PostalOrderSubmitRsp.class, "orderSubmit");
    }

    /**
     * 生成订单收款二维码
     */
    public PostalQrCodeRsp generateQrCode(PostalQrCodeReq req) {
        if (mockEnabled) {
            return mockService.mockQrCode(req);
        }
        return callPostalApi(req, PostalQrCodeRsp.class, "qrCode");
    }

    /**
     * 支付状态查询
     */
    public PostalPayStatusRsp queryPayStatus(PostalPayStatusReq req) {
        if (mockEnabled) {
            return mockService.mockPayStatusQuery(req);
        }
        return callPostalApi(req, PostalPayStatusRsp.class, "payStatusQuery");
    }

    /**
     * 统一调用邮政接口
     * 1. 生成 TransactionID
     * 2. 组装 SessionHeader
     * 3. 计算 DigitalSign
     * 4. 发送 HTTP POST
     * 5. 检查返回码, 映射错误
     */
    private <T, R> R callPostalApi(T req, Class<R> rspClass, String serviceCode) {
        String transactionId = TransactionIdGenerator.generate(platformCode);
        String reqTime = LocalDateTime.now().format(TIME_FMT);

        // 幂等去重
        if (idempotentCache.putIfAbsent(transactionId, req) != null) {
            log.warn("重复请求, TransactionID={}", transactionId);
            throw new BizException(409, "重复请求");
        }

        String sessionBody;
        try {
            sessionBody = objectMapper.writeValueAsString(req);
        } catch (JsonProcessingException e) {
            throw new BizException("序列化请求体失败: " + e.getMessage());
        }

        String digitalSign = SignatureUtil.sign(
                serviceCode, version, "0", transactionId,
                srcSysId, dstSysId, reqTime, sessionBody);

        SessionHeader header = SessionHeader.builder()
                .serviceCode(serviceCode)
                .version(version)
                .actionCode("0")
                .transactionId(transactionId)
                .srcSysId(srcSysId)
                .dstSysId(dstSysId)
                .digitalSign(digitalSign)
                .reqTime(reqTime)
                .build();

        YYRoot<T> yyRoot = new YYRoot<>(header, req);

        try {
            // TODO: 实际 HTTP 调用 (RestTemplate 或 WebClient)
            // 当前仅实现 Mock，真实调用预留
            log.info("[PostalClient] 真实接口调用 (未实现, 返回Mock): serviceCode={}, transactionId={}",
                    serviceCode, transactionId);
            // 降级到 Mock
            throw new UnsupportedOperationException("真实邮政接口调用未实现，请切换为 Mock 模式");
        } catch (Exception e) {
            log.error("邮政接口调用失败: {}", e.getMessage());
            throw new BizException(PostalErrorCode.BIZ_HTTP_HANDSHAKE_FAIL,
                    "邮政接口[" + serviceCode + "]调用失败: " + e.getMessage());
        }
    }
}

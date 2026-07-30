package com.postal.robotdemo.adapter.mock;

import com.postal.robotdemo.dto.postal.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 邮政接口 Mock 桩
 * 按 profile 开关切换 (postal.mock-enabled=true)
 * 支持模拟: 正常返回、支付状态流转、超时、签名错误等场景
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "postal.mock-enabled", havingValue = "true", matchIfMissing = true)
public class PostalMockService {

    /** 模拟支付状态流转: 支付流水号 → 当前状态 (00→01) */
    private final ConcurrentHashMap<String, String> payStateMap = new ConcurrentHashMap<>();

    // === 1. 邮件资费查询 Mock ===
    public PostalRateRsp mockRateQuery(PostalRateReq req) {
        log.info("[Mock] 邮件资费查询: productCode={}, weight={}g", req.getProductCode(), req.getWeight());

        PostalRateRsp rsp = new PostalRateRsp();
        PostalRateRsp.RateResult result = new PostalRateRsp.RateResult();
        result.setCode(200);
        result.setMsg("查询成功");

        PostalRateRsp.RateData data = new PostalRateRsp.RateData();
        // 模拟资费: 基础12元 + 每500g加2元
        double fee = 12.0 + Math.max(0, (req.getWeight() - 500) / 500.0) * 2.0;
        data.setFee(Math.round(fee * 100.0) / 100.0);
        result.setData(data);
        rsp.setResult(result);
        return rsp;
    }

    // === 2. 邮件号码生成 Mock ===
    public PostalMailNoRsp mockMailNoGenerate(PostalMailNoReq req) {
        log.info("[Mock] 邮件号码生成: YWCPDM={}, JGBH={}", req.getVYwcpdm(), req.getVJgbh());

        PostalMailNoRsp rsp = new PostalMailNoRsp();
        PostalMailNoRsp.MailNoResult result = new PostalMailNoRsp.MailNoResult();
        result.setCode(200);
        // 模拟 EMS 邮件号码: 13位数字
        String mailNo = "EMS" + System.currentTimeMillis() % 10_000_000_000L;
        result.setVYjhm(mailNo);
        result.setMsg("生成成功");
        rsp.setResult(result);
        return rsp;
    }

    // === 3. 收寄订单提交 Mock ===
    public PostalOrderSubmitRsp mockOrderSubmit(PostalOrderSubmitReq req) {
        log.info("[Mock] 收寄订单提交: YJTM={}, JSRXM={}", req.getVYjtm(), req.getVSjrxm());

        PostalOrderSubmitRsp rsp = new PostalOrderSubmitRsp();
        PostalOrderSubmitRsp.SubmitResult result = new PostalOrderSubmitRsp.SubmitResult();
        result.setCode(200);
        result.setVCxlsh("CX" + System.currentTimeMillis());  // V_CXLSH
        result.setFZzf(Double.parseDouble(req.getFZzf() != null ? req.getFZzf() : "0"));
        result.setFYszzf(Double.parseDouble(req.getFYszzf() != null ? req.getFYszzf() : "0"));
        rsp.setResult(result);
        return rsp;
    }

    // === 4. 生成收款二维码 Mock ===
    public PostalQrCodeRsp mockQrCode(PostalQrCodeReq req) {
        String payTradeNo = "PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        log.info("[Mock] 生成二维码: JGBH={}, TXDM={}, payTradeNo={}", req.getVJgbh(), req.getVTxdm(), payTradeNo);

        // 初始化支付状态为"支付中"
        payStateMap.put(payTradeNo, "00");

        PostalQrCodeRsp rsp = new PostalQrCodeRsp();
        PostalQrCodeRsp.QrCodeResult result = new PostalQrCodeRsp.QrCodeResult();
        result.setCode("200");
        result.setMsg("生成成功");

        PostalQrCodeRsp.QrCodeDatas datas = new PostalQrCodeRsp.QrCodeDatas();
        datas.setVRemark("收款二维码");
        datas.setVPtlsh("PT" + System.currentTimeMillis());  // V_PTLSH
        datas.setVZflsh(payTradeNo);                         // V_ZFLSH
        datas.setVEwmurl("https://mock.qrcode.example.com/pay?id=" + payTradeNo);  // V_EWMURL
        result.setDatas(datas);
        rsp.setResult(result);
        return rsp;
    }

    // === 5. 支付状态查询 Mock ===
    /**
     * 模拟支付状态流转: 第1次查询返回00(支付中), 第2次及以后返回01(支付成功)
     */
    public PostalPayStatusRsp mockPayStatusQuery(PostalPayStatusReq req) {
        String payTradeNo = req.getVZflsh();
        log.info("[Mock] 支付状态查询: ZFLSH={}", payTradeNo);

        // 首次查询返回"支付中", 后续自动转为"支付成功"
        String currentState = payStateMap.getOrDefault(payTradeNo, "00");
        if ("00".equals(currentState)) {
            payStateMap.put(payTradeNo, "01");  // 下次变成功
        }

        PostalPayStatusRsp rsp = new PostalPayStatusRsp();
        PostalPayStatusRsp.PayStatusResult result = new PostalPayStatusRsp.PayStatusResult();
        result.setCode("200");
        result.setMsg("查询成功");
        result.setZfzt(currentState);
        rsp.setResult(result);
        return rsp;
    }

    // === 模拟异常场景 ===

    /** 模拟签名错误 */
    public PostalPayStatusRsp mockSignError() {
        PostalPayStatusRsp rsp = new PostalPayStatusRsp();
        PostalPayStatusRsp.PayStatusResult result = new PostalPayStatusRsp.PayStatusResult();
        result.setCode("1002");
        result.setMsg("签名错误");
        result.setZfzt(null);
        rsp.setResult(result);
        return rsp;
    }

    /** 模拟报文解密错误 */
    public PostalPayStatusRsp mockDecryptError() {
        PostalPayStatusRsp rsp = new PostalPayStatusRsp();
        PostalPayStatusRsp.PayStatusResult result = new PostalPayStatusRsp.PayStatusResult();
        result.setCode("9009");
        result.setMsg("请求报文解密错误");
        result.setZfzt(null);
        rsp.setResult(result);
        return rsp;
    }

    /** 模拟超时 */
    public void mockTimeout() throws InterruptedException {
        log.info("[Mock] 模拟超时场景...");
        Thread.sleep(15_000); // 超过默认 10s 超时
    }

    /** 模拟访问量超限 */
    public PostalPayStatusRsp mockRateLimit() {
        PostalPayStatusRsp rsp = new PostalPayStatusRsp();
        PostalPayStatusRsp.PayStatusResult result = new PostalPayStatusRsp.PayStatusResult();
        result.setCode("1006");
        result.setMsg("接口服务范围已经超出当日访问量限制");
        result.setZfzt(null);
        rsp.setResult(result);
        return rsp;
    }
}

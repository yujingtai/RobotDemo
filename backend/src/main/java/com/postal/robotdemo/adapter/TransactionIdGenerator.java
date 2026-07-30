package com.postal.robotdemo.adapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TransactionID 生成器
 * 格式: [5位平台编码] + [17位日期 yyyyMMddHHmmssfff] + [10位流水号]
 * 总计 32 位，全局唯一
 */
public class TransactionIdGenerator {

    private static final AtomicLong SEQ = new AtomicLong(0);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    /**
     * 生成全局唯一 TransactionID
     * @param platformCode 5位平台编码 (如 ROBOT)
     */
    public static String generate(String platformCode) {
        String datePart = LocalDateTime.now().format(FMT);
        long seq = SEQ.incrementAndGet() % 10_000_000_000L;
        String seqPart = String.format("%010d", seq);
        return platformCode + datePart + seqPart;
    }
}

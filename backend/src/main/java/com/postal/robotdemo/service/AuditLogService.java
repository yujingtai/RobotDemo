package com.postal.robotdemo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.postal.robotdemo.entity.Alert;
import com.postal.robotdemo.entity.AuditLog;
import com.postal.robotdemo.mapper.AlertMapper;
import com.postal.robotdemo.mapper.AuditLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogMapper auditLogMapper;
    private final AlertMapper alertMapper;

    public void log(String operator, String operateType, String target, String targetId,
                    String result, String detail, String traceId) {
        AuditLog log = new AuditLog();
        log.setOperator(operator);
        log.setOperateTime(LocalDateTime.now());
        log.setOperateType(operateType);
        log.setTarget(target);
        log.setTargetId(targetId);
        log.setResult(result);
        log.setDetail(detail);
        log.setTraceId(traceId);
        log.setCreateTime(LocalDateTime.now());
        auditLogMapper.insert(log);
    }

    public Page<AuditLog> page(int page, int size, String operateType, String operator) {
        LambdaQueryWrapper<AuditLog> qw = new LambdaQueryWrapper<>();
        if (operateType != null && !operateType.isEmpty()) qw.eq(AuditLog::getOperateType, operateType);
        if (operator != null && !operator.isEmpty()) qw.eq(AuditLog::getOperator, operator);
        qw.orderByDesc(AuditLog::getOperateTime);
        return auditLogMapper.selectPage(new Page<>(page, size), qw);
    }

    public Page<Alert> listAlerts(int page, int size, String status) {
        LambdaQueryWrapper<Alert> qw = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) qw.eq(Alert::getStatus, status);
        qw.orderByDesc(Alert::getCreateTime);
        return alertMapper.selectPage(new Page<>(page, size), qw);
    }
}

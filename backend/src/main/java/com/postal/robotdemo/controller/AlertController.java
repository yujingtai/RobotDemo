package com.postal.robotdemo.controller;

import com.postal.robotdemo.common.Result;
import com.postal.robotdemo.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AuditLogService auditLogService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String status) {
        return Result.ok(auditLogService.listAlerts(page, size, status));
    }
}

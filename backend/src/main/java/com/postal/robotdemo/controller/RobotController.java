package com.postal.robotdemo.controller;

import com.postal.robotdemo.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 机器人状态上报接口 (技术规范书3.2.6)
 * 预留: 接受机器人上报的位置/电量/任务/故障等状态
 */
@Slf4j
@RestController
@RequestMapping("/api/robot")
public class RobotController {

    /** 机器人状态上报 */
    @PostMapping("/status")
    public Result<Void> reportStatus(@RequestBody Map<String, Object> body) {
        log.info("[机器人状态上报] {}", body);
        // TODO: 解析并存储机器人状态 (位置、电量、任务状态、故障码)
        return Result.ok();
    }

    /** 视觉库存校验结果回写 (技术规范书3.4.7) */
    @PostMapping("/inventory/check")
    public Result<Void> inventoryCheck(@RequestBody Map<String, Object> body) {
        log.info("[视觉库存校验] {}", body);
        // TODO: 调用 InventoryService.reportVisualCheck
        return Result.ok();
    }
}

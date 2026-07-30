package com.postal.robotdemo.controller;

import com.postal.robotdemo.common.Result;
import com.postal.robotdemo.enums.TaskStatus;
import com.postal.robotdemo.enums.TaskType;
import com.postal.robotdemo.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) TaskStatus status) {
        return Result.ok(taskService.list(page, size, status));
    }

    @PostMapping
    public Result<?> create(@RequestBody Map<String, Object> body) {
        TaskType type = TaskType.valueOf((String) body.getOrDefault("taskType", "SPEECH"));
        int priority = Integer.parseInt(body.getOrDefault("priority", "5").toString());
        String inputData = (String) body.getOrDefault("inputData", null);
        Long dependTaskId = body.containsKey("dependTaskId") ?
                Long.valueOf(body.get("dependTaskId").toString()) : null;
        return Result.ok(taskService.createTask(type, priority, inputData, dependTaskId));
    }
}

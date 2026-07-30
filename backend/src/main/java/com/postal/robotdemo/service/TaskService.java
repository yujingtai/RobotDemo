package com.postal.robotdemo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.postal.robotdemo.entity.Alert;
import com.postal.robotdemo.entity.TaskInfo;
import com.postal.robotdemo.enums.AlertLevel;
import com.postal.robotdemo.enums.TaskStatus;
import com.postal.robotdemo.enums.TaskType;
import com.postal.robotdemo.mapper.AlertMapper;
import com.postal.robotdemo.mapper.TaskInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskInfoMapper taskMapper;
    private final AlertMapper alertMapper;

    public TaskInfo createTask(TaskType type, Integer priority, String inputData, Long dependTaskId) {
        TaskInfo task = new TaskInfo();
        task.setTaskNo("TASK" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        task.setTaskType(type);
        task.setStatus(TaskStatus.CREATED);
        task.setPriority(priority != null ? priority : 5);
        task.setDependTaskId(dependTaskId);
        task.setInputData(inputData);
        task.setTimeoutSeconds(300);
        task.setMaxRetry(3);
        task.setRetryCount(0);
        taskMapper.insert(task);
        log.info("任务创建: taskNo={}, type={}", task.getTaskNo(), type);
        return task;
    }

    @Transactional
    public void updateStatus(String taskNo, TaskStatus newStatus, String failReason, String outputData) {
        TaskInfo task = taskMapper.selectOne(
                new LambdaQueryWrapper<TaskInfo>().eq(TaskInfo::getTaskNo, taskNo));
        if (task == null) {
            log.warn("任务不存在: {}", taskNo);
            return;
        }
        task.setStatus(newStatus);
        if (failReason != null) task.setFailReason(failReason);
        if (outputData != null) task.setOutputData(outputData);

        if (newStatus == TaskStatus.RUNNING && task.getStartTime() == null) {
            task.setStartTime(LocalDateTime.now());
        }
        if (newStatus.isTerminal()) {
            task.setEndTime(LocalDateTime.now());
            if (task.getStartTime() != null) {
                task.setDurationMs(java.time.Duration.between(task.getStartTime(), task.getEndTime()).toMillis());
            }
        }
        if (newStatus == TaskStatus.FAILED) {
            int retries = task.getRetryCount() + 1;
            task.setRetryCount(retries);
            if (retries >= task.getMaxRetry()) {
                task.setStatus(TaskStatus.MANUAL_REQUIRED); // 超过重试次数转人工
                createAlert(task);  // 创建告警
            }
        }
        taskMapper.updateById(task);
    }

    private void createAlert(TaskInfo task) {
        Alert alert = new Alert();
        alert.setAlertType(mapTaskTypeToAlertType(task.getTaskType()));
        alert.setLevel(AlertLevel.ERROR);
        alert.setSource("任务模块");
        alert.setTitle(String.format("任务[%s]需人工处理", task.getTaskNo()));
        alert.setDetail(String.format("类型=%s, 失败原因=%s, 重试次数=%d",
                task.getTaskType(), task.getFailReason(), task.getRetryCount()));
        alert.setStatus("OPEN");
        alertMapper.insert(alert);
    }

    private String mapTaskTypeToAlertType(TaskType type) {
        return switch (type) {
            case NAV -> "NAV_FAIL";
            case GRASP -> "GRASP_FAIL";
            case CHECKOUT -> "PAY_ERROR";
            case SPEECH, INSPECTION -> "SYSTEM_ERROR";
            case SAFETY -> "NAV_FAIL";
        };
    }

    public Page<TaskInfo> list(int page, int size, TaskStatus status) {
        LambdaQueryWrapper<TaskInfo> qw = new LambdaQueryWrapper<>();
        if (status != null) qw.eq(TaskInfo::getStatus, status);
        qw.orderByDesc(TaskInfo::getCreateTime);
        return taskMapper.selectPage(new Page<>(page, size), qw);
    }
}

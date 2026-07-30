package com.postal.robotdemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.postal.robotdemo.enums.TaskStatus;
import com.postal.robotdemo.enums.TaskType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("task_info")
public class TaskInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskNo;
    private TaskType taskType;
    private TaskStatus status;
    private Integer priority;       // 1(最高)-10(最低)
    private Long dependTaskId;
    private String inputData;       // JSON
    private String outputData;      // JSON
    private Integer timeoutSeconds;
    private Integer retryCount;
    private Integer maxRetry;
    private String failReason;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMs;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

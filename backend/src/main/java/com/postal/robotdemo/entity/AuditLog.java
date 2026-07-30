package com.postal.robotdemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("audit_log")
public class AuditLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String operator;
    private LocalDateTime operateTime;
    private String operateType;  // LOGIN/CONFIG/ORDER/PAY/POSTAL/TASK/ALERT/USER
    private String target;
    private String targetId;
    private String result;       // SUCCESS/FAIL
    private String detail;
    private String traceId;
    private LocalDateTime createTime;
}

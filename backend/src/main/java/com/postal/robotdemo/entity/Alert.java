package com.postal.robotdemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.postal.robotdemo.enums.AlertLevel;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("alert")
public class Alert {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String alertType;   // NAV_FAIL/GRASP_FAIL/PAY_ERROR/STOCK_LOW/STOCK_MISMATCH/NETWORK_DOWN/SYSTEM_ERROR
    private AlertLevel level;
    private String source;
    private String title;
    private String detail;
    private String status;      // OPEN/ACKNOWLEDGED/RESOLVED/CLOSED
    private String handler;
    private String handleRecord;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

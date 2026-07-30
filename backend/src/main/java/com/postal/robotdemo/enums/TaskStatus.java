package com.postal.robotdemo.enums;

import lombok.Getter;

@Getter
public enum TaskStatus {
    CREATED("已创建"),
    QUEUED("已排队"),
    RUNNING("运行中"),
    PAUSED("已暂停"),
    SUCCEEDED("已成功"),
    FAILED("已失败"),
    CANCELLED("已取消"),
    MANUAL_REQUIRED("需人工处理");

    private final String desc;

    TaskStatus(String desc) { this.desc = desc; }

    public boolean isTerminal() {
        return this == SUCCEEDED || this == FAILED || this == CANCELLED || this == MANUAL_REQUIRED;
    }
}

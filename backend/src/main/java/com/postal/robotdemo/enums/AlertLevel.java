package com.postal.robotdemo.enums;

import lombok.Getter;

@Getter
public enum AlertLevel {
    INFO("信息"),
    WARN("警告"),
    ERROR("错误"),
    CRITICAL("严重");

    private final String desc;

    AlertLevel(String desc) { this.desc = desc; }
}

package com.postal.robotdemo.enums;

import lombok.Getter;

@Getter
public enum TaskType {
    NAV("导航"),
    GRASP("抓取"),
    SPEECH("语音"),
    CHECKOUT("结算"),
    INSPECTION("巡检"),
    SAFETY("安全");

    private final String desc;

    TaskType(String desc) { this.desc = desc; }
}

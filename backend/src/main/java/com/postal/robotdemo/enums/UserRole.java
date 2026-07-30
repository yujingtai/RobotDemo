package com.postal.robotdemo.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("管理员"),
    OPERATOR("运营"),
    MAINTAINER("维护");

    private final String desc;

    UserRole(String desc) { this.desc = desc; }
}

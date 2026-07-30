package com.postal.robotdemo.adapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮政接口统一请求根结构 YYRoot
 * 严格遵循接口文档 "2.4.4 会话控制格式"
 *
 * {
 *   "SessionHeader": { ... },
 *   "SessionBody": { ... }
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YYRoot<T> {

    @JsonProperty("SessionHeader")
    private SessionHeader sessionHeader;

    @JsonProperty("SessionBody")
    private T sessionBody;
}

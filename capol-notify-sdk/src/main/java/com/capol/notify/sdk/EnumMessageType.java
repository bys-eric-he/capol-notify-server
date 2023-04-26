package com.capol.notify.sdk;

import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 消息类型
 */
@Getter
public enum EnumMessageType {
    DING_NORMAL_MESSAGE("DING_NORMAL_MESSAGE", "钉钉普通消息"),
    DING_GROUP_MESSAGE("DING_GROUP_MESSAGE", "钉钉群组消息"),
    EMAIL_MESSAGE("EMAIL_MESSAGE", "邮件消息");
    private final String code;
    private final String desc;

    EnumMessageType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EnumMessageType ofValue(String code) {
        Objects.requireNonNull(code);
        return Stream.of(values()).filter(type -> Objects.equals(type.getCode(), code)).findFirst().orElseThrow(() -> new RuntimeException(String.format("消息类型,不存在Code为: %s 的枚举值!", code)));
    }
}

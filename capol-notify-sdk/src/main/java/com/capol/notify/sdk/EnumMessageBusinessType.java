package com.capol.notify.sdk;

import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 消息业务类型
 */
@Getter
public enum EnumMessageBusinessType {
    APPLY_FOR_OT("APPLY_FOR_OT", "加班申请"),
    ASK_FOR_LEAVE("ASK_FOR_LEAVE", "请假申请"),
    APPLY_FOR_LOAN("APPLY_FOR_LOAN", "借款申请"),
    APPLY_COMPENSATORY_LEAVE("APPLY_COMPENSATORY_LEAVE", "调休申请"),
    APPLY_RESIGNATION_LEAVE("APPLY_RESIGNATION_LEAVE", "离职申请");
    private final String code;
    private final String desc;

    EnumMessageBusinessType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EnumMessageBusinessType ofValue(String code) {
        Objects.requireNonNull(code);
        return Stream.of(values()).filter(type -> Objects.equals(type.getCode(), code)).findFirst().orElseThrow(() -> new RuntimeException(String.format("消息业务类型,不存在Code为: %s 的枚举值!", code)));
    }
}

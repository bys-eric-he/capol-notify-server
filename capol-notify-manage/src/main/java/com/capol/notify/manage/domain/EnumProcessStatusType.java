package com.capol.notify.manage.domain;

import lombok.Getter;

/**
 * 消息处理状态
 */
@Getter
public enum EnumProcessStatusType {

    WAIT_TODO(0, "待处理"),
    SUCCESS(1, "处理成功"),
    FAILURE(2, "处理失败");

    private final Integer code;
    private final String desc;

    EnumProcessStatusType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

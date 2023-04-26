package com.capol.notify.manage.domain;

import lombok.Getter;

/**
 * 记录状态
 */
@Getter
public enum EnumStatusType {

    DELETE(0,"删除"),
    NORMAL(1,"正常");

    private final Integer code;
    private final String desc;

    EnumStatusType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

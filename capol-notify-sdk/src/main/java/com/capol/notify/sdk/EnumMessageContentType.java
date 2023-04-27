package com.capol.notify.sdk;

import lombok.Getter;

/**
 * 消息内容类型
 */
@Getter
public enum EnumMessageContentType {
    TEXT("TEXT", "text"),
    IMAGE("IMAGE", "image"),
    FILE("FILE", "file"),
    LINK("LINK", "link"),
    MARKDOWN("MARKDOWN", "markdown"),
    OA("OA", "oa"),
    ACTION_CARD("ACTION_CARD", "action_card");
    private String type;
    private String typeName;

    EnumMessageContentType(String type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }
}

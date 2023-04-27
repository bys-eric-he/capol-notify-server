package com.capol.notify.sdk.command;

import com.capol.notify.sdk.EnumMessageBusinessType;
import com.capol.notify.sdk.EnumMessageContentType;
import com.capol.notify.sdk.EnumMessageType;
import lombok.Data;

/**
 * 钉钉群组消息传输对象
 */
@Data
public class DingDingGroupMsgCommand extends BaseMsgCommand {
    private static final long serialVersionUID = -6643212454457854214L;

    /**
     * 群会话ID
     */
    private String chatId;

    /**
     * 消息内容
     */
    private String content;

    public DingDingGroupMsgCommand(String chatId, String content, Integer priority, EnumMessageType messageType,
                                   EnumMessageContentType contentType, EnumMessageBusinessType businessType) {
        this.chatId = chatId;
        this.content = content;
        this.setPriority(priority);
        this.setMessageType(messageType);
        this.setContentType(contentType);
        this.setBusinessType(businessType);
    }
}

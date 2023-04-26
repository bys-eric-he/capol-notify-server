package com.capol.notify.sdk.command;

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
}

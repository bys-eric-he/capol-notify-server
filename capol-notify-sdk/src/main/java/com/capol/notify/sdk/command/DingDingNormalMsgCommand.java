package com.capol.notify.sdk.command;

import com.capol.notify.sdk.EnumMessageBusinessType;
import com.capol.notify.sdk.EnumMessageType;
import lombok.Data;

import java.util.List;

/**
 * 钉钉普通消息传输对象
 */
@Data
public class DingDingNormalMsgCommand extends BaseMsgCommand {
    private static final long serialVersionUID = -6643212454457854652L;

    /**
     * 接收者的用户userIds列表，最大列表长度100
     */
    private List<String> userIds;
    /**
     * 应用agentId
     */
    private Long agentId;

    /**
     * 消息内容
     */
    private String content;

    public DingDingNormalMsgCommand() {
    }

    public DingDingNormalMsgCommand(List<String> userIds, Long agentId, String content, Integer priority, EnumMessageType messageType,
                                    EnumMessageBusinessType businessType) {
        this.userIds = userIds;
        this.agentId = agentId;
        this.content = content;
        this.setPriority(priority);
        this.setMessageType(messageType);
        this.setBusinessType(businessType);
    }

    public DingDingNormalMsgCommand(List<String> userIds, Long agentId, String content, Long messageId,
                                    Long userId, Integer priority, EnumMessageType messageType,
                                    EnumMessageBusinessType businessType) {
        this.userIds = userIds;
        this.agentId = agentId;
        this.content = content;
        this.setMessageId(messageId);
        this.setUserId(userId);
        this.setPriority(priority);
        this.setMessageType(messageType);
        this.setBusinessType(businessType);
    }
}

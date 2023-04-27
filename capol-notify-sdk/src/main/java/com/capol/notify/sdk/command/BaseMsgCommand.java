package com.capol.notify.sdk.command;

import com.capol.notify.sdk.EnumMessageBusinessType;
import com.capol.notify.sdk.EnumMessageContentType;
import com.capol.notify.sdk.EnumMessageType;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseMsgCommand implements Serializable {
    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 当前发送消息的身份ID(通过该ID，获取用户队列信息)
     */
    private Long userId;

    /**
     * 消息优先级
     */
    private Integer priority;

    /**
     * 消息类型(1-钉钉普通消息 2-钉钉群组消息 3-邮件消息)
     */
    private EnumMessageType messageType;

    /**
     * 消息内容类型(text,image,file,link,markdown,oa,action_card)
     */
    private EnumMessageContentType contentType;

    /**
     * 消息业务类型(根据业务类型确定要发送的消息队列)
     */
    private EnumMessageBusinessType businessType;
}

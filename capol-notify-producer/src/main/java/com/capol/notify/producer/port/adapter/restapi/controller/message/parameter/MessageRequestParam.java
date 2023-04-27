package com.capol.notify.producer.port.adapter.restapi.controller.message.parameter;

import com.capol.notify.sdk.EnumMessageBusinessType;
import com.capol.notify.sdk.EnumMessageType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel("普通消息传输对象")
@Data
@NoArgsConstructor
public class MessageRequestParam {
    @ApiModelProperty("消息类型(1-钉钉普通消息 2-钉钉群组消息 3-邮件消息)")
    @NotNull(message = "消息类型不允许为空->1.DING_NORMAL_MESSAGE:钉钉普通消息,2.DING_GROUP_MESSAGE:钉钉群组消息,3.EMAIL_MESSAGE:邮件消息")
    private EnumMessageType messageType;

    @ApiModelProperty("消息优先级")
    private Integer priority;

    @ApiModelProperty("消息业务类型")
    @NotNull(message = "消息业务类型不允许为空!")
    private EnumMessageBusinessType businessType;

    /**
     * 普通消息用户ID
     */
    @ApiModelProperty("接收者的用户userIds列表，最大列表长度100")
    private List<String> userIds;
    /**
     * 群会话ID
     */
    @ApiModelProperty("接收者的用户群会话ID")
    private String chatId;

    @ApiModelProperty("应用agentId")
    private Long agentId;

    @ApiModelProperty("消息内容")
    @NotNull(message = "消息内容不允许为空!")
    @NotBlank(message = "消息内容不允许为空!")
    private String content;
}

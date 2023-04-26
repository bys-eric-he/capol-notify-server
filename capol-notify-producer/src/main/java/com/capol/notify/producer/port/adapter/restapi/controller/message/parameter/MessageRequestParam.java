package com.capol.notify.producer.port.adapter.restapi.controller.message.parameter;

import com.capol.notify.sdk.EnumMessageBusinessType;
import com.capol.notify.sdk.EnumMessageType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel("消息传输对象")
@Data
@NoArgsConstructor
public class MessageRequestParam {

    @ApiModelProperty("消息类型(1-钉钉普通消息 2-钉钉群组消息 3-邮件消息)")
    private EnumMessageType messageType;

    @ApiModelProperty("消息优先级")
    private Integer priority;

    @ApiModelProperty("消息业务类型")
    private EnumMessageBusinessType businessType;

    @ApiModelProperty("接收者的用户userIds列表，最大列表长度100")
    private List<String> userIds;

    @ApiModelProperty("应用agentId")
    private Long agentId;

    @ApiModelProperty("消息内容")
    private String content;

}

package com.capol.notify.producer.port.adapter.restapi.controller.message;

import com.capol.notify.manage.application.ApplicationException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.producer.application.message.SendMessageService;
import com.capol.notify.producer.port.adapter.restapi.controller.message.parameter.MessageRequestParam;
import com.capol.notify.sdk.command.DingDingGroupMsgCommand;
import com.capol.notify.sdk.command.DingDingNormalMsgCommand;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/v1.0/service/message")
@Api(tags = "消息发送API")
public class MessageSendController {

    private SendMessageService applyForOTMessageService;

    public MessageSendController(SendMessageService applyForOTMessageService) {
        this.applyForOTMessageService = applyForOTMessageService;
    }

    @PostMapping("/send-request")
    @ApiOperation("消息发送请求")
    public void messageSendRequest(@RequestBody @Valid MessageRequestParam request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ApplicationException(bindingResult.getFieldError().getDefaultMessage(), EnumExceptionCode.BadRequest);
        }
        switch (request.getMessageType()) {
            case DING_NORMAL_MESSAGE: {
                if (CollectionUtils.isEmpty(request.getUserIds())) {
                    throw new ApplicationException(String.format("钉钉用户会话ID (UserIds)不允许为空,长度最长100位用户!"), EnumExceptionCode.BadRequest);
                }
                if (request.getAgentId() == null || request.getAgentId() == 0L) {
                    throw new ApplicationException(String.format("AgentID 不允许为空!"), EnumExceptionCode.BadRequest);
                }
                applyForOTMessageService.sendDingDingNormalMsg(new DingDingNormalMsgCommand(
                        request.getUserIds(),
                        request.getAgentId(),
                        request.getContent(),
                        request.getPriority(),
                        request.getMessageType(),
                        request.getBusinessType()));
                break;
            }
            case DING_GROUP_MESSAGE: {
                if (StringUtils.isEmpty(request.getChatId())) {
                    throw new ApplicationException(String.format("钉钉群组会话ID (ChatID) 不允许为空!"), EnumExceptionCode.BadRequest);
                }
                applyForOTMessageService.sendDingDingGroupMsg(new DingDingGroupMsgCommand(
                        request.getChatId(),
                        request.getContent(),
                        request.getPriority(),
                        request.getMessageType(),
                        request.getBusinessType()));
                break;
            }
            case EMAIL_MESSAGE: {
                break;
            }
        }
    }
}

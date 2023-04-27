package com.capol.notify.producer.application.message;

import com.capol.notify.manage.domain.model.IdGenerator;
import com.capol.notify.manage.domain.model.permission.CurrentUserService;
import com.capol.notify.producer.domain.model.message.MessageProducer;
import com.capol.notify.sdk.command.DingDingGroupMsgCommand;
import com.capol.notify.sdk.command.DingDingNormalMsgCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendMessageService {

    private MessageProducer messageProducer;
    private CurrentUserService currentUserService;

    public SendMessageService(MessageProducer messageProducer, CurrentUserService currentUserService) {
        this.messageProducer = messageProducer;
        this.currentUserService = currentUserService;
    }

    /**
     * 钉钉普通消息发送方法
     *
     * @param normalMsgCommand
     */
    public void sendDingDingNormalMsg(DingDingNormalMsgCommand normalMsgCommand) {
        if (normalMsgCommand.getUserId() == null) {
            Long userId = currentUserService.getCurrentUserId() != null ? Long.valueOf(currentUserService.getCurrentUserId()) : null;
            normalMsgCommand.setUserId(userId);
        }
        if (normalMsgCommand.getMessageId() == null) {
            normalMsgCommand.setMessageId(IdGenerator.generateId());
        }
        log.info("-->发送消息ID:{}", normalMsgCommand.getMessageId());
        messageProducer.sendDingDingNormalMsg(normalMsgCommand);
    }

    /**
     * 钉钉群组消息发送方法
     *
     * @param groupMsgCommand
     */
    public void sendDingDingGroupMsg(DingDingGroupMsgCommand groupMsgCommand) {
        if (groupMsgCommand.getUserId() == null) {
            Long userId = currentUserService.getCurrentUserId() != null ? Long.valueOf(currentUserService.getCurrentUserId()) : null;
            groupMsgCommand.setUserId(userId);
        }
        if (groupMsgCommand.getMessageId() == null) {
            groupMsgCommand.setMessageId(IdGenerator.generateId());
        }
        log.info("-->发送消息ID:{}", groupMsgCommand.getMessageId());
        messageProducer.sendDingDingGroupMsg(groupMsgCommand);
    }
}

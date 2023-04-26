package com.capol.notify.producer.domain.model.message;

import com.capol.notify.manage.application.queue.QueueService;
import com.capol.notify.manage.application.user.querystack.UserQueueDTO;
import com.capol.notify.manage.domain.model.message.MQMessageSave;
import com.capol.notify.sdk.command.DingDingNormalMsgCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 消息生产者
 */
@Slf4j
@Component
public class MessageProducer {

    private MessagePublisher messagePublisher;
    private QueueService queueService;

    public MessageProducer(MessagePublisher messagePublisher, QueueService queueService) {
        this.messagePublisher = messagePublisher;
        this.queueService = queueService;
    }

    @MQMessageSave(argsIndex = 0)
    public void sentApplyNoticeMsg(DingDingNormalMsgCommand dingMsgCommand) {
        UserQueueDTO userQueueDTO = queueService.getUserQueueByIdAndType(dingMsgCommand.getUserId(), dingMsgCommand.getBusinessType());
        if (userQueueDTO != null) {
            messagePublisher.messageSender(dingMsgCommand, userQueueDTO.getQueue(), dingMsgCommand.getPriority(), dingMsgCommand.getMessageId().toString());
        } else {
            log.error("消息发送失败, 指定的用户：{} 队列不存在!", dingMsgCommand.getUserId());
        }
    }
}

package com.capol.notify.consumer.domain.model.message;

import com.capol.notify.manage.application.message.AsyncMessageService;
import com.capol.notify.manage.domain.EnumProcessStatusType;
import com.capol.notify.sdk.MessageReceiveConfirmCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class MessageReceiveConfirmCallbackImpl extends MessageReceiveConfirmCallback {

    private AsyncMessageService asyncMessageService;

    public MessageReceiveConfirmCallbackImpl(AsyncMessageService asyncMessageService) {
        this.asyncMessageService = asyncMessageService;
    }

    /**
     * 消息消费成功回调方法
     *
     * @param ack            是否消费成功
     * @param messageId      消息MessageID
     * @param throwException 异常信息
     */
    @Override
    public void receiveConfirmCallback(boolean ack, Long messageId, Exception throwException) {
        log.info("-->消息消费成功回调方法!!ACK：{} , 消息ID：{}", ack, messageId);
        CompletableFuture<String> future = asyncMessageService.updateMessage(messageId, ack ? EnumProcessStatusType.SUCCESS : EnumProcessStatusType.FAILURE, throwException);
        // 异步任务完成后的回调
        future.thenAccept(result -> {
            log.info("--->异步任务完成后的回调方法完成!");
        });
    }
}

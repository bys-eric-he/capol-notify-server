package com.capol.notify.job.domain.model.message;

import com.capol.notify.sdk.MessageSendConfirmCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * JOB任务消息发送回调
 */
@Slf4j
@Service
public class JobMessageSendConfirmCallback extends MessageSendConfirmCallback {
    /**
     * 消息消发送成功回调方法
     *
     * @param ack 是否发送成功
     */
    @Override
    public void sendConfirmCallback(boolean ack) {
        log.info("-->Job重试任务消息消发送成功回调方法!!ACK：{}", ack);
    }
}

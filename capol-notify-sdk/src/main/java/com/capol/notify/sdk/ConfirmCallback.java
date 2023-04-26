package com.capol.notify.sdk;

import org.springframework.amqp.rabbit.connection.CorrelationData;

public interface ConfirmCallback {

    /**
     * 消息发送确认回调函数
     *
     * @param correlationData 关联数据
     * @param ack 确认结果
     * @param cause 失败原因（如果确认结果为 false）
     */
    void confirm(CorrelationData correlationData, boolean ack, String cause);
}
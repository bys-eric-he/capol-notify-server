package com.capol.notify.sdk;

/**
 * 消息消费回调
 */
public abstract class MessageReceiveConfirmCallback {
    /**
     * 消息消费成功回调方法
     *
     * @param ack            是否消费成功
     * @param messageId      消息ID
     * @param throwException 消息异常
     */
    public abstract void receiveConfirmCallback(boolean ack, Long messageId, Exception throwException);
}

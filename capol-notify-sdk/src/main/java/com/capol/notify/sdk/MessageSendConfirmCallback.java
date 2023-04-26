package com.capol.notify.sdk;

/**
 * 消息发送回调
 */
public abstract class MessageSendConfirmCallback {
    /**
     * 消息消发送成功回调方法
     *
     * @param ack 是否发送成功
     */
    public abstract void sendConfirmCallback(boolean ack);
}

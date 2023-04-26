package com.capol.notify.consumer.domain.model.message;

import com.capol.notify.manage.application.ApplicationException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.manage.domain.model.IdGenerator;
import com.capol.notify.manage.domain.model.message.MQMessageEdit;
import com.capol.notify.sdk.MessageReceiveConfirmCallback;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageReceiveListener {

    private RabbitTemplate rabbitTemplate;
    private RabbitProperties rabbitProperties;
    private MessageReceiveConfirmCallback messageReceiveConfirmCallback;

    public MessageReceiveListener(RabbitTemplate rabbitTemplate, RabbitProperties rabbitProperties,
                                  MessageReceiveConfirmCallback messageReceiveConfirmCallback) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitProperties = rabbitProperties;
        this.messageReceiveConfirmCallback = messageReceiveConfirmCallback;
    }

    /**
     * 指定队列创建监听
     *
     * @param queueName
     */
    public void startListening(String queueName) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(rabbitTemplate.getConnectionFactory());
        container.setQueueNames(queueName);
        // 设置当前消费者的数量
        container.setConcurrentConsumers(rabbitProperties.getListener().getSimple().getConcurrency());
        // 设置最大的消费者的数量
        container.setMaxConcurrentConsumers(rabbitProperties.getListener().getSimple().getMaxConcurrency());
        // 设置每次从队列中拿取的消息数量
        container.setPrefetchCount(rabbitProperties.getListener().getSimple().getPrefetch());
        // 设置 SimpleMessageListenerContainer 是否自动启动
        container.setAutoStartup(rabbitProperties.getListener().getSimple().isAutoStartup());
        // 是否重回队列，一般都不允许重回队里
        container.setDefaultRequeueRejected(rabbitProperties.getListener().getSimple().getDefaultRequeueRejected());
        // 可以设置签收模式, 比如设置为 自动签收
        container.setAcknowledgeMode(rabbitProperties.getListener().getSimple().getAcknowledgeMode());
        // 设置消费端的标签，生成策略，自定义消费端的标签生成策略
        // 标签 可以标识这个消息的唯一性
        container.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue + "_" + IdGenerator.generateId();
            }
        });
        // 监听消息
        container.setMessageListener(new ChannelAwareMessageListener() {
            /**
             * 如果有消息传递过来，就会进入这个 onMessage 方法
             * @param message 消息
             * @param channel 消息管道
             * @throws Exception
             */
            @MQMessageEdit(argsIndex = 0)
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                String msg = new String(message.getBody(), "UTF-8");
                String messageId = message.getMessageProperties().getMessageId();
                try {
                    log.info("消费者:{} 接收到消息：{}", message.getMessageProperties().getConsumerTag(), msg);
                    log.info("消息优先级为：" + message.getMessageProperties().getPriority());
                    log.info("消息交换机为：" + message.getMessageProperties().getReceivedExchange());
                    log.info("消息路由为：" + message.getMessageProperties().getReceivedRoutingKey());
                    log.info("消息队列为:{}", message.getMessageProperties().getConsumerQueue());
                    log.info("消费者集群ID：{}", message.getMessageProperties().getClusterId());
                    log.info("消息ID为：" + message.getMessageProperties().getMessageId());

                    // .....处理发送钉钉消息的逻辑
                    if (messageReceiveConfirmCallback != null) {
                        messageReceiveConfirmCallback.receiveConfirmCallback(true, Long.valueOf(messageId), null);
                    }
                } catch (Exception exception) {
                    log.error("-->消处理异常：{}", exception);
                    if (messageReceiveConfirmCallback != null) {
                        messageReceiveConfirmCallback.receiveConfirmCallback(false, Long.valueOf(messageId), exception);
                    }
                } finally {
                    //手动ACK处理消息
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 确认消息已经消费
                }
            }
        });
        container.start();
        log.info("-->MessageReceiveListener创建队列:{}监听成功!", queueName);
    }
}

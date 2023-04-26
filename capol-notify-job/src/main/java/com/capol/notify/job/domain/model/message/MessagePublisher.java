package com.capol.notify.job.domain.model.message;


import com.capol.notify.sdk.MessageSendConfirmCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Component;

/**
 * 消息发送者
 */
@Slf4j
@Component
public class MessagePublisher {

    private RabbitTemplate rabbitTemplate;
    private MessageSendConfirmCallback messageSendConfirmCallback;

    public MessagePublisher(RabbitTemplate rabbitTemplate, MessageSendConfirmCallback messageSendConfirmCallback) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageSendConfirmCallback = messageSendConfirmCallback;
    }

    /**
     * 实现一个 RabbitTemplate.ConfirmCallback 接口，并重写其中的 confirm 方法。
     * 当消息发送成功时，输出消息发送成功的信息，当消息发送失败时，输出消息发送失败的信息和失败原因。
     * 注意，在使用 RabbitTemplate.ConfirmCallback 接口时，需要通过 RabbitTemplate.setConfirmCallback 方法将确认回调函数设置到 RabbitTemplate 实例中
     */
    RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            log.info("-->【RabbitMQ】消息发送到Exchange后回调触发!");
            if (correlationData != null) {
                ReturnedMessage returnedMessage = correlationData.getReturned();
                if (returnedMessage != null) {
                    log.info("-->【RabbitMQ】消息发送回调返回的内容:[{}]", returnedMessage.getMessage());
                }
            }
            if (ack) {
                log.info("-->【RabbitMQ】消息发送成功, 整个消息投递的过程：producer→RabbitMQ broker → exchange → queue → consumer !!");
            } else {
                log.error("-->【RabbitMQ】消息发送失败, 数据未到达交换机, 原因:{}", cause);
            }

            //业务类的回调实现
            if (messageSendConfirmCallback != null) {
                messageSendConfirmCallback.sendConfirmCallback(ack);
            }
        }
    };

    /**
     * 当消息无法被路由到队列时，输出退回的消息、退回原因、交换机名称和路由键等信息。
     */
    RabbitTemplate.ReturnsCallback returnsCallback = new RabbitTemplate.ReturnsCallback() {
        @Override
        public void returnedMessage(ReturnedMessage returnedMessage) {
            String message = new String(returnedMessage.getMessage().getBody());
            log.error("-->【RabbitMQ】消息入路由队列失败!");
            log.error("消息退回：" + message);
            log.error("退回原因：" + returnedMessage.getReplyCode() + " " + returnedMessage.getReplyText());
            log.error("交换机名称：" + returnedMessage.getExchange());
            log.error("路由键：" + returnedMessage.getRoutingKey());
        }
    };

    /**
     * 消息发送
     *
     * @param content  发送的消息内容
     * @param exchange 发送消息的交换机
     * @param routing  发送消息的路由
     * @param priority 消息优先级
     * @param messageId 消息ID
     */
    public void messageSender(Object content, String exchange, String routing, Integer priority, String messageId) {
        //开启confirm机制，当消息发送到Exchange后回调confirm方法，在方法中判断ack，如果为true，则发送成功。如果为false，则发送失败
        rabbitTemplate.setConfirmCallback(confirmCallback);

        //开启returns机制，当消息由Exchange路由到queue失败后触发
        rabbitTemplate.setReturnsCallback(returnsCallback);

        //当mandatory标志位设置为true时，如果exchange根据自身类型和消息routingKey无法找到一个合适的queue存储消息，
        //那么broker会调用basic.return方法将消息返还给生产者
        rabbitTemplate.setMandatory(true);

        //如果配置了Jackson2JsonMessageConverter会将发送的对象转化成json字符串之后再转成字节数组；
        //如果未配置会使用JDK的序列化方式来序列化成字节数组。
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        //发送消息
        rabbitTemplate.convertAndSend(exchange, routing, content, message -> {
            // 设置消息的优先级
            message.getMessageProperties().setPriority(priority);
            // 设置消息ID
            message.getMessageProperties().setMessageId(messageId);
            //消息持久化
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });

        log.info("-->【RabbitMQ】消息发送成功! 交换机:{}, 路由:{}, 消息内容:{}", exchange, routing, content);
    }

    /**
     * 消息发送
     *
     * @param content   发送的消息内容
     * @param queue     发送消息的队列
     * @param priority  消息优先级
     * @param messageId 消息ID
     */
    public void messageSender(Object content, String queue, Integer priority, String messageId) {
        //开启confirm机制，当消息发送到Exchange后回调confirm方法，在方法中判断ack，如果为true，则发送成功。如果为false，则发送失败
        rabbitTemplate.setConfirmCallback(confirmCallback);

        //开启returns机制，当消息由Exchange路由到queue失败后触发
        rabbitTemplate.setReturnsCallback(returnsCallback);

        //当mandatory标志位设置为true时，如果exchange根据自身类型和消息routingKey无法找到一个合适的queue存储消息，
        //那么broker会调用basic.return方法将消息返还给生产者
        rabbitTemplate.setMandatory(true);

        //如果配置了Jackson2JsonMessageConverter会将发送的对象转化成json字符串之后再转成字节数组；
        //如果未配置会使用JDK的序列化方式来序列化成字节数组。
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        MessagePostProcessor messagePostProcessor = message -> {
            // 设置消息的优先级
            message.getMessageProperties().setPriority(priority);
            // 设置消息ID
            message.getMessageProperties().setMessageId(messageId);
            // 消息持久化
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        };

        //发送消息,直接将消息发送到具体的队列中，将 exchange 参数设置为空字符串。
        rabbitTemplate.convertAndSend("", queue, content, messagePostProcessor);

        log.info("-->【RabbitMQ】消息发送成功! 队列:{}, 消息内容:{}", queue, content);
    }
}

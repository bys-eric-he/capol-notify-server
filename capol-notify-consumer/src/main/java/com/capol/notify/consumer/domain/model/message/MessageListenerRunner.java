package com.capol.notify.consumer.domain.model.message;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.capol.notify.manage.application.ApplicationException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.manage.domain.EnumStatusType;
import com.capol.notify.manage.domain.model.queue.UserQueueDO;
import com.capol.notify.manage.domain.repository.UserQueueMapper;
import com.capol.notify.sdk.MessageReceiveConfirmCallback;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 统一监听入口
 */
@Slf4j
@Component
@Order(1)
public class MessageListenerRunner implements CommandLineRunner {

    private RabbitTemplate rabbitTemplate;
    private final UserQueueMapper userQueueMapper;
    private MessageReceiveListener messageReceiveListener;
    private RabbitProperties rabbitProperties;
    private MessageReceiveConfirmCallback messageReceiveConfirmCallback;

    public MessageListenerRunner(RabbitTemplate rabbitTemplate,
                                 UserQueueMapper userQueueMapper,
                                 MessageReceiveListener messageReceiveListener,
                                 RabbitProperties rabbitProperties,
                                 MessageReceiveConfirmCallback messageReceiveConfirmCallback) {
        this.rabbitTemplate = rabbitTemplate;
        this.userQueueMapper = userQueueMapper;
        this.messageReceiveListener = messageReceiveListener;
        this.rabbitProperties = rabbitProperties;
        this.messageReceiveConfirmCallback = messageReceiveConfirmCallback;
    }

    @Override
    public void run(String... args) throws Exception {
        LambdaQueryWrapper<UserQueueDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserQueueDO::getDisabled, false);
        queryWrapper.eq(UserQueueDO::getStatus, EnumStatusType.NORMAL.getCode());
        List<UserQueueDO> userQueueDOS = userQueueMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(userQueueDOS)) {
            log.warn("-->当前服务无队列配置信息!!!");
            return;
        }

        log.info("-->开始创建消息队列监听!!!");
        log.info("-->消息服务器:{} ,端口号：{} ,虚拟主机:{}", rabbitProperties.getHost(), rabbitProperties.getPort(), rabbitProperties.getVirtualHost());
        final ConnectionFactory factory = rabbitTemplate.getConnectionFactory();
        final Connection connection = factory.createConnection();

        // 创建频道
        Channel channel = connection.createChannel(false);

        //创建消费者, 并设置消息处理
        MessageConsumer messageConsumer = new MessageConsumer(messageReceiveConfirmCallback);
        messageConsumer.setChannel(channel);
        //监听消息
        for (UserQueueDO queue : userQueueDOS) {
            /**
             * 参数1：队列名称
             * 参数2：是否自动确认，设置为true为表示消息接收到自动向mq回复接收到了，mq接收到回复会删除消息，设置为false则需要手动确认
             * 参数3：消息接收到后回调
             */
            try {
                //channel.basicConsume(queue.getQueue(), false, messageConsumer);
                messageReceiveListener.startListening(queue.getQueue());
                log.info("-->创建监听队列:{},并设置消息接收入口!", queue.getQueue());
            } catch (Exception exception) {
                log.error("-->创建监听队列:" + queue.getQueue() + "异常! 详细内容：" + exception);
                throw new ApplicationException(String.format("创建队列：%s 监听异常!", queue.getQueue()), EnumExceptionCode.InternalServerError);
            }
        }
    }
}

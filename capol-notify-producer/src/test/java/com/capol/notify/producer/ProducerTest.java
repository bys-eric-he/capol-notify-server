package com.capol.notify.producer;

import com.capol.notify.manage.domain.model.IdGenerator;
import com.capol.notify.producer.domain.model.message.MessagePublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProducerTest {

    private static final String EXCHANGE = "capol_notify_exchange";
    private static final String ROUTING = "capol_notify_routing_apply_for_leave";
    private static final String QUEUE = "capol_notify_queue_apply_for_leave";

    @Autowired
    private MessagePublisher messagePublisher;

    @Test
    public void sendMessage() {
        String message = "-------这是来自Capol-Notify-Server的测试消息!!进队列capol_notify_queue_4!!------------";
        messagePublisher.messageSender(message, EXCHANGE, ROUTING, 2, IdGenerator.generateId().toString());
        message = "*****这是来自Capol-Notify-Server的测试消息!!而且这是直接发送到队列capol_notify_queue_1的消息，没有经过交换机!!*****";
        messagePublisher.messageSender(message, QUEUE, 3, IdGenerator.generateId().toString());
    }
}

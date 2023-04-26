package com.capol.notify.job;

import com.capol.notify.job.domain.model.message.MessagePublisher;
import com.capol.notify.job.application.UserQueueMessageDeleteJobHandler;
import com.capol.notify.job.application.UserQueueMessageRetryJobHandler;
import com.capol.notify.manage.application.message.MessageService;
import com.capol.notify.manage.application.queue.QueueService;
import com.xxl.job.core.handler.IJobHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JobHandlerTest {

    private IJobHandler iJobHandler;

    @Autowired
    private MessageService messageService;
    @Autowired
    private QueueService queueService;
    @Autowired
    private MessagePublisher messagePublisher;

    @Test
    public void sendMessage() {
        try {
            iJobHandler = new UserQueueMessageRetryJobHandler(messageService, queueService, messagePublisher);
            iJobHandler.execute();
        } catch (Exception exception) {

        }
    }

    @Test
    public void deleteMessage() {
        try {
            iJobHandler = new UserQueueMessageDeleteJobHandler(messageService);
            iJobHandler.execute();
        } catch (Exception exception) {

        }
    }
}

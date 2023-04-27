package com.capol.notify.job;

import com.capol.notify.job.application.UserQueueMessageDeleteJobHandler;
import com.capol.notify.job.application.UserQueueMessageRetryJobHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JobHandlerTest {

    @Autowired
    private UserQueueMessageDeleteJobHandler userQueueMessageDeleteJobHandler;

    @Autowired
    private UserQueueMessageRetryJobHandler userQueueMessageRetryJobHandler;

    @Test
    public void sendMessage() {
        try {
            userQueueMessageRetryJobHandler.checkRetryMessageJob();
        } catch (Exception exception) {

        }
    }

    @Test
    public void deleteMessage() {
        try {
            userQueueMessageDeleteJobHandler.checkDeleteMessageJob();
        } catch (Exception exception) {

        }
    }
}

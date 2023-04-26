package com.capol.notify.job.application;

import com.capol.notify.manage.application.message.MessageService;
import com.capol.notify.manage.domain.model.message.UserQueueMessageDO;
import com.xxl.job.core.handler.IJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Component
public class UserQueueMessageDeleteJobHandler extends IJobHandler {
    private MessageService messageService;

    public UserQueueMessageDeleteJobHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void execute() throws Exception {
        //开始时间为3天之前的
        LocalDateTime startDateTime = LocalDateTime.now().minusDays(3);
        //结束时间为当前时间1天之前的
        LocalDateTime endDateTime = LocalDateTime.now().minusDays(1);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startTime = dateTimeFormatter.format(startDateTime);
        String endTime = dateTimeFormatter.format(endDateTime);
        log.info("-->消息<删除处理器>进来了,开始时间:{} 结束时间:{}!", startTime, endTime);

        List<UserQueueMessageDO> userQueueMessageDOS = messageService.getMessageByDateTime(startTime, endTime);

        if (CollectionUtils.isEmpty(userQueueMessageDOS)) {
            log.info("-->暂无符合<删除>条件的消息记录! 查询时间区间, 开始时间:{} 结束时间:{}", startTime, endTime);
            return;
        }

        List<Long> ids = userQueueMessageDOS.stream().map(UserQueueMessageDO::getId).collect(Collectors.toList());
        messageService.deleteMessageByIds(ids);
    }
}

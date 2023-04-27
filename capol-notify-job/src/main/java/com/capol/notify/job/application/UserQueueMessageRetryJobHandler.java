package com.capol.notify.job.application;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.capol.notify.job.domain.model.message.MessagePublisher;
import com.capol.notify.manage.application.message.MessageService;
import com.capol.notify.manage.application.queue.QueueService;
import com.capol.notify.manage.application.user.querystack.UserQueueDTO;
import com.capol.notify.manage.domain.EnumProcessStatusType;
import com.capol.notify.manage.domain.PageParam;
import com.capol.notify.manage.domain.PageResult;
import com.capol.notify.manage.domain.model.message.UserQueueMessageDO;
import com.capol.notify.sdk.EnumMessageBusinessType;
import com.capol.notify.sdk.EnumMessageType;
import com.capol.notify.sdk.command.DingDingGroupMsgCommand;
import com.capol.notify.sdk.command.DingDingNormalMsgCommand;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 消息监控处理器
 */
@Slf4j
@Component
public class UserQueueMessageRetryJobHandler {
    /**
     * 要监控的消息类型
     */
    List<EnumMessageType> messageTypes = Arrays.asList(
            EnumMessageType.DING_NORMAL_MESSAGE,
            EnumMessageType.DING_GROUP_MESSAGE,
            EnumMessageType.EMAIL_MESSAGE);

    /**
     * 要监控的消息处理状态
     */
    List<EnumProcessStatusType> processStatusTypes = Arrays.asList(
            EnumProcessStatusType.FAILURE,
            EnumProcessStatusType.WAIT_TODO
    );

    private MessageService messageService;
    private QueueService queueService;
    private MessagePublisher messagePublisher;

    public UserQueueMessageRetryJobHandler(MessageService messageService,
                                           QueueService queueService,
                                           MessagePublisher messagePublisher) {
        this.messageService = messageService;
        this.queueService = queueService;
        this.messagePublisher = messagePublisher;
    }

    public void retryInit() {
        log.info("-->定时任务初始化, 用途:定时检测需要<重发>的消息!");
    }

    public void retryDestroy() {
        log.info("-->定时任务销毁, 用途:定时检测需要<重发>的消息!");
    }

    @XxlJob(value = "userQueueMessageRetryJobHandler", init = "retryInit", destroy = "retryDestroy")
    public ReturnT<String> checkRetryMessageJob() throws Exception {
        try {
            //开始时间为3天之前的
            LocalDateTime startDateTime = LocalDateTime.now().minusDays(3);
            //结束时间为当前时间前30分钟的
            LocalDateTime endDateTime = LocalDateTime.now().minusMinutes(30);

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String startTime = dateTimeFormatter.format(startDateTime);
            String endTime = dateTimeFormatter.format(endDateTime);
            log.info("-->消息<重发处理器>进来了,开始时间:{} 结束时间:{}!", startTime, endTime);

            Long totalCount = messageService.getTotalCountByParam(processStatusTypes, messageTypes, startTime, endTime);

            if (totalCount <= 0) {
                log.info("-->暂无符合<重发>条件的消息记录! 查询时间区间, 开始时间:{} 结束时间:{}", startTime, endTime);
                return ReturnT.SUCCESS;
            }

            PageParam pageParam = new PageParam();
            pageParam.setPageSize(100);
            pageParam.setTotalCount(totalCount.intValue());

            //钉钉普通消息
            DingDingNormalMsgCommand dingNormalMsgCommand = null;
            DingDingGroupMsgCommand dingDingGroupMsgCommand = null;
            while (pageParam.getPageNo() <= pageParam.getTotalPageNumber()) {
                PageResult<UserQueueMessageDO> messageDOPageResult = messageService.getMessageByPage(processStatusTypes, messageTypes, startTime, endTime, pageParam);
                List<UserQueueMessageDO> records = messageDOPageResult.getList();
                for (UserQueueMessageDO messageDO : records) {
                    UserQueueDTO userQueueDTO = queueService.getUserQueueByIdAndType(messageDO.getUserId(), EnumMessageBusinessType.ofValue(messageDO.getBusinessType()));
                    if (userQueueDTO == null) {
                        log.error("-->定时任务重发消息异常, 消息业务类型:{} 消息ID:{} 消息所属业务系统:{} 消息发送队列不存在或已禁用!",
                                messageDO.getBusinessType(),
                                messageDO.getId(),
                                messageDO.getServiceId());
                        continue;
                    }
                    switch (EnumMessageType.ofValue(messageDO.getMessageType())) {
                        case DING_NORMAL_MESSAGE: {
                            dingNormalMsgCommand = JSON.parseObject(messageDO.getContent(), new TypeReference<DingDingNormalMsgCommand>() {
                            });
                            //处理失败
                            if (EnumProcessStatusType.FAILURE.getCode().equals(messageDO.getProcessStatus())) {
                                log.info("-->定时任务重发<处理失败的消息>, 消息业务类型:{} 消息ID:{} 消息所属业务系统:{} 消息发送队列:{}",
                                        messageDO.getBusinessType(),
                                        messageDO.getId(),
                                        messageDO.getServiceId(),
                                        userQueueDTO.getQueue());
                            }
                            if (EnumProcessStatusType.WAIT_TODO.getCode().equals(messageDO.getProcessStatus())) {
                                log.info("-->定时任务重发<等待处理的消息>, 消息业务类型:{} 消息ID:{} 消息所属业务系统:{} 消息发送队列:{}",
                                        messageDO.getBusinessType(),
                                        messageDO.getId(),
                                        messageDO.getServiceId(),
                                        userQueueDTO.getQueue());
                            }
                            //重发的消息优先级默认为10
                            messagePublisher.messageSender(dingNormalMsgCommand, userQueueDTO.getQueue(), 10, String.valueOf(messageDO.getId()));
                            break;
                        }
                        case DING_GROUP_MESSAGE: {
                            break;
                        }
                        case EMAIL_MESSAGE: {
                            break;
                        }
                    }
                }

                //处完一页数据后，页码+1
                pageParam.setPageNo(pageParam.getPageNo() + 1);
            }
            log.info("-->消息重发监控Job任务处理器执行成功!!!!");
            return ReturnT.SUCCESS;
        } catch (Exception exception) {
            log.error("-->消息重发监控Job任务处理器执行异常,异常详情:{}", exception);
            return ReturnT.FAIL;
        }
    }
}

package com.capol.notify.manage.application.message;

import cn.hutool.core.date.DateUtil;
import com.capol.notify.manage.domain.EnumProcessStatusType;
import com.capol.notify.manage.domain.model.message.UserQueueMessageDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class AsyncMessageService {

    @Value("${capol.notify.retry.max-count}")
    private Long retryMaxCount;

    private MessageService messageService;

    public AsyncMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    // 声明一个线程池，用于执行异步任务
    @Async("Capol-Notify-ThreadPool")
    public CompletableFuture<String> updateMessage(Long messageId,
                                                   EnumProcessStatusType statusType,
                                                   Exception throwException) {
        try {
            //这里之所以要睡眠2秒，是因为发送者发送完消息后，发送端的AOP切面保存消息的事务还没有提交，然后消费端就已经收到消息了，会导致查询不到消息的情况。
            Thread.sleep(2000);
        } catch (Exception exception) {
            log.error("-->线程睡眠异常! 异常信息：", exception);
        }
        // 异步执行的逻辑
        UserQueueMessageDO queueMessageDO = messageService.getMessageById(messageId);
        if (queueMessageDO != null) {
            queueMessageDO.buildBaseInfo();
            queueMessageDO.setConsumerStartTime(DateUtil.dateSecond());
            String oldSendResponse = queueMessageDO.getSendResponse();
            if (EnumProcessStatusType.FAILURE == statusType) {
                String sendResponse = null;
                if (isReachedRetryMaxCount(queueMessageDO)) {
                    sendResponse = "消费失败, 原因：" + throwException.getMessage() + ", 已经达到最大重试次数,后续不再处理! 最后一次处理时间: " + DateUtil.dateSecond();
                } else {
                    if (throwException != null) {
                        sendResponse = "消费失败, 失败次数: " + Integer.valueOf(queueMessageDO.getRetryCount() + 1) + ", 原因：" + throwException.getMessage() + "! 最后一次处理时间: " + DateUtil.dateSecond();
                    } else {
                        sendResponse = "消费失败, 失败次数: " + Integer.valueOf(queueMessageDO.getRetryCount() + 1) + ", 本次没有抛出任何异常详情! 最后一次处理时间: " + DateUtil.dateSecond();
                    }
                    // 消费成功后, 需要判断该消息是否是处于失败状态,重新发送处理的消息,如果是将消息重试次数+1
                    // 如果不是, 则说明是待处理的状态,说明是第一次消费失败,这时不需要对重试次数+1
                    if (EnumProcessStatusType.FAILURE.getCode().equals(queueMessageDO.getProcessStatus())) {
                        queueMessageDO.setRetryCountIncrease();
                    }
                }
                queueMessageDO.setSendResponse(ObjectUtils.isNotEmpty(oldSendResponse) ? oldSendResponse.concat("->").concat(sendResponse) : sendResponse);
            } else if (EnumProcessStatusType.SUCCESS == statusType) {
                String sendResponse = "消费成功, 处理时间=" + DateUtil.dateSecond();
                queueMessageDO.setSendResponse(ObjectUtils.isNotEmpty(oldSendResponse) ? oldSendResponse.concat("->").concat(sendResponse) : sendResponse);
                //消费成功后, 需要判断该消息是否是处于失败状态,重新发送处理的消息,如果是则消费成功后,除了要将状态置为成功之外,还要将消息重试次数+1
                if (EnumProcessStatusType.FAILURE.getCode().equals(queueMessageDO.getProcessStatus())) {
                    queueMessageDO.setRetryCountIncrease();
                }
            }
            queueMessageDO.setConsumerEndTime(DateUtil.dateSecond());
            queueMessageDO.setProcessStatus(statusType.getCode());
            messageService.saveOrUpdateMessage(queueMessageDO);
            log.info("-->消息：{} 持久化更新处理成功！", messageId);
        } else {
            log.warn("-->消息：{} 不存在, 未进行任何处理！", messageId);
        }
        return CompletableFuture.completedFuture("-->异步方法更新消息完成 finished!!!");
    }

    private boolean isReachedRetryMaxCount(UserQueueMessageDO userQueueMessageDO) {
        if (userQueueMessageDO != null) {
            if (userQueueMessageDO.getRetryCount() >= retryMaxCount) {
                log.info("-->消息已经达到最大重试次数:{}, 当前消息已经重试的次数:{}", retryMaxCount, userQueueMessageDO.getRetryCount());
                return true;
            } else {
                log.info("-->消息当前重试的次数:{}, 最大重试次数:{}", userQueueMessageDO.getRetryCount(), retryMaxCount);
                return false;
            }
        }
        return false;
    }
}

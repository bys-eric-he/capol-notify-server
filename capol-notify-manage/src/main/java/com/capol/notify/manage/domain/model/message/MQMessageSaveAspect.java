package com.capol.notify.manage.domain.model.message;

import com.alibaba.fastjson.JSON;
import com.capol.notify.manage.application.message.MessageService;
import com.capol.notify.manage.application.queue.QueueService;
import com.capol.notify.manage.application.user.UserService;
import com.capol.notify.manage.application.user.querystack.UserDTO;
import com.capol.notify.manage.application.user.querystack.UserQueueDTO;
import com.capol.notify.manage.domain.*;
import com.capol.notify.sdk.EnumMessageBusinessType;
import com.capol.notify.sdk.EnumMessageType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 保存消息的切面
 */
@Slf4j
@Component
@Aspect
public class MQMessageSaveAspect {
    private MessageService messageService;
    private UserService userService;
    private QueueService queueService;

    public MQMessageSaveAspect(MessageService messageService, UserService userService, QueueService queueService) {
        this.messageService = messageService;
        this.queueService = queueService;
        this.userService = userService;
    }

    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.capol.notify.manage.domain.model.message.MQMessageSave)")
    private void pointCut() {

    }

    /**
     * 返回通知（After Returning Advice）：在目标方法执行后调用，只有在目标方法正常返回时才会调用。
     *
     * @param joinPoint
     * @param mqMessageSave
     * @param rvt
     */
    @AfterReturning(value = "pointCut() && @annotation(mqMessageSave)", returning = "rvt")
    public void afterReturning(JoinPoint joinPoint, MQMessageSave mqMessageSave, Object rvt) {
        log.info("-->消息发送成功切面进入执行!");
        Object message = joinPoint.getArgs()[mqMessageSave.argsIndex()];
        Object messageId = ReflectionUtils.getFieldValue(message, "messageId");
        Object userId = ReflectionUtils.getFieldValue(message, "userId");
        Object messageType = ReflectionUtils.getFieldValue(message, "messageType");
        Object businessType = ReflectionUtils.getFieldValue(message, "businessType");
        Object priority = ReflectionUtils.getFieldValue(message, "priority");

        this.processMessageContent(
                Long.valueOf(String.valueOf(messageId)),
                Integer.valueOf(String.valueOf(priority)),
                String.valueOf(userId),
                EnumMessageType.ofValue(String.valueOf(messageType)),
                EnumMessageBusinessType.ofValue(String.valueOf(businessType)),
                EnumProcessStatusType.WAIT_TODO, message);
        log.info("-->消息发送成功切面执行完成!");
    }

    /**
     * 异常通知（After Throwing Advice）：在目标方法抛出异常时调用。
     *
     * @param joinPoint
     * @param mqMessageSave
     * @param exception
     */
    @AfterThrowing(value = "pointCut() && @annotation(mqMessageSave)", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, MQMessageSave mqMessageSave, Throwable exception) {
        log.error("-->消息发送异常切面进入执行!");
        log.error("-->异常原因：" + exception);
        Object message = joinPoint.getArgs()[mqMessageSave.argsIndex()];
        Object messageId = ReflectionUtils.getFieldValue(message, "messageId");
        Object userId = ReflectionUtils.getFieldValue(message, "userId");
        Object messageType = ReflectionUtils.getFieldValue(message, "messageType");
        Object businessType = ReflectionUtils.getFieldValue(message, "businessType");
        Object priority = ReflectionUtils.getFieldValue(message, "priority");

        this.processMessageContent(
                Long.valueOf(String.valueOf(messageId)),
                Integer.valueOf(String.valueOf(priority)),
                String.valueOf(userId),
                EnumMessageType.ofValue(String.valueOf(messageType)),
                EnumMessageBusinessType.ofValue(String.valueOf(businessType)),
                EnumProcessStatusType.FAILURE, message);

        log.error("-->消息发送异常切面执行完成!");
    }

    private void processMessageContent(Long messageId, Integer priority, String userId, EnumMessageType messageType, EnumMessageBusinessType messageBusinessType, EnumProcessStatusType processStatusType, Object message) {
        UserQueueMessageDO userQueueMessageDO = messageService.getMessageById(messageId);

        UserDTO userDTO = userService.userBaseInfo(userId);
        if (userDTO == null) {
            log.error("切面记录消息失败, 指定的用户：{}不存在!", userId);
            throw new DomainException(String.format("切面记录消息失败, 指定的用户：%s不存在!", userId), EnumExceptionCode.InternalServerError);
        }

        UserQueueDTO userQueueDTO = queueService.getUserQueueByIdAndType(Long.valueOf(userId), messageBusinessType);
        if (userQueueDTO == null) {
            log.error("切面记录消息失败, 指定的用户：{} 业务类型：{}的队列不存在!", userId, messageBusinessType);
            throw new DomainException(String.format("切面记录消息失败, 指定的用户：%s 业务类型：%s 的队列不存在!", userId, messageBusinessType), EnumExceptionCode.InternalServerError);
        }

        if (userQueueMessageDO == null) {
            userQueueMessageDO = new UserQueueMessageDO();
            userQueueMessageDO.buildBaseInfo();
            userQueueMessageDO.setId(messageId);
            userQueueMessageDO.setServiceId(userDTO.getServiceId());
            userQueueMessageDO.setUserId(Long.valueOf(userId));
            userQueueMessageDO.setQueueId(userQueueDTO.getQueueId());
            userQueueMessageDO.setPriority(priority);
            userQueueMessageDO.setMessageType(messageType.getCode());
            userQueueMessageDO.setBusinessType(messageBusinessType.getCode());
            userQueueMessageDO.setRetryCount(0);
        } else {
            userQueueMessageDO.buildBaseInfo();
        }
        userQueueMessageDO.setContent(JSON.toJSONString(message));
        userQueueMessageDO.setProcessStatus(processStatusType.getCode());
        if (EnumProcessStatusType.FAILURE.getCode().equals(userQueueMessageDO.getProcessStatus())) {
            userQueueMessageDO.setRetryCountIncrease();
        }
        messageService.saveOrUpdateMessage(userQueueMessageDO);
        log.info("-->持久化消息成功! 消息ID:{}", userQueueMessageDO.getId());
    }
}

package com.capol.notify.manage.domain.model.message;

import com.baomidou.mybatisplus.annotation.TableName;
import com.capol.notify.manage.domain.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 业务系统用户消息队列
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user_queue_message")
@NoArgsConstructor
public class UserQueueMessageDO extends BaseEntity {
    /**
     * 消息所属业务系统ID
     */
    private String serviceId;

    /**
     * 业务系统用户ID
     */
    private Long userId;

    /**
     * 消息所属队列ID
     */
    private Long queueId;

    /**
     * 消息优先级
     */
    private Integer priority;

    /**
     * 消息类型(1-钉钉普通消息 2-钉钉群组消息 3-邮件消息)
     */
    private String messageType;

    /**
     * 消息业务类型
     */
    private String businessType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息发送响应内容
     */
    private String sendResponse;

    /**
     * 消息处理状态(0-待处理 1-处理成功 2-处理失败)
     */
    private Integer processStatus;

    /**
     * 消息处理失败重试次数
     */
    private Integer retryCount;

    /**
     * 消费端开始消费时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date consumerStartTime;

    /**
     * 消费端消费结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date consumerEndTime;

    public UserQueueMessageDO(String serviceId, Long userId, Long queueId, Integer priority, String messageType, String businessType, String content,
                              String sendResponse, Integer processStatus, Integer retryCount) {
        Validate.notNull(serviceId, "ServiceId must be provided.");
        Validate.notNull(queueId, "UserId must not be null.");
        Validate.notNull(queueId, "QueueId must not be null.");
        Validate.notNull(messageType, "MessageType must not be null.");
        Validate.notNull(businessType, "Business type must not be null.");
        Validate.notBlank(content, "Content must not be null.");
        this.serviceId = serviceId;
        this.userId = userId;
        this.queueId = queueId;
        this.priority = priority;
        this.messageType = messageType;
        this.businessType = businessType;
        this.content = content;
        this.sendResponse = sendResponse;
        this.processStatus = processStatus;
        this.retryCount = retryCount;
    }

    /**
     * 设置重试次数自动+1
     */
    public void setRetryCountIncrease() {
        if (this.retryCount != null) {
            this.retryCount++;
        } else {
            this.retryCount = 1;
        }
    }
}

package com.capol.notify.manage.domain.model.queue;


import com.baomidou.mybatisplus.annotation.TableName;
import com.capol.notify.manage.domain.model.BaseEntity;
import com.capol.notify.manage.domain.model.user.UserId;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Validate;

/**
 * 业务系统用户消息队列
 */
@Getter
@EqualsAndHashCode(callSuper = false)
@TableName("t_user_queue")
@AllArgsConstructor
@NoArgsConstructor
public class UserQueueDO extends BaseEntity {
    /**
     * 业务系统ID
     */
    private UserId userId;

    /**
     * 交换机名称
     */
    private String exchange;

    /**
     * 路由名称
     */
    private String routing;

    /**
     * 队列名称
     */
    private String queue;

    /**
     * 队列优先级
     */
    private Integer priority;

    /**
     * 队列业务类型
     */
    private String businessType;

    /**
     * 是否禁用
     */
    private Boolean disabled;

    public UserQueueDO(UserId userId, String exchange, String routing, String queue, Integer priority, String businessType) {
        Validate.notNull(userId, "UserID must be provided.");
        Validate.notBlank(exchange, "Exchange must not be null.");
        Validate.notBlank(exchange, "Routing must not be null.");
        Validate.notBlank(exchange, "Queue must not be null.");
        this.userId = userId;
        this.exchange = exchange;
        this.disabled = false;
        this.routing = routing;
        this.queue = queue;
        this.priority = priority;
        this.businessType = businessType;
    }
}
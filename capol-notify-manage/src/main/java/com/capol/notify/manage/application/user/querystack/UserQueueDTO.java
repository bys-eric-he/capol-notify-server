package com.capol.notify.manage.application.user.querystack;

import com.capol.notify.manage.domain.model.user.UserId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(value = "业务系统用户队列信息")
@NoArgsConstructor
public class UserQueueDTO {
    /**
     * 队列ID
     */
    @ApiModelProperty("队列ID")
    private Long queueId;
    /**
     * 业务系统ID
     */
    @ApiModelProperty("业务系统ID")
    private UserId userId;

    /**
     * 交换机名称
     */
    @ApiModelProperty("交换机名称")
    private String exchange;

    /**
     * 路由名称
     */
    @ApiModelProperty("路由名称")
    private String routing;

    /**
     * 队列名称
     */
    @ApiModelProperty("队列名称")
    private String queue;

    /**
     * 队列优先级
     */
    @ApiModelProperty("队列优先级")
    private Integer priority;

    /**
     * 队列业务类型
     */
    @ApiModelProperty("队列业务类型")
    private String businessType;

    /**
     * 是否禁用
     */
    @ApiModelProperty("是否禁用")
    private Boolean disabled;

    public UserQueueDTO(Long queueId, UserId userId, String exchange, String routing, String queue, Integer priority, String businessType, Boolean disabled) {
        this.queueId = queueId;
        this.userId = userId;
        this.exchange = exchange;
        this.routing = routing;
        this.queue = queue;
        this.priority = priority;
        this.businessType = businessType;
        this.disabled = disabled;
    }
}

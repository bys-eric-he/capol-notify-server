package com.capol.notify.manage.application.user.querystack;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@ApiModel(value = "业务系统用户信息")
@NoArgsConstructor
public class UserInfoDTO {

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("用户账号")
    private String account;

    @ApiModelProperty("业务系统名称")
    private String serviceName;

    @ApiModelProperty("用户队列")
    private List<UserQueueDTO> queues;

    public UserInfoDTO(String userId, String account, String serviceName,  List<UserQueueDTO> queues) {
        this.userId = userId;
        this.account = account;
        this.serviceName = serviceName;
        this.queues = queues;
    }
}
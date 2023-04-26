package com.capol.notify.manage.application.user.querystack;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(value = "业务系统用户基本信息")
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("用户账号")
    private String account;

    @ApiModelProperty("业务系统ID")
    private String serviceId;

    @ApiModelProperty("业务系统名称")
    private String serviceName;
}

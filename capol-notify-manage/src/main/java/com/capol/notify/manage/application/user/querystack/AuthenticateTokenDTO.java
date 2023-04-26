package com.capol.notify.manage.application.user.querystack;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "业务系统用户Token信息")
@NoArgsConstructor
public class AuthenticateTokenDTO {

    @ApiModelProperty("Token")
    private String token;

    @ApiModelProperty("Token过期时间")
    private LocalDateTime expiresTime;

    public AuthenticateTokenDTO(String token, LocalDateTime expiresTime) {
        this.token = token;
        this.expiresTime = expiresTime;
    }
}

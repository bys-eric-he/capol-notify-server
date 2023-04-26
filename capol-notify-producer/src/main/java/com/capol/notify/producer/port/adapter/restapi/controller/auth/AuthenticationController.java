package com.capol.notify.producer.port.adapter.restapi.controller.auth;


import com.capol.notify.manage.application.user.UserService;
import com.capol.notify.manage.application.user.querystack.AuthenticateTokenDTO;
import com.capol.notify.manage.application.user.querystack.UserInfoDTO;
import com.capol.notify.manage.domain.model.permission.CurrentUserService;
import com.capol.notify.producer.port.adapter.restapi.AllowAnonymous;
import com.capol.notify.producer.port.adapter.restapi.controller.auth.parameter.PasswordRequestParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1.0/service/auth")
@Api(tags = "业务系统用户API")
public class AuthenticationController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    public AuthenticationController(UserService userService,
                                    CurrentUserService currentUserService) {
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/auth/authPassword")
    @ApiOperation("通过账号密码认证")
    @AllowAnonymous
    public AuthenticateTokenDTO authenticateByPassword(@Valid @RequestBody PasswordRequestParam request) {
        return userService.authenticateByPassword(
                request.getAccount(),
                request.getPassword());
    }

    @GetMapping("/userInfo")
    @ApiOperation("获取当前用户信息")
    public UserInfoDTO userInfo() {
        return userService.userInfo(currentUserService.getCurrentUserId());
    }

}

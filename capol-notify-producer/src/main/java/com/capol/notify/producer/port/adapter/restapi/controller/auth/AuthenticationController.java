package com.capol.notify.producer.port.adapter.restapi.controller.auth;


import com.alibaba.fastjson.JSON;
import com.capol.notify.manage.application.ApplicationException;
import com.capol.notify.manage.application.user.UserService;
import com.capol.notify.manage.application.user.querystack.AuthenticateTokenDTO;
import com.capol.notify.manage.application.user.querystack.UserInfoDTO;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.manage.domain.model.permission.CurrentUserService;
import com.capol.notify.producer.port.adapter.restapi.AllowAnonymous;
import com.capol.notify.producer.port.adapter.restapi.controller.auth.parameter.PasswordRequestParam;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1.0/service/auth")
@Api(tags = "业务系统用户API")
public class AuthenticationController {
    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private final UserService userService;
    private final CurrentUserService currentUserService;

    public AuthenticationController(UserService userService,
                                    CurrentUserService currentUserService) {
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/token")
    @ApiOperation("通过账号密码认证")
    @AllowAnonymous
    public AuthenticateTokenDTO authenticateByPassword(@Valid @RequestBody PasswordRequestParam request) {
        return userService.authenticateByPassword(
                request.getAccount(),
                request.getPassword());
    }

    @ApiOperation(value = "刷新token")
    @RequestMapping(value = "/refresh-token", method = RequestMethod.GET)
    @ResponseBody
    public String refreshToken(HttpServletRequest request, String token) {
        String value = request.getHeader(AUTH_HEADER);
        if (value == null) {
            value = token;
        }
        String refreshToken = userService.refreshToken(value);
        if (refreshToken == null) {
            throw new ApplicationException("刷新Token失败!", EnumExceptionCode.InternalServerError);
        }
        HashMap<String, String> map = Maps.newHashMapWithExpectedSize(2);
        map.put("tokenPrefix", TOKEN_PREFIX);
        map.put("refreshToken", refreshToken);
        return JSON.toJSONString(map);
    }

    @GetMapping("/userInfo")
    @ApiOperation("获取当前用户信息")
    public UserInfoDTO userInfo() {
        return userService.userInfo(currentUserService.getCurrentUserId());
    }
}
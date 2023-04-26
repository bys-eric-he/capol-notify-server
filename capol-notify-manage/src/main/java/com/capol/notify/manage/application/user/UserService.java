package com.capol.notify.manage.application.user;


import com.capol.notify.manage.application.ApplicationException;
import com.capol.notify.manage.application.user.querystack.AuthenticateTokenDTO;
import com.capol.notify.manage.application.user.querystack.UserDTO;
import com.capol.notify.manage.application.user.querystack.UserInfoDTO;
import com.capol.notify.manage.application.user.querystack.UserQueueDTO;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.manage.domain.model.permission.AuthenticateToken;
import com.capol.notify.manage.domain.model.permission.AuthenticationService;
import com.capol.notify.manage.domain.model.permission.TokenService;
import com.capol.notify.manage.domain.model.permission.UserDescriptor;
import com.capol.notify.manage.domain.model.user.UserDO;
import com.capol.notify.manage.domain.model.user.UserId;
import com.capol.notify.manage.domain.repository.UserMapper;
import com.capol.notify.manage.domain.repository.UserQueueMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final TokenService tokenService;
    private final AuthenticationService authenticationService;

    private final UserMapper userMapper;
    private final UserQueueMapper userQueueMapper;

    public UserService(TokenService tokenService,
                       AuthenticationService authenticationService,
                       UserMapper userMapper,
                       UserQueueMapper userQueueMapper) {
        this.tokenService = tokenService;
        this.authenticationService = authenticationService;
        this.userMapper = userMapper;
        this.userQueueMapper = userQueueMapper;
    }

    /**
     * 通过账号密码登录
     *
     * @param account  账号
     * @param password 密码
     * @return 登录Token
     */
    @Transactional
    public AuthenticateTokenDTO authenticateByPassword(String account, String password) {
        UserDescriptor descriptor = authenticationService.authenticateByPassword(account, password);
        if (descriptor == null) {
            throw new ApplicationException(String.format("登录失败,账户不存在!"), EnumExceptionCode.UserNotExists);
        }

        log.info(">>>>>> 业务系统用户 {}({}) 通过密码方式访问消息服务.", descriptor.getName(), descriptor.getUserId());
        AuthenticateToken token = tokenService.generateToken(descriptor);
        return new AuthenticateTokenDTO(
                token.getToken(),
                token.getExpiresTime());
    }

    /**
     * 获取业务系统身份及队列信息
     *
     * @param anUserId 用户ID
     */
    @Transactional(readOnly = true)
    public UserInfoDTO userInfo(String anUserId) {
        UserDO user = userMapper.findByUserId(new UserId(anUserId));
        if (user == null) {
            throw new ApplicationException(String.format("账户不存在!"), EnumExceptionCode.UserNotExists);
        }
        return new UserInfoDTO(
                user.getUserId().getId(),
                user.getAccount(),
                user.getServiceName(),
                userQueueMapper.findByUserId(user.getUserId()).stream()
                        .map(queue -> new UserQueueDTO(
                                queue.getId(),
                                queue.getUserId(),
                                queue.getExchange(),
                                queue.getRouting(),
                                queue.getQueue(),
                                queue.getPriority(),
                                queue.getBusinessType(),
                                queue.getDisabled()
                        )).collect(Collectors.toList())
        );
    }

    /**
     * 获取业务系统身份信息
     *
     * @param anUserId 用户ID
     */
    @Transactional(readOnly = true)
    public UserDTO userBaseInfo(String anUserId) {
        UserDO user = userMapper.findByUserId(new UserId(anUserId));
        if (user == null) {
            throw new ApplicationException(String.format("账户不存在!"), EnumExceptionCode.UserNotExists);
        }

        return new UserDTO(user.getUserId().getId(), user.getAccount(), user.getServiceId(), user.getServiceName());
    }
}
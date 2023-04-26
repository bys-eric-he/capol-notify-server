package com.capol.notify.manage.domain.model.permission;


import com.capol.notify.manage.domain.DomainException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.manage.domain.model.user.UserDO;
import com.capol.notify.manage.domain.repository.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

/**
 * 用户鉴权领域服务
 */
@Service
public class AuthenticationService {

    private final UserMapper userMapper;
    private final PasswordService passwordService;

    public AuthenticationService(UserMapper userMapper,
                                 PasswordService passwordService) {
        this.userMapper = userMapper;
        this.passwordService = passwordService;
    }

    /**
     * 通过密码验证身份
     *
     * @param account  账号
     * @param password 密码
     * @return 用户信息
     */
    public UserDescriptor authenticateByPassword(String account, String password) {
        Validate.notBlank(account, "Account must be provided.");
        Validate.notBlank(password, "Password must be provided.");
        UserDO user = userMapper.findByAccount(account);
        if (user == null || user.isDisabled() || StringUtils.isBlank(user.getPassword())) {
            throw new DomainException("账户名称不正确!", EnumExceptionCode.IllegalAccountOrPassword);
        }
        String encryptedPassword = passwordService.encryptPassword(password, user.getSalt());
        if (!StringUtils.equals(user.getPassword(), encryptedPassword)) {
            throw new DomainException("账户密码不正确!", EnumExceptionCode.IllegalAccountOrPassword);
        }
        return loginAndCheckPermissions(user);
    }

    /**
     * 登录并且检查权限
     *
     * @param user 用户
     * @return 用户描述
     */
    private UserDescriptor loginAndCheckPermissions(UserDO user) {
        if (user == null) {
            throw new DomainException("账户不存在!", EnumExceptionCode.UserNotExists);
        }
        // 禁用不允许登录
        if (user.isDisabled()) {
            throw new DomainException("账户被禁用不允许登录!", EnumExceptionCode.Forbidden);
        }
        user.login();
        return user.userDescriptor();
    }
}
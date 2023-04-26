package com.capol.notify.manage.domain.model.permission;

public interface TokenService {

    /**
     * 生成Token
     *
     * @param userDescriptor 用户描述信息
     * @return Token
     */
    AuthenticateToken generateToken(UserDescriptor userDescriptor);

    /**
     * 解析用户Token
     *
     * @param token Token
     * @return 解析后的用户描述
     */
    UserDescriptor decodeToken(String token);
}

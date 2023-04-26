package com.capol.notify.manage.domain.model.permission;

import com.capol.notify.manage.domain.model.user.UserDO;
import com.capol.notify.manage.domain.model.user.UserId;
import com.capol.notify.manage.domain.repository.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private final UserMapper userMapper;

    public AuthorizationService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 检查操作是否授权
     *
     * @param userId    操作用户ID
     */
    public boolean authorizedOperation(UserId userId) {
        if (userId == null) {
            return false;
        }
        UserDO user = userMapper.findByUserId(userId);
        if (user == null) {
            return false;
        }
        return !user.isDisabled();
    }
}
package com.capol.notify.manage.domain.model.permission;


public interface CurrentUserService {

    /**
     * 获取当前用户ID
     */
    String getCurrentUserId();

    /**
     * 当前用户是否已鉴权
     */
    boolean isCurrentUserAuthenticated();

    /**
     * 是否为当前用户
     */
    boolean isCurrentUser(String userId);

    /**
     * 获取当前用户请求IP
     */
    String getCurrentUserIp();

    /**
     * 获取当前用户的用户名
     */
    String getCurrentUserName();
}
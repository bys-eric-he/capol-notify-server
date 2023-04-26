package com.capol.notify.manage.domain.model.user;


import com.baomidou.mybatisplus.annotation.TableName;
import com.capol.notify.manage.domain.model.BaseEntity;
import com.capol.notify.manage.domain.model.permission.PasswordService;
import com.capol.notify.manage.domain.model.permission.UserDescriptor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;

/**
 * 业务系统用户
 */
@Getter
@EqualsAndHashCode(callSuper = false)
@TableName("t_user")
public class UserDO extends BaseEntity {

    /**
     * 业务系统ID
     */
    private UserId userId;

    /**
     * 服务ID
     */
    private String serviceId;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 账号
     */
    private String account;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 密码Salt
     */
    private String salt;

    /**
     * 是否禁用(禁止登录)
     */
    private Boolean disabled;

    /**
     * 最近一次登录时间
     */
    private LocalDateTime latestLoginDateTime;

    protected UserDO() {
    }

    public UserDO(UserId userId, String account, String serviceId, String serviceName, Boolean disabled) {
        Validate.notNull(userId, "UserID must be provided.");
        Validate.notBlank(account, "User account must not be null.");

        this.userId = userId;
        this.account = account;
        this.disabled = disabled;
        this.serviceId = serviceId;
        this.setServiceName(serviceName);
    }

    public UserDO(UserId userId, String account, String serviceId, String serviceName, String password, PasswordService passwordService) {
        Validate.notNull(userId, "UserID must be provided.");
        Validate.notBlank(account, "User account must not be null.");

        this.userId = userId;
        this.account = account;
        this.disabled = false;
        this.serviceId = serviceId;
        this.setServiceName(serviceName);
        this.salt = passwordService.generateSalt();
        this.password = passwordService.encryptPassword(password, this.salt);
    }

    /**
     * 生成用户描述信息
     */
    public UserDescriptor userDescriptor() {
        return new UserDescriptor(this.userId.getId(), this.getServiceName());
    }

    public void login() {
        this.latestLoginDateTime = LocalDateTime.now();
    }

    /**
     * 禁用用户
     */
    public void disable() {
        this.disabled = true;
    }

    /**
     * 重新启用用户
     */
    public void enable() {
        this.disabled = false;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * 是否被禁用
     */
    public boolean isDisabled() {
        return BooleanUtils.isTrue(disabled);
    }

    private void setServiceName(String serviceName) {
        Validate.notBlank(serviceName, "User serviceName must not be null.");
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", account='" + account + '\'' +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }
}
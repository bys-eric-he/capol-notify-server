package com.capol.notify.manage.domain.model.permission;


import com.capol.notify.manage.domain.model.ValueObject;

import java.security.Principal;
import java.util.Objects;

/**
 * 包含用户关键信息
 */
public class UserDescriptor extends ValueObject implements Principal {

    private final String userId;
    private final String name;

    public UserDescriptor(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDescriptor that = (UserDescriptor) o;
        return Objects.equals(userId, that.userId) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name);
    }

    @Override
    public String toString() {
        return "UserDescriptor{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
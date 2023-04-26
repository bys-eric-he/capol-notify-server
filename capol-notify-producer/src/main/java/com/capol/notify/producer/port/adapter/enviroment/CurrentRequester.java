package com.capol.notify.producer.port.adapter.enviroment;


import com.capol.notify.manage.domain.model.permission.UserDescriptor;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * 当前请求相关信息
 */
@Getter
public class CurrentRequester extends AbstractAuthenticationToken {

    private final UserDescriptor userDescriptor;
    private final String ip;
    private final boolean authenticated;

    public CurrentRequester(UserDescriptor userDescriptor, String ip, boolean authenticated) {
        super(null);
        this.userDescriptor = userDescriptor;
        this.ip = ip;
        this.authenticated = authenticated;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public Object getPrincipal() {
        return userDescriptor;
    }
}

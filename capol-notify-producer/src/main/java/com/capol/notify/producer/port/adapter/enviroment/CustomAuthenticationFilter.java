package com.capol.notify.producer.port.adapter.enviroment;


import com.capol.notify.manage.domain.model.permission.TokenService;
import com.capol.notify.manage.domain.model.permission.UserDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 客户端权限过滤
 *
 * @author heyong
 * @since 2023-04-17
 */
@Slf4j
public class CustomAuthenticationFilter extends OncePerRequestFilter implements Filter {

    private static final String AUTH_HEADER = "Authorization";

    private TokenService tokenService;

    public CustomAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Get authorization header.
        String authHeader = request.getHeader(AUTH_HEADER);
        authHeader = authHeader != null ? authHeader : request.getParameter(AUTH_HEADER);
        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }
        this.authenticate(authHeader, request);
        filterChain.doFilter(request, response);
    }

    /**
     * 验证用户
     */
    private void authenticate(String token, HttpServletRequest request) {
        if (StringUtils.isBlank(token)) {
            return;
        }

        UserDescriptor userDescriptor = null;
        if (request.getRequestURI().startsWith("/api/v1.0/service/")) {
            userDescriptor = tokenService.decodeToken(token);
        }
        if (userDescriptor == null) {
            log.warn("-->The currently requested API is not authorized!!!");
            return;
        }
        CurrentRequester requester = new CurrentRequester(userDescriptor,
                getIpAddress(request), true);
        SecurityContextHolder.getContext().setAuthentication(requester);
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

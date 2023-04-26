package com.capol.notify.producer.port.adapter.enviroment;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * WebMVC配置
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final CustomAuthorizeInterceptor authorizeInterceptor;

    public WebMvcConfiguration(CustomAuthorizeInterceptor authorizeInterceptor) {
        this.authorizeInterceptor = authorizeInterceptor;
    }

    /**
     * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizeInterceptor)
                .addPathPatterns(provideAuthInterceptPath())
                .excludePathPatterns(provideAuthExcludePath());
    }

    /**
     * 需要检查权限的资源
     */
    private List<String> provideAuthInterceptPath() {
        return List.of(
                "/api/v1.0/service/**",
                "/api/v1.0/open/**"
        );
    }

    /**
     * 排除检查权限的资源
     */
    private List<String> provideAuthExcludePath() {
        return List.of();
    }
}
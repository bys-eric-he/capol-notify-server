package com.capol.notify.producer.port.adapter.enviroment;


import com.capol.notify.manage.domain.model.permission.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final ObjectMapper objectMapper;
    private final TokenService tokenService;

    public SecurityConfiguration(ObjectMapper objectMapper, TokenService tokenService) {
        this.objectMapper = objectMapper;
        this.tokenService = tokenService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .antMatchers("/api/v1.0/service/**").authenticated()
                .antMatchers("/api/v1.0/open/**").authenticated()
                .and()
                .addFilterBefore(new ExceptionHandlerFilter(objectMapper), BasicAuthenticationFilter.class)
                .addFilterAfter(new CustomAuthenticationFilter(tokenService), ExceptionHandlerFilter.class)
                .httpBasic();
    }
}
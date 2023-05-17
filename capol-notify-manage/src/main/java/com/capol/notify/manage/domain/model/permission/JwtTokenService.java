package com.capol.notify.manage.domain.model.permission;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.capol.notify.manage.domain.DomainException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
public class JwtTokenService implements TokenService {

    private static final String TOKEN_PREFIX = "Bearer ";

    @Value("${capol.jwt.issuer}")
    private String issuer = "capol";

    @Value("${capol.jwt.key}")
    private String key = "fa6Y!#2zYfdnzD#Z@USNFlQIgMROqR6aNnPeHy%Yw$9eCtOnjt";

    @Value("${capol.jwt.expires-hours}")
    private Long expiresHours = 8L;

    @Override
    public AuthenticateToken generateToken(UserDescriptor userDescriptor) {
        LocalDateTime expiresTime = LocalDateTime.now().plusHours(expiresHours);
        String token = JWT.create()
                .withIssuer(issuer)
                .withSubject(userDescriptor.getUserId())
                .withClaim("name", userDescriptor.getName())
                .withIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .withExpiresAt(Date.from(expiresTime.atZone(ZoneId.systemDefault()).toInstant()))
                .sign(Algorithm.HMAC256(key));
        return new AuthenticateToken(TOKEN_PREFIX + token, expiresTime);
    }

    /**
     * 刷新Token
     *
     * @param oldToken
     * @return
     */
    @Override
    public String refreshToken(String oldToken) {
        if (oldToken.startsWith(TOKEN_PREFIX)) {
            oldToken = oldToken.replaceFirst(TOKEN_PREFIX, "");
        }
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(key)).withIssuer(issuer).build();
        try {
            DecodedJWT jwt = verifier.verify(oldToken);
            if (jwt == null) {
                throw new DomainException("token解析失败, 不支持刷新, 请重新认证获取！", EnumExceptionCode.InternalServerError);
            }

            //如果token已经过期，不支持刷新
            boolean isExpired = jwt.getExpiresAt().before(new java.util.Date());
            if (isExpired) {
                throw new DomainException("token已经过期，不支持刷新, 请重新认证获取！", EnumExceptionCode.InternalServerError);
            }

            //判断是否已经到要刷新的时间范围内
            if (!tokenRefreshJustBefore(oldToken, 30 * 60)) {
                log.info("-->无需刷新Token, 直接返回旧的!");
                return oldToken;
            }

            LocalDateTime expiresTime = LocalDateTime.now().plusHours(expiresHours);
            String token = JWT.create().withIssuer(issuer).withSubject(jwt.getSubject()).withClaim("name", jwt.getClaim("name") == null ? null : jwt.getClaim("name").asString()).withIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())).withExpiresAt(Date.from(expiresTime.atZone(ZoneId.systemDefault()).toInstant())).sign(Algorithm.HMAC256(key));
            log.info("-->刷新Token成功!");
            return token;
        } catch (JWTVerificationException jwtVerificationException) {
            log.error("-->解析Token异常, 异常信息：{}", jwtVerificationException);
            return null;
        }
    }

    @Override
    public UserDescriptor decodeToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.replaceFirst(TOKEN_PREFIX, "");
        }
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(key))
                .withIssuer(issuer)
                .build();
        try {
            DecodedJWT jwt = verifier.verify(token);
            return new UserDescriptor(
                    jwt.getSubject(),
                    jwt.getClaim("name") == null ? null : jwt.getClaim("name").asString());
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    /**
     * 判断token在指定时间内是否刚刚刷新过
     *
     * @param token 原token
     * @param time  指定时间（秒）
     */
    private boolean tokenRefreshJustBefore(String token, int time) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(key)).withIssuer(issuer).build();
        try {
            DecodedJWT jwt = verifier.verify(token);
            java.util.Date expireTime = jwt.getExpiresAt();
            java.util.Date refreshDate = new java.util.Date();
            //刷新时间在创建时间的指定时间内
            if (refreshDate.after(expireTime) && refreshDate.before(DateUtil.offsetSecond(expireTime, time))) {
                return true;
            }
            log.info("-->token还未到过期刷新时间范围内,过期时间:{} !", DateFormatUtils.format(jwt.getExpiresAt(), "yyyy-MM-dd HH:mm:ss"));

        } catch (JWTVerificationException jwtVerificationException) {
            log.error("-->解析Token异常, 异常信息：{}", jwtVerificationException);
        }
        return false;
    }
}
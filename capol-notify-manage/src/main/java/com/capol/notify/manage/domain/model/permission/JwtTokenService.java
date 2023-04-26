package com.capol.notify.manage.domain.model.permission;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
}
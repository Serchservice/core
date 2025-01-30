package com.serch.server.core.token.implementations;

import com.serch.server.core.token.JwtService;
import com.serch.server.core.validator.implementations.KeyValidator;
import com.serch.server.domains.auth.requests.RequestSessionToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementation of the JwtService interface for managing JWT operations.
 * It implements its wrapper class {@link JwtService}
 */
@Service
@RequiredArgsConstructor
public class JwtImplementation implements JwtService {
    private final KeyValidator validator;

    @Value("${application.security.jwt-secret-key}")
    protected String JWT_SECRET_KEY;

    @Value("${application.security.jwt-expiration-time}")
    protected Long JWT_EXPIRATION_TIME;

    @Value("${application.access.signature}")
    private String ACCESS_SIGNATURE;

    /**
     * Retrieves the signing key for JWT.
     *
     * @return The signing key.
     */
    protected Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET_KEY));
    }

    /**
     * Parses and retrieves the claims from the given access token.
     *
     * @param accessToken The JWT access token.
     * @return The claims extracted from the token.
     */
    protected Claims fetchClaims(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }

    /**
     * Extracts specific claims from the JWT token.
     *
     * @param accessToken The JWT access token.
     * @param fetch       The function to fetch the claims.
     * @param <T>         The type of the claim.
     * @return The extracted claim.
     */
    protected <T> T extractClaims(String accessToken, Function<Claims, T> fetch) {
        return fetch.apply(fetchClaims(accessToken));
    }

    @Override
    public String generateToken(Map<String, Object> data, String subject) {
        return Jwts
                .builder()
                .setClaims(data)
                .setSubject(subject)
                .setIssuer(Encoders.BASE64.encode(ACCESS_SIGNATURE.getBytes()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String generateToken(RequestSessionToken request) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", request.getRole());
        claims.put("serch_id", request.getId());
        claims.put("session_id", request.getSessionId());
        claims.put("refresh_id", request.getRefreshId());

        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(request.getEmailAddress())
                .setIssuer(Encoders.BASE64.encode(ACCESS_SIGNATURE.getBytes()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public boolean isTokenExpired(String accessToken) {
        return extractClaims(accessToken, Claims::getExpiration).before(new Date());
    }

    @Override
    public String getItemFromToken(String token, String identifier) {
        return extractClaims(token, claims -> claims.get(identifier, String.class));
    }

    @Override
    public boolean isTokenIssuedBySerch(String token) {
        return validator.isSigned(new String(Decoders.BASE64.decode(extractClaims(token, Claims::getIssuer))));
    }

    @Override
    public String getEmailFromToken(String token) {
        return extractClaims(token, Claims::getSubject);
    }
}

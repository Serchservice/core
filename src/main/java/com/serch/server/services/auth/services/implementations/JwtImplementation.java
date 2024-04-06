package com.serch.server.services.auth.services.implementations;

import com.serch.server.services.auth.requests.RequestSessionToken;
import com.serch.server.services.auth.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtImplementation implements JwtService {
    @Value("${application.security.jwt-secret-key}")
    protected String JWT_SECRET_KEY;

    @Value("${application.security.jwt-expiration-time}")
    protected Long JWT_EXPIRATION_TIME;

    protected Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET_KEY));
    }

    protected Claims fetchClaims(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }

    protected <T> T extractClaims(String accessToken, Function<Claims, T> fetch) {
        return fetch.apply(fetchClaims(accessToken));
    }

    @Override
    public String generateToken(RequestSessionToken request) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", request.getRole());
        claims.put("serch_id", request.getSerchId());
        claims.put("session_id", request.getSessionId());
        claims.put("refresh_id", request.getRefreshId());

        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(request.getEmailAddress())
                .setIssuer("Serch")
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
        return extractClaims(token, Claims::getIssuer).equals("Serch");
    }

    @Override
    public String getEmailFromToken(String token) {
        return extractClaims(token, Claims::getSubject);
    }
}

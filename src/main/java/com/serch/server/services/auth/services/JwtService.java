package com.serch.server.services.auth.services;

import com.serch.server.services.auth.requests.RequestSessionToken;

public interface JwtService {
    String generateToken(RequestSessionToken tokenRequest);
    boolean isTokenExpired(String token);
    String getItemFromToken(String token, String identifier);
    boolean isTokenIssuedBySerch(String token);
    String getEmailFromToken(String token);
}

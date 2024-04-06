package com.serch.server.services.auth.services;

public interface TokenService {
    String generateOtp();
    String generateCode(int length);
    String generateRefreshToken();
}

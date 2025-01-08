package com.serch.server.core.validator.implementations;

import com.serch.server.core.validator.AllowedOriginValidatorService;
import com.serch.server.utils.ServerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllowedOriginValidator implements AllowedOriginValidatorService {
    @Value("${application.access}")
    private String ACCESS;

    private boolean isDevelopment() {
        return ACCESS.equalsIgnoreCase("development") || ACCESS.equalsIgnoreCase("dev");
    }

    private boolean isSandbox() {
        return ACCESS.equalsIgnoreCase("sandbox") || ACCESS.equalsIgnoreCase("sand");
    }

    @Override
    public String[] getWebSocketOrigins() {
        if(isDevelopment() || isSandbox()) {
            return getOrigins(ServerUtil.ALLOWED_ORIGINS);
        } else {
            return getOrigins(ServerUtil.ALLOWED_PRODUCTION_ORIGINS);
        }
    }

    private String[] getOrigins(List<String> origins) {
        return origins.toArray(new String[0]);
    }

    @Override
    public String[] getWebSocketOriginPatterns() {
        if(isDevelopment() || isSandbox()) {
            return getOrigins(ServerUtil.ALLOWED_ORIGIN_PATTERNS);
        } else {
            return getOrigins(ServerUtil.ALLOWED_PRODUCTION_ORIGIN_PATTERNS);
        }
    }

    @Override
    public List<String> getServerOrigins() {
        if(isDevelopment() || isSandbox()) {
            return ServerUtil.ALLOWED_ORIGINS;
        } else {
            return ServerUtil.ALLOWED_PRODUCTION_ORIGINS;
        }
    }

    @Override
    public List<String> getServerOriginPatterns() {
        if(isDevelopment() || isSandbox()) {
            return ServerUtil.ALLOWED_ORIGIN_PATTERNS;
        } else {
            return ServerUtil.ALLOWED_PRODUCTION_ORIGIN_PATTERNS;
        }
    }
}
package com.serch.server.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

public class ServerUtil {
    /**
     * Allowing headers for both development and production purposes
     */
    public static final List<String> HEADERS = List.of(
            "X-CSRF-TOKEN",
            HttpHeaders.AUTHORIZATION,
            HttpHeaders.ACCEPT,
            HttpHeaders.CONTENT_TYPE
    );

    public static final List<String> ALLOWED_ORIGINS = List.of(
            "https://www.serchservice.com", "https://serchservice.com",
            "https://help.serchservice.com", "https://portal.serchservice.com",
            "flutter-app://com.serch.user", "flutter-app://com.serch.business",
            "flutter-app://com.serch.provider", "capacitor://localhost"
    );

    public static final List<String> ALLOWED_ORIGIN_PATTERNS = List.of(
            "http://localhost:*", "http://127.0.0.1:*", "https://*.serchservice.com"
    );

    public static final List<String> ALLOWED_IP_ADDRESSES = List.of(
            "34.240.137.52", "44.230.128.108", "52.36.32.43", "52.213.46.74"
    );

    /**
     * Allowing methods for both development and production purposes
     */
    public static final List<String> METHODS = List.of(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.DELETE.name()
    );

    public static List<String> getOrigins(String data) {
        return Arrays.stream(data.split(",")).toList();
    }
}

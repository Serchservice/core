package com.serch.server.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.List;

public class ServerUtil {
    /**
     * Allowing urls for production purposes
     */
    public static final List<String> PRODUCTION = List.of(
            "https://www.serchservice.com", // Frontend
            "https://serchservice.com", // Frontend
            "flutter-app://com.serch.user", // Scheme for Flutter mobile app (User)
            "flutter-app://com.serch.business", // Scheme for Flutter mobile app (Business)
            "flutter-app://com.serch.provider", // Scheme for Flutter mobile app (Provider)
            "capacitor://localhost" // if using Capacitor for Flutter
    );

    /**
     * Allowing localhost for development purposes
     */
    public static final List<String> DEVELOPMENT = List.of(
            "http://localhost:*",
            "http://127.0.0.1:*"
    );

    /**
     * Allowing headers for both development and production purposes
     */
    public static final List<String> HEADERS = List.of(
            "X-CSRF-TOKEN",
            HttpHeaders.AUTHORIZATION,
            HttpHeaders.ACCEPT,
            HttpHeaders.CONTENT_TYPE
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
}

package com.serch.server.utils;

import com.serch.server.enums.ServerHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerUtil {
    /**
     * Allowing headers for both development and production purposes
     */
    public static final List<String> HEADERS = getHeaders();

    static List<String> getHeaders() {
        List<String> headers = new ArrayList<>(Arrays.stream(ServerHeader.values()).map(ServerHeader::getValue).toList());
        headers.add("X-CSRF-TOKEN");
        headers.addAll(List.of(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.ACCEPT,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,
                HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                HttpHeaders.ACCESS_CONTROL_MAX_AGE,
                HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
                HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD
        ));

        return headers;
    }

    public static final List<String> ALLOWED_ORIGINS = List.of(
            "https://www.serchservice.com", "https://serchservice.com",
            "https://help.serchservice.com", "https://portal.serchservice.com",
            "https://*.serchservice.com",
            "flutter-app://com.serch.user", "flutter-app://com.serch.business",
            "flutter-app://com.serch.provider", "capacitor://localhost"
    );

    public static final List<String> ALLOWED_PRODUCTION_ORIGINS = List.of(
            "https://www.serchservice.com", "https://serchservice.com",
            "https://help.serchservice.com", "https://portal.serchservice.com",
            "https://*.serchservice.com"
    );

    public static final List<String> ALLOWED_ORIGIN_PATTERNS = List.of(
            "http://localhost:*", "http://127.0.0.1:*", "https://*.serchservice.com"
    );

    public static final List<String> ALLOWED_PRODUCTION_ORIGIN_PATTERNS = List.of("https://*.serchservice.com");

    public static final List<String> ALLOWED_IP_ADDRESSES = List.of(
            "34.240.137.52", "44.230.128.108", "52.36.32.43", "52.213.46.74"
    );

    /**
     * Allowing methods for both development and production purposes
     */
    public static final List<String> METHODS = Arrays.stream(HttpMethod.values()).map(HttpMethod::name).toList();
}

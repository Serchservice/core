package com.serch.server.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

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

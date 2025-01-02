package com.serch.server.core;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;

import java.util.Arrays;

public class Logging {
    private static final Logger log = LoggerFactory.getLogger(Logging.class);

    static public void logRequest(HttpServletRequest request, String from) {
        String prefix = String.format("SERCH %s:::", from);

        if(request != null) {
            log.info(String.format("%s %s::: %s", prefix, "Request Url", request.getRequestURL()));
            log.info(String.format("%s %s::: %s", prefix,  "Request Uri", request.getRequestURI()));
            log.info(String.format("%s %s::: %s", prefix,  "Remote Address", request.getRemoteAddr()));
            log.info(String.format("%s Servlet Path::: %s", prefix,  request.getServletPath()));
            log.info(String.format("%s Method::: %s", prefix,  request.getMethod()));
            log.info(String.format("%s Query String::: %s", prefix,  request.getQueryString()));
            log.info(String.format("%s Local Address::: %s", prefix,  request.getLocalAddr()));
            log.info(String.format("%s Locale::: %s", prefix,  request.getLocale().toString()));
            log.info(String.format("%s Remote Host::: %s", prefix,  request.getRemoteHost()));
            log.info(String.format("%s Remote Port::: %s", prefix,  request.getRemotePort()));
            log.info(String.format("%s Protocol::: %s", prefix,  request.getProtocol()));
            log.info(String.format("%s Scheme::: %s", prefix,  request.getScheme()));
            log.info(String.format("%s Server Name::: %s", prefix,  request.getServerName()));
            log.info(String.format("%s Server Port::: %s", prefix,  request.getServerPort()));
            request.getParameterMap().forEach((a, b) -> log.info(String.format("%s Request Param = %s | Value = %s", prefix, a, Arrays.toString(b))));
        }
    }

    static public void logRequest(ServerHttpRequest request, String from) {
        String prefix = String.format("SERCH WEBSOCKET %s:::", from);

        if(request != null) {
            log.info(String.format("%s Method::: %s", prefix, request.getMethod()));
            log.info(String.format("%s Uri::: %s", prefix, request.getURI()));
            log.info(String.format("%s Uri Authority::: %s", prefix, request.getURI().getAuthority()));
            log.info(String.format("%s Uri Fragment::: %s", prefix, request.getURI().getFragment()));
            log.info(String.format("%s Uri host::: %s", prefix, request.getURI().getHost()));
            log.info(String.format("%s Uri path::: %s", prefix, request.getURI().getPath()));
            log.info(String.format("%s Uri port::: %s", prefix, request.getURI().getPort()));
            log.info(String.format("%s Uri query::: %s", prefix, request.getURI().getQuery()));
            log.info(String.format("%s Uri scheme::: %s", prefix, request.getURI().getScheme()));
            log.info(String.format("%s Uri scheme specific part::: %s", prefix, request.getURI().getSchemeSpecificPart()));
            log.info(String.format("%s Uri user info::: %s", prefix, request.getURI().getUserInfo()));
            log.info(String.format("%s Local Address::: %s", prefix, request.getLocalAddress().getAddress()));
            log.info(String.format("%s Remote Address::: %s", prefix, request.getRemoteAddress().getAddress()));
        }
    }
}
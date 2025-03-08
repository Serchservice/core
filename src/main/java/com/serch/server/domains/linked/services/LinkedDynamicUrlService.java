package com.serch.server.domains.linked.services;

import com.serch.server.domains.linked.dtos.LinkedDynamicUrlDto;
import jakarta.servlet.http.HttpServletResponse;

public interface LinkedDynamicUrlService {
    String generate(LinkedDynamicUrlDto request);

    LinkedDynamicUrlDto build(LinkedDynamicUrlDto request);

    String request(String identifier);

    String preview(String identifier, String userAgent, HttpServletResponse response);
}
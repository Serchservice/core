package com.serch.server.domains.linked.controllers;

import com.serch.server.domains.linked.services.LinkedDynamicUrlService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LinkedDynamicUrlController {
    private final LinkedDynamicUrlService service;

    @GetMapping("/{shortUrl}")
    public ResponseEntity<String> preview(
            @PathVariable String shortUrl,
            @RequestHeader(required = false, value = "User-Agent") String userAgent,
            HttpServletResponse response
    ) {
        String preview = service.preview(shortUrl, userAgent, response);
        if(preview == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(preview);
        }
    }
}
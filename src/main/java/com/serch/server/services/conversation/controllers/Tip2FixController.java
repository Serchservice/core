package com.serch.server.services.conversation.controllers;

import com.serch.backend.bases.ApiResponse;
import com.serch.backend.platform.call.services.Tip2FixService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tip2fix")
public class Tip2FixController {
    private final Tip2FixService service;

    @GetMapping
    public ResponseEntity<ApiResponse<String>> checkSession(
            @RequestParam Integer duration, @RequestParam String channel
    ) {
        var response = service.checkSession(duration, channel);
        return new ResponseEntity<>(response, response.getStatus());
    }
}

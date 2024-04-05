package com.serch.server.services.media.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.media.responses.MediaNewsroomResponse;
import com.serch.server.services.media.services.MediaNewsroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company/media/newsroom")
public class MediaNewsroomController {
    private final MediaNewsroomService service;

    @GetMapping("/news")
    public ResponseEntity<ApiResponse<MediaNewsroomResponse>> fetchNews(@RequestParam String key) {
        ApiResponse<MediaNewsroomResponse> response = service.findNews(key);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MediaNewsroomResponse>>> fetchAllNews(
            @RequestParam(required = false) Integer page
    ) {
        ApiResponse<List<MediaNewsroomResponse>> response = service.findAllNews(page);
        return new ResponseEntity<>(response, response.getStatus());
    }
}

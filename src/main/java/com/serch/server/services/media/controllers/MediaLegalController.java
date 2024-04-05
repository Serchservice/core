package com.serch.server.services.media.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.media.responses.MediaLegalGroupResponse;
import com.serch.server.services.media.responses.MediaLegalResponse;
import com.serch.server.services.media.services.MediaLegalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company/media/legal")
public class MediaLegalController {
    private final MediaLegalService service;

    @GetMapping("/document")
    public ResponseEntity<ApiResponse<MediaLegalResponse>> fetchLegal(@RequestParam String key) {
        ApiResponse<MediaLegalResponse> response = service.fetchLegal(key);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MediaLegalGroupResponse>>> fetchAllLegals() {
        ApiResponse<List<MediaLegalGroupResponse>> response = service.fetchAllLegals();
        return new ResponseEntity<>(response, response.getStatus());
    }
}

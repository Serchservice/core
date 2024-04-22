package com.serch.server.services.media.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.media.responses.MediaAssetResponse;
import com.serch.server.services.media.services.MediaAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/company/media/assets")
@RequiredArgsConstructor
public class MediaAssetController {
    private final MediaAssetService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MediaAssetResponse>>> fetchAllAssets() {
        ApiResponse<List<MediaAssetResponse>> response = service.fetchAllAssets();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/download")
    public ResponseEntity<ApiResponse<String>> download(@RequestParam Long key) {
        ApiResponse<String> response = service.download(key);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
package com.serch.server.domains.nearby.services.media.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.services.media.services.GoMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/go/media")
public class GoMediaController {
    private final GoMediaService service;

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        ApiResponse<Void> response = service.delete(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
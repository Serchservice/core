package com.serch.server.nearby.services.drive.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.nearby.services.drive.requests.NearbyDriveRequest;
import com.serch.server.nearby.services.drive.services.NearbyDriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nearby/drive")
public class NearbyDriveController {
    private final NearbyDriveService service;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> drive(@RequestBody NearbyDriveRequest request) {
        ApiResponse<String> response = service.drive(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<String>> search(@RequestBody String type) {
        ApiResponse<String> response = service.search(type);
        return ResponseEntity.ok(response);
    }
}
package com.serch.server.services.verified;

import com.serch.server.bases.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/verification")
public class VerifiedController {
    private final VerifiedService service;

    @PostMapping("/status")
    public ResponseEntity<ApiResponse<String>> smileStatus(@RequestBody Map<Object, Object> body) {
        ApiResponse<String> response = service.smileStatus(body);
        return new ResponseEntity<>(response, response.getStatus());
    }
}

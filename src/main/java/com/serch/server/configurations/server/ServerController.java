package com.serch.server.configurations.server;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.server.services.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/server")
public class ServerController {
    private final ServerService service;

    @GetMapping("/memory/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMemoryStatistics() {
        ApiResponse<Map<String, Object>> response = service.getMemoryStatistics();

        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/openapi/generate")
    public ResponseEntity<ApiResponse<String>> saveOpenApi(@RequestParam(name = "is_json") Boolean isJson) {
        ApiResponse<String> response = service.generateOpenApiSpecification(isJson);

        return new ResponseEntity<>(response, response.getStatus());
    }
}
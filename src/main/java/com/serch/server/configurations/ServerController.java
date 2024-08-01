package com.serch.server.configurations;

import com.serch.server.bases.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/server")
public class ServerController {
    private final RestTemplate rest;

    @Value("${application.base-url}")
    private String SERCH_BASE_URL;

    @GetMapping("/memory/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMemoryStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_memory", Runtime.getRuntime().totalMemory());
        stats.put("max_memory", Runtime.getRuntime().maxMemory());
        stats.put("free_memory", Runtime.getRuntime().freeMemory());
        return ResponseEntity.ok(new ApiResponse<>(stats));
    }

    @GetMapping("/openapi/save")
    public ResponseEntity<ApiResponse<String>> saveOpenApiJson() {
        String openApiUrl = SERCH_BASE_URL + "/v3/api-docs";
        String openApiJson = rest.getForObject(openApiUrl, String.class);

        try (FileOutputStream fos = new FileOutputStream("serch-server-openapi.json")) {
            assert openApiJson != null;
            fos.write(openApiJson.getBytes());
            return ResponseEntity.ok(new ApiResponse<>("OpenAPI JSON file saved successfully!", HttpStatus.OK));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Error saving OpenAPI JSON file."));
        }
    }
}
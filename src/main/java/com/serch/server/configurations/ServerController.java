package com.serch.server.configurations;

import com.serch.server.bases.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/server")
public class ServerController {
    private final RestTemplate rest;

    @Value("${application.link.server.base}")
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
    public ResponseEntity<byte[]> saveOpenApiJson() {
        String openApiUrl = SERCH_BASE_URL + "/v3/api-docs";
        String openApiJson = rest.getForObject(openApiUrl, String.class);

        if (openApiJson == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching OpenAPI JSON".getBytes());
        }

        byte[] fileContent = openApiJson.getBytes();

        // Set headers for file download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDisposition(ContentDisposition.attachment().filename("Serch|ServerApi.json").build());
        headers.setContentLength(fileContent.length);

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

}
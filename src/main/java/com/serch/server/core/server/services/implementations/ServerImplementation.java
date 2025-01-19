package com.serch.server.core.server.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.server.services.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServerImplementation implements ServerService {
    private final RestTemplate rest;

    @Value("${application.link.server.base}")
    private String BASE_URL;

    @Override
    public ApiResponse<Map<String, Object>> getMemoryStatistics() {
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> stats = new HashMap<>();

        // Format function to decide between MB and GB
        stats.put("total_memory", formatMemory(runtime.totalMemory()));
        stats.put("max_memory", formatMemory(runtime.maxMemory()));
        stats.put("free_memory", formatMemory(runtime.freeMemory()));
        stats.put("used_memory", formatMemory(runtime.totalMemory() - runtime.freeMemory()));

        return new ApiResponse<>(stats);
    }

    private String formatMemory(long bytes) {
        double valueInMB = bytes / (1024.0 * 1024.0);
        if (valueInMB < 1024) {
            return String.format("%.2f MB", valueInMB);
        }

        return String.format("%.2f GB", valueInMB / 1024.0);
    }

    @Override
    public ApiResponse<String> generateOpenApiSpecification(Boolean isJson) {
        String openApiUrl = String.format("%s/v3/api-docs", BASE_URL);
        String openApiSpec = rest.getForObject(openApiUrl, String.class);

        if (openApiSpec == null) {
            return new ApiResponse<>("Error fetching OpenAPI specification", HttpStatus.BAD_REQUEST);
        }

        String fileName = "documentation." + (isJson ? "json" : "yml");
        File file = new File(fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(openApiSpec.getBytes());
            return new ApiResponse<>("OpenAPI specification saved to " + file.getAbsolutePath(), HttpStatus.OK);
        } catch (IOException e) {
            return new ApiResponse<>("Error saving OpenAPI JSON file.");
        }
    }
}

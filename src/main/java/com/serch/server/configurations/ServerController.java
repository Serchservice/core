package com.serch.server.configurations;

import com.serch.server.bases.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/server")
public class ServerController {
    @GetMapping("/memory/status")
    public ApiResponse<Map<String, Object>> getMemoryStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_memory", Runtime.getRuntime().totalMemory());
        stats.put("max_memory", Runtime.getRuntime().maxMemory());
        stats.put("free_memory", Runtime.getRuntime().freeMemory());
        return new ApiResponse<>(stats);
    }
}
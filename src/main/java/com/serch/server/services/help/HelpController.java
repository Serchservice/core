package com.serch.server.services.help;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.help.requests.HelpAskRequest;
import com.serch.server.services.help.response.*;
import com.serch.server.services.help.services.HelpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/company/help")
@RequiredArgsConstructor
public class HelpController {
    private final HelpService service;

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<HelpCategoryResponse>>> fetchCategories() {
        ApiResponse<List<HelpCategoryResponse>> response = service.fetchCategories();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/category")
    public ResponseEntity<ApiResponse<HelpCategoryResponse>> fetchCategory(@RequestParam String key) {
        ApiResponse<HelpCategoryResponse> response = service.fetchCategory(key);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/sections")
    public ResponseEntity<ApiResponse<List<HelpSectionResponse>>> fetchSections(@RequestParam String key) {
        ApiResponse<List<HelpSectionResponse>> response = service.fetchSections(key);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/section")
    public ResponseEntity<ApiResponse<HelpSectionResponse>> fetchSection(@RequestParam String key) {
        ApiResponse<HelpSectionResponse> response = service.fetchSection(key);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/groups")
    public ResponseEntity<ApiResponse<List<HelpGroupResponse>>> fetchGroups(
            @RequestParam String category, @RequestParam String section
    ) {
        ApiResponse<List<HelpGroupResponse>> response = service.fetchGroups(category, section);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/group")
    public ResponseEntity<ApiResponse<HelpGroupResponse>> fetchGroup(@RequestParam String key) {
        ApiResponse<HelpGroupResponse> response = service.fetchGroup(key);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<HelpSearchResponse>>> search(@RequestParam String q) {
        ApiResponse<List<HelpSearchResponse>> response = service.search(q);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<HelpResponse>> fetchHelp(UUID id) {
        ApiResponse<HelpResponse> response = service.fetchHelp(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/ask")
    public ResponseEntity<ApiResponse<String>> ask(@RequestBody HelpAskRequest request) {
        ApiResponse<String> response = service.ask(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
package com.serch.server.services.company.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.responses.SpecialtyKeywordResponse;
import com.serch.server.services.company.services.SpecialtyKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company/keyword")
public class SpecialtyKeywordController {
    private final SpecialtyKeywordService specialtyKeywordService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SpecialtyKeywordResponse>>> searchService(@RequestParam String query) {
        var response = specialtyKeywordService.searchService(query);
        return new ResponseEntity<>(response, response.getStatus());
    }
}

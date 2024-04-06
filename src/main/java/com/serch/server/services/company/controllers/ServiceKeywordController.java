package com.serch.server.services.company.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.services.auth.responses.SpecialtyResponse;
import com.serch.server.services.company.services.KeywordService;
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
public class ServiceKeywordController {
    private final KeywordService keywordService;

    @GetMapping("/specialties")
    public ResponseEntity<ApiResponse<List<SpecialtyResponse>>> getAllSpecialties(@RequestParam SerchCategory type) {
        var response = keywordService.getAllSpecialties(type);
        return new ResponseEntity<>(response, response.getStatus());
    }
}

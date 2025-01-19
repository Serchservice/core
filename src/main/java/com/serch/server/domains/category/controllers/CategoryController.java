package com.serch.server.domains.category.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.category.response.SerchCategoryResponse;
import com.serch.server.domains.category.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService service;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<SerchCategoryResponse>>> categories() {
        ApiResponse<List<SerchCategoryResponse>> response = service.categories();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<SerchCategoryResponse>>> popular() {
        ApiResponse<List<SerchCategoryResponse>> response = service.popular();
        return new ResponseEntity<>(response, response.getStatus());
    }
}

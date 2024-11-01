package com.serch.server.services.shop;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.shop.requests.ShopDriveRequest;
import com.serch.server.services.shop.responses.DriveCategoryResponse;
import com.serch.server.services.shop.services.ShopDriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/drive")
public class ShopDriveController {
    private final ShopDriveService service;

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<DriveCategoryResponse>>> getCategories() {
        ApiResponse<List<DriveCategoryResponse>> response = service.categories();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> drive(@RequestBody ShopDriveRequest request) {
        ApiResponse<String> response = service.drive(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/rate")
    public ResponseEntity<ApiResponse<String>> rate(@RequestParam Long id, @RequestParam Double rating) {
        ApiResponse<String> response = service.rate(id, rating);
        return ResponseEntity.ok(response);
    }
}
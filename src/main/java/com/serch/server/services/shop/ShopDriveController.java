package com.serch.server.services.shop;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.shop.requests.ShopDriveRequest;
import com.serch.server.services.shop.services.ShopDriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/drive")
public class ShopDriveController {
    private final ShopDriveService service;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> drive(@RequestBody ShopDriveRequest request) {
        ApiResponse<String> response = service.drive(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/rate")
    public ResponseEntity<ApiResponse<String>> rate(@RequestParam Long id, @RequestParam Double rating) {
        ApiResponse<String> response = service.rateShop(id, rating);
        return ResponseEntity.ok(response);
    }
}
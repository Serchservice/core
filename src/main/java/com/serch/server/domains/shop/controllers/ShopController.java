package com.serch.server.domains.shop.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.shop.DriveScope;
import com.serch.server.enums.shop.ShopStatus;
import com.serch.server.domains.shop.requests.*;
import com.serch.server.domains.shop.responses.SearchShopResponse;
import com.serch.server.domains.shop.responses.ShopResponse;
import com.serch.server.domains.shop.services.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for managing shop-related operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shop")
public class ShopController {
    private final ShopService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShopResponse>>> fetchShops(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<ShopResponse>> response = service.fetch(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SearchShopResponse>>> drive(
            @RequestParam(required = false, name = "c") String category,
            @RequestParam(required = false, name = "q") String query,
            @RequestParam(name = "lng") Double lng,
            @RequestParam(name = "lat") Double lat,
            @RequestParam(required = false, name = "radius") Double radius,
            @RequestParam(required = false) DriveScope scope,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<SearchShopResponse>> response = service.drive(query, category, lng, lat, radius, scope, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> createShop(@RequestBody CreateShopRequest request) {
        ApiResponse<List<ShopResponse>> response = service.create(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create/weekday/{id}")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ShopResponse>> createWeekday(@PathVariable String id, @RequestBody ShopWeekdayRequest request) {
        ApiResponse<ShopResponse> response = service.create(id, request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create/service")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ShopResponse>> createService(@RequestBody CreateShopServiceRequest request) {
        ApiResponse<ShopResponse> response = service.create(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ShopResponse>> updateShop(@RequestBody UpdateShopRequest request) {
        ApiResponse<ShopResponse> response = service.update(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update/weekday")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ShopResponse>> updateWeekday(
            @RequestParam Long id, @RequestParam String shop, @RequestBody ShopWeekdayRequest request
    ) {
        ApiResponse<ShopResponse> response = service.update(id, shop, request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update/service")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ShopResponse>> updateService(@RequestBody UpdateShopServiceRequest request) {
        ApiResponse<ShopResponse> response = service.update(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/status")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ShopResponse>> changeStatus(@RequestParam String shop, @RequestParam ShopStatus status) {
        ApiResponse<ShopResponse> response = service.changeStatus(shop, status);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/status/all")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> changeAllStatus() {
        ApiResponse<List<ShopResponse>> response = service.changeAllStatus();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/remove")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> removeShop(@RequestParam String shop) {
        ApiResponse<List<ShopResponse>> response = service.remove(shop);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/remove/weekday")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ShopResponse>> removeWeekday(@RequestParam Long id, @RequestParam String shop) {
        ApiResponse<ShopResponse> response = service.removeWeekday(id, shop);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/remove/service")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ShopResponse>> removeService(@RequestParam Long id, @RequestParam String shop) {
        ApiResponse<ShopResponse> response = service.removeService(id, shop);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
package com.serch.server.services.shop;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.shop.DriveScope;
import com.serch.server.enums.shop.ShopStatus;
import com.serch.server.services.shop.requests.CreateShopRequest;
import com.serch.server.services.shop.requests.ShopWeekdayRequest;
import com.serch.server.services.shop.requests.UpdateShopRequest;
import com.serch.server.services.shop.responses.SearchShopResponse;
import com.serch.server.services.shop.responses.ShopResponse;
import com.serch.server.services.shop.services.ShopService;
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
@RequestMapping("/shop")
public class ShopController {
    private final ShopService shopService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShopResponse>>> fetchShops() {
        ApiResponse<List<ShopResponse>> response = shopService.fetch();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SearchShopResponse>>> drive(
            @RequestParam(required = false, name = "c") String category,
            @RequestParam(required = false, name = "q") String query,
            @RequestParam(name = "lng") Double lng,
            @RequestParam(name = "lat") Double lat,
            @RequestParam(required = false, name = "radius") Double radius,
            @RequestParam(required = false) DriveScope scope
            ) {
        ApiResponse<List<SearchShopResponse>> response = shopService.drive(query, category, lng, lat, radius, scope);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> createShop(@RequestBody CreateShopRequest request) {
        ApiResponse<List<ShopResponse>> response = shopService.create(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create/weekday")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ShopResponse>> createWeekday(
            @RequestParam String shop, @RequestBody ShopWeekdayRequest request
    ) {
        ApiResponse<ShopResponse> response = shopService.create(shop, request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create/service")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ShopResponse>> createService(
            @RequestParam String shop, @RequestBody String service
    ) {
        ApiResponse<ShopResponse> response = shopService.create(shop, service);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ShopResponse>> updateShop(@RequestBody UpdateShopRequest request) {
        ApiResponse<ShopResponse> response = shopService.update(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update/weekday")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ShopResponse>> updateWeekday(
            @RequestParam Long id, @RequestParam String shop, @RequestBody ShopWeekdayRequest request
    ) {
        ApiResponse<ShopResponse> response = shopService.update(id, shop, request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update/service")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ShopResponse>> updateService(
            @RequestParam Long id, @RequestParam String shop, @RequestBody String service
    ) {
        ApiResponse<ShopResponse> response = shopService.update(id, shop, service);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/status")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ShopResponse>> changeStatus(
            @RequestParam String shop, @RequestParam ShopStatus status
    ) {
        ApiResponse<ShopResponse> response = shopService.changeStatus(shop, status);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/status/all")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> changeAllStatus() {
        ApiResponse<List<ShopResponse>> response = shopService.changeAllStatus();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/remove")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> removeShop(@RequestParam String shop) {
        ApiResponse<List<ShopResponse>> response = shopService.remove(shop);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/remove/weekday")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ShopResponse>> removeWeekday(
            @RequestParam Long id, @RequestParam String shop
    ) {
        ApiResponse<ShopResponse> response = shopService.removeWeekday(id, shop);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/remove/service")
    @PreAuthorize("hasRole('BUSINESS') || hasRole('PROVIDER')")
    public ResponseEntity<ApiResponse<ShopResponse>> removeService(
            @RequestParam Long id, @RequestParam String shop
    ) {
        ApiResponse<ShopResponse> response = shopService.removeService(id, shop);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
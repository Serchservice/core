package com.serch.server.services.shop;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.shop.ShopStatus;
import com.serch.server.services.shop.requests.CreateShopRequest;
import com.serch.server.services.shop.requests.ShopWeekdayRequest;
import com.serch.server.services.shop.requests.UpdateShopRequest;
import com.serch.server.services.shop.responses.SearchShopResponse;
import com.serch.server.services.shop.responses.ShopResponse;
import com.serch.server.services.shop.services.ShopServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for managing shop-related operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {
    private final ShopServices shopService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShopResponse>>> fetchShops() {
        ApiResponse<List<ShopResponse>> response = shopService.fetchShops();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SearchShopResponse>>> drive(
            @RequestParam String category, @RequestParam String query,
            @RequestParam Double lng, @RequestParam Double lat,
            @RequestParam(required = false) Double radius
    ) {
        ApiResponse<List<SearchShopResponse>> response = shopService.drive(query, category, lng, lat, radius);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> createShop(@RequestBody CreateShopRequest request) {
        ApiResponse<List<ShopResponse>> response = shopService.createShop(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create/weekday")
    public ResponseEntity<ApiResponse<ShopResponse>> createWeekday(
            @RequestParam String shop, @RequestBody ShopWeekdayRequest request
    ) {
        ApiResponse<ShopResponse> response = shopService.createWeekday(shop, request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create/service")
    public ResponseEntity<ApiResponse<ShopResponse>> createService(
            @RequestParam String shop, @RequestBody String service
    ) {
        ApiResponse<ShopResponse> response = shopService.createService(shop, service);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<ShopResponse>> updateShop(@RequestBody UpdateShopRequest request) {
        ApiResponse<ShopResponse> response = shopService.updateShop(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update/weekday")
    public ResponseEntity<ApiResponse<ShopResponse>> updateWeekday(
            @RequestParam Long id, @RequestParam String shop, @RequestBody ShopWeekdayRequest request
    ) {
        ApiResponse<ShopResponse> response = shopService.updateWeekday(id, shop, request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update/service")
    public ResponseEntity<ApiResponse<ShopResponse>> updateService(
            @RequestParam Long id, @RequestParam String shop, @RequestBody String service
    ) {
        ApiResponse<ShopResponse> response = shopService.updateService(id, shop, service);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/status")
    public ResponseEntity<ApiResponse<ShopResponse>> changeStatus(
            @RequestParam String shop, @RequestParam ShopStatus status
    ) {
        ApiResponse<ShopResponse> response = shopService.changeStatus(shop, status);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/status/all")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> changeAllStatus() {
        ApiResponse<List<ShopResponse>> response = shopService.changeAllStatus();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> removeShop(@RequestParam String shop) {
        ApiResponse<List<ShopResponse>> response = shopService.removeShop(shop);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/remove/weekday")
    public ResponseEntity<ApiResponse<ShopResponse>> removeWeekday(
            @RequestParam Long id, @RequestParam String shop
    ) {
        ApiResponse<ShopResponse> response = shopService.removeWeekday(id, shop);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/remove/service")
    public ResponseEntity<ApiResponse<ShopResponse>> removeService(
            @RequestParam Long id, @RequestParam String shop
    ) {
        ApiResponse<ShopResponse> response = shopService.removeService(id, shop);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
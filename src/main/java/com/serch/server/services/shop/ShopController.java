package com.serch.server.services.shop;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.shop.requests.AddShopServiceRequest;
import com.serch.server.services.shop.requests.CreateShopRequest;
import com.serch.server.services.shop.requests.RemoveShopServiceRequest;
import com.serch.server.services.shop.requests.UpdateShopRequest;
import com.serch.server.services.shop.responses.ShopResponse;
import com.serch.server.services.shop.services.ShopServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {
    private final ShopServices shopService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShopResponse>>> fetchShops() {
        var response = shopService.fetchShops();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> createShop(@RequestBody CreateShopRequest request) {
        var response = shopService.createShop(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/service/add")
    public ResponseEntity<ApiResponse<String>> addService(@RequestBody AddShopServiceRequest request) {
        var response = shopService.addService(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> updateShop(@RequestBody UpdateShopRequest request) {
        var response = shopService.updateShop(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/status/toggle")
    public ResponseEntity<ApiResponse<String>> changeStatus(@RequestParam(name = "shop") String shop) {
        var response = shopService.changeStatus(shop);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/all/close")
    public ResponseEntity<ApiResponse<String>> markAllClosed() {
        var response = shopService.markAllClosed();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/all/open")
    public ResponseEntity<ApiResponse<String>> markAllOpen() {
        var response = shopService.markAllOpen();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/service/remove")
    public ResponseEntity<ApiResponse<String>> removeService(@RequestBody RemoveShopServiceRequest request) {
        var response = shopService.removeService(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}

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

/**
 * Controller class for managing shop-related operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {
    private final ShopServices shopService;

    /**
     * Fetches a list of all shops.
     *
     * @return A response containing the list of shops.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ShopResponse>>> fetchShops() {
        var response = shopService.fetchShops();
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Creates a new shop.
     *
     * @param request The request containing details of the shop to be created.
     * @return A response containing information about the created shop.
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> createShop(@RequestBody CreateShopRequest request) {
        var response = shopService.createShop(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Adds a service to a shop.
     *
     * @param request The request containing details of the service to be added.
     * @return A response indicating the success or failure of the operation.
     */
    @PostMapping("/service/add")
    public ResponseEntity<ApiResponse<String>> addService(@RequestBody AddShopServiceRequest request) {
        var response = shopService.addService(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Updates details of a shop.
     *
     * @param request The request containing updated details of the shop.
     * @return A response containing information about the updated shop.
     */
    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> updateShop(@RequestBody UpdateShopRequest request) {
        var response = shopService.updateShop(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Toggles the status of a shop between open and closed.
     *
     * @param shop The ID or name of the shop.
     * @return A response indicating the success or failure of the operation.
     */
    @PatchMapping("/status/toggle")
    public ResponseEntity<ApiResponse<String>> changeStatus(@RequestParam(name = "shop") String shop) {
        var response = shopService.changeStatus(shop);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Marks all shops as closed.
     *
     * @return A response indicating the success or failure of the operation.
     */
    @PatchMapping("/all/close")
    public ResponseEntity<ApiResponse<String>> markAllClosed() {
        var response = shopService.markAllClosed();
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Marks all shops as open.
     *
     * @return A response indicating the success or failure of the operation.
     */
    @PatchMapping("/all/open")
    public ResponseEntity<ApiResponse<String>> markAllOpen() {
        var response = shopService.markAllOpen();
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * Removes a service from a shop.
     *
     * @param request The request containing details of the service to be removed.
     * @return A response indicating the success or failure of the operation.
     */
    @DeleteMapping("/service/remove")
    public ResponseEntity<ApiResponse<String>> removeService(@RequestBody RemoveShopServiceRequest request) {
        var response = shopService.removeService(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
package com.serch.server.services.shop.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.shop.requests.AddShopServiceRequest;
import com.serch.server.services.shop.requests.CreateShopRequest;
import com.serch.server.services.shop.requests.RemoveShopServiceRequest;
import com.serch.server.services.shop.requests.UpdateShopRequest;
//import com.serch.server.services.shop.responses.SearchShopResponse;
import com.serch.server.services.shop.responses.ShopResponse;

import java.util.List;

public interface ShopServices {
    ApiResponse<List<ShopResponse>> createShop(CreateShopRequest request);
    ApiResponse<List<ShopResponse>> updateShop(UpdateShopRequest request);
    ApiResponse<List<ShopResponse>> fetchShops();
    ApiResponse<String> removeService(RemoveShopServiceRequest request);
    ApiResponse<String> addService(AddShopServiceRequest request);
    ApiResponse<String> changeStatus(String shopId);
    ApiResponse<String> markAllOpen();
    ApiResponse<String> markAllClosed();
//    ApiResponse<List<SearchShopResponse>> search(
//            String query, String category, Double longitude, Double latitude, Double radius
//    );
}

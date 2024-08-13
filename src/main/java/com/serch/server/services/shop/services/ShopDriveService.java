package com.serch.server.services.shop.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.shop.requests.ShopDriveRequest;

public interface ShopDriveService {
    ApiResponse<String> drive(ShopDriveRequest request);
    ApiResponse<String> rateShop(Long id, Double rating);
}
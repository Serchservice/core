package com.serch.server.services.shop.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.shop.requests.ShopDriveRequest;

/**
 * ShopDriveService interface defines operations related to managing shop drives,
 * including initiating drives and rating shops.
 * <p>
 * This service provides a high-level interface for shop-related activities,
 * encapsulating the implementation details and ensuring proper interactions
 * with shop drives and their ratings.
 */
public interface ShopDriveService {

    /**
     * Initiates a shop drive based on the provided request.
     * <p>
     * This method processes a request to start a drive for a specific shop,
     * which may include various parameters such as shop ID, drive duration,
     * and any promotional details. The response indicates whether the drive
     * initiation was successful or not.
     *
     * @param request The request containing the details for initiating the shop drive.
     * @return An ApiResponse containing a success message or an error description.
     */
    ApiResponse<String> drive(ShopDriveRequest request);

    /**
     * Rates a shop based on the provided rating.
     * <p>
     * This method allows users to submit a rating for a shop, typically after
     * a shopping experience or drive. The rating is associated with the shop's ID,
     * and the response indicates the success or failure of the rating operation.
     *
     * @param id The ID of the shop being rated.
     * @param rating The rating value to be assigned to the shop, typically within a defined range (e.g., 1-5).
     * @return An ApiResponse containing a success message or an error description.
     */
    ApiResponse<String> rate(Long id, Double rating);
}
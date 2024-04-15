package com.serch.server.services.shop.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.shop.requests.AddShopServiceRequest;
import com.serch.server.services.shop.requests.CreateShopRequest;
import com.serch.server.services.shop.requests.RemoveShopServiceRequest;
import com.serch.server.services.shop.requests.UpdateShopRequest;
import com.serch.server.services.shop.responses.SearchShopResponse;
import com.serch.server.services.shop.responses.ShopResponse;

import java.util.List;

/**
 * Interface defining operations related to shop management.
 *
 * @see ShopImplementation
 */
public interface ShopServices {
    /**
     * Creates a new shop based on the provided request.
     *
     * @param request The request containing details of the shop to be created.
     * @return A response containing information about the created shop.
     */
    ApiResponse<List<ShopResponse>> createShop(CreateShopRequest request);

    /**
     * Updates details of an existing shop.
     *
     * @param request The request containing updated details of the shop.
     * @return A response containing information about the updated shop.
     */
    ApiResponse<List<ShopResponse>> updateShop(UpdateShopRequest request);

    /**
     * Fetches a list of all existing shops.
     *
     * @return A response containing the list of shops.
     */
    ApiResponse<List<ShopResponse>> fetchShops();

    /**
     * Removes a service from a shop based on the provided request.
     *
     * @param request The request containing details of the service to be removed.
     * @return A response indicating the success or failure of the operation.
     */
    ApiResponse<String> removeService(RemoveShopServiceRequest request);

    /**
     * Adds a service to a shop based on the provided request.
     *
     * @param request The request containing details of the service to be added.
     * @return A response indicating the success or failure of the operation.
     */
    ApiResponse<String> addService(AddShopServiceRequest request);

    /**
     * Toggles the status of a shop between open and closed.
     *
     * @param shopId The ID or name of the shop.
     * @return A response indicating the success or failure of the operation.
     */
    ApiResponse<String> changeStatus(String shopId);

    /**
     * Marks all shops as open.
     *
     * @return A response indicating the success or failure of the operation.
     */
    ApiResponse<String> markAllOpen();

    /**
     * Marks all shops as closed.
     *
     * @return A response indicating the success or failure of the operation.
     */
    ApiResponse<String> markAllClosed();

    /**
     * Searches for shops based on a query or category within a specified radius.
     *
     * @param query The search query, which can be a service name or null.
     * @param category The category of the shop.
     * @param longitude The longitude of the user's location.
     * @param latitude The latitude of the user's location.
     * @param radius The search radius in kilometers.
     * @return An {@link ApiResponse} containing a list of {@link SearchShopResponse} objects
     *         filtered by the query or category.
     */
    ApiResponse<List<SearchShopResponse>> drive(String query, String category, Double longitude, Double latitude, Double radius);
}
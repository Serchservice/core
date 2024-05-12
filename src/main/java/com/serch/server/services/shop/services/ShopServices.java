package com.serch.server.services.shop.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.shop.ShopStatus;
import com.serch.server.services.shop.requests.CreateShopRequest;
import com.serch.server.services.shop.requests.UpdateShopRequest;
import com.serch.server.services.shop.requests.ShopWeekdayRequest;
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
     * @return {@link ApiResponse} list of {@link ShopResponse}
     */
    ApiResponse<List<ShopResponse>> createShop(CreateShopRequest request);

    /**
     * Creates a new weekday for an existing shop.
     *
     * @param request The request containing a weekday.
     * @param shopId The Shop id
     *
     * @return {@link ApiResponse} of updated {@link ShopResponse}
     */
    ApiResponse<ShopResponse> createWeekday(String shopId, ShopWeekdayRequest request);

    /**
     * Creates a new service for an existing shop.
     *
     * @param service The service.
     * @param shopId The Shop id
     *
     * @return {@link ApiResponse} of updated {@link ShopResponse}
     */
    ApiResponse<ShopResponse> createService(String shopId, String service);

    /**
     * Updates details of an existing shop.
     *
     * @param request The request containing updated details of the shop.
     * @return {@link ApiResponse} of updated {@link ShopResponse}
     */
    ApiResponse<ShopResponse> updateShop(UpdateShopRequest request);

    /**
     * Updates details of an existing shop's weekday.
     *
     * @param request The request containing updated details of the shop weekday.
     * @param id The ShopWeekday id
     * @param shopId The Shop id
     *
     * @return {@link ApiResponse} of updated {@link ShopResponse}
     */
    ApiResponse<ShopResponse> updateWeekday(Long id, String shopId, ShopWeekdayRequest request);

    /**
     * Updates details of an existing shop's service.
     *
     * @param service The request containing updated details of the shop service.
     * @param id The Service id
     * @param shopId The Shop id
     *
     * @return {@link ApiResponse} of updated {@link ShopResponse}
     */
    ApiResponse<ShopResponse> updateService(Long id, String shopId, String service);

    /**
     * Fetches a list of all existing shops.
     *
     * @return {@link ApiResponse} list of {@link ShopResponse}
     */
    ApiResponse<List<ShopResponse>> fetchShops();

    /**
     * Removes a service from a shop.
     *
     * @param shopId The shop id.
     * @param id The service id
     * @return {@link ApiResponse} of {@link ShopResponse}
     */
    ApiResponse<ShopResponse> removeService(Long id, String shopId);

    /**
     * Removes a shop.
     *
     * @param shopId The shop id.
     * @return {@link ApiResponse} list of updated {@link ShopResponse}
     */
    ApiResponse<List<ShopResponse>> removeShop(String shopId);

    /**
     * Removes a weekday from a shop.
     *
     * @param shopId The shop id.
     * @param id The weekday id
     * @return {@link ApiResponse} of {@link ShopResponse}
     */
    ApiResponse<ShopResponse> removeWeekday(Long id, String shopId);

    /**
     * Toggles the status of a shop between open and closed.
     *
     * @param shopId The ID or name of the shop.
     * @param status The status to change to.
     * @return {@link ApiResponse} of updated {@link ShopResponse}
     */
    ApiResponse<ShopResponse> changeStatus(String shopId, ShopStatus status);

    /**
     * Marks all shops as open/closed.
     *
     * @return {@link ApiResponse} list of updated {@link ShopResponse}
     */
    ApiResponse<List<ShopResponse>> changeAllStatus();

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

    /**
     * This will automatically check for shops that have reached its opening or closing time,
     * then it will either open or close the shop
     */
    void openOrCloseShops();
}
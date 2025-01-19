package com.serch.server.domains.shop.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.shop.services.implementations.ShopImplementation;
import com.serch.server.enums.shop.DriveScope;
import com.serch.server.enums.shop.ShopStatus;
import com.serch.server.models.shop.Shop;
import com.serch.server.domains.shop.requests.*;
import com.serch.server.domains.shop.responses.SearchShopResponse;
import com.serch.server.domains.shop.responses.ShopResponse;

import java.util.List;

/**
 * Interface defining operations related to shop management.
 * This interface provides various methods to manage shops, including creation, updating,
 * removal, and retrieval of shop details, alongside managing shop statuses and opening hours.
 *
 * @see ShopImplementation
 */
public interface ShopService {
    /**
     * Prepares and transforms the provided shop response into a response format.
     *
     * @param shop The {@link Shop} response to be formatted into a response.
     * @return A {@link ShopResponse} object containing the formatted shop information.
     */
    ShopResponse response(Shop shop);

    /**
     * Creates a new shop using the details provided in the request.
     * This method processes the creation request and returns a response
     * indicating the success or failure of the operation along with the created shop details.
     *
     * @param request The request containing details for the new shop.
     * @return An {@link ApiResponse} containing a list of {@link ShopResponse} representing the created shop(s).
     */
    ApiResponse<List<ShopResponse>> create(CreateShopRequest request);

    /**
     * Adds a new weekday entry for an existing shop.
     * This allows the shop to specify its operational hours on a given weekday,
     * facilitating better management of shop schedules.
     *
     * @param request The request containing details of the weekday to be created.
     * @param shopId The ID of the shop to which the weekday will be added.
     * @return An {@link ApiResponse} containing the updated {@link ShopResponse} with the new weekday information.
     */
    ApiResponse<ShopResponse> create(String shopId, ShopWeekdayRequest request);

    /**
     * Adds a new service to an existing shop.
     * This method allows for expanding the range of services offered by the shop,
     * enhancing its value to customers.
     *
     * @param request The request containing service details of the shop.
     * @return An {@link ApiResponse} containing the updated {@link ShopResponse} reflecting the newly added service.
     */
    ApiResponse<ShopResponse> create(CreateShopServiceRequest request);

    /**
     * Updates details of an existing shop with new information.
     * This method processes the update request and returns the updated shop details,
     * ensuring the shop's information remains current.
     *
     * @param request The request containing updated details of the shop.
     * @return An {@link ApiResponse} containing the updated {@link ShopResponse} with the new shop details.
     */
    ApiResponse<ShopResponse> update(UpdateShopRequest request);

    /**
     * Updates the details of a specific weekday for a shop.
     * This allows modifications to the shop's schedule and helps maintain accurate operating hours.
     *
     * @param request The request containing updated details for the weekday.
     * @param id The ID of the weekday to be updated.
     * @param shopId The ID of the shop that owns this weekday.
     * @return An {@link ApiResponse} containing the updated {@link ShopResponse} after the weekday modification.
     */
    ApiResponse<ShopResponse> update(Long id, String shopId, ShopWeekdayRequest request);

    /**
     * Updates the details of a service associated with a shop.
     * This method allows the shop to modify service offerings, such as changing descriptions or prices.
     *
     * @param request The request containing updated details for the shop service.
     * @return An {@link ApiResponse} containing the updated {@link ShopResponse} reflecting the service changes.
     */
    ApiResponse<ShopResponse> update(UpdateShopServiceRequest request);

    /**
     * Fetches a list of all existing shops in the system.
     * This method is useful for displaying shop information in a user interface or
     * for administrative purposes.
     *
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @return An {@link ApiResponse} containing a list of {@link ShopResponse} for all shops.
     */
    ApiResponse<List<ShopResponse>> fetch(Integer page, Integer size);

    /**
     * Removes a service from a specified shop.
     * This operation is essential for keeping the shop's offerings up-to-date and relevant,
     * allowing shops to discontinue services that are no longer provided.
     *
     * @param shopId The ID of the shop from which the service will be removed.
     * @param id The ID of the service to be removed.
     * @return An {@link ApiResponse} containing the updated {@link ShopResponse} after the service removal.
     */
    ApiResponse<ShopResponse> removeService(Long id, String shopId);

    /**
     * Removes a shop from the system.
     * This operation effectively deletes the shop and its associated response from the database,
     * which is crucial for maintaining an accurate representation of available shops.
     *
     * @param shopId The ID of the shop to be removed.
     * @return An {@link ApiResponse} containing a list of {@link ShopResponse} representing remaining shops.
     */
    ApiResponse<List<ShopResponse>> remove(String shopId);

    /**
     * Removes a weekday entry from a shop's schedule.
     * This operation is necessary for updating a shop's operational days and hours,
     * ensuring that the shop's schedule reflects current practices.
     *
     * @param shopId The ID of the shop from which the weekday will be removed.
     * @param id The ID of the weekday to be removed.
     * @return An {@link ApiResponse} containing the updated {@link ShopResponse} after the weekday removal.
     */
    ApiResponse<ShopResponse> removeWeekday(Long id, String shopId);

    /**
     * Toggles the status of a shop between open and closed.
     * This method allows for real-time management of shop operations based on operational hours
     * or other criteria, providing flexibility in managing shop availability.
     *
     * @param shopId The ID of the shop whose status will be changed.
     * @param status The new status to apply to the shop (open or closed).
     * @return An {@link ApiResponse} containing the updated {@link ShopResponse} reflecting the new status.
     */
    ApiResponse<ShopResponse> changeStatus(String shopId, ShopStatus status);

    /**
     * Marks all shops as either open or closed.
     * This method can be used for bulk operations, such as closing all shops at a certain time
     * or opening them for a special event.
     *
     * @return An {@link ApiResponse} containing a list of updated {@link ShopResponse} for all shops.
     */
    ApiResponse<List<ShopResponse>> changeAllStatus();

    /**
     * Searches for shops based on a given query or category within a specified radius.
     * This method is critical for users looking to find shops that meet specific criteria
     * based on their location, enhancing the user experience through targeted searches.
     *
     * @param scope The drive scope to return the result with
     * @param query The search query, which can be a service name or null for general search.
     * @param category The category of shops to filter the search.
     * @param longitude The longitude of the user's location for proximity searches.
     * @param latitude The latitude of the user's location for proximity searches.
     * @param radius The search radius in kilometers for locating nearby shops.
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     *
     * @return An {@link ApiResponse} containing a list of {@link SearchShopResponse} objects filtered by the query or category.
     */
    ApiResponse<List<SearchShopResponse>> drive(String query, String category, Double longitude, Double latitude, Double radius, DriveScope scope, Integer page, Integer size);

    /**
     * Searches for shops based on a query or category within a specified radius.
     * This method provides an alternative way to retrieve shop response directly,
     * without the wrapper of an {@link ApiResponse}, suitable for internal operations.
     *
     * @param scope The drive scope to return the result with
     * @param query The search query, which can be a service name or null for general search.
     * @param category The category of shops to filter the search.
     * @param longitude The longitude of the user's location for proximity searches.
     * @param latitude The latitude of the user's location for proximity searches.
     * @param radius The search radius in kilometers for locating nearby shops.
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     *
     * @return A list of {@link SearchShopResponse} objects filtered by the query or category.
     */
    List<SearchShopResponse> list(String query, String category, Double longitude, Double latitude, Double radius, DriveScope scope, Integer page, Integer size);

    /**
     * Automatically checks the current time against each shop's opening and closing times,
     * and adjusts the shop's status accordingly.
     * This method is crucial for maintaining accurate shop availability and operational efficiency.
     */
    void openOrCloseShops();
}
package com.serch.server.services.help.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.help.requests.HelpAskRequest;
import com.serch.server.services.help.response.*;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing help resources and queries.
 *
 * @see HelpImplementation
 */
public interface HelpService {

    /**
     * Retrieves all help categories.
     *
     * @return ApiResponse containing a list of HelpCategoryResponse objects.
     *
     * @see ApiResponse
     * @see HelpCategoryResponse
     */
    ApiResponse<List<HelpCategoryResponse>> fetchCategories();

    /**
     * Retrieves a specific help category by its key.
     *
     * @param category The unique key of the help category to retrieve.
     * @return ApiResponse containing the HelpCategoryResponse if found.
     *
     * @see HelpCategoryResponse
     * @see ApiResponse
     */
    ApiResponse<HelpCategoryResponse> fetchCategory(String category);

    /**
     * Retrieves all help sections for a given category.
     *
     * @param category The unique key of the help category.
     * @return ApiResponse containing a list of HelpSectionResponse objects.
     *
     * @see ApiResponse
     * @see HelpSectionResponse
     */
    ApiResponse<List<HelpSectionResponse>> fetchSections(String category);

    /**
     * Retrieves a specific help section by its key.
     *
     * @param category The unique key of the help category.
     * @return ApiResponse containing the HelpSectionResponse if found.
     *
     * @see HelpSectionResponse
     * @see ApiResponse
     */
    ApiResponse<HelpSectionResponse> fetchSection(String category);

    /**
     * Retrieves all help groups for a given category and section.
     *
     * @param category The unique key of the help category.
     * @param section  The unique key of the help section.
     * @return ApiResponse containing a list of HelpGroupResponse objects.
     *
     * @see ApiResponse
     * @see HelpGroupResponse
     */
    ApiResponse<List<HelpGroupResponse>> fetchGroups(String category, String section);

    /**
     * Retrieves a specific help group by its key.
     *
     * @param key The unique key of the help group to retrieve.
     * @return ApiResponse containing the HelpGroupResponse if found.
     *
     * @see HelpGroupResponse
     * @see ApiResponse
     */
    ApiResponse<HelpGroupResponse> fetchGroup(String key);

    /**
     * Retrieves a specific help resource by its ID.
     *
     * @param id The unique ID of the help resource to retrieve.
     * @return ApiResponse containing the HelpResponse if found.
     *
     * @see HelpResponse
     * @see ApiResponse
     */
    ApiResponse<HelpResponse> fetchHelp(UUID id);

    /**
     * Searches for help resources based on a query string.
     *
     * @param query The search query string.
     * @return ApiResponse containing a list of HelpSearchResponse objects matching the query.
     *
     * @see ApiResponse
     * @see HelpSearchResponse
     */
    ApiResponse<List<HelpSearchResponse>> search(String query);

    /**
     * Submits a help query.
     *
     * @param request The HelpAskRequest containing the details of the query.
     * @return ApiResponse indicating the status of the query submission.
     *
     * @see ApiResponse
     * @see HelpAskRequest
     */
    ApiResponse<String> ask(HelpAskRequest request);
}
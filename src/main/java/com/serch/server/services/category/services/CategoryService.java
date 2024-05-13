package com.serch.server.services.category.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.category.SerchCategoryResponse;

import java.util.List;

/**
 * This is the wrapper class that fetches the different Serch categories in the platform
 */
public interface CategoryService {
    /**
     * Gets all the categories in Serch
     *
     * @return ApiResponse list of SerchCategoryResponse
     *
     * @see SerchCategoryResponse
     */
    ApiResponse<List<SerchCategoryResponse>> categories();

    /**
     * Gets all the popular categories in Serch
     *
     * @return ApiResponse list of SerchCategoryResponse
     *
     * @see SerchCategoryResponse
     */
    ApiResponse<List<SerchCategoryResponse>> popular();
}

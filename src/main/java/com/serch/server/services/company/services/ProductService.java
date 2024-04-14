package com.serch.server.services.company.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.responses.ProductResponse;

import java.util.List;

/**
 * Service interface for managing products.
 *
 * @see com.serch.server.services.company.services.implementations.ProductImplementation
 */
public interface ProductService {

    /**
     * Retrieves all products.
     *
     * @return ApiResponse containing a list of ProductResponse objects.
     *
     * @see ApiResponse
     * @see ProductResponse
     */
    ApiResponse<List<ProductResponse>> getProducts();
}
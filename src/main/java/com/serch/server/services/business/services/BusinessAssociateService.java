package com.serch.server.services.business.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.requests.AddAssociateRequest;
import com.serch.server.services.business.controllers.BusinessAssociateController;
import com.serch.server.services.business.services.implementations.BusinessAssociateImplementation;

import java.util.UUID;

/**
 * Service interface for managing business associates, including adding, deleting, deactivating,
 * and activating providers associated with a business.
 *
 * @see BusinessAssociateImplementation
 * @see BusinessAssociateController
 */
public interface BusinessAssociateService {
    /**
     * Adds a provider as an associate to the business.
     * @param request The request containing information about the provider to be added.
     * @return ApiResponse indicating the success or failure of the operation.
     *
     * @see ApiResponse
     * @see AddAssociateRequest
     */
    ApiResponse<String> add(AddAssociateRequest request);

    /**
     * Deletes a provider associated with the business.
     * @param id The ID of the provider to be deleted.
     * @return ApiResponse indicating the success or failure of the operation.
     *
     * @see ApiResponse
     */
    ApiResponse<String> delete(UUID id);

    /**
     * Deactivates a provider associated with the business.
     * @param id The ID of the provider to be deactivated.
     * @return ApiResponse indicating the success or failure of the operation.
     *
     * @see ApiResponse
     */
    ApiResponse<String> deactivate(UUID id);

    /**
     * Activates a previously deactivated provider associated with the business.
     * @param id The ID of the provider to be activated.
     * @return ApiResponse indicating the success or failure of the operation.
     *
     * @see ApiResponse
     */
    ApiResponse<String> activate(UUID id);
}


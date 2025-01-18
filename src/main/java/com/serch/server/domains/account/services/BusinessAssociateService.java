package com.serch.server.domains.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.account.Profile;
import com.serch.server.domains.account.requests.AddAssociateRequest;
import com.serch.server.domains.account.controllers.BusinessAssociateController;
import com.serch.server.domains.account.responses.BusinessAssociateResponse;
import com.serch.server.domains.account.services.implementations.BusinessAssociateImplementation;

import java.util.List;
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
     * @return ApiResponse of list {@link BusinessAssociateResponse}
     *
     * @see ApiResponse
     * @see AddAssociateRequest
     */
    ApiResponse<List<BusinessAssociateResponse>> add(AddAssociateRequest request);

    /**
     * Resend an email invite
     *
     * @param id The associate provider id
     *
     * @return {@link ApiResponse}
     */
    ApiResponse<String> resendInvite(UUID id);

    /**
     * Deletes a provider associated with the business.
     * @param id The ID of the provider to be deleted.
     * @return ApiResponse of list {@link BusinessAssociateResponse}
     *
     * @see ApiResponse
     */
    ApiResponse<List<BusinessAssociateResponse>> delete(UUID id);

    /**
     * Deactivates a provider associated with the business.
     * @param id The ID of the provider to be deactivated.
     * @return ApiResponse of {@link BusinessAssociateResponse}
     *
     * @see ApiResponse
     */
    ApiResponse<BusinessAssociateResponse> deactivate(UUID id);

    /**
     * Activates a previously deactivated provider associated with the business.
     * @param id The ID of the provider to be activated.
     * @return ApiResponse of {@link BusinessAssociateResponse}
     *
     * @see ApiResponse
     */
    ApiResponse<BusinessAssociateResponse> activate(UUID id);

    /**
     * Fetches the list of business associates that belongs to the business
     *
     * @param q The search query for filtering
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     *
     * @return ApiResponse of list {@link BusinessAssociateResponse}
     *
     * @see ApiResponse
     */
    ApiResponse<List<BusinessAssociateResponse>> all(String q, Integer page, Integer size);

    /**
     * Builds the profile of an associate provider
     *
     * @param profile The profile of the provider {@link Profile}
     *
     * @return {@link BusinessAssociateResponse}
     */
    BusinessAssociateResponse response(Profile profile);
}


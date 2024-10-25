package com.serch.server.admin.services.organization.services;

import com.serch.server.admin.services.organization.data.OrganizationDto;
import com.serch.server.admin.services.organization.data.OrganizationResponse;
import com.serch.server.bases.ApiResponse;

import java.util.List;

/**
 * Service interface for managing organizations within the system.
 * Provides methods for retrieving, adding, deleting, and updating organization records.
 */
public interface OrganizationService {

    /**
     * Retrieves a list of all organizations in the system.
     *
     * @return An {@link ApiResponse} containing a list of {@link OrganizationResponse} objects
     *         representing all the organizations.
     */
    ApiResponse<List<OrganizationResponse>> getAllOrganizations();

    /**
     * Adds a new organization to the system.
     *
     * @param organization The {@link OrganizationResponse} object containing the details of the organization to be added.
     * @return An {@link ApiResponse} containing an updated list of {@link OrganizationResponse} objects
     *         including the newly added organization.
     */
    ApiResponse<List<OrganizationResponse>> add(OrganizationDto organization);

    /**
     * Deletes an existing organization from the system based on the given ID.
     *
     * @param id The unique identifier of the organization to be deleted.
     * @return An {@link ApiResponse} containing an updated list of {@link OrganizationResponse} objects
     *         after the specified organization has been removed.
     */
    ApiResponse<List<OrganizationResponse>> delete(Long id);

    /**
     * Updates the details of an existing organization.
     *
     * @param organization The {@link OrganizationDto} object containing the updated information.
     * @return An {@link ApiResponse} containing an updated list of {@link OrganizationDto} objects
     *         reflecting the changes made to the specified organization.
     */
    ApiResponse<List<OrganizationResponse>> update(OrganizationDto organization, Long id);

    /**
     * Retrieves the details of a specific organization based on the provided secret.
     *
     * @param secret A unique secret associated with the organization.
     * @return An {@link ApiResponse} containing the {@link OrganizationDto} object
     *         representing the organization matching the given secret.
     */
    ApiResponse<OrganizationDto> getOrganization(String secret);
}
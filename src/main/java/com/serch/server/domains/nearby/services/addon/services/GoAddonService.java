package com.serch.server.domains.nearby.services.addon.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.core.payment.responses.PaymentAuthorization;
import com.serch.server.domains.nearby.models.go.addon.GoAddonPlan;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.services.addon.responses.GoAddonResponse;
import com.serch.server.domains.nearby.services.addon.responses.GoAddonVerificationResponse;
import com.serch.server.domains.nearby.services.addon.responses.GoUserAddonResponse;

import java.util.List;

/**
 * Service interface for managing Go Add-ons.
 * Provides methods to retrieve, initialize, add, change, cancel, activate, and renew add-ons.
 */
public interface GoAddonService {
    /**
     * Retrieves a list of available Go add-ons.
     *
     * @return {@link ApiResponse} containing a list of {@link GoAddonResponse} objects.
     */
    ApiResponse<List<GoAddonResponse>> get();

    /**
     * Retrieves a list of all user add-ons.
     *
     * @return {@link ApiResponse} containing a list of {@link GoUserAddonResponse} objects.
     */
    ApiResponse<List<GoUserAddonResponse>> getAll();

    /**
     * Initializes a payment process for a given add-on ID.
     *
     * @param id The ID of the add-on.
     *
     * @return {@link ApiResponse} containing payment initialization data.
     */
    ApiResponse<InitializePaymentData> init(String id);

    /**
     * Fetches the payload data of the addon plan the user wants to register to, using a payment reference.
     *
     * @param reference The payment reference.
     *
     * @return {@link ApiResponse} containing {@link GoAddonVerificationResponse}.
     */
    ApiResponse<GoAddonVerificationResponse> see(String reference);

    /**
     * Adds a user add-on using a payment reference.
     *
     * @param reference The payment reference.
     *
     * @return {@link ApiResponse} containing a list of user add-ons.
     */
    ApiResponse<List<GoUserAddonResponse>> add(String reference);

    /**
     * Changes an existing user add-on.
     *
     * @param id The ID of the user add-on.
     * @param useExistingAuthorization Whether to use an existing payment authorization.
     *
     * @return {@link ApiResponse} containing the operation result.
     */
    ApiResponse<Object> change(String id, Boolean useExistingAuthorization);

    /**
     * Cancels a user add-on.
     *
     * @param id The ID of the user add-on.
     *
     * @return {@link ApiResponse} containing the updated list of user add-ons.
     */
    ApiResponse<List<GoUserAddonResponse>> cancel(Long id);

    /**
     * Cancels a switch operation for a user add-on.
     *
     * @param id The ID of the user add-on.
     *
     * @return {@link ApiResponse} containing the updated list of user add-ons.
     */
    ApiResponse<List<GoUserAddonResponse>> cancelSwitch(Long id);

    /**
     * Activates a user add-on.
     *
     * @param id The ID of the user add-on.
     *
     * @return {@link ApiResponse} containing the updated list of user add-ons.
     */
    ApiResponse<List<GoUserAddonResponse>> activate(Long id);

    /**
     * Renews a user add-on.
     *
     * @param id The ID of the user add-on.
     * @param useExistingAuthorization Whether to use an existing payment authorization.
     *
     * @return {@link ApiResponse} containing the operation result.
     */
    ApiResponse<Object> renew(Long id, Boolean useExistingAuthorization);

    /**
     * Creates a new {@link com.serch.server.domains.nearby.models.go.user.GoUserAddon} for the user
     *
     * @param user The {@link GoUser} who owns the plan
     * @param plan The {@link GoAddonPlan} to be added to the user
     * @param authorization The {@link PaymentAuthorization} data of the payment
     */
    void create(GoUser user, GoAddonPlan plan, PaymentAuthorization authorization);
}
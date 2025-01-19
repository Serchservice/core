package com.serch.server.domains.trip.services;

import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.models.trip.Trip;

import java.util.UUID;

/**
 * Service interface for handling trip-related payment processing.
 * <p>
 * This interface provides methods for processing service fees, initializing
 * payments, verifying payment statuses, and crediting users and providers.
 * </p>
 */
public interface TripPayService {

    /**
     * Processes the service fee charges and shared percentage for a given trip.
     * <p>
     * This method handles payment processing for service fees and any applicable
     * shared percentage for the trip.
     * </p>
     *
     * @param trip The {@link Trip} instance representing the trip for which the
     *             payment needs to be processed.
     * @return {@code true} if the payment is successfully processed, otherwise
     *         {@code false}.
     */
    Boolean processPayment(Trip trip);

    /**
     * Initializes payment for special trip categories, such as shopping trips.
     * <p>
     * This method prepares the payment initialization response required for web-based
     * payment processing.
     * </p>
     *
     * @param trip The {@link Trip} instance representing the special trip category
     *             for which the payment is to be initialized.
     * @return An {@link InitializePaymentData} object containing the payment
     *         initialization details for the web payment.
     */
    InitializePaymentData initializeShoppingPayment(Trip trip);

    /**
     * Processes payment for both the service fee and any additional fees.
     * <p>
     * This method handles payment processing for the total fees associated with
     * the trip, including both service charges and other fees.
     * </p>
     *
     * @param id   The ID of the provider for whom the payment is being processed.
     * @param trip The {@link Trip} instance representing the trip for which the
     *             payment needs to be processed.
     * @return An {@link InitializePaymentData} object containing the payment
     *         initialization details for the web payment.
     */
    InitializePaymentData pay(Trip trip, UUID id);

    /**
     * Verifies the payment status for a special trip category.
     * <p>
     * This method checks if the payment associated with the provided reference ID
     * has been successfully completed.
     * </p>
     *
     * @param reference The reference ID associated with the payment to be verified.
     * @return {@code true} if the payment is successfully verified, otherwise
     *         {@code false}.
     */
    Boolean verify(String reference);

    /**
     * Credits the provider and user after the completion of a trip.
     * <p>
     * This method processes the crediting of funds to both the user and provider
     * after the successful completion of the trip.
     * </p>
     *
     * @param trip The {@link Trip} instance representing the trip for which the
     *             crediting is to be performed.
     */
    void processCredit(Trip trip);
}
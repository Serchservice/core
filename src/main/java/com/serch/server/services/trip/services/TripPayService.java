package com.serch.server.services.trip.services;

import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.models.trip.Trip;

import java.util.UUID;

public interface TripPayService {
    /**
     * Process service fee charges and user shared percentage (if required)
     *
     * @param trip The {@link Trip} data to process payment for
     *
     * @return true (If everything goes well) or false (it there is an error)
     */
    Boolean processPayment(Trip trip);

    /**
     * Initialize payment for special trip categories
     *
     * @param trip The {@link Trip} data to process payment for
     *
     * @return {@link InitializePaymentData} for web payment
     */
    InitializePaymentData initializeShoppingPayment(Trip trip);

    /**
     * Pay for both service fee, and other fees
     *
     * @param id The provider id
     * @param trip The {@link Trip} data to process payment for
     *
     * @return {@link InitializePaymentData} for web payment
     */
    InitializePaymentData pay(Trip trip, UUID id);

    /**
     * Verify the payment for special categories
     *
     * @param reference The reference id
     *
     * @return true or false if verified
     */
    Boolean verify(String reference);

    /**
     * Credit the provider and user after trip
     *
     * @param trip The {@link Trip} data
     */
    void processCredit(Trip trip);
}
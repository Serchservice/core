package com.serch.server.core.sms.implementation;

/**
 * Service interface for managing SMS operations.
 * <p>
 * This interface defines methods for sending SMS messages. Implementations of this
 * interface are responsible for providing the actual SMS sending functionality,
 * handling various SMS providers, and managing the delivery of messages to users.
 * </p>
 */
public interface SmsService {

    /**
     * Sends an authentication SMS message for a trip.
     * <p>
     * This method is used to send a trip authentication message to the specified phone number.
     * The message content typically contains important information related to the trip,
     * such as authentication codes, trip details, or notifications.
     * </p>
     *
     * @param phone The phone number to which the SMS should be sent. This should be
     *              a valid phone number in a supported format.
     * @param content The content of the SMS message to be sent. This should include
     *                the necessary information related to the trip authentication.
     */
    void sendTripAuth(String phone, String content);
}
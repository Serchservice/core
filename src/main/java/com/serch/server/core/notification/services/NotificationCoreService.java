package com.serch.server.core.notification.services;

import com.serch.server.core.notification.requests.NotificationMessage;

/**
 * Interface for services notification services.
 * <p>
 * This interface defines the contract for sending notifications to
 * specific devices. Implementations of this interface are responsible
 * for handling the details of notification delivery mechanisms,
 * whether through push notifications, SMS, email, or other channels.
 * </p>
 */
public interface NotificationCoreService {

    /**
     * Sends a notification to a specific device.
     * <p>
     * This method accepts a notification message request, which
     * contains all the necessary information to compose and send
     * the notification. The implementation should ensure that the
     * notification is delivered to the intended device or user.
     * </p>
     *
     * @param request The {@link NotificationMessage} request that contains
     *                the details of the notification to be sent, including
     *                the recipient, message content, and any relevant metadata.
     *                The message can be of a generic type to allow for
     *                flexibility in the content structure.
     */
    void send(NotificationMessage<?> request);
}

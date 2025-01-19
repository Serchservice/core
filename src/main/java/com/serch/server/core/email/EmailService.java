package com.serch.server.core.email;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.email.implementations.EmailSender;
import com.serch.server.models.email.SendEmail;

/**
 * Service interface for managing email sending operations.
 * <p>
 * This interface defines methods for sending emails through the application.
 * Implementations should handle the specifics of email delivery, including
 * addressing, formatting, and error handling. The email service is crucial
 * for functionalities such as notifications, user communications, and alerts.
 * </p>
 *
 * @see EmailSender
 */
public interface EmailService {

    /**
     * Sends an email.
     * <p>
     * This method is responsible for sending an email using the provided
     * {@link SendEmail} object, which encapsulates the details of the email
     * such as the recipient, subject, body, and any attachments.
     * Implementations should ensure that the email is sent in a secure and
     * reliable manner, with appropriate error handling for any failures that
     * may occur during the sending process.
     * </p>
     *
     * @param email {@link SendEmail} response payload that contains all necessary
     *              information for sending the email, including recipient
     *              address, subject line, and body content.
     *
     * @throws com.serch.server.exceptions.others.EmailException if there is an error while sending the email,
     *                            such as network issues or SMTP server errors.
     *
     * @see ApiResponse
     */
    void send(SendEmail email);
}
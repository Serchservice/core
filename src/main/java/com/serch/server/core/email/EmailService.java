package com.serch.server.core.email;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.email.implementations.EmailSender;
import com.serch.server.domains.nearby.models.go.user.GoUserAddon;
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

    /**
     * Sends an email with additional context.
     * <p>
     * This method allows sending an email related to a specific {@link GoUserAddon}
     * with additional information. The implementation should determine how the
     * extra data and success status influence the email content.
     * </p>
     *
     * @param addon    {@link GoUserAddon} representing the user's addon subscription.
     * @param extra    Additional context or message to include in the email.
     * @param isSuccess Boolean flag indicating if the process related to the addon was successful.
     *
     * @throws com.serch.server.exceptions.others.EmailException if there is an error while sending the email.
     */
    void send(GoUserAddon addon, String extra, boolean isSuccess);

    /**
     * Sends an email related to a user's addon.
     * <p>
     * This method is a simplified version of the previous method, intended to send
     * an email based solely on the provided {@link GoUserAddon} without extra parameters.
     * </p>
     *
     * @param isSwitch Whether it is for an invoice switch or not
     * @param addon {@link GoUserAddon} representing the user's addon subscription.
     *
     * @throws com.serch.server.exceptions.others.EmailException if there is an error while sending the email.
     */
    void send(GoUserAddon addon, boolean isSwitch);

    /**
     * Sends an email related to a user's addon.
     * <p>
     * This method is a simplified version of the previous method, intended to send
     * an email based solely on the provided {@link GoUserAddon} without extra parameters.
     * </p>
     *
     * @param addon {@link GoUserAddon} representing the user's addon subscription.
     *
     * @throws com.serch.server.exceptions.others.EmailException if there is an error while sending the email.
     */
    void send(GoUserAddon addon, String link);
}
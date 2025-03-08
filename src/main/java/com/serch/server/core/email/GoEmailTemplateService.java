package com.serch.server.core.email;

import com.serch.server.domains.nearby.models.go.user.GoUserAddon;

/**
 * Service interface for generating email templates related to user addons.
 * <p>
 * This interface provides methods for constructing various email templates
 * such as failure notifications, success confirmations, and invoices.
 * Implementations should handle formatting and dynamic content population
 * to ensure clear and professional communication with users.
 * </p>
 */
public interface GoEmailTemplateService {
    /**
     * Generates an email template for a failed addon transaction.
     * <p>
     * This method constructs the HTML content for an email notifying the user
     * that their addon charge has failed. The email should include relevant details
     * about the failure and guidance on resolving the issue.
     * </p>
     *
     * @param addon         The {@link GoUserAddon} associated with the failed transaction.
     * @param errorMessage  A descriptive error message explaining the failure.
     * @return A string containing the HTML content of the failure email.
     */
    String failed(GoUserAddon addon, String errorMessage);

    /**
     * Generates an email template for a successful addon transaction.
     * <p>
     * This method constructs the HTML content for an email confirming that an addon
     * charge was successful. The email should include transaction details and any
     * relevant references.
     * </p>
     *
     * @param addon     The {@link GoUserAddon} associated with the successful transaction.
     * @param reference A transaction reference or identifier for the payment.
     * @return A string containing the HTML content of the success email.
     */
    String success(GoUserAddon addon, String reference);

    /**
     * Generates an email template for an addon invoice.
     * <p>
     * This method constructs the HTML content for an invoice email related to an addon charge.
     * The email should provide billing details, payment instructions, and relevant subscription
     * information.
     * </p>
     *
     * @param addon The {@link GoUserAddon} associated with the invoice.
     * @return A string containing the HTML content of the invoice email.
     */
    String invoice(GoUserAddon addon);

    /**
     * Generates an email template for an addon invoice.
     * <p>
     * This method constructs the HTML content for an invoice email related to an addon charge.
     * The email should provide billing details, payment instructions, and relevant subscription
     * information.
     * </p>
     *
     * @param addon The {@link GoUserAddon} associated with the invoice.
     * @return A string containing the HTML content of the invoice email.
     */
    String pending(GoUserAddon addon, String link);

    /**
     * Generates an email template for an invoice when switching plans.
     * <p>
     * This method constructs the HTML content for an invoice email related to switching
     * from one addon plan to another. It should provide details about the change, new charges,
     * and any necessary actions required from the user.
     * </p>
     *
     * @param addon The {@link GoUserAddon} associated with the invoice for the plan switch.
     * @return A string containing the HTML content of the invoice email for the switch.
     */
    String invoiceSwitch(GoUserAddon addon);
}
package com.serch.server.services.transaction.services;

import com.serch.server.models.subscription.Subscription;
import com.serch.server.services.transaction.services.implementations.InvoiceImplementation;

/**
 * This is the wrapper class for Invoice in payment.
 *
 * @see InvoiceImplementation
 */
public interface InvoiceService {
    /**
     * Creates a subscription invoice based on the subscription details.
     * @param subscription The subscription for which the invoice is created.
     * @param amount The amount associated with the subscription.
     * @param mode The payment mode for the subscription.
     * @param reference The reference number for the subscription.
     *
     * @see Subscription
     */
    void createInvoice(Subscription subscription, String amount, String mode, String reference);
}

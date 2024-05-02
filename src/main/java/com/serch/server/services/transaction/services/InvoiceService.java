package com.serch.server.services.transaction.services;

import com.serch.server.models.account.Profile;
import com.serch.server.models.subscription.Subscription;
import com.serch.server.services.transaction.services.implementations.InvoiceImplementation;

import java.util.List;

/**
 * This is the wrapper class for Invoice in payment.
 *
 * @see InvoiceImplementation
 */
public interface InvoiceService {
    /**
     * Creates a subscription invoice based on the subscription details.
     * @param subscription The subscription for which the invoice is created.
     * @param associates The list of associates tied to the subscription
     *
     * @see Subscription
     */
    void createInvoice(Subscription subscription, List<Profile> associates);
}

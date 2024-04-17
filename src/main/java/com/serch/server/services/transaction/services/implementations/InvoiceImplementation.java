package com.serch.server.services.transaction.services.implementations;

import com.serch.server.exceptions.subscription.SubscriptionException;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.subscription.Subscription;
import com.serch.server.models.subscription.SubscriptionAssociate;
import com.serch.server.models.subscription.SubscriptionInvoice;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.subscription.SubscriptionAssociateRepository;
import com.serch.server.repositories.subscription.SubscriptionInvoiceRepository;
import com.serch.server.services.transaction.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The InvoiceImplementation class implements the {@link InvoiceService} interface
 * and provides methods to verify subscription requests.
 * <p></p>
 * It interacts with payment services and repositories to create invoice for payments.
 *
 * @see InvoiceService
 * @see SubscriptionInvoiceRepository
 * @see BusinessProfileRepository
 * @see SubscriptionAssociateRepository
 */
@Service
@RequiredArgsConstructor
public class InvoiceImplementation implements InvoiceService {
    private final SubscriptionInvoiceRepository subscriptionInvoiceRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final SubscriptionAssociateRepository subscriptionAssociateRepository;

    @Override
    public void createInvoice(Subscription subscription, String amount, String mode, String reference) {
        SubscriptionInvoice invoice = new SubscriptionInvoice();

        if(subscription.getUser().isProfile()) {
            invoice.setSize(1);
        } else {
            BusinessProfile profile = businessProfileRepository.findById(subscription.getUser().getId())
                    .orElseThrow(() -> new SubscriptionException("Business not found"));
            invoice.setSize(
                    profile.getAssociates().stream()
                            .filter(sub -> !sub.getUser().isBusinessLocked())
                            .toList()
                            .size()
            );
        }
        invoice.setSubscription(subscription);
        invoice.setAmount(amount);
        invoice.setReference(reference);
        invoice.setMode(mode);
        invoice.setPlan(
                subscription.getChild() != null
                        ? subscription.getChild().getName()
                        : subscription.getPlan().getType().getType()
        );
        SubscriptionInvoice savedInvoice = subscriptionInvoiceRepository.save(invoice);

        if(!subscription.getUser().isProfile()) {
            BusinessProfile business = businessProfileRepository.findById(subscription.getUser().getId())
                    .orElseThrow(() -> new SubscriptionException("Business not found"));
            business.getAssociates()
                    .stream()
                    .filter(sub -> !sub.getUser().isBusinessLocked())
                    .forEach(profile -> {
                        SubscriptionAssociate associate = new SubscriptionAssociate();
                        associate.setInvoice(savedInvoice);
                        associate.setBusiness(business);
                        associate.setProfile(profile);
                        subscriptionAssociateRepository.save(associate);
                    });
        }
    }
}

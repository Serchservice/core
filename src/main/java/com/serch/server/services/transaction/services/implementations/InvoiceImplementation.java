package com.serch.server.services.transaction.services.implementations;

import com.serch.server.enums.account.AccountStatus;
import com.serch.server.exceptions.subscription.SubscriptionException;
import com.serch.server.mappers.SubscriptionMapper;
import com.serch.server.models.business.BusinessProfile;
import com.serch.server.models.business.BusinessSubscription;
import com.serch.server.models.account.Profile;
import com.serch.server.models.subscription.Subscription;
import com.serch.server.models.subscription.SubscriptionAssociate;
import com.serch.server.models.subscription.SubscriptionInvoice;
import com.serch.server.repositories.business.BusinessProfileRepository;
import com.serch.server.repositories.business.BusinessSubscriptionRepository;
import com.serch.server.repositories.subscription.SubscriptionAssociateRepository;
import com.serch.server.repositories.subscription.SubscriptionInvoiceRepository;
import com.serch.server.services.transaction.services.InvoiceService;
import com.serch.server.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    private final BusinessSubscriptionRepository businessSubscriptionRepository;

    @Override
    public void createInvoice(Subscription subscription, List<Profile> associates) {
        SubscriptionInvoice savedInvoice = getSubscriptionInvoice(subscription);
        if(!associates.isEmpty()) {
            BusinessProfile business = businessProfileRepository.findById(subscription.getUser().getId())
                    .orElseThrow(() -> new SubscriptionException("Business not found"));
            associates.forEach(profile -> {
                SubscriptionAssociate associate = new SubscriptionAssociate();
                associate.setInvoice(savedInvoice);
                associate.setBusiness(business);
                associate.setProfile(profile);
                subscriptionAssociateRepository.save(associate);

                businessSubscriptionRepository.findByProfile_Id(profile.getId())
                        .ifPresentOrElse(businessSubscription -> {
                            businessSubscription.setStatus(AccountStatus.ACTIVE);
                            businessSubscription.setUpdatedAt(LocalDateTime.now());
                            businessSubscriptionRepository.save(businessSubscription);
                        }, () -> {
                            BusinessSubscription businessSubscription = new BusinessSubscription();
                            businessSubscription.setBusiness(business);
                            businessSubscription.setProfile(profile);
                            businessSubscription.setStatus(AccountStatus.ACTIVE);
                            businessSubscriptionRepository.save(businessSubscription);
                        });
            });
        }
    }

    private SubscriptionInvoice getSubscriptionInvoice(Subscription subscription) {
        SubscriptionInvoice invoice = SubscriptionMapper.INSTANCE.invoice(subscription);
        invoice.setAmount(MoneyUtil.formatToNaira(subscription.getAmount()));
        invoice.setPlan(
                subscription.getChild() != null
                        ? subscription.getChild().getName()
                        : subscription.getPlan().getType().getType()
        );
        return subscriptionInvoiceRepository.save(invoice);
    }
}
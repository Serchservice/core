package com.serch.server.domains.nearby.services.addon.services.implementations;

import com.serch.server.core.email.EmailService;
import com.serch.server.core.notification.services.NotificationService;
import com.serch.server.core.payment.requests.InitializePaymentRequest;
import com.serch.server.core.payment.requests.PaymentChargeRequest;
import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.core.payment.responses.PaymentVerificationData;
import com.serch.server.core.payment.services.PaymentService;
import com.serch.server.domains.nearby.models.go.addon.GoAddonTransaction;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.models.go.user.GoUserAddon;
import com.serch.server.domains.nearby.models.go.user.GoUserAddonChange;
import com.serch.server.domains.nearby.repositories.go.GoAddonTransactionRepository;
import com.serch.server.domains.nearby.repositories.go.GoUserAddonChangeRepository;
import com.serch.server.domains.nearby.repositories.go.GoUserAddonRepository;
import com.serch.server.domains.nearby.repositories.go.GoUserRepository;
import com.serch.server.domains.nearby.services.addon.services.GoAddonService;
import com.serch.server.enums.nearby.GoUserAddonStatus;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.serch.server.enums.transaction.TransactionStatus.FAILED;
import static com.serch.server.enums.transaction.TransactionStatus.SUCCESSFUL;

@Slf4j
@Configuration
@Transactional
@RequiredArgsConstructor
public class GoAddonLifecycle {
    private final GoUserAddonChangeRepository goUserAddonChangeRepository;
    private final PaymentService paymentService;
    private final EmailService emailService;
    private final GoAddonService addonService;
    private final NotificationService goNotification;
    private final GoUserAddonRepository goUserAddonRepository;
    private final GoAddonTransactionRepository goAddonTransactionRepository;
    private final GoUserRepository goUserRepository;

    // Run every midnight to check for upcoming invoices (3 days before due)
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkUpcomingInvoices() {
        log.info("Processing checkUpcomingInvoices in GoAddonLifecycle for {}", TimeUtil.log());

        goUserAddonRepository.findByNextBillingDateAndIsRecurringTrue(LocalDate.now().plusDays(3))
                .forEach(this::sendInvoice);
    }

    private void sendInvoice(GoUserAddon addon) {
        addon.setStatus(GoUserAddonStatus.RENEWAL_DUE);
        addon.setUpdatedAt(TimeUtil.now());

        goUserAddonRepository.save(addon);
        emailService.send(addon, addon.hasSwitch());
    }

    // Run every midnight to charge subscriptions due today
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void chargeSubscriptions() {
        log.info("Processing chargeSubscriptions in GoAddonLifecycle for {}", TimeUtil.log());

        // Charge subscriptions due today
        goUserAddonRepository.findByNextBillingDateAndIsRecurringTrue(LocalDate.now()).forEach(this::processCharge);
    }

    @Transactional
    protected void processCharge(GoUserAddon addon) {
        try {
            processSubscription(addon);
        } catch (Exception e) {
            addon.setStatus(GoUserAddonStatus.RENEWAL_DUE);
            addon.setUpdatedAt(TimeUtil.now());

            goUserAddonRepository.save(addon);
            emailService.send(addon, e.getMessage(), false);
        }
    }

    private void processSubscription(GoUserAddon addon) {
        if(addon.hasSwitch()) {
            if(addon.getChange().getUseExistingAuthorization()) {
                var data = paymentService.charge(PaymentChargeRequest.build(
                        addon.getUser().getEmailAddress(),
                        addon.getChange().getPlan().getAmt(),
                        addon.getAuthorization().getAuthorizationCode()
                ));
                buildTransaction(addon, data);
                addon.setNextBillingDate(getNextBillingDate(addon));
                addon.setStatus(GoUserAddonStatus.ACTIVE);
                addon.setPlan(addon.getChange().getPlan());
                addon.setSubscriptionDate(LocalDate.now());
                addon.setUpdatedAt(TimeUtil.now());

                goUserAddonRepository.save(addon);
                goUserAddonChangeRepository.delete(addon.getChange());
                emailService.send(addon, data.getReference(), true);
            } else {
                var data = paymentService.initialize(request(addon.getChange()));
                buildTransaction(addon.getChange(), data);

                emailService.send(addon, data.getAuthorizationUrl());
            }
        } else if(addon.hasAuthorization()) {
            var data = paymentService.charge(PaymentChargeRequest.build(
                    addon.getUser().getEmailAddress(),
                    addon.getPlan().getAmt(),
                    addon.getAuthorization().getAuthorizationCode()
            ));
            buildTransaction(addon, data);
            addon.setNextBillingDate(getNextBillingDate(addon));
            addon.setStatus(GoUserAddonStatus.ACTIVE);
            addon.setSubscriptionDate(LocalDate.now());
            addon.setUpdatedAt(TimeUtil.now());

            goUserAddonRepository.save(addon);
            emailService.send(addon, data.getReference(), true);
        }
    }

    private void buildTransaction(GoUserAddon addon, PaymentVerificationData data) {
        GoAddonTransaction transaction = new GoAddonTransaction();

        transaction.setVerified(true);
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transaction.setUser(addon.getUser());
        transaction.setPlan(addon.getPlan());
        transaction.setReference(data.getReference());
        goAddonTransactionRepository.save(transaction);
    }

    private void buildTransaction(GoUserAddonChange change, InitializePaymentData data) {
        GoAddonTransaction transaction = new GoAddonTransaction();

        transaction.setVerified(false);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setUser(change.getAddon().getUser());
        transaction.setPlan(change.getPlan());
        transaction.setReference(data.getReference());
        goAddonTransactionRepository.save(transaction);
    }

    private LocalDate getNextBillingDate(GoUserAddon user) {
        LocalDate date = user.getNextBillingDate();

        return switch (user.getPlan().getInterval()) {
            case DAILY -> date.plusDays(1);
            case WEEKLY -> date.plusWeeks(1);
            case MONTHLY -> date.plusMonths(1);
            case QUARTERLY -> date.plusMonths(3);
            case YEARLY -> date.plusYears(1);
            default -> date;
        };
    }

    private InitializePaymentRequest request(GoUserAddonChange change) {
        InitializePaymentRequest payment = new InitializePaymentRequest();
        payment.setAmount(change.getPlan().getAmt());
        payment.setEmail(change.getAddon().getUser().getEmailAddress());
        payment.setCallbackUrl("https://nearby.serchservice.com");

        return payment;
    }

    // Run every midnight to charge subscriptions due today
    @Scheduled(cron = "0 0 0 * * ?")
    public void retry() {
        log.info("Processing retry in GoAddonLifecycle for {}", TimeUtil.log());

        // Retry failed payments for subscriptions with status RENEWAL_DUE
        goUserAddonRepository.findByStatusAndNextBillingDateBefore(GoUserAddonStatus.RENEWAL_DUE, LocalDate.now())
                .forEach(this::retryFailedPayment);
    }

    private void retryFailedPayment(GoUserAddon addon) {
        if (addon.getTrials() >= 3) {
            addon.setStatus(GoUserAddonStatus.EXPIRED);
            addon.setUpdatedAt(TimeUtil.now());

            goUserAddonRepository.save(addon);
            return;
        }

        try {
            processSubscription(addon);
        } catch (Exception e) {
            addon.setTrials(addon.getTrials() + 1);
            addon.setStatus(GoUserAddonStatus.RENEWAL_DUE);
            addon.setUpdatedAt(TimeUtil.now());

            log.error("Retry failed for subscription: {}", addon.getId());

            goUserAddonRepository.save(addon);
            emailService.send(addon, e.getMessage(), false);
        }
    }

    /**
     * Executes the remove method periodically, to check and verify pending transactions
     * This method is scheduled to run every 10 minutes.
     */
    @Transactional
    @Scheduled(cron = "0 0/10 * * * ?")
    public void processPendingTransactions() {
        log.info("Processing processPendingTransactions in GoAddonLifecycle for {}", TimeUtil.log());

        List<GoAddonTransaction> transactions = goAddonTransactionRepository.findAllPending();

        transactions.forEach(transaction -> {
            GoUser user = transaction.getUser();

            try {
                var data = paymentService.verify(transaction.getReference());

                transaction.setStatus(SUCCESSFUL);
                transaction.setVerified(true);
                transaction.setUpdatedAt(TimeUtil.now());
                goAddonTransactionRepository.save(transaction);

                user.setCustomerCode(data.getCustomer().getCode());
                user.setUpdatedAt(TimeUtil.now());
                goUserRepository.save(user);

                addonService.create(user, transaction.getPlan(), data.getAuthorization());

                if(!user.getMessagingToken().isEmpty()) {
                    goNotification.send(
                            user.getMessagingToken(),
                            "Your addon is now registered.",
                            "%s now belongs to you, powering your user experience".formatted(transaction.getPlan().getName())
                    );
                }
            } catch (Exception e) {
                transaction.setStatus(FAILED);
                transaction.setVerified(false);
                transaction.setUpdatedAt(TimeUtil.now());
                goAddonTransactionRepository.save(transaction);
            }
        });
    }
}
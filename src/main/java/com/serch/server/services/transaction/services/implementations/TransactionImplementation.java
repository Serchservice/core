package com.serch.server.services.transaction.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import com.serch.server.exceptions.transaction.WalletException;
import com.serch.server.models.schedule.SchedulePayment;
import com.serch.server.models.subscription.SubscriptionInvoice;
import com.serch.server.models.transaction.Transaction;
import com.serch.server.models.transaction.Wallet;
import com.serch.server.repositories.schedule.SchedulePaymentRepository;
import com.serch.server.repositories.subscription.SubscriptionInvoiceRepository;
import com.serch.server.repositories.transaction.TransactionRepository;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.services.shared.services.GuestAuthService;
import com.serch.server.services.transaction.responses.AssociateTransactionData;
import com.serch.server.services.transaction.responses.TransactionResponse;
import com.serch.server.services.transaction.services.TransactionService;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The TransactionImplementation class provides implementation for handling user transactions.
 *
 * @see TransactionService
 */
@Service
@RequiredArgsConstructor
public class TransactionImplementation implements TransactionService {
    private final GuestAuthService guestAuthService;
    private final UserUtil userUtil;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final SubscriptionInvoiceRepository subscriptionInvoiceRepository;
    private final SchedulePaymentRepository schedulePaymentRepository;

    @Override
    public ApiResponse<List<TransactionResponse>> transactions() {
        Wallet wallet = walletRepository.findByUser_Id(userUtil.getUser().getId())
                .orElseThrow(() -> new WalletException("User not found"));

        List<TransactionResponse> transactions = new ArrayList<>();

        transactions.addAll(
                transactionRepository.findBySender_User_Id(wallet.getUser().getId()).isEmpty()
                        ? List.of()
                        : transactionRepository.findBySender_User_Id(wallet.getUser().getId())
                        .stream()
                        .filter(transaction -> transaction.getType() != TransactionType.TRIP)
                        .map(transaction -> createResponse(transaction, wallet))
                        .toList()
        );

        transactions.addAll(
                transactionRepository.findByAccount(wallet.getId()).isEmpty()
                        ? List.of()
                        : transactionRepository.findByAccount(wallet.getId())
                        .stream()
                        .filter(transaction -> transaction.getType() != TransactionType.TRIP)
                        .map(transaction -> createResponse(transaction, wallet))
                        .toList()
        );

        transactions.addAll(
                transactionRepository.findByAccount(wallet.getId()).isEmpty()
                        ? List.of()
                        : transactionRepository.findByAccount(wallet.getId())
                        .stream()
                        .filter(transaction -> transaction.getType() == TransactionType.TRIP)
                        .map(transaction -> createTripResponse(transaction, wallet))
                        .toList()
        );

        transactions.addAll(
                schedulePaymentRepository.findByPayment(wallet.getUser().getId()).isEmpty()
                         ? List.of()
                        : schedulePaymentRepository.findByPayment(wallet.getUser().getId())
                        .stream()
                        .map(pay -> schedule(pay, wallet))
                        .toList()
        );

        transactions.sort(Comparator.comparing(TransactionResponse::getCreatedAt));
        return new ApiResponse<>(transactions);
    }

    private static TransactionResponse schedule(SchedulePayment pay, Wallet wallet) {
        TransactionResponse response = new TransactionResponse();
        response.setMode("SCHEDULE");
        response.setId(pay.getId());
        response.setAmount(MoneyUtil.formatToNaira(pay.getAmount()));
        response.setReason(
                "This payment was made because %s closed schedule %s"
                        .formatted(
                                pay.getSchedule().getUser().isSameAs(pay.getSchedule().getClosedBy())
                                        ? pay.getSchedule().getProvider().getFirstName()
                                        : pay.getSchedule().getUser().getFirstName(),
                                pay.getSchedule().getClosedAt()
                        ) +
                        " the scheduled time %s"
                                .formatted(pay.getSchedule().getTime())
        );
        response.setCompletedAt(TimeUtil.formatDay(pay.getUpdatedAt()));
        response.setStatus(pay.getStatus());
        response.setType(
                wallet.getUser().getId() == pay.getSchedule().getClosedBy()
                        ? TransactionType.WITHDRAW
                        : TransactionType.FUNDING
        );
        response.setIsIncoming(wallet.getUser().getId() != pay.getSchedule().getClosedBy());
        response.setTime(TimeUtil.formatTime(pay.getCreatedAt()));
        response.setRequestedAt(TimeUtil.formatDay(pay.getCreatedAt()));
        response.setCreatedAt(pay.getCreatedAt());
        response.setName(
                pay.getSchedule().getUser().isSameAs(pay.getSchedule().getClosedBy())
                        ? pay.getSchedule().getProvider().getFullName()
                        : pay.getSchedule().getUser().getFullName()
        );
        response.setReference(pay.getSchedule().getId());
        return response;
    }

    private static TransactionResponse createResponse(Transaction transaction, Wallet wallet) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setAmount(MoneyUtil.formatToNaira(transaction.getAmount()));
        response.setReason(transaction.getReason());
        response.setCompletedAt(TimeUtil.formatDay(transaction.getUpdatedAt()));
        response.setStatus(transaction.getStatus());
        response.setType(transaction.getType());
        response.setIsIncoming(transaction.getType() == TransactionType.FUNDING);
        response.setTime(TimeUtil.formatTime(transaction.getCreatedAt()));
        response.setRequestedAt(TimeUtil.formatDay(transaction.getCreatedAt()));
        response.setCreatedAt(transaction.getCreatedAt());
        response.setName(transaction.getSender().getUser().getFullName());
        response.setReference(transaction.getReference());

        if(transaction.getAssociate() != null) {
            response.setAssociates(List.of(
                    AssociateTransactionData.builder()
                            .name(transaction.getAssociate().getFullName())
                            .category(transaction.getAssociate().getCategory().getType())
                            .rating(transaction.getAssociate().getRating())
                            .avatar(transaction.getAssociate().getAvatar())
                            .build()
            ));
        }
        response.setMode("WALLET");
        return response;
    }

    private TransactionResponse createTripResponse(Transaction transaction, Wallet wallet) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setAmount(MoneyUtil.formatToNaira(transaction.getTrip().getShared().getAmount()));
//        response.setReason(
//                "Transaction for Shared Trip %s - %s".formatted(
//                        transaction.getTrip().getShared().getStatus().getSharedLink().getLink(),
//                        transaction.getTrip().getShared().getStatus().getId()
//                )
//        );
        response.setCompletedAt(TimeUtil.formatDay(transaction.getUpdatedAt()));
        response.setStatus(transaction.getStatus());
        response.setType(transaction.getType());
        response.setIsIncoming(wallet.getId().equals(transaction.getAccount()));
        response.setTime(TimeUtil.formatTime(transaction.getCreatedAt()));
        response.setRequestedAt(TimeUtil.formatDay(transaction.getTrip().getShared().getCreatedAt()));
        response.setCreatedAt(transaction.getCreatedAt());
        response.setName(transaction.getSender().getUser().getFullName());
        response.setReference(transaction.getReference());
//        response.setPricing(
//                guestAuthService.getSharedPricingData(
//                        transaction.getTrip().getShared().getStatus().getSharedLink(),
//                        transaction.getTrip().getShared()
//                )
//        );
        response.setMode("TRIP");
        return response;
    }

    @Override
    public ApiResponse<List<TransactionResponse>> subscriptions() {
        List<TransactionResponse> transactions = new ArrayList<>(
                subscriptionInvoiceRepository.findBySubscription_User_Id(userUtil.getUser().getId()).isEmpty()
                        ? List.of()
                        : subscriptionInvoiceRepository.findBySubscription_User_Id(userUtil.getUser().getId())
                        .stream()
                        .map(this::subscription)
                        .toList()
        );
        transactions.sort(Comparator.comparing(TransactionResponse::getCreatedAt));
        return new ApiResponse<>(transactions);
    }

    private TransactionResponse subscription(SubscriptionInvoice invoice) {
        TransactionResponse response = new TransactionResponse();
        response.setId("%s - %s".formatted(invoice.getSubscription().getId(), invoice.getId()));
        response.setAmount(MoneyUtil.formatToNaira(BigDecimal.valueOf(Integer.parseInt(invoice.getAmount()))));
        response.setCompletedAt(TimeUtil.formatDay(invoice.getUpdatedAt()));
        response.setStatus(TransactionStatus.SUCCESSFUL);
        response.setIsIncoming(false);
        response.setTime(TimeUtil.formatTime(invoice.getCreatedAt()));
        response.setRequestedAt(TimeUtil.formatDay(invoice.getCreatedAt()));
        response.setCreatedAt(invoice.getCreatedAt());
        response.setName(invoice.getSubscription().getUser().getFullName());
        response.setReference(invoice.getReference());

        if(!invoice.getAssociates().isEmpty()) {
            response.setAssociates(
                    invoice.getAssociates()
                            .stream()
                            .map(associate -> AssociateTransactionData.builder()
                                    .name(associate.getProfile().getFullName())
                                    .category(associate.getProfile().getCategory().getType())
                                    .rating(associate.getProfile().getRating())
                                    .avatar(associate.getProfile().getAvatar())
                                    .build()
                            )
                            .toList()
            );
        }
        response.setMode(invoice.getMode());
        return response;
    }
}

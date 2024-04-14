package com.serch.server.services.transaction.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import com.serch.server.exceptions.transaction.WalletException;
import com.serch.server.mappers.TransactionMapper;
import com.serch.server.models.auth.User;
import com.serch.server.models.conversation.Call;
import com.serch.server.models.subscription.PlanChild;
import com.serch.server.models.subscription.PlanParent;
import com.serch.server.models.subscription.Subscription;
import com.serch.server.models.transaction.Transaction;
import com.serch.server.models.transaction.Wallet;
import com.serch.server.repositories.call.CallRepository;
import com.serch.server.repositories.subscription.PlanChildRepository;
import com.serch.server.repositories.subscription.PlanParentRepository;
import com.serch.server.repositories.subscription.SubscriptionRepository;
import com.serch.server.repositories.transaction.TransactionRepository;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.services.payment.core.PaymentService;
import com.serch.server.services.payment.requests.InitializePaymentRequest;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.payment.responses.PaymentVerificationData;
import com.serch.server.services.subscription.services.InitSubscriptionService;
import com.serch.server.services.subscription.services.VerifySubscriptionService;
import com.serch.server.services.transaction.requests.*;
import com.serch.server.services.transaction.responses.WalletResponse;
import com.serch.server.services.transaction.services.WalletService;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.UserUtil;
import com.serch.server.utils.WalletUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * The WalletImplementation class provides implementation for managing user wallets and transactions.
 * <p></p>
 * This class handles various operations related to user wallets, such as creating wallets, funding wallets,
 * making payments, and viewing wallet details.
 *
 * @see WalletService
 */
@Service
@RequiredArgsConstructor
public class WalletImplementation implements WalletService {
    private final PaymentService paymentService;
    private final InitSubscriptionService initSubscriptionService;
    private final VerifySubscriptionService verifySubscriptionService;
    private final WalletUtil util;
    private final UserUtil userUtil;
    private final WalletRepository walletRepository;
    private final CallRepository callRepository;
    private final TransactionRepository transactionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PlanParentRepository planParentRepository;
    private final PlanChildRepository planChildRepository;

    @Value("${application.wallet.fund-limit}")
    private Integer FUND_LIMIT;
    @Value("${application.call.tip2fix.amount}")
    private Integer TIP2FIX_AMOUNT;
    @Value("${application.wallet.withdraw-limit}")
    private Integer WITHDRAW_LIMIT;

    @Override
    public void createWallet(User user) {
        if(walletRepository.existsByUser_Id(user.getId())) {
            throw new WalletException("User already owns a wallet");
        }

        if(user.getRole() != Role.ASSOCIATE_PROVIDER) {
            Wallet wallet = new Wallet();
            wallet.setUser(user);
            wallet.setBalance(BigDecimal.valueOf(0.00));
            wallet.setDeposit(BigDecimal.valueOf(0.00));
            walletRepository.save(wallet);
        }
    }

    private static String generateReference() {
        return "STR_%s".formatted(UUID.randomUUID().toString().substring(0, 8));
    }

    @Override
    public ApiResponse<String> pay(PayRequest request) {
        if(request.getType() == TransactionType.T2F) {
            return payTip2Fix(request);
        } else if(request.getType() == TransactionType.SUBSCRIPTION) {
            return paySubscription(request);
        } else if(request.getType() == TransactionType.TRIP) {
            return payTrip(request);
        } else {
            return new ApiResponse<>("Error");
        }
    }

    private ApiResponse<String> payTip2Fix(PayRequest request) {
        Optional<Call> call = callRepository.findById(request.getEvent());
        if(call.isPresent()) {
            Optional<Wallet> sender = walletRepository.findByUser_Id(call.get().getCaller().getSerchId());
            if(sender.isPresent()) {
                BalanceUpdateRequest senderUpdate = BalanceUpdateRequest.builder()
                        .type(TransactionType.T2F)
                        .user(call.get().getCaller().getSerchId())
                        .amount(BigDecimal.valueOf(TIP2FIX_AMOUNT))
                        .build();
                BalanceUpdateRequest receiverUpdate = BalanceUpdateRequest.builder()
                        .type(TransactionType.T2F)
                        .user(call.get().getCalled().getSerchId())
                        .amount(BigDecimal.valueOf(TIP2FIX_AMOUNT))
                        .build();

                if(call.get().getCalled().getUser().getRole() == Role.ASSOCIATE_PROVIDER) {
                    Optional<Wallet> wallet = walletRepository.findByUser_Id(call.get().getCalled().getBusiness().getSerchId());
                    if(wallet.isPresent()) {
                        receiverUpdate.setUser(call.get().getCalled().getBusiness().getSerchId());
                        Transaction transaction = processTip2Fix(
                                sender.get(), call.get(), senderUpdate,
                                receiverUpdate, wallet.get()
                        );
                        transaction.setAssociate(call.get().getCalled());
                        transactionRepository.save(transaction);
                    } else {
                        return new ApiResponse<>("Recipient not found");
                    }
                } else {
                    Optional<Wallet> wallet = walletRepository.findByUser_Id(call.get().getCalled().getSerchId());
                    if(wallet.isPresent()) {
                        Transaction transaction = processTip2Fix(
                                sender.get(), call.get(), senderUpdate,
                                receiverUpdate, wallet.get()
                        );
                        transactionRepository.save(transaction);
                    } else {
                        return new ApiResponse<>("Recipient not found");
                    }
                }
            } else {
                return new ApiResponse<>("Caller not found");
            }
        } else {
            return new ApiResponse<>("Call not found");
        }
        return new ApiResponse<>("Payment successful", HttpStatus.OK);
    }

    private Transaction processTip2Fix(
            Wallet sender, Call call, BalanceUpdateRequest senderUpdate,
            BalanceUpdateRequest receiverUpdate, Wallet receiver
    ) {
        util.updateBalance(senderUpdate);
        util.updateBalance(receiverUpdate);

        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(TIP2FIX_AMOUNT));
        transaction.setType(TransactionType.T2F);
        transaction.setVerified(true);
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transaction.setAccount(receiver.getId());
        transaction.setSender(sender);
        transaction.setCall(call);
        transaction.setReference(generateReference());
        return transaction;
    }

    @Override
    public ApiResponse<String> payTrip(PayRequest request) {
        return null;
    }

    @Override
    public ApiResponse<String> paySubscription(PayRequest request) {
        Subscription subscription = subscriptionRepository.findByUser_Id(userUtil.getUser().getId())
                .orElseThrow(() -> new WalletException("You cannot pay with wallet"));

        if(subscription.isActive()) {
            throw new WalletException("You have an active subscription");
        } else {
            if (subscription.getPlan().getType() != PlanType.FREE && (request.getEvent() == null || request.getEvent().isEmpty())) {
                if (subscription.getChild() != null) {
                    request.setEvent(subscription.getChild().getId());
                } else {
                    request.setEvent(subscription.getPlan().getId());
                }
            }
            return subscribeWithWalletBalance(request, subscription);
        }
    }

    private ApiResponse<String> subscribeWithWalletBalance(PayRequest request, Subscription subscription) {
        BigDecimal amount;
        Wallet wallet = walletRepository.findByUser_Id(userUtil.getUser().getId())
                .orElseThrow(() -> new WalletException("Wallet not found"));

        Optional<PlanParent> parent = planParentRepository.findById(request.getEvent());
        if(parent.isPresent()) {
            amount = BigDecimal.valueOf(Integer.parseInt(parent.get().getAmount()));

            if(!subscription.getUser().isProfile()) {
                int businessAmount = Integer.parseInt(parent.get().getAmount()) * initSubscriptionService.getBusinessSize(subscription);
                amount = BigDecimal.valueOf(businessAmount);
            }

            if(util.isBalanceSufficient(
                    BalanceUpdateRequest.builder()
                            .amount(amount)
                            .user(subscription.getUser().getId())
                            .type(TransactionType.SUBSCRIPTION)
                            .build()
            )) {
                BalanceUpdateRequest update = BalanceUpdateRequest.builder()
                        .type(TransactionType.SUBSCRIPTION)
                        .user(subscription.getUser().getId())
                        .amount(amount)
                        .build();
                util.updateBalance(update);

                subscription.setUpdatedAt(LocalDateTime.now());
                subscription.setPlanStatus(PlanStatus.ACTIVE);
                subscription.setSubscribedAt(LocalDateTime.now());
                subscription.setPlan(parent.get());

                subscriptionRepository.save(subscription);
            } else {
                throw new WalletException("Insufficient balance to finish transaction");
            }
        } else {
            PlanChild child = planChildRepository.findById(request.getEvent())
                    .orElseThrow(() -> new WalletException("Plan not found"));

            amount = BigDecimal.valueOf(Integer.parseInt(child.getAmount()));

            if(!subscription.getUser().isProfile()) {
                int businessAmount = Integer.parseInt(child.getAmount()) * initSubscriptionService.getBusinessSize(subscription);
                amount = BigDecimal.valueOf(businessAmount);
            }

            if(util.isBalanceSufficient(
                    BalanceUpdateRequest.builder()
                            .amount(amount)
                            .user(subscription.getUser().getId())
                            .type(TransactionType.SUBSCRIPTION)
                            .build()
            )) {
                BalanceUpdateRequest update = BalanceUpdateRequest.builder()
                        .type(TransactionType.SUBSCRIPTION)
                        .user(subscription.getUser().getId())
                        .amount(amount)
                        .build();
                util.updateBalance(update);

                subscription.setUpdatedAt(LocalDateTime.now());
                subscription.setSubscribedAt(LocalDateTime.now());
                subscription.setPlanStatus(PlanStatus.ACTIVE);
                subscription.setPlan(child.getParent());
                subscription.setChild(child);

                subscriptionRepository.save(subscription);
            } else {
                throw new WalletException("Insufficient balance to finish transaction");
            }
        }

        String ref = generateReference();
        verifySubscriptionService.createInvoice(subscription, String.valueOf(amount), "WALLET", ref);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(TransactionType.SUBSCRIPTION);
        transaction.setVerified(true);
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transaction.setAccount(wallet.getId());
        transaction.setSender(wallet);
        transaction.setReference(ref);
        transactionRepository.save(transaction);
        return new ApiResponse<>("Success", HttpStatus.OK);
    }

    @Override
    public ApiResponse<String> checkIfUserCanPayForTripWithWallet(String trip) {
        return null;
    }

    @Override
    public ApiResponse<InitializePaymentData> fundWallet(FundRequest request) {
        Wallet wallet = walletRepository.findByUser_Id(userUtil.getUser().getId())
                .orElseThrow(() -> new WalletException("User wallet not found"));
        if(request.getAmount() >= FUND_LIMIT) {
            InitializePaymentRequest paymentRequest = new InitializePaymentRequest();
            paymentRequest.setAmount(String.valueOf(request.getAmount()));
            paymentRequest.setEmail(wallet.getUser().getEmailAddress());
            paymentRequest.setCallbackUrl(request.getCallbackUrl());
            InitializePaymentData data = paymentService.initialize(paymentRequest);

            Transaction transaction = new Transaction();
            transaction.setAmount(BigDecimal.valueOf(request.getAmount()));
            transaction.setType(TransactionType.FUNDING);
            transaction.setVerified(false);
            transaction.setStatus(TransactionStatus.PENDING);
            transaction.setAccount(wallet.getId());
            transaction.setSender(wallet);
            transaction.setReference(data.getReference());
            transactionRepository.save(transaction);

            return new ApiResponse<>(data);
        } else {
            throw new WalletException("You can only fund %s above".formatted(FUND_LIMIT));
        }
    }

    @Override
    public ApiResponse<String> verifyFund(String reference) {
        Transaction transaction = transactionRepository.findByReference(reference)
                .orElseThrow(() -> new WalletException("Transaction not found"));

        if(transaction.getStatus() == TransactionStatus.SUCCESSFUL) {
            throw new WalletException("Transaction is already verified");
        } else {
            PaymentVerificationData data = paymentService.verify(transaction.getReference());
            if(data.getStatus().equalsIgnoreCase("success")) {
                BalanceUpdateRequest update = BalanceUpdateRequest.builder()
                        .type(TransactionType.FUNDING)
                        .user(transaction.getSender().getUser().getId())
                        .amount(transaction.getAmount())
                        .build();
                util.updateBalance(update);

                transaction.setStatus(TransactionStatus.SUCCESSFUL);
                transaction.setVerified(true);
                transaction.setUpdatedAt(LocalDateTime.now());
                transactionRepository.save(transaction);

                return new ApiResponse<>("Verified", HttpStatus.OK);
            } else {
                transaction.setStatus(TransactionStatus.FAILED);
                transaction.setVerified(false);
                transaction.setReason(data.getMessage());
                transaction.setUpdatedAt(LocalDateTime.now());
                transactionRepository.save(transaction);

                return new ApiResponse<>("Not Verified", HttpStatus.OK);
            }
        }
    }

    @Override
    public ApiResponse<String> requestWithdraw(WithdrawRequest request) {
        Wallet wallet = walletRepository.findByUser_Id(userUtil.getUser().getId())
                .orElseThrow(() -> new WalletException("User wallet not found"));
        if(request.getAmount() >= WITHDRAW_LIMIT) {
            if(util.isBalanceSufficient(
                    BalanceUpdateRequest.builder()
                            .amount(BigDecimal.valueOf(request.getAmount()))
                            .user(wallet.getUser().getId())
                            .type(TransactionType.WITHDRAW)
                            .build()
            )) {
                Transaction transaction = new Transaction();
                transaction.setSender(wallet);
                transaction.setType(TransactionType.WITHDRAW);
                transaction.setAccount(wallet.getAccountName() + " - " + wallet.getAccountNumber());
                transaction.setAmount(BigDecimal.valueOf(request.getAmount()));
                transaction.setReference(generateReference());
                transaction.setStatus(TransactionStatus.PENDING);
                transaction.setVerified(false);
                transactionRepository.save(transaction);
                return new ApiResponse<>(
                        "Withdrawal request is being processed. Expect payment in 3 working days",
                        HttpStatus.CREATED
                );
            } else {
                throw new WalletException("Insufficient balance");
            }
        } else {
            throw new WalletException("You can only withdraw %s above".formatted(WITHDRAW_LIMIT));
        }
    }

    @Override
    public ApiResponse<WalletResponse> view() {
        Wallet wallet = walletRepository.findByUser_Id(userUtil.getUser().getId())
                .orElseThrow(() -> new WalletException("User wallet not found"));

        WalletResponse response = TransactionMapper.INSTANCE.wallet(wallet);
        response.setBalance(MoneyUtil.formatToNaira(wallet.getBalance()));
        response.setDeposit(MoneyUtil.formatToNaira(wallet.getDeposit()));
        return new ApiResponse<>(response);
    }

    @Override
    public ApiResponse<String> update(WalletUpdateRequest request) {
        Wallet wallet = walletRepository.findByUser_Id(userUtil.getUser().getId())
                .orElseThrow(() -> new WalletException("User wallet not found"));

        if(request.getAccountName() != null && !request.getAccountName().isEmpty()) {
            wallet.setAccountName(request.getAccountName());
        }
        if(request.getAccountNumber() != null && !request.getAccountNumber().isEmpty()) {
            wallet.setAccountNumber(request.getAccountNumber());
        }
        if(request.getBankName() != null && !request.getBankName().isEmpty()) {
            wallet.setBankName(request.getBankName());
        }
        walletRepository.save(wallet);
        return new ApiResponse<>("Wallet updated", HttpStatus.OK);
    }
}

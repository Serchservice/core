package com.serch.server.domains.transaction.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.notification.services.NotificationService;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import com.serch.server.exceptions.transaction.WalletException;
import com.serch.server.mappers.TransactionMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.conversation.Call;
import com.serch.server.models.schedule.Schedule;
import com.serch.server.models.transaction.Transaction;
import com.serch.server.models.transaction.Wallet;
import com.serch.server.models.trip.Trip;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.conversation.CallRepository;
import com.serch.server.repositories.schedule.ScheduleRepository;
import com.serch.server.repositories.transaction.TransactionRepository;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.core.payment.services.PaymentService;
import com.serch.server.core.payment.requests.InitializePaymentRequest;
import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.domains.transaction.requests.*;
import com.serch.server.domains.transaction.responses.*;
import com.serch.server.domains.transaction.services.WalletService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.serch.server.enums.transaction.TransactionStatus.FAILED;
import static com.serch.server.enums.transaction.TransactionStatus.SUCCESSFUL;

/**
 * The WalletImplementation class provides implementation for managing user wallets and transactions.
 * <p></p>
 * This class handles various operations related to user wallets, such as creating wallets, funding wallets,
 * making payments, and viewing wallet details.
 *
 * @see WalletService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletImplementation implements WalletService {
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final UserUtil userUtil;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final ScheduleRepository scheduleRepository;
    private final ProfileRepository profileRepository;
    private final CallRepository callRepository;
    private final TripRepository tripRepository;

    @Value("${application.wallet.fund-limit}")
    private Integer FUND_LIMIT;
    @Value("${application.wallet.withdraw-limit}")
    private Integer WITHDRAW_LIMIT;
    @Value("${application.call.tip2fix.amount}")
    private Integer TIP2FIX_AMOUNT;

    private static final Integer PAYDAY = 3;

    @Override
    public void create(User user) {
        if(walletRepository.existsByUser_Id(user.getId())) {
            return;
        }

        if(user.getRole() != Role.ASSOCIATE_PROVIDER) {
            Wallet wallet = new Wallet();
            wallet.setUser(user);
            wallet.setBalance(BigDecimal.valueOf(0.00));
            wallet.setDeposit(BigDecimal.valueOf(0.00));
            walletRepository.save(wallet);
        }
    }

    @Override
    @Transactional
    public void undo(User user) {
        walletRepository.findByUser_Id(user.getId()).ifPresent(wallet -> {
            walletRepository.delete(wallet);
            walletRepository.flush();
        });
    }

    @Override
    public ApiResponse<InitializePaymentData> fund(FundWalletRequest request) {
        Wallet wallet = walletRepository.findByUser_Id(userUtil.getUser().getId())
                .orElseThrow(() -> new WalletException("User wallet not found"));

        if(request.getAmount() >= FUND_LIMIT) {
            InitializePaymentData data = initializePayment(request, wallet);
            saveCreditTransaction(request, wallet, data);

            return new ApiResponse<>(data);
        } else {
            throw new WalletException("You can only fund with %s above".formatted(MoneyUtil.formatToNaira(BigDecimal.valueOf(FUND_LIMIT))));
        }
    }

    protected void saveCreditTransaction(FundWalletRequest request, Wallet wallet, InitializePaymentData data) {
        Transaction transaction = new Transaction();

        transaction.setAmount(BigDecimal.valueOf(request.getAmount()));
        transaction.setType(TransactionType.FUNDING);
        transaction.setVerified(false);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setAccount(wallet.getId());
        transaction.setMode("PAYSTACK");
        transaction.setSender(String.valueOf(userUtil.getUser().getId()));
        transaction.setReference(data.getReference());
        transactionRepository.save(transaction);
    }

    protected InitializePaymentData initializePayment(FundWalletRequest request, Wallet wallet) {
        InitializePaymentRequest payment = new InitializePaymentRequest();
        payment.setAmount(String.valueOf(request.getAmount()));
        payment.setEmail(wallet.getUser().getEmailAddress());
        payment.setCallbackUrl(request.getCallbackUrl());

        return paymentService.initialize(payment);
    }

    @Override
    public ApiResponse<WalletResponse> verify(String reference) {
        Transaction transaction = transactionRepository.findByReference(reference)
                .orElseThrow(() -> new WalletException("Transaction not found"));

        if(UUID.fromString(transaction.getSender()).equals(userUtil.getUser().getId())) {
            if(transaction.getStatus() == SUCCESSFUL) {
                throw new WalletException("Transaction is already verified");
            } else {
                try {
                    var data = paymentService.verify(transaction.getReference());
                    creditWallet(transaction.getAmount(), userUtil.getUser().getId());
                    transaction.setStatus(SUCCESSFUL);
                    transaction.setVerified(true);
                    transaction.setMode(data.getChannel().toUpperCase().replaceAll("_", " "));
                    transaction.setUpdatedAt(TimeUtil.now());
                    transactionRepository.save(transaction);

                    return view();
                } catch (Exception e) {
                    transaction.setStatus(FAILED);
                    transaction.setVerified(false);
                    transaction.setReason("Error in verification");
                    transaction.setUpdatedAt(TimeUtil.now());
                    transactionRepository.save(transaction);

                    throw new WalletException("Couldn't verify transaction. Try again or contact support");
                }
            }
        } else {
            throw new WalletException("Access denied. You cannot verify this transaction");
        }
    }

    protected void creditWallet(BigDecimal amount, UUID receiver) {
        walletRepository.findByUser_Id(receiver)
                .ifPresent(wallet -> {
                    wallet.setDeposit(wallet.getDeposit().add(amount));
                    wallet.setUpdatedAt(TimeUtil.now());
                    walletRepository.save(wallet);

                    processUnclearedDebts(wallet.getId());
                });
    }

    @Override
    public void processUnclearedDebts(String id) {
        walletRepository.findById(id)
                .ifPresent(wallet -> {
                    if(wallet.getUncleared().compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal amount = wallet.getUncleared();

                        if(wallet.getDeposit().compareTo(amount) > 0) {
                            wallet.setDeposit(wallet.getDeposit().subtract(amount));
                            wallet.setUncleared(BigDecimal.ZERO);
                            wallet.setUpdatedAt(TimeUtil.now());
                            walletRepository.save(wallet);

                            getDebitTransaction(amount, wallet, SUCCESSFUL, "Debt Payment", String.valueOf(wallet.getUser().getId()));

                            notificationService.send(wallet.getUser().getId(), amount);
                        }
                    }
                });
    }

    @Override
    public ApiResponse<String> withdraw(WithdrawRequest request) {
        Wallet wallet = walletRepository.findByUser_Id(userUtil.getUser().getId())
                .orElseThrow(() -> new WalletException("User wallet not found"));

        if(request.getAmount() >= WITHDRAW_LIMIT) {
            /// Check if the money in the user's balance is sufficient for withdraws
            if(wallet.getBalance().compareTo(BigDecimal.valueOf(request.getAmount())) > 0) {
                if(wallet.hasBank()) {
                    saveDebitTransaction(BigDecimal.valueOf(request.getAmount()), wallet, String.valueOf(userUtil.getUser().getId()));

                    return new ApiResponse<>(
                            "Withdrawal request is being processed. Expect payment in %s working days".formatted(PAYDAY),
                            HttpStatus.CREATED
                    );
                } else {
                    throw new WalletException("You cannot request for withdrawal without a bank account");
                }
            } else {
                throw new WalletException("Insufficient balance");
            }
        } else {
            throw new WalletException("You can only withdraw %s above".formatted(MoneyUtil.formatToNaira(BigDecimal.valueOf(WITHDRAW_LIMIT))));
        }
    }

    protected void saveDebitTransaction(BigDecimal amount, Wallet wallet, String sender) {
        Transaction transaction = getDebitTransaction(amount, wallet, TransactionStatus.PENDING, "Debit", sender);

        wallet.setUncleared(wallet.getUncleared().add(transaction.getAmount()));
        wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
        wallet.setUpdatedAt(TimeUtil.now());
        walletRepository.save(wallet);
    }

    private Transaction getDebitTransaction(BigDecimal amount, Wallet wallet, TransactionStatus status, String mode, String sender) {
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setAccount(wallet.getAccountName() + " - " + wallet.getAccountNumber());
        transaction.setAmount(amount);
        transaction.setReference(HelperUtil.generateReference("STR"));
        transaction.setStatus(status);
        transaction.setMode(String.format("SERCH | %s", mode));
        transaction.setVerified(false);

        return transactionRepository.save(transaction);
    }

    @Override
    public ApiResponse<WalletResponse> view() {
        Wallet wallet = walletRepository.findByUser_Id(userUtil.getUser().getId())
                .orElseThrow(() -> new WalletException("User wallet not found"));

        return new ApiResponse<>(buildWallet(wallet, TransactionMapper.INSTANCE.wallet(wallet)));
    }

    @Override
    public <T extends WalletResponse> T buildWallet(Wallet wallet, T response) {
        response.setBalance(MoneyUtil.formatToNaira(wallet.getBalance()));
        response.setDeposit(MoneyUtil.formatToNaira(wallet.getDeposit()));
        response.setUncleared(MoneyUtil.formatToNaira(wallet.getUncleared()));
        response.setPayout(MoneyUtil.formatToNaira(wallet.getPayout()));
        response.setWallet(wallet(wallet.getId()));
        response.setNextPayday(formatNextPayday(wallet));

        return response;
    }

    private String wallet(String wallet) {
        if(wallet.length() >= 4) {
            return "Serch Wallet - %s".formatted(wallet.substring(wallet.length() - 4));
        } else {
            return "Serch Wallet - %s".formatted(wallet);
        }
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

        if(request.getPayday() != null && !request.getPayday().equals(wallet.getPayday())) {
            if(request.getPayday() < 3) {
                throw new WalletException("Due to our transfer policy, you cannot set your paydays to anything less than %s days".formatted(PAYDAY));
            } else {
                wallet.setPayday(request.getPayday());
            }
        }

        if(request.getPayout() != null && !BigDecimal.valueOf(request.getPayout()).equals(wallet.getPayout())) {
            if(request.getPayout() >= WITHDRAW_LIMIT) {
                wallet.setPayout(BigDecimal.valueOf(request.getPayout()));
            } else {
                throw new WalletException("Your payout amount must be %s above".formatted(MoneyUtil.formatToNaira(BigDecimal.valueOf(WITHDRAW_LIMIT))));
            }
        }

        if(request.getPayoutOnPayday() != null && !request.getPayoutOnPayday().equals(wallet.getPayoutOnPayday())) {
            wallet.setPayoutOnPayday(request.getPayoutOnPayday());
        }

        wallet.setUpdatedAt(TimeUtil.now());
        walletRepository.save(wallet);
        return new ApiResponse<>("Wallet updated", HttpStatus.OK);
    }

    @Override
    public void processTip2FixCallPayment(String channel, UUID sender, Profile receiver) {
        UUID recipientId = receiver.getId();
        if(receiver.isAssociate()) {
            recipientId = receiver.getBusiness().getId();
        }

        Transaction transaction = getTip2FixTransaction(sender, recipientId, channel);

        walletRepository.findByUser_Id(sender).ifPresentOrElse(wallet -> {
            if(wallet.getDeposit().compareTo(transaction.getAmount()) > 0) {
                wallet.setDeposit(wallet.getDeposit().subtract(transaction.getAmount()));
            } else {
                BigDecimal debit = transaction.getAmount().subtract(wallet.getDeposit());
                wallet.setBalance(wallet.getBalance().subtract(debit));
                wallet.setDeposit(BigDecimal.ZERO);
            }

            wallet.setUpdatedAt(TimeUtil.now());
            walletRepository.save(wallet);

            notificationService.send(sender, false, transaction.getAmount());
        }, () -> {
            throw new WalletException("Couldn't process Tip2Fix payment. Try again");
        });

        walletRepository.findByUser_Id(recipientId).ifPresentOrElse(wallet -> {
            wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
            wallet.setUpdatedAt(TimeUtil.now());
            walletRepository.save(wallet);

            notificationService.send(wallet.getUser().getId(), true, transaction.getAmount());
        }, () -> {
            throw new WalletException("Couldn't process Tip2Fix payment. Try again");
        });

        transaction.setUpdatedAt(TimeUtil.now());
        transaction.setVerified(true);
        transaction.setStatus(SUCCESSFUL);
        transactionRepository.save(transaction);
    }

    protected Transaction getTip2FixTransaction(UUID sender, UUID recipientId, String channel) {
        Transaction transaction = new Transaction();
        transaction.setSender(String.valueOf(sender));
        transaction.setType(TransactionType.TIP2FIX);
        transaction.setAccount(String.valueOf(recipientId));
        transaction.setAmount(BigDecimal.valueOf(TIP2FIX_AMOUNT));
        transaction.setReference(HelperUtil.generateReference("ST2F"));
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setMode("SERCH");
        transaction.setVerified(false);
        transaction.setEvent(channel);

        return transactionRepository.save(transaction);
    }

    @Override
    public void checkBalanceForTip2Fix(UUID caller) {
        Wallet wallet = walletRepository.findByUser_Id(caller)
                .orElseThrow(() -> new WalletException("You need to have a Serch wallet to place T2F calls"));

        if(!(wallet.getDeposit().add(wallet.getBalance()).compareTo(BigDecimal.valueOf(TIP2FIX_AMOUNT)) >= 0)) {
            throw new WalletException("Insufficient balance to start tip2fix. Tip2Fix is charged at ₦%s".formatted(TIP2FIX_AMOUNT));
        }
    }

    @Override
    public ApiResponse<List<TransactionGroupResponse>> transactions(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Transaction> transactions = transactionRepository.findBySenderOrReceiver(String.valueOf(userUtil.getUser().getId()), pageable);

        if(transactions == null || transactions.getTotalElements() == 0) {
            return new ApiResponse<>(List.of());
        } else {
            return response(transactions.getContent());
        }
    }

    @Override
    public ApiResponse<List<TransactionGroupResponse>> recent() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Transaction> page = transactionRepository.findRecentBySenderOrReceiver(String.valueOf(userUtil.getUser().getId()), pageable);

        if(page == null || page.getTotalElements() == 0) {
            return new ApiResponse<>(List.of());
        } else {
            return response(page.getContent());
        }
    }

    private ApiResponse<List<TransactionGroupResponse>> response(List<Transaction> transactions) {
        List<TransactionGroupResponse> list = new ArrayList<>();
        Map<LocalDate, List<Transaction>> map = transactions.stream()
                .collect(Collectors.groupingBy(transaction -> transaction.getCreatedAt().toLocalDate()));

        map.forEach((date, transactionList) -> {
            TransactionGroupResponse group = new TransactionGroupResponse();
            group.setLabel(TimeUtil.formatChatLabel(LocalDateTime.of(date, transactionList.getFirst().getCreatedAt().toLocalTime()), userUtil.getUser().getTimezone()));

            List<TransactionResponse> response = transactionList.stream()
                    .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                    .map(this::response).toList();
            group.setTransactions(response);
            list.add(group);
        });

        return new ApiResponse<>(list);
    }

    protected TransactionResponse response(Transaction transaction) {
        return switch (transaction.getType()) {
            case FUNDING -> prepareFundingTransactionResponse(transaction);
            case WITHDRAW -> prepareWithdrawTransactionResponse(transaction);
            case SCHEDULE -> prepareScheduleTransactionResponse(transaction);
            case TIP2FIX -> prepareTip2FixTransactionResponse(transaction);
            case TRIP_CHARGE -> prepareTripChargeTransactionResponse(transaction);
            case SHOPPING -> new TransactionResponse();
            case TRIP_SHARE -> prepareTripShareTransactionResponse(transaction);
        };
    }

    protected TransactionResponse prepareFundingTransactionResponse(Transaction transaction) {
        TransactionResponse response = getResponse(transaction, true);

        response.setRecipient("Your %s".formatted(
                walletRepository.findById(transaction.getAccount())
                        .map(wallet -> wallet(wallet.getId()))
                        .orElse("Wallet")
        ));

        TransactionData data = TransactionMapper.INSTANCE.data(transaction);
        data.setHeader("Transaction %s".formatted(transaction.getStatus().getType()));
        data.setDescription("Your deposit of %s to your Serch wallet %s".formatted(
                MoneyUtil.formatToNaira(transaction.getAmount()),
                transaction.getStatus().getSentence()
        ));
        data.setDate(TimeUtil.formatDay(transaction.getCreatedAt(), userUtil.getUser().getTimezone()));
        data.setUpdatedAt(TimeUtil.formatDay(transaction.getUpdatedAt(), userUtil.getUser().getTimezone()));
        response.setData(data);

        return response;
    }

    private TransactionResponse prepareWithdrawTransactionResponse(Transaction transaction) {
        TransactionResponse response = getResponse(transaction, false);
        response.setRecipient(transaction.getAccount());

        TransactionData data = TransactionMapper.INSTANCE.data(transaction);
        data.setHeader("Transaction %s".formatted(transaction.getStatus().getType()));
        data.setDescription("Your debit of %s from your Serch wallet %s".formatted(
                MoneyUtil.formatToNaira(transaction.getAmount()),
                transaction.getStatus().getSentence()
        ));
        data.setDate(TimeUtil.formatDay(transaction.getCreatedAt(), userUtil.getUser().getTimezone()));
        data.setUpdatedAt(TimeUtil.formatDay(transaction.getUpdatedAt(), userUtil.getUser().getTimezone()));
        response.setData(data);

        return response;
    }

    protected TransactionResponse prepareScheduleTransactionResponse(Transaction transaction) {
        boolean isIncoming = String.valueOf(userUtil.getUser().getId()).equals(transaction.getAccount());
        TransactionResponse response = getResponse(transaction, isIncoming);

        TransactionData data = getData(transaction);
        data.setDescription(prepareScheduleDescription(transaction, isIncoming));
        response.setData(data);

        if(userUtil.getUser().isBusiness()) {
            addAssociateData(scheduleRepository.findById(transaction.getEvent()).map(Schedule::getProvider), response);
        }
        return response;
    }

    private TransactionData getData(Transaction transaction) {
        TransactionData data = TransactionMapper.INSTANCE.data(transaction);
        data.setHeader("Transaction %s".formatted(transaction.getStatus().getType()));
        data.setDate(TimeUtil.formatDay(transaction.getCreatedAt(), userUtil.getUser().getTimezone()));
        data.setUpdatedAt(TimeUtil.formatDay(transaction.getUpdatedAt(), userUtil.getUser().getTimezone()));

        return data;
    }

    protected TransactionResponse getResponse(Transaction transaction, boolean isIncoming) {
        String account;

        try {
            account = walletRepository.findByUser_Id(UUID.fromString(transaction.getAccount()))
                    .map(wallet -> wallet(wallet.getId()))
                    .orElse("Wallet");
        } catch (IllegalArgumentException e) {
            account = walletRepository.findById(transaction.getAccount())
                    .map(wallet -> wallet(wallet.getId()))
                    .orElse("Wallet");
        }

        TransactionResponse response = new TransactionResponse();
        response.setIsIncoming(isIncoming);
        response.setLabel(TimeUtil.formatDay(transaction.getCreatedAt(), userUtil.getUser().getTimezone()));
        response.setType(transaction.getType());
        response.setStatus(transaction.getStatus());
        response.setAmount(MoneyUtil.formatToNaira(transaction.getAmount()));
        response.setRecipient(isIncoming ? String.format("Your %s", account) : String.format(account));

        return response;
    }

    protected String prepareScheduleDescription(Transaction transaction, boolean isIncoming) {
        if(isIncoming) {
            if(transaction.getVerified() && transaction.getStatus() == SUCCESSFUL) {
                return getIncomingScheduleTransactionDescription(transaction, "%s was sent to your wallet because %s closed schedule %s with %s for %s on %s, late");
            } else {
                return getIncomingScheduleTransactionDescription(transaction, "%s will be sent to your wallet because %s closed schedule %s with %s for %s on %s, late");
            }
        } else {
            if(transaction.getVerified() && transaction.getStatus() == SUCCESSFUL) {
                return getOutgoingScheduleTransactionDescription(transaction, "%s was debited from your wallet because %s closed schedule %s with %s for %s on %s, late");
            } else {
                return getOutgoingScheduleTransactionDescription(transaction, "%s will be debited from your wallet because %s closed schedule %s with %s for %s on %s, late");
            }
        }
    }

    private String getIncomingScheduleTransactionDescription(Transaction transaction, String format) {
        Optional<Schedule> optional = scheduleRepository.findById(transaction.getEvent());

        return String.format(
                format,
                MoneyUtil.formatToNaira(transaction.getAmount()),
                optional.map(schedule -> profileRepository.findById(schedule.getClosedBy()).map(Profile::getFullName).orElse("someone")).orElse("someone"),
                optional.map(Schedule::getId).orElse(""),
                getScheduleParticipant(transaction),
                optional.map(Schedule::getTime).orElse(""),
                optional.map(schedule -> TimeUtil.formatDay(schedule.getCreatedAt(), userUtil.getUser().getTimezone())).orElse("")
        );
    }

    private String getOutgoingScheduleTransactionDescription(Transaction transaction, String format) {
        Optional<Schedule> optional = scheduleRepository.findById(transaction.getEvent());

        return String.format(
                format,
                MoneyUtil.formatToNaira(transaction.getAmount()),
                getScheduleParticipant(transaction),
                optional.map(Schedule::getId).orElse(""),
                optional.map(schedule -> profileRepository.findById(schedule.getClosedBy()).map(Profile::getFullName).orElse("someone")).orElse("someone"),
                optional.map(Schedule::getTime).orElse(""),
                optional.map(schedule -> TimeUtil.formatDay(schedule.getCreatedAt(), userUtil.getUser().getTimezone())).orElse("")
        );
    }

    protected String getScheduleParticipant(Transaction transaction) {
        return scheduleRepository.findById(transaction.getEvent()).map(schedule -> {
            /// If user is the same person that closed the schedule
            if(schedule.getUser().isSameAs(schedule.getClosedBy()) ) {
                /// If the logged-in user is same as the provider
                if(schedule.getProvider().isSameAs(userUtil.getUser().getId())) {
                    return "you";
                } else {
                    return profileRepository.findById(schedule.getProvider().getId()).map(Profile::getFullName).orElse("someone");
                }
            } else {
                /// If the logged-in user is same as the user
                if(schedule.getUser().isSameAs(userUtil.getUser().getId())) {
                    return "you";
                } else {
                    return profileRepository.findById(schedule.getUser().getId()).map(Profile::getFullName).orElse("someone");
                }
            }
        }).orElse("someone");
    }

    private void addAssociateData(Optional<Profile> associate, TransactionResponse response) {
        associate.ifPresent(profile -> response.setAssociate(AssociateTransactionData.builder()
                .avatar(profile.getAvatar())
                .name(profile.getFullName())
                .category(profile.getCategory().getType())
                .rating(profile.getRating())
                .image(profile.getCategory().getImage())
                .build()
        ));
    }

    protected TransactionResponse prepareTip2FixTransactionResponse(Transaction transaction) {
        boolean isIncoming = userUtil.getUser().getRole() != Role.USER;
        TransactionResponse response = getResponse(transaction, isIncoming);

        TransactionData data = getData(transaction);
        data.setDescription(prepareCallDescription(transaction, isIncoming));
        response.setData(data);

        if(userUtil.getUser().isBusiness()) {
            addAssociateData(callRepository.findById(transaction.getEvent()).map(Call::getCalled), response);
        }
        return response;
    }

    protected String prepareCallDescription(Transaction transaction, boolean isIncoming) {
        if(isIncoming) {
            if(transaction.getVerified() && transaction.getStatus() == SUCCESSFUL) {
                return getIncomingCallTransactionDescription(transaction, "%s was sent to your wallet because %s had a Tip2Fix session %s with %s");
            } else {
                return getIncomingCallTransactionDescription(transaction, "%s will be sent to your wallet because %s had a Tip2Fix session %s with %s");
            }
        } else {
            if(transaction.getVerified() && transaction.getStatus() == SUCCESSFUL) {
                return getOutgoingCallTransactionDescription(transaction, "%s was debited from your wallet because you had a Tip2Fix session %s with %s");
            } else {
                return getOutgoingCallTransactionDescription(transaction, "%s will be debited from your wallet because you had a Tip2Fix session %s with %s");
            }
        }
    }

    private String getOutgoingCallTransactionDescription(Transaction transaction, String format) {
        Optional<Call> optional = callRepository.findById(transaction.getEvent());

        return String.format(
                format,
                MoneyUtil.formatToNaira(transaction.getAmount()),
                optional.map(Call::getChannel).orElse(""),
                optional.map(call -> call.getCalled().getFullName()).orElse("someone")
        );
    }

    private String getIncomingCallTransactionDescription(Transaction transaction, String format) {
        Optional<Call> optional = callRepository.findById(transaction.getEvent());

        return String.format(
                format,
                MoneyUtil.formatToNaira(transaction.getAmount()),
                optional.map(call -> call.getCaller().getFullName()).orElse("someone"),
                optional.map(Call::getChannel).orElse(""),
                optional.map(call -> call.getCalled().getFullName()).orElse("someone")
        );
    }

    protected TransactionResponse prepareTripShareTransactionResponse(Transaction transaction) {
        boolean isIncoming = String.valueOf(userUtil.getUser().getId()).equals(transaction.getAccount());
        TransactionResponse response = getResponse(transaction, isIncoming);

        TransactionData data = getData(transaction);
        data.setDescription(prepareTripShareDescription(transaction));
        response.setData(data);

        if(userUtil.getUser().isBusiness()) {
            addAssociateData(tripRepository.findById(transaction.getEvent()).map(Trip::getProvider), response);
        }
        return response;
    }

    protected String prepareTripShareDescription(Transaction transaction) {
        if(userUtil.getUser().isBusiness()) {
            return String.format(
                    "This is the amount sent to the user that shared %s based on the attended trip %s",
                    tripRepository.findById(transaction.getEvent()).map(trip -> trip.getProvider().getFullName()).orElse("your associate provider"),
                    transaction.getEvent()
            );
        } else if(userUtil.getUser().isUser()) {
            return String.format("This is your received percentage for sharing the link that made %s happen. Keep sharing!!!", transaction.getEvent());
        } else {
            return String.format("This is the trip charge for your attended trip %s", transaction.getEvent());
        }
    }

    protected TransactionResponse prepareTripChargeTransactionResponse(Transaction transaction) {
        TransactionResponse response = getResponse(transaction, false);
        response.setRecipient("Service Fee");

        TransactionData data = getData(transaction);
        data.setDescription(prepareTripChargeDescription(transaction));
        response.setData(data);

        if(userUtil.getUser().isBusiness()) {
            addAssociateData(tripRepository.findById(transaction.getEvent()).map(Trip::getProvider), response);
        }

        return response;
    }

    protected String prepareTripChargeDescription(Transaction transaction) {
        if(userUtil.getUser().isBusiness()) {
            return String.format(
                    "This is a trip charge for %s based on the attended trip %s",
                    tripRepository.findById(transaction.getEvent()).map(trip -> trip.getProvider().getFullName()).orElse("your associate provider"),
                    transaction.getEvent()
            );
        } else {
            return String.format("This is the trip charge for your attended trip %s", transaction.getEvent());
        }
    }

    @Override
    @Transactional
    public void processPaydays() {
        List<Wallet> wallets = walletRepository.findAll();

        if(!wallets.isEmpty()) {
            wallets.forEach(wallet -> {
                if(wallet.getPayoutOnPayday()) {
                    if(isPayday(wallet)) {
                        if(wallet.getBalance().compareTo(wallet.getPayout()) > 0) {
                            if(wallet.hasBank()) {
                                saveDebitTransaction(wallet.getPayout(), wallet, String.valueOf(wallet.getUser().getId()));
                                updateLastPayday(wallet, SUCCESSFUL);
                            } else {
                                updateLastPayday(wallet, FAILED);
                            }
                        } else {
                            updateLastPayday(wallet, FAILED);
                        }
                    }
                }
            });
        }
    }

    private boolean isPayday(Wallet wallet) {
        LocalDate today = LocalDate.now();
        LocalDate nextPayday = getNextPayday(wallet, today);

        return !today.isBefore(nextPayday) && today.isEqual(nextPayday);
    }

    private LocalDate getNextPayday(Wallet wallet, LocalDate today) {
        LocalDate lastPayday = wallet.getLastPayday() != null ? wallet.getLastPayday() : today.minusDays(365);
        long daysSinceLastPayday = ChronoUnit.DAYS.between(lastPayday, today);
        long daysToNextPayday = wallet.getPayday() - (daysSinceLastPayday % wallet.getPayday());
        LocalDate nextPayday = lastPayday.plusDays(daysToNextPayday);

        return nextPayday.isBefore(today) ? nextPayday.plusDays(wallet.getPayday()) : nextPayday;
    }

    private void updateLastPayday(Wallet wallet, TransactionStatus status) {
        LocalDate today = LocalDate.now();
        // Set last payday to today, and calculate the new next payout date
        wallet.setLastPayday(today);
        walletRepository.save(wallet); // Persist the changes

        /// Send Notification
        notificationService.send(
                wallet.getUser().getId(),
                wallet.getPayout(),
                status == SUCCESSFUL,
                formatNextPayday(wallet),
                String.format("%s - %s", wallet.getBankName(), wallet.getAccountName())
        );
    }

    @Override
    public String formatNextPayday(Wallet wallet) {
        if(wallet.getPayoutOnPayday()) {
            LocalDate today = LocalDate.now();
            LocalDate nextPayday = getNextPayday(wallet, today);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d'th', MMMM, yyyy");
            return nextPayday.format(formatter);
        } else {
            return "not set";
        }
    }

    @Override
    @Transactional
    public void processPendingVerifications() {
        List<Transaction> transactions = transactionRepository.findAllPending();

        transactions.forEach(transaction -> {
            if(transaction.getType() == TransactionType.FUNDING) {
                try {
                    var data = paymentService.verify(transaction.getReference());

                    creditWallet(transaction.getAmount(), UUID.fromString(transaction.getSender()));
                    transaction.setStatus(SUCCESSFUL);
                    transaction.setVerified(true);
                    transaction.setMode(data.getChannel().toUpperCase().replaceAll("_", " "));
                    transaction.setUpdatedAt(TimeUtil.now());
                    transactionRepository.save(transaction);

                    notificationService.send(UUID.fromString(transaction.getSender()), true, transaction.getAmount());
                } catch (Exception e) {
                    transaction.setStatus(FAILED);
                    transaction.setVerified(false);
                    transaction.setReason("Error in verification");
                    transaction.setUpdatedAt(TimeUtil.now());
                    transactionRepository.save(transaction);
                }
            }
        });
    }
}

package com.serch.server.admin.services.scopes.payment.services.implementations;

import com.serch.server.admin.mappers.CommonMapper;
import com.serch.server.admin.mappers.ScopeMapper;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.models.transaction.Payout;
import com.serch.server.admin.repositories.transactions.PayoutRepository;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.services.scopes.common.services.CommonProfileService;
import com.serch.server.admin.services.scopes.payment.responses.payouts.PayoutResponse;
import com.serch.server.admin.services.scopes.payment.responses.payouts.PayoutScopeResponse;
import com.serch.server.admin.services.scopes.payment.responses.transactions.PaymentApiResponse;
import com.serch.server.admin.services.scopes.payment.services.PayoutScopeService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.models.transaction.Transaction;
import com.serch.server.models.transaction.Wallet;
import com.serch.server.repositories.transaction.TransactionRepository;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.serch.server.enums.transaction.TransactionStatus.FAILED;
import static com.serch.server.enums.transaction.TransactionStatus.SUCCESSFUL;
import static com.serch.server.enums.transaction.TransactionType.WITHDRAW;

@Service
@RequiredArgsConstructor
public class PayoutScopeImplementation implements PayoutScopeService {
    private final CommonProfileService profileService;
    private final UserUtil userUtil;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final PayoutRepository payoutRepository;
    private final AdminRepository adminRepository;

    @Override
    public ApiResponse<List<PaymentApiResponse<PayoutScopeResponse>>> payouts(Integer page, Integer size, TransactionStatus status) {
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Map<TransactionStatus, Map<LocalDate, List<Transaction>>> groups = transactionRepository.findWithdrawals(pageable)
                .stream()
                .collect(Collectors.groupingBy(Transaction::getStatus, Collectors.groupingBy(t -> t.getUpdatedAt().toLocalDate())));

        List<PaymentApiResponse<PayoutScopeResponse>> responses = createEmptyApiResponse();
        responses.forEach(response -> {
            TransactionStatus current = response.getStatus();
            if (groups.containsKey(current)) {
                PaymentApiResponse<PayoutScopeResponse> data = createGroupResponse(current, groups.get(current));
                response.setTotal(data.getTotal());
                response.setTransactions(data.getTransactions());
            }
        });

        return new ApiResponse<>(responses);
    }

    private List<PaymentApiResponse<PayoutScopeResponse>> createEmptyApiResponse() {
        return Arrays.stream(TransactionStatus.values()).map(s -> {
            PaymentApiResponse<PayoutScopeResponse> response = new PaymentApiResponse<>();
            response.setStatus(s);
            response.setTitle(s.getType());

            return response;
        }).toList();
    }

    private PaymentApiResponse<PayoutScopeResponse> createGroupResponse(TransactionStatus key, Map<LocalDate, List<Transaction>> value) {
        PaymentApiResponse<PayoutScopeResponse> response = new PaymentApiResponse<>();
        response.setTitle(key.getType());
        response.setStatus(key);
        response.setTotal(transactionRepository.countWithdrawals(key));
        response.setTransactions(value.entrySet().stream().map(ent -> createScopeResponse(ent.getKey(), ent.getValue())).toList());

        return response;
    }

    private PayoutScopeResponse createScopeResponse(LocalDate key, List<Transaction> value) {
        PayoutScopeResponse response = new PayoutScopeResponse();
        response.setLabel(TimeUtil.formatChatLabel(
                LocalDateTime.of(key, value.getFirst().getCreatedAt().toLocalTime()),
                userUtil.getUser().getTimezone()
        ));
        response.setPayouts(value.stream().map(this::mapToPayoutResponse).toList());

        return response;
    }

    private PayoutResponse mapToPayoutResponse(Transaction transaction) {
        PayoutResponse response = ScopeMapper.instance.payout(transaction);
        response.setAmount(MoneyUtil.formatToNaira(transaction.getAmount()));
        response.setUser(profileService.fromTransaction(transaction.getSender()));

        UUID uuid = HelperUtil.parseUUID(transaction.getSender());
        if(uuid != null) {
            response.setAccountName(walletRepository.findByUser_Id(uuid).map(Wallet::getAccountName).orElse(""));
            response.setAccountNumber(walletRepository.findByUser_Id(uuid).map(Wallet::getAccountNumber).orElse(""));
            response.setBankName(walletRepository.findByUser_Id(uuid).map(Wallet::getBankName).orElse(""));
        }

        payoutRepository.findByTransaction_Id(transaction.getId())
                .ifPresent(payout -> {
                    response.setId(payout.getId());
                    response.setAdmin(CommonMapper.instance.response(payout.getPayoutBy()));
                    response.setUpdatedAt(payout.getUpdatedAt());
                });

        return response;
    }

    @Override
    public ApiResponse<PayoutResponse> find(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new SerchException("Transaction not found"));
        return new ApiResponse<>(mapToPayoutResponse(transaction));
    }

    @Override
    public ApiResponse<PayoutResponse> cancel(String id) {
        Admin admin = adminRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new SerchException("Admin not found"));
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new SerchException("Transaction not found"));

        validate(transaction);
        createPayoutRecord(transaction, FAILED, admin);

        return new ApiResponse<>(mapToPayoutResponse(transaction));
    }

    private void validate(Transaction transaction) {
        if(transaction.getType() != WITHDRAW) {
            throw new SerchException("This is not a withdrawal request transaction");
        }

        if(transaction.getStatus() == SUCCESSFUL) {
            throw new SerchException("Transaction is already resolved");
        }
    }

    private void createPayoutRecord(Transaction transaction, TransactionStatus status, Admin admin) {
        transaction.setStatus(status);
        transaction.setUpdatedAt(TimeUtil.now());
        transactionRepository.save(transaction);

        Payout payout = new Payout();
        payout.setPayoutBy(admin);
        payout.setTransaction(transaction);
        payoutRepository.save(payout);
    }

    @Override
    public ApiResponse<PayoutResponse> payout(String id) {
        Admin admin = adminRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new SerchException("Admin not found"));
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new SerchException("Transaction not found"));

        validate(transaction);
        createPayoutRecord(transaction, SUCCESSFUL, admin);

        return new ApiResponse<>(mapToPayoutResponse(transaction));
    }

    @Override
    public ApiResponse<List<PaymentApiResponse<PayoutScopeResponse>>> payout(Integer page, Integer size, List<String> id) {
        return getListApiResponse(page, size, id, SUCCESSFUL);
    }

    private ApiResponse<List<PaymentApiResponse<PayoutScopeResponse>>> getListApiResponse(Integer page, Integer size, List<String> id, TransactionStatus status) {
        Admin admin = adminRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new SerchException("Admin not found"));
        List<Transaction> transactions = transactionRepository.findAllById(id);

        if(!transactions.isEmpty()) {
            transactions.forEach(this::validate);
            transactions.forEach(transaction -> createPayoutRecord(transaction, status, admin));
        }

        return payouts(page, size, null);
    }

    @Override
    public ApiResponse<List<PaymentApiResponse<PayoutScopeResponse>>> cancel(Integer page, Integer size, List<String> id) {
        return getListApiResponse(page, size, id, FAILED);
    }
}

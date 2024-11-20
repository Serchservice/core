package com.serch.server.admin.services.scopes.payment.services.implementations;

import com.serch.server.admin.exceptions.PermissionException;
import com.serch.server.admin.mappers.CommonMapper;
import com.serch.server.admin.mappers.ScopeMapper;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.models.transaction.ResolvedTransaction;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.repositories.transactions.ResolvedTransactionRepository;
import com.serch.server.admin.services.scopes.common.services.CommonProfileService;
import com.serch.server.admin.services.scopes.payment.responses.transactions.*;
import com.serch.server.admin.services.scopes.payment.services.TransactionScopeService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.models.transaction.Transaction;
import com.serch.server.repositories.transaction.TransactionRepository;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.serch.server.enums.transaction.TransactionStatus.PENDING;

@Service
@RequiredArgsConstructor
public class TransactionScopeImplementation implements TransactionScopeService {
    private final CommonProfileService profileService;
    private final UserUtil userUtil;
    private final TransactionRepository transactionRepository;
    private final AdminRepository adminRepository;
    private final ResolvedTransactionRepository resolvedTransactionRepository;

    @Override
    public ApiResponse<List<PaymentApiResponse<TransactionGroupScopeResponse>>> transactions(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // Fetch transactions and group by status
        Map<TransactionStatus, Map<LocalDate, List<Transaction>>> groups = transactionRepository.findAll(pageable)
                .stream()
                .collect(Collectors.groupingBy(Transaction::getStatus, Collectors.groupingBy(t -> t.getCreatedAt().toLocalDate())));

        List<PaymentApiResponse<TransactionGroupScopeResponse>> responses = createEmptyApiResponse();
        // Update the responses with actual data where available
        responses.forEach(response -> getResult(transactionRepository.countByStatus(response.getStatus()), response, groups));

        return new ApiResponse<>(responses);
    }

    private List<PaymentApiResponse<TransactionGroupScopeResponse>> createEmptyApiResponse() {
        return Arrays.stream(TransactionStatus.values()).map(s -> {
            PaymentApiResponse<TransactionGroupScopeResponse> response = new PaymentApiResponse<>();
            response.setStatus(s);
            response.setTitle(s.getType());

            return response;
        }).toList();
    }

    private void getResult(Long total, PaymentApiResponse<TransactionGroupScopeResponse> response, Map<TransactionStatus, Map<LocalDate, List<Transaction>>> groups) {
        TransactionStatus current = response.getStatus();
        if (groups.containsKey(current)) {
            PaymentApiResponse<TransactionGroupScopeResponse> data = createApiResponse(current, groups.get(current), total);
            response.setTransactions(data.getTransactions());
            response.setTotal(data.getTotal());
        }
        System.out.println(total);
    }

    private PaymentApiResponse<TransactionGroupScopeResponse> createApiResponse(TransactionStatus status, Map<LocalDate, List<Transaction>> grouped, Long total) {
        PaymentApiResponse<TransactionGroupScopeResponse> response = new PaymentApiResponse<>();
        response.setStatus(status);
        response.setTitle(status.getType());
        response.setTotal(total);

        // Convert the date-grouped transactions to groups of TransactionGroupScopeResponse
        List<TransactionGroupScopeResponse> groups = grouped.entrySet()
                .stream()
                .map(entry -> createGroupScopeResponse(entry.getKey(), entry.getValue()))
                .toList();

        response.setTransactions(groups);
        return response;
    }

    private TransactionGroupScopeResponse createGroupScopeResponse(LocalDate date, List<Transaction> transactions) {
        TransactionGroupScopeResponse group = new TransactionGroupScopeResponse();
        Transaction firstTransaction = transactions.getFirst(); // Get the first transaction for time reference

        // Set group label and creation date
        group.setLabel(TimeUtil.formatChatLabel(
                LocalDateTime.of(date, firstTransaction.getCreatedAt().toLocalTime()),
                userUtil.getUser().getTimezone()
        ));
        group.setCreatedAt(ZonedDateTime.of(
                LocalDateTime.of(date, firstTransaction.getCreatedAt().toLocalTime()),
                TimeUtil.zoneId(userUtil.getUser().getTimezone())
        ));

        // Transform transactions to TransactionScopeResponse
        List<TransactionScopeResponse> transactionResponses = transactions.stream()
                .map(this::mapTransactionToResponse)
                .toList();

        group.setTransactions(transactionResponses);
        return group;
    }

    private TransactionScopeResponse mapTransactionToResponse(Transaction transaction) {
        TransactionScopeResponse response = ScopeMapper.instance.transaction(transaction);
        response.setAmount(MoneyUtil.formatToNaira(transaction.getAmount()));
        response.setLabel(transaction.getType().getType());
        response.setSender(profileService.fromTransaction(transaction.getSender()));
        response.setRecipient(profileService.fromTransaction(transaction.getAccount()));
        response.setResolution(resolvedTransactionRepository.findByTransaction_Id(transaction.getId()).map(ResolvedTransaction::getId).orElse(null));

        return response;
    }

    @Override
    public ApiResponse<List<PaymentApiResponse<TransactionGroupScopeResponse>>> search(Integer page, Integer size, String query) {
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // Fetch transactions and group by status
        Map<TransactionStatus, Map<LocalDate, List<Transaction>>> groups = transactionRepository
                .findByQuery(query, pageable)
                .stream()
                .collect(Collectors.groupingBy(Transaction::getStatus, Collectors.groupingBy(t -> t.getCreatedAt().toLocalDate())));

        List<PaymentApiResponse<TransactionGroupScopeResponse>> responses = createEmptyApiResponse();
        // Update the responses with actual data where available
        responses.forEach(response -> getResult(transactionRepository.countByStatus(query, response.getStatus()), response, groups));

        return new ApiResponse<>(responses);
    }

    @Override
    public ApiResponse<PaymentApiResponse<TransactionGroupScopeResponse>> filter(
            Integer page, Integer size, TransactionType type, ZonedDateTime start,
            ZonedDateTime end, TransactionStatus status
    ) {
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Map<LocalDate, List<Transaction>> groups = new HashMap<>();
        long total = 0L;

        if(start != null && end != null) {
            groups = transactionRepository.findAllByCreatedAtBetweenAndStatus(start, end, status, pageable)
                    .stream()
                    .collect(Collectors.groupingBy(t -> t.getCreatedAt().toLocalDate()));
            total = transactionRepository.countByCreatedAtBetweenAndStatus(start, end, status);
        } else if(start != null) {
            end = transactionRepository.findEarliestTransactionDate();
            if (end != null) {
                groups = transactionRepository.findAllByCreatedAtBetweenAndStatus(start, end, status, pageable)
                        .stream()
                        .collect(Collectors.groupingBy(t -> t.getCreatedAt().toLocalDate()));
                total = transactionRepository.countByCreatedAtBetweenAndStatus(start, end, status);
            }
        } else if(end != null) {
            start = TimeUtil.now().truncatedTo(ChronoUnit.DAYS);
            groups = transactionRepository.findAllByCreatedAtBetweenAndStatus(start, end, status, pageable)
                    .stream()
                    .collect(Collectors.groupingBy(t -> t.getCreatedAt().toLocalDate()));
            total = transactionRepository.countByCreatedAtBetweenAndStatus(start, end, status);
        } else if(type != null) {
            groups = transactionRepository.findAllByTypeAndStatus(type, status, pageable)
                    .stream()
                    .collect(Collectors.groupingBy(t -> t.getCreatedAt().toLocalDate()));
            total = transactionRepository.countByTypeAndStatus(type, status);
        } else {
            groups = transactionRepository.findAllByStatus(status, pageable)
                    .stream()
                    .collect(Collectors.groupingBy(t -> t.getCreatedAt().toLocalDate()));
            total = transactionRepository.countByStatus(status);
        }

        PaymentApiResponse<TransactionGroupScopeResponse> response = createEmptyApiResponse(status);
        if(!groups.isEmpty()) {
            List<TransactionGroupScopeResponse> transactions = groups.entrySet()
                    .stream()
                    .map(entry -> createGroupScopeResponse(entry.getKey(), entry.getValue()))
                    .toList();

            response.setTotal(total);
            response.setTransactions(transactions);
        }

        return new ApiResponse<>(response);
    }

    private PaymentApiResponse<TransactionGroupScopeResponse> createEmptyApiResponse(TransactionStatus status) {
        PaymentApiResponse<TransactionGroupScopeResponse> response = new PaymentApiResponse<>();
        response.setStatus(status);
        response.setTitle(status.getType());

        return response;
    }

    @Override
    public ApiResponse<List<TransactionTypeResponse>> fetchTypes() {
        List<TransactionTypeResponse> list = Arrays.stream(TransactionType.values())
                .map(type -> TransactionTypeResponse.builder().type(type).name(type.getType()).build())
                .toList();

        return new ApiResponse<>(list);
    }

    @Override
    public ApiResponse<TransactionScopeResponse> resolve(String id, TransactionStatus status) {
        Admin admin = adminRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new PermissionException("Admin not found"));
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new PermissionException("Transaction not found"));

        if (transaction.getStatus() == PENDING) {
            if (status != PENDING) {
                return updateTransaction(id, status, transaction, admin);
            } else {
                // Throw exception if trying to update to PENDING again
                throw new PermissionException("Cannot update the transaction to PENDING again.");
            }
        } else if (status == PENDING) {
            // Update the status to the new status
            return updateTransaction(id, status, transaction, admin);
        } else {
            // Throw exception if the current transaction status is not PENDING
            throw new PermissionException("Transaction is not pending. Cannot resolve transaction.");
        }
    }

    private ApiResponse<TransactionScopeResponse> updateTransaction(String id, TransactionStatus status, Transaction transaction, Admin admin) {
        transaction.setStatus(status);
        transaction.setUpdatedAt(TimeUtil.now());
        transactionRepository.save(transaction);

        // Fetch the resolved transaction or create a new one
        ResolvedTransaction resolved = resolvedTransactionRepository.findByTransaction_Id(id)
                .orElse(new ResolvedTransaction());
        resolved.setTransaction(transaction);
        resolved.setStatus(status);
        resolved.setResolvedBy(admin);
        resolvedTransactionRepository.save(resolved);

        // Return a successful response
        return new ApiResponse<>("Transaction status updated", mapTransactionToResponse(transaction), HttpStatus.OK);
    }

    @Override
    public ApiResponse<TransactionScopeResponse> transaction(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new PermissionException("Transaction not found"));
        return new ApiResponse<>("Transaction found", mapTransactionToResponse(transaction), HttpStatus.OK);
    }

    @Override
    public ApiResponse<PaymentApiResponse<ResolvedTransactionResponse>> resolvedTransactions(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        List<ResolvedTransaction> list = resolvedTransactionRepository.findAll(pageable).getContent();

        return getResolvedResponse(list, resolvedTransactionRepository.count());
    }

    private ApiResponse<PaymentApiResponse<ResolvedTransactionResponse>> getResolvedResponse(List<ResolvedTransaction> list, long total) {
        PaymentApiResponse<ResolvedTransactionResponse> response = new PaymentApiResponse<>();
        response.setTotal(total);

        if (!list.isEmpty()) {
            response.setTransactions(list.stream().map(this::getResolvedTransactionResponse).toList());
        }

        return new ApiResponse<>(response);
    }

    @Override
    public ApiResponse<PaymentApiResponse<ResolvedTransactionResponse>> searchResolved(Integer page, Integer size, String query) {
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        List<ResolvedTransaction> list = resolvedTransactionRepository.findByQuery(query, pageable).getContent();

        return getResolvedResponse(list, resolvedTransactionRepository.countByQuery(query));
    }

    @Override
    public ApiResponse<PaymentApiResponse<ResolvedTransactionResponse>> filterResolved(Integer page, Integer size, ZonedDateTime start, ZonedDateTime end, TransactionType type, TransactionStatus status) {
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        List<ResolvedTransaction> list = new ArrayList<>();
        long total = 0L;

        if(start != null && end != null) {
            list = resolvedTransactionRepository.findAllByCreatedAtBetweenAndStatus(start, end, status, pageable)
                    .stream()
                    .toList();
            total = resolvedTransactionRepository.countByCreatedAtBetweenAndStatus(start, end, status);
        } else if(start != null) {
            end = resolvedTransactionRepository.findEarliestTransactionDate();
            if (end != null) {
                list = resolvedTransactionRepository.findAllByCreatedAtBetweenAndStatus(start, end, status, pageable)
                        .stream()
                        .toList();
                total = resolvedTransactionRepository.countByCreatedAtBetweenAndStatus(start, end, status);
            }
        } else if(end != null) {
            start = TimeUtil.now().truncatedTo(ChronoUnit.DAYS);
            list = resolvedTransactionRepository.findAllByCreatedAtBetweenAndStatus(start, end, status, pageable)
                    .stream()
                    .toList();
            total = resolvedTransactionRepository.countByCreatedAtBetweenAndStatus(start, end, status);
        } else if(type != null) {
            list = resolvedTransactionRepository.findAllByTransaction_TypeAndStatus(type, status, pageable)
                    .stream()
                    .toList();
            total = resolvedTransactionRepository.countByTypeAndStatus(type, status);
        }

        return getResolvedResponse(list, total);
    }

    private ResolvedTransactionResponse getResolvedTransactionResponse(ResolvedTransaction resolve) {
        ResolvedTransactionResponse resolved = ScopeMapper.instance.resolved(resolve);
        resolved.setTransaction(mapTransactionToResponse(resolve.getTransaction()));
        resolved.setAdmin(CommonMapper.instance.response(resolve.getResolvedBy()));

        return resolved;
    }

    @Override
    public ApiResponse<ResolvedTransactionResponse> resolved(Long id) {
        ResolvedTransaction transaction = resolvedTransactionRepository.findById(id)
                .orElseThrow(() -> new SerchException("Transaction not found"));

        return new ApiResponse<>(getResolvedTransactionResponse(transaction));
    }
}
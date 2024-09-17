package com.serch.server.admin.services.scopes.common;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.models.auth.AccountStatusTracker;
import com.serch.server.models.auth.User;
import com.serch.server.models.transaction.Transaction;
import com.serch.server.repositories.auth.AccountStatusTrackerRepository;
import com.serch.server.repositories.auth.SessionRepository;
import com.serch.server.repositories.auth.mfa.MFAChallengeRepository;
import com.serch.server.repositories.transaction.TransactionRepository;
import com.serch.server.utils.AdminUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.*;

import static com.serch.server.enums.account.AccountStatus.*;

@Service
@RequiredArgsConstructor
public class CommonAccountAnalysisImplementation implements CommonAccountAnalysisService {
    private final AccountStatusTrackerRepository accountStatusTrackerRepository;
    private final TransactionRepository transactionRepository;
    private final MFAChallengeRepository mFAChallengeRepository;
    private final SessionRepository sessionRepository;

    @Override
    @Transactional
    public List<ChartMetric> accountStatus(User user, Integer year) {
        ZonedDateTime start = AdminUtil.getStartYear(year);
        ZonedDateTime end = start.plusYears(1);

        return Arrays.stream(AccountStatus.values())
                .filter(status -> {
                    if(user.isAdmin()) {
                        return status == ACTIVE || status == SUSPENDED || status == DELETED;
                    } else if(user.getRole() == Role.ASSOCIATE_PROVIDER) {
                        return status == ACTIVE || status == SUSPENDED || status == DELETED
                                || status == DEACTIVATED || status == BUSINESS_DEACTIVATED
                                || status == BUSINESS_DELETED || status == HAS_REPORTED_ISSUES;
                    } else {
                        return status == ACTIVE || status == SUSPENDED || status == DELETED
                                || status == DEACTIVATED || status == HAS_REPORTED_ISSUES;
                    }
                })
                .map(status -> {
                    List<AccountStatusTracker> count = accountStatusTrackerRepository
                            .findByUser_IdAndStatusAndCreatedAtBetween(user.getId(), status, start, end);
                    ChartMetric metric = new ChartMetric();
                    metric.setLabel(status.getType());
                    metric.setValue(count != null ? count.size() : 0);
                    metric.setColor(AdminUtil.randomColor());
                    return metric;
                })
                .toList();
    }

    @Override
    @Transactional
    public List<Integer> years(User user) {
        int start = user.getCreatedAt().getYear();
        int current = LocalDateTime.now().getYear();

        List<Integer> years = new ArrayList<>();
        for(int i = start; i <= current; i++) {
            years.add(i);
        }
        return years;
    }

    private String getKey(Transaction transaction, User user) {
        String typeLabel = "";
        switch (transaction.getType()) {
            case FUNDING:
                typeLabel = "Deposit";
                break;
            case SCHEDULE:
            case TIP2FIX:
            case TRIP_SHARE:
                if (transaction.getAccount().equals(String.valueOf(user.getId()))) {
                    typeLabel = "Earning";
                }
                break;
            case WITHDRAW:
                typeLabel = "Withdraw";
                break;
            case TRIP_CHARGE:
                if (transaction.getAccount().equals(String.valueOf(user.getId())) || transaction.getSender().equals(String.valueOf(user.getId()))) {
                    typeLabel = "Trip Charge";
                }
                break;
            default:
                break;
        }
        return typeLabel + " - " + transaction.getStatus().getType();
    }

    @Override
    @Transactional
    public List<ChartMetric> wallet(User user, Integer year) {
        ZonedDateTime start = AdminUtil.getStartYear(year);
        ZonedDateTime end = start.plusYears(1);

        List<Transaction> transactions = transactionRepository
                .findByUserAndDateRange(String.valueOf(user.getId()), start, end);
        Map<String, BigDecimal> metricsMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            String key = getKey(transaction, user);
            metricsMap.put(key, metricsMap.getOrDefault(key, BigDecimal.ZERO).add(transaction.getAmount()));
        }

        return getAmountMetrics(metricsMap);
    }

    private List<ChartMetric> getAmountMetrics(Map<String, BigDecimal> metricsMap) {
        List<ChartMetric> metrics = new ArrayList<>();
        metricsMap.forEach((label, value) -> {
            ChartMetric metric = new ChartMetric();
            metric.setLabel(label);
            metric.setAmount(value);
            metric.setColor(AdminUtil.randomColor());
            metrics.add(metric);
        });

        return metrics;
    }

    @Override
    @Transactional
    public List<ChartMetric> transaction(User user, Integer year) {
        ZonedDateTime start = AdminUtil.getStartYear(year);
        ZonedDateTime end = start.plusYears(1);

        List<Transaction> transactions = transactionRepository.findByUserAndDateRange(String.valueOf(user.getId()), start, end);

        Map<String, BigDecimal> metricsMap = new HashMap<>();
        for (Transaction transaction : transactions) {
            String label = transaction.getStatus().name();
            metricsMap.put(label, metricsMap.getOrDefault(label, BigDecimal.ZERO).add(transaction.getAmount()));
        }

        return getAmountMetrics(metricsMap);
    }

    @Override
    @Transactional
    public List<ChartMetric> activity(User user, Integer year) {
        return List.of();
    }

    @Override
    @Transactional
    public List<ChartMetric> auth(User user, Integer year) {
        ZonedDateTime start = AdminUtil.getStartYear(year);

        List<ChartMetric> metrics = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            ZonedDateTime startMonth = start.withMonth(month);
            ZonedDateTime endMonth = startMonth.plusMonths(1).minusSeconds(1);

            long challenges = mFAChallengeRepository.countByUserAndDateRange(user.getId(), startMonth, endMonth);
            long sessions = sessionRepository.countByUserAndDateRange(user.getId(), startMonth, endMonth);
            long devices = sessionRepository.countDistinctDevicesByUserAndDateRange(user.getId(), startMonth, endMonth);

            ChartMetric metric = new ChartMetric();
            metric.setLabel(startMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            metric.setChallenges((int) challenges);
            metric.setSessions((int) sessions);
            metric.setDevices((int) devices);
            metric.setColor(AdminUtil.randomColor());
            metrics.add(metric);
        }

        return metrics;
    }

    @Override
    @Transactional
    public List<ChartMetric> account(User user, Integer year) {
        return List.of();
    }

    @Override
    @Transactional
    public List<ChartMetric> associates(User user, Integer year) {
        return List.of();
    }

    @Override
    @Transactional
    public List<ChartMetric> subscriptions(User user, Integer year) {
        return List.of();
    }

    @Override
    @Transactional
    public List<ChartMetric> subscribed(User user, Integer year) {
        return List.of();
    }
}

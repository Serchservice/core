package com.serch.server.domains.nearby.listeners;

import com.serch.server.domains.nearby.models.go.user.GoUserInterest;
import com.serch.server.domains.nearby.services.interest.services.GoInterestTrendService;
import jakarta.persistence.PostPersist;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

@Component
public class GoUserInterestListener {
    private final GoInterestTrendService trendService;

    // Thread-local cache to store entities until commit
    private static final ThreadLocal<List<GoUserInterest>> CACHE = ThreadLocal.withInitial(ArrayList::new);

    public GoUserInterestListener(@Lazy GoInterestTrendService trendService) {
        this.trendService = trendService;
    }

    @PostPersist
    public void onInsert(GoUserInterest userInterest) {
        // Add to cache
        CACHE.get().add(userInterest);

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                CACHE.get().forEach(trendService::onTrending);
                CACHE.remove();
            }
        });
    }
}
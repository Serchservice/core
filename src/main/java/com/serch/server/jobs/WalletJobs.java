package com.serch.server.jobs;

import com.serch.server.services.transaction.services.WalletService;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles Complaint and SpeakWithSerch deletion for old records
 */
@Slf4j
@Configuration
@Transactional
@RequiredArgsConstructor
public class WalletJobs {
    private final WalletService service;

    /**
     * Executes the remove method periodically, to process paydays
     * This method is scheduled to run every midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void processPayouts() {
        log.info("Processing paydays in WALLET SERVICE for %s".formatted(TimeUtil.log()));
        service.processPaydays();
    }

    /**
     * Executes the remove method periodically, to check and verify pending transactions
     * This method is scheduled to run every midnight.
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void processPendingTransactions() {
        log.info("Processing pending transactions in WALLET SERVICE for %s".formatted(TimeUtil.log()));
        service.processPendingVerifications();
    }
}
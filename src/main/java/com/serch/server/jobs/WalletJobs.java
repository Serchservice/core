package com.serch.server.jobs;

import com.serch.server.services.transaction.services.WalletService;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Handles Complaint and SpeakWithSerch deletion for old records
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WalletJobs {
    private final WalletService service;

    /**
     * Executes the remove method periodically, to remove incomplete authentication accounts created
     * one year ago.
     * This method is scheduled to run every midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void processPayouts() {
//        log.info("Running scheduled task for remove in %s on %s".formatted(WalletService.class, TimeUtil.log()));
        service.processPaydays();
    }
}

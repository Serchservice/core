package com.serch.server.jobs;

import com.serch.server.services.company.services.ComplaintService;
import com.serch.server.services.company.services.SpeakWithSerchService;
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
public class CompanyJobs {
    private final ComplaintService complaintService;
    private final SpeakWithSerchService serchService;

    /**
     * Executes the remove method periodically, to remove incomplete authentication accounts created
     * one year ago.
     * This method is scheduled to run every midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeComplaints() {
//        log.info("Running scheduled task for remove in %s on %s".formatted(ComplaintService.class, TimeUtil.log()));
        complaintService.removeOldContents();
    }

    /**
     * Executes the remove method periodically, to remove incomplete authentication accounts created
     * one year ago.
     * This method is scheduled to run every midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeSpeakWithSerch() {
//        log.info("Running scheduled task for remove in %s on %s".formatted(SpeakWithSerchService.class, TimeUtil.log()));
        serchService.removeOldContents();
    }
}

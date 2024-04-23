package com.serch.server.jobs;

import com.serch.server.services.removal.AccountRemovalService;
import com.serch.server.services.removal.GuestRemovalService;
import com.serch.server.services.removal.IncompleteRemovalService;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

/**
 * The ServerCronJobs class configured scheduled cron jobs for executing periodic tasks in the application.
 * It is annotated with @Configuration to indicate that it defines application beans.
 * Additionally, it uses constructor injection for dependency management.
 * <p></p>
 * Cron expressions are used to specify when the scheduled tasks should run.
 * Each cron expression consists of six fields representing (from left to right):
 * <ul>
 *     <li>Second: 0-59</li>
 *     <li>Minute: 0-59</li>
 *     <li>Hour: 0-23</li>
 *     <li>Day of the Month: 1-31</li>
 *     <li>Month: 1-12</li>
 *     <li>Day of the Week: 0-6 (Sunday to Saturday)</li>
 * </ul>
 * <p></p>
 * Special characters:
 * <ol>
 *     <li>Asterisk (*) represents any value within the field.</li>
 *     <li>Question mark (?) can be used to indicate "no specific value" for Day of the Month or Day of the Week.</li>
 *     <li>Slash (/) is used to specify intervals for the field.</li>
 * </ol>
 * To run every day, you can use @Scheduled(cron = "0 0 0 * * ?"), which specifies midnight every day.
 * <p></p>
 * To run every second, you would use @Scheduled(fixedRate = 1000) or @Scheduled(fixedDelay = 1000).
 * Both are equivalent; they run every 1000 milliseconds, which is every second.
 * <p></p>
 * For a custom time, you can specify it using the cron expression.
 * For example, to run at 4:30 AM every day, you would use @Scheduled(cron = "0 30 4 * * ?").
 * <p></p>
 * @see org.springframework.context.annotation.Configuration
 * @see IncompleteRemovalService
 * @see GuestRemovalService
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AccountRemovalJobs {
    private final IncompleteRemovalService incompleteRemovalService;
    private final GuestRemovalService guestRemovalService;
    private final AccountRemovalService accountRemovalService;

    /**
     * Executes the remove method periodically, to remove incomplete authentication accounts created
     * one year ago.
     * This method is scheduled to run every midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeIncompleteAccountsCreatedOneYearAgoFromCurrentYear() {
        log.info("Running scheduled task for remove in %s on %s".formatted(IncompleteRemovalService.class, TimeUtil.log(LocalDateTime.now())));
        incompleteRemovalService.remove();
    }

    /**
     * Executes the remove method periodically.
     * This method is scheduled to run every midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeGuestsWithLessThanAYearActiveTrip() {
        log.info("Running scheduled task for remove in %s on %s".formatted(GuestRemovalService.class, TimeUtil.log(LocalDateTime.now())));
        guestRemovalService.remove();
    }

    /**
     * Executes the remove method periodically.
     * This method is scheduled to run every midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeAccountsAfter5YearsOfDeletionRequestGranted() {
        log.info("Running scheduled task for remove in %s on %s".formatted(AccountRemovalService.class, TimeUtil.log(LocalDateTime.now())));
        accountRemovalService.remove();
    }
}

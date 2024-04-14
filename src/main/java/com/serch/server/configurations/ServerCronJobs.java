package com.serch.server.configurations;

import com.serch.server.services.subscription.services.UpdateSubscriptionService;
import com.serch.server.services.transaction.services.SchedulePayService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

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
 * <p></p>
 * @see org.springframework.context.annotation.Configuration
 */
@Configuration
@RequiredArgsConstructor
public class ServerCronJobs {
    /**
     * Service for updating subscriptions.
     */
    private final UpdateSubscriptionService updateSubscriptionService;

    /**
     * Service for scheduling payments.
     */
    private final SchedulePayService schedulePayService;

    /**
     * Executes the updateSubscriptions method periodically, according to the specified cron expression.
     */
    @Scheduled(cron = "* * * */1 * *")
    public void updateSubscriptions() {
        updateSubscriptionService.checkSubscriptions();
    }

    /**
     * Executes the payScheduleUnclearedDebts method periodically, according to the specified cron expression.
     */
    @Scheduled(cron = "* * */1 * * ?")
    public void payScheduleUnclearedDebts() {
        schedulePayService.pay();
    }
}


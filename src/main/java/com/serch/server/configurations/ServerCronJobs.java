package com.serch.server.configurations;

import com.serch.server.services.subscription.services.UpdateSubscriptionService;
import com.serch.server.services.transaction.services.SchedulePayService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * *(Second) *(Minute) *(Hour) *(Day of the Month) *(Month) *(Day of the Week)
 *     0-59     0-59     0-23           1-31          1-12         0-6
 * Can use ? for Day of the month or day of the week - NO SPECIFIC VALUE
 * (/) is used to make a job occur every certain amount of time - 3 days = *
 */
@Configuration
@RequiredArgsConstructor
public class ServerCronJobs {
    private final UpdateSubscriptionService updateSubscriptionService;
    private final SchedulePayService schedulePayService;

    @Scheduled(cron = "* * * */1 * *")
    public void updateSubscriptions() {
        updateSubscriptionService.checkSubscriptions();
    }
    @Scheduled(cron = "* * */1 * * ?")
    public void payScheduleUnclearedDebts() {
        schedulePayService.pay();
    }
}

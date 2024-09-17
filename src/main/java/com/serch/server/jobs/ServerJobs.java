package com.serch.server.jobs;

import com.serch.server.services.conversation.services.CallService;
import com.serch.server.services.conversation.services.ChattingService;
import com.serch.server.services.shop.services.ShopService;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class configured scheduled cron jobs for executing periodic tasks in the application.
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
 * @see ShopService
 */
@Slf4j
@Configuration
@Transactional
@RequiredArgsConstructor
public class ServerJobs {
    private final ShopService shopService;
    private final CallService callService;
    private final ChattingService chatService;

    @Scheduled(cron = "0 * * * * *") // Runs every minute
    public void updateShops() {
        log.info("Running schedule task for updateShops in %s on %s".formatted(ShopService.class, TimeUtil.log()));
        shopService.openOrCloseShops();
    }

    @Scheduled(cron = "0 * * * * *") // The cron job is scheduled to run at midnight (00:00:00) every day.
    public void closeRingingCalls() {
        log.info("Running schedule task for closeRingingCalls in %s on %s".formatted(CallService.class, TimeUtil.log()));
        callService.closeRingingCalls();
    }

    @Scheduled(cron = "0 0 0 * * *") // The cron job is scheduled to run at midnight (00:00:00) every day.
    public void clearChats() {
        log.info("Running schedule task for clearChats in %s on %s".formatted(ChattingService.class, TimeUtil.log()));
        chatService.clearChats();
    }
}
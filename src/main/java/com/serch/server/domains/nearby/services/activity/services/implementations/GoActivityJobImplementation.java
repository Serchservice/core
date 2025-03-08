package com.serch.server.domains.nearby.services.activity.services.implementations;

import com.serch.server.domains.nearby.services.activity.services.GoActivityJobService;
import com.serch.server.enums.trip.TripStatus;
import com.serch.server.domains.nearby.repositories.go.GoActivityRepository;
import com.serch.server.domains.nearby.services.activity.services.GoActivityLifecycleService;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Configuration
@Transactional
@RequiredArgsConstructor
public class GoActivityJobImplementation implements GoActivityJobService {
    private final GoActivityLifecycleService lifecycle;
    private final GoActivityRepository goActivityRepository;

    @Override
    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    public void process() {
        log.info("Processing process in GoEventJobImplementation for {}", TimeUtil.log());

        goActivityRepository.getEndingNow().forEach(event -> {
            event.setStatus(TripStatus.CLOSED);
            event.setUpdatedAt(TimeUtil.now());
            goActivityRepository.save(event);

            lifecycle.onEnded(event);
        });

        goActivityRepository.getStartingNow().forEach(event -> {
            event.setStatus(TripStatus.ACTIVE);
            event.setUpdatedAt(TimeUtil.now());
            goActivityRepository.save(event);

            lifecycle.onStarted(event);
        });
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?") /// Run every midnight
    public void handle() {
        log.info("Processing handle in GoEventJobImplementation for {}", TimeUtil.log());
        goActivityRepository.getYesterday().forEach(activity -> {
            if (activity.getStatus() == TripStatus.WAITING) {
                activity.setStatus(TripStatus.CLOSED);
            } else if(activity.getStatus() == TripStatus.ACTIVE) {
                activity.setStatus(TripStatus.CLOSED);
            }

            activity.setUpdatedAt(TimeUtil.now());
            goActivityRepository.save(activity);
        });
    }

    @Override
    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    public void run() {
        log.info("Processing run in GoEventJobImplementation for {}", TimeUtil.log());

        goActivityRepository.getHistory().forEach(event -> {
            if (event.getStatus() == TripStatus.WAITING) {
                event.setStatus(TripStatus.CLOSED);
            } else if(event.getStatus() == TripStatus.ACTIVE) {
                event.setStatus(TripStatus.CLOSED);
            }

            event.setUpdatedAt(TimeUtil.now());
            goActivityRepository.save(event);
        });
    }
}
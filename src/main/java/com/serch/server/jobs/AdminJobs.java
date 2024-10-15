package com.serch.server.jobs;

import com.serch.server.admin.services.permission.services.PermissionService;
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
public class AdminJobs {
    private final PermissionService permissionService;

    /**
     * Executes the remove method periodically, to remove incomplete authentication accounts created
     * one year ago.
     * This method is scheduled to run every midnight.
     */
    @Scheduled(cron = "0 * * * * ?")
    public void revokePermissions() {
        log.info("Running scheduled task for revokeExpiredPermissions in %s on %s".formatted(PermissionService.class, TimeUtil.log()));
        permissionService.revokeExpiredPermissions();
    }
}
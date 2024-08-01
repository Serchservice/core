package com.serch.server.core.notification.core;

import com.serch.server.core.notification.requests.NotificationMessage;

public interface NotificationCoreService {
    /**
     * Send notification to a specific device
     *
     * @param request The {@link NotificationMessage} request
     */
    void send(NotificationMessage<?> request);
}

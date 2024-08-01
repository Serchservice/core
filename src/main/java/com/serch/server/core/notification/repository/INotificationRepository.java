package com.serch.server.core.notification.repository;

import java.util.UUID;

public interface INotificationRepository {
    String getToken(String id);
    String getBusinessToken(UUID id);
    String getName(String id);
    String getAvatar(String id);
}

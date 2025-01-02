package com.serch.server.admin.services.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminNotificationController {
    private final AdminNotificationService service;

    @MessageMapping("/notification/mark")
    public void markRead(@Payload Long id, SimpMessageHeaderAccessor header) {
        service.markAsRead(id);
    }

    @MessageMapping("/notification/all")
    public void fetchAll(SimpMessageHeaderAccessor header) {
        service.notifications();
    }

    @MessageMapping("/notification/clear")
    public void clear(@Payload Long id, SimpMessageHeaderAccessor header) {
        service.clear(id);
    }

    @MessageMapping("/notification/clear/all")
    public void clearAll(SimpMessageHeaderAccessor header) {
        service.clearAll();
    }
}

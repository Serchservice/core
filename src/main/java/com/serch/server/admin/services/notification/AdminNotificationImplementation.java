package com.serch.server.admin.services.notification;

import com.serch.server.admin.enums.AdminNotificationStatus;
import com.serch.server.admin.enums.AdminNotificationType;
import com.serch.server.admin.mappers.AdminMapper;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.models.AdminNotification;
import com.serch.server.admin.repositories.AdminNotificationRepository;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.enums.auth.Role;
import com.serch.server.models.auth.User;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNotificationImplementation implements AdminNotificationService {
    private final UserUtil userUtil;
    private final SimpMessagingTemplate template;
    private final AdminNotificationRepository adminNotificationRepository;
    private final AdminRepository adminRepository;

    @Override
    public void create(String message, String event, AdminNotificationType type, User user) {
        assert user != null && message != null;
        assert type != AdminNotificationType.PERMISSION_REQUEST || event != null;

        AdminNotification notification = new AdminNotification();
        notification.setMessage(message);
        notification.setEvent(event);
        if(type != null) {
            notification.setType(type);
        }
        notification.setUser(user);
        adminNotificationRepository.save(notification);
        sendNotification(user);
    }

    @Override
    public void notifications() {
        User user = userUtil.getUser();
        if(user.isAdmin()) {
            sendNotification(user);
        }
    }

    private Admin getSuperAdmin() {
        return adminRepository.findByUser_Role(Role.SUPER_ADMIN).orElse(null);
    }

    private User getSuperUser() {
        return getSuperAdmin() != null ? getSuperAdmin().getUser() : null;
    }

    private void sendNotification(User user) {
        List<AdminNotificationResponse> response = getNotificationResponse(user);
        template.convertAndSend("/topic/notifications/%s".formatted(String.valueOf(user.getId())), response);

        if(getSuperUser() != null && !getSuperUser().getId().equals(user.getId())) {
            List<AdminNotificationResponse> note = getNotificationResponse(getSuperUser());
            template.convertAndSend("/topic/notifications/%s".formatted(String.valueOf(getSuperUser().getId())), note);
        }
    }

    private List<AdminNotificationResponse> getNotificationResponse(User user) {
        List<AdminNotification> notifications = adminNotificationRepository.findByUser_Id(user.getId());
        List<AdminNotificationResponse> list;
        if(notifications != null && !notifications.isEmpty()) {
            list = new ArrayList<>(notifications.stream().map(this::response).toList());
        } else {
            list = new ArrayList<>();
        }
        return list;
    }

    private AdminNotificationResponse response(AdminNotification notification) {
        AdminNotificationResponse response = AdminMapper.instance.response(notification);
        if(notification.getEvent() != null && !notification.getEvent().isEmpty()) {
            String image = adminRepository.findByPass(notification.getEvent()).map(Admin::getAvatar).orElse("");
            response.setImage(image);
        }
        return response;
    }

    @Override
    public void markAsRead(Long id) {
        User user = userUtil.getUser();
        AdminNotification notification = adminNotificationRepository.findByIdAndUser_Id(id, user.getId()).orElse(null);
        if(notification != null) {
            notification.setStatus(AdminNotificationStatus.READ);
            notification.setUpdatedAt(LocalDateTime.now());
            adminNotificationRepository.save(notification);
        }
    }

    @Override
    public void clear(Long id) {
        User user = userUtil.getUser();
        adminNotificationRepository.findByIdAndUser_Id(id, user.getId()).ifPresent(adminNotificationRepository::delete);
    }

    @Override
    public void clearAll() {
        User user = userUtil.getUser();
        adminNotificationRepository.deleteAll(adminNotificationRepository.findByUser_Id(user.getId()));
    }
}

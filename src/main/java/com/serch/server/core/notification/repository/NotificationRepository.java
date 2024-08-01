package com.serch.server.core.notification.repository;

import com.serch.server.admin.models.Admin;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.shared.Guest;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.shared.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationRepository implements INotificationRepository {
    private final ProfileRepository profileRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final AdminRepository adminRepository;
    private final GuestRepository guestRepository;

    @Override
    public String getToken(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return profileRepository.findById(uuid)
                    .map(Profile::getFcmToken)
                    .orElse(businessProfileRepository.findById(uuid)
                            .map(BusinessProfile::getFcmToken)
                            .orElse(adminRepository.findById(uuid)
                                    .map(Admin::getFcmToken)
                                    .orElse("")
                            )
                    );
        } catch (Exception ignored) {
            return guestRepository.findById(id)
                    .map(Guest::getFcmToken)
                    .orElse("");
        }
    }

    @Override
    public String getBusinessToken(UUID id) {
        return profileRepository.findById(id)
                .map(profile -> profile.isAssociate() ? profile.getBusiness().getFcmToken() : "")
                .orElse("");
    }

    @Override
    public String getName(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return profileRepository.findById(uuid)
                    .map(Profile::getFullName)
                    .orElse("");
        } catch (Exception ignored) {
            return guestRepository.findById(id)
                    .map(Guest::getFullName)
                    .orElse("");
        }
    }

    @Override
    public String getAvatar(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return profileRepository.findById(uuid)
                    .map(Profile::getAvatar)
                    .orElse("");
        } catch (Exception ignored) {
            return guestRepository.findById(id)
                    .map(Guest::getAvatar)
                    .orElse("");
        }
    }
}

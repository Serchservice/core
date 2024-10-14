package com.serch.server.core.notification.repository;

import com.serch.server.admin.models.Admin;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.shared.Guest;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationRepository implements INotificationRepository {
    private final ProfileRepository profileRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final AdminRepository adminRepository;
    private final GuestRepository guestRepository;

    @Override
    @Transactional
    public String getToken(String id) {
        UUID uuid = HelperUtil.parseUUID(id);
        if (uuid != null) {
            return getTokenFromId(uuid);
        }
        return guestRepository.findById(id)
                .map(Guest::getFcmToken)
                .orElse("");
    }

    private String getTokenFromId(UUID uuid) {
        return profileRepository.findById(uuid)
                .map(Profile::getFcmToken)
                .orElseGet(() -> businessProfileRepository.findById(uuid)
                        .map(BusinessProfile::getFcmToken)
                        .orElseGet(() -> adminRepository.findById(uuid)
                                .map(Admin::getFcmToken)
                                .orElse("")));
    }

    @Override
    @Transactional
    public String getBusinessToken(UUID id) {
        return profileRepository.findById(id)
                .filter(Profile::isAssociate)
                .map(profile -> profile.getBusiness().getFcmToken())
                .orElse("");
    }

    @Override
    @Transactional
    public String getName(String id) {
        UUID uuid = HelperUtil.parseUUID(id);
        if (uuid != null) {
            return getNameFromId(uuid);
        }
        return guestRepository.findById(id)
                .map(Guest::getFullName)
                .orElse("");
    }

    private String getNameFromId(UUID uuid) {
        return profileRepository.findById(uuid)
                .map(Profile::getFullName)
                .orElseGet(() -> businessProfileRepository.findById(uuid)
                        .map(BusinessProfile::getFullName)
                        .orElseGet(() -> adminRepository.findById(uuid)
                                .map(Admin::getFullName)
                                .orElse("")));
    }

    @Override
    @Transactional
    public String getAvatar(String id) {
        UUID uuid = HelperUtil.parseUUID(id);
        if (uuid != null) {
            return getAvatarFromId(uuid);
        }
        return guestRepository.findById(id)
                .map(Guest::getAvatar)
                .orElse("");
    }

    private String getAvatarFromId(UUID uuid) {
        return profileRepository.findById(uuid)
                .map(Profile::getAvatar)
                .orElseGet(() -> adminRepository.findById(uuid)
                        .map(Admin::getAvatar)
                        .orElseGet(() -> businessProfileRepository.findById(uuid)
                                .map(BusinessProfile::getFullName)
                                .orElse("")));
    }
}

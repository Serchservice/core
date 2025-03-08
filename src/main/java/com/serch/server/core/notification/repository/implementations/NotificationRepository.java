package com.serch.server.core.notification.repository.implementations;

import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.core.notification.repository.INotificationRepository;
import com.serch.server.models.account.Profile;
import com.serch.server.models.shared.Guest;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationRepository implements INotificationRepository {
    private final ProfileRepository profileRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final AdminRepository adminRepository;
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Optional<String> getToken(String id) {
        UUID uuid = HelperUtil.parseUUID(id);
        String token;

        if (uuid != null) {
            if(profileRepository.findById(uuid).isPresent()) {
                token = profileRepository.findById(uuid).get().getFcmToken();
            } else if(businessProfileRepository.findById(uuid).isPresent()) {
                token = businessProfileRepository.findById(uuid).get().getFcmToken();
            } else if(adminRepository.findById(uuid).isPresent()) {
                token = adminRepository.findById(uuid).get().getFcmToken();
            } else {
                token = "";
            }
        } else {
            token = guestRepository.findById(id).map(Guest::getFcmToken).orElse("");
        }

        return token.isEmpty() ? Optional.empty() : Optional.of(token);
    }

    @Override
    @Transactional
    public String getRole(String id) {
        UUID uuid = HelperUtil.parseUUID(id);
        if (uuid != null) {
            return userRepository.findById(uuid).map(user -> user.getRole().getType()).orElse("");
        }

        return guestRepository.findById(id).map(guest -> "GUEST").orElse("");
    }

    @Override
    @Transactional
    public Optional<String> getBusinessToken(UUID id) {
        String token = profileRepository.findById(id)
                .filter(Profile::isAssociate)
                .map(profile -> profile.getBusiness().getFcmToken())
                .orElse("");

        return token.isEmpty() ? Optional.empty() : Optional.of(token);
    }

    @Override
    @Transactional
    public String getName(String id) {
        UUID uuid = HelperUtil.parseUUID(id);
        if (uuid != null) {
            if(profileRepository.findById(uuid).isPresent()) {
                return profileRepository.findById(uuid).get().getFullName();
            } else if(businessProfileRepository.findById(uuid).isPresent()) {
                return businessProfileRepository.findById(uuid).get().getFullName();
            } else if(adminRepository.findById(uuid).isPresent()) {
                return adminRepository.findById(uuid).get().getFullName();
            } else {
                return "";
            }
        }

        return guestRepository.findById(id).map(Guest::getFullName).orElse("");
    }

    @Override
    @Transactional
    public String getAvatar(String id) {
        UUID uuid = HelperUtil.parseUUID(id);
        if (uuid != null) {
            if(profileRepository.findById(uuid).isPresent()) {
                return profileRepository.findById(uuid).get().getAvatar();
            } else if(businessProfileRepository.findById(uuid).isPresent()) {
                return businessProfileRepository.findById(uuid).get().getAvatar();
            } else if(adminRepository.findById(uuid).isPresent()) {
                return adminRepository.findById(uuid).get().getAvatar();
            } else {
                return "";
            }
        }

        return guestRepository.findById(id).map(Guest::getAvatar).orElse("");
    }
}

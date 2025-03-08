package com.serch.server.core.notification.repository.implementations;

import com.serch.server.core.notification.repository.INotificationRepository;
import com.serch.server.domains.nearby.repositories.go.GoUserRepository;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoNotificationRepository implements INotificationRepository {
    private final GoUserRepository goUserRepository;

    @Override
    @Transactional
    public Optional<String> getToken(String id) {
        UUID uuid = HelperUtil.parseUUID(id);
        String token;

        if (uuid != null) {
            if(goUserRepository.findById(uuid).isPresent()) {
                token = goUserRepository.findById(uuid).get().getMessagingToken();
            } else {
                token = "";
            }
        } else {
            token = "";
        }

        return token.isEmpty() ? Optional.empty() : Optional.of(token);
    }

    @Override
    public String getName(String id) {
        UUID uuid = HelperUtil.parseUUID(id);
        if (uuid != null) {
            if(goUserRepository.findById(uuid).isPresent()) {
                return goUserRepository.findById(uuid).get().getFullName();
            } else {
                return "";
            }
        }

        return "";
    }

    @Override
    public String getAvatar(String id) {
        UUID uuid = HelperUtil.parseUUID(id);
        if (uuid != null) {
            if(goUserRepository.findById(uuid).isPresent()) {
                return goUserRepository.findById(uuid).get().getAvatar();
            } else {
                return "";
            }
        }

        return "";
    }
}

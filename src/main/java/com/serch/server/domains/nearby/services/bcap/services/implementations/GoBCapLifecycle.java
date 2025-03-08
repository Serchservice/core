package com.serch.server.domains.nearby.services.bcap.services.implementations;

import com.serch.server.core.notification.services.NotificationService;
import com.serch.server.domains.nearby.mappers.GoMapper;
import com.serch.server.domains.nearby.models.go.GoBCap;
import com.serch.server.domains.nearby.services.bcap.responses.GoBCapLifecycleResponse;
import com.serch.server.domains.nearby.services.bcap.services.GoBCapLifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoBCapLifecycle implements GoBCapLifecycleService {
    private final NotificationService goNotification;

    @Override
    public void onCreated(GoBCap cap) {
        GoBCapLifecycleResponse response = GoMapper.instance.lifecycle(cap);
        response.setMessage("See what %s has to say about this activity".formatted(cap.getActivity().getUser().getFirstName()));

        cap.getActivity().getAttendingUsers().forEach(user -> {
            response.setTitle("%s, where you there or did you miss out?".formatted(user.getUser().getFirstName()));
            goNotification.send(response, user.getUser().getId());
        });
    }
}
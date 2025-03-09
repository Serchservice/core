package com.serch.server.domains.nearby.services.activity.services.implementations;

import com.serch.server.core.notification.services.NotificationService;
import com.serch.server.domains.nearby.mappers.GoMapper;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.domains.nearby.repositories.go.GoUserInterestRepository;
import com.serch.server.domains.nearby.services.activity.responses.GoActivityLifecycleResponse;
import com.serch.server.domains.nearby.services.activity.services.GoActivityLifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoActivityLifecycle implements GoActivityLifecycleService {
    private final NotificationService goNotification;
    private final GoUserInterestRepository goUserInterestRepository;

    @Override
    public void onCreated(GoActivity event) {
        GoActivityLifecycleResponse response = GoMapper.instance.lifecycle(event);
        response.setTitle("NEW! Nearby %s activity".formatted(event.getInterest().getTitle()));
        response.setSummary("From %s".formatted(event.getUser().getFullName()));

        goUserInterestRepository.findNearbyUsersWithSameInterest(
                event.getLocation().getLatitude(),
                event.getLocation().getLongitude(),
                event.getInterest().getId(),
                event.getUser().getId()
        ).forEach(goUser -> goNotification.send(response, goUser.getUser().getId()));
    }

    @Override
    public void onAttending(GoActivity event) {
        GoActivityLifecycleResponse response = GoMapper.instance.lifecycle(event);

        if(event.getAttendingUsers().isEmpty()) {
            response.setTitle("Just so you know, %s".formatted(event.getUser().getFirstName()));
            response.setSummary(event.getInterest().getTitle());
            response.setMessage("Your interest can be someone else's interest too, and the person might be nearby!");
        } else {
            response.setTitle("Exciting! Your %s activity is getting crowded".formatted(event.getInterest().getTitle()));
            response.setSummary("%s attending".formatted(event.getAttendingUsers().size()));
            response.setMessage("Hope you're ready to have fun, this is looking real good!");
        }

        goNotification.send(response, event.getUser().getId());
    }

    @Override
    public void onStarted(GoActivity event) {
        GoActivityLifecycleResponse response = GoMapper.instance.lifecycle(event);
        response.setTitle("%s activity is live now!".formatted(event.getInterest().getTitle()));
        response.setSummary("From %s".formatted(event.getUser().getFullName()));
        response.setMessage("Happening live at %s".formatted(event.getLocation().getPlace()));

        goUserInterestRepository.findNearbyUsersWithSameInterestNotAttendingEvent(
                event.getLocation().getLatitude(),
                event.getLocation().getLongitude(),
                event.getInterest().getId(),
                event.getAttendingUsers().stream().map(user -> user.getUser().getId()).toList()
        ).forEach(goUser -> goNotification.send(response, goUser.getUser().getId()));

        event.getAttendingUsers().forEach(user -> goNotification.send(response, user.getUser().getId()));
    }

    @Override
    public void onEnded(GoActivity event) {
        GoActivityLifecycleResponse response = GoMapper.instance.lifecycle(event);
        response.setTitle("%s activity has ended".formatted(event.getInterest().getTitle()));
        response.setSummary("From %s".formatted(event.getUser().getFullName()));

        event.getAttendingUsers().forEach(user -> {
            response.setMessage("Share your experience about this activity, %s".formatted(user.getUser().getFirstName()));
            goNotification.send(response, user.getUser().getId());
        });
    }

    @Override
    public void onDeleted(GoActivity event) {
        GoActivityLifecycleResponse response = GoMapper.instance.lifecycle(event);
        response.setMessage("Just in! Nearby %s activity is now cancelled".formatted(event.getInterest().getTitle()));
        response.setSummary("From %s".formatted(event.getUser().getFullName()));

        event.getAttendingUsers().forEach(user -> {
            response.setTitle("Maybe next time, %s".formatted(user.getUser().getFirstName()));
            goNotification.send(response, user.getUser().getId());
        });
    }
}
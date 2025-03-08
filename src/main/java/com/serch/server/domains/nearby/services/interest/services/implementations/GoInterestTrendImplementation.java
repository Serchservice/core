package com.serch.server.domains.nearby.services.interest.services.implementations;

import com.serch.server.core.notification.services.NotificationService;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.models.go.user.GoUserInterest;
import com.serch.server.domains.nearby.repositories.go.GoUserInterestRepository;
import com.serch.server.domains.nearby.services.interest.responses.GoInterestTrendResponse;
import com.serch.server.domains.nearby.services.interest.services.GoInterestTrendService;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoInterestTrendImplementation implements GoInterestTrendService {
    private final NotificationService goNotification;
    private final GoUserInterestRepository goUserInterestRepository;

    @Async
    @Override
    public void onTrending(GoUserInterest userInterest) {
        checkAndNotify(userInterest);
    }

    private void checkAndNotify(GoUserInterest interest) {
        GoUser user = interest.getUser();

        if(user.getLocation() != null && user.getLocation().getLatitude() != null && user.getLocation().getLongitude() != null) {
            List<GoUserInterest> interests = goUserInterestRepository.findNearbyUsersWithSameInterest(
                    user.getLocation().getLatitude(),
                    user.getLocation().getLongitude(),
                    interest.getInterest().getId(),
                    user.getId()
            );

            interests.forEach(userInterest -> {
                GoUser notifyUser = userInterest.getUser();

                if(notifyUser.getLocation() != null && notifyUser.getLocation().getLatitude() != null && notifyUser.getLocation().getLongitude() != null) {
                    long count = goUserInterestRepository.countNearbyUsersWithInterest(
                            interest.getInterest().getId(),
                            notifyUser.getLocation().getLatitude(),
                            notifyUser.getLocation().getLongitude(),
                            notifyUser.getSearchRadius()
                    );

                    // 🔥 Only notify when count is a multiple of 100 (100, 200, 300, ...)
                    if (count > 0 && count % 100 == 0) {
                        GoInterestTrendResponse response = new GoInterestTrendResponse();
                        response.setMessage("Your interest '" + interest.getInterest().getTitle()
                                + "' is trending now! 🚀 "
                                + HelperUtil.format(count)
                                + " people near you are interested.");
                        response.setTitle("Trending Now");
                        response.setInterest(interest.getInterest().getId());

                        goNotification.send(response, notifyUser.getId());
                    }
                }
            });
        }
    }

    @Override
    public void onTrending(Long interest) {
        goUserInterestRepository.findById(interest).ifPresent(this::checkAndNotify);
    }
}
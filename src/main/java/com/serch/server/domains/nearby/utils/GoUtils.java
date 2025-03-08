package com.serch.server.domains.nearby.utils;

import com.serch.server.bases.BaseLocation;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.domains.nearby.models.go.interest.GoInterest;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.models.go.user.GoUserInterest;
import com.serch.server.domains.nearby.repositories.go.GoAttendingUserRepository;
import com.serch.server.domains.nearby.repositories.go.GoInterestRepository;
import com.serch.server.domains.nearby.repositories.go.GoUserInterestRepository;
import com.serch.server.utils.AuthUtil;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoUtils {
    private final GoUserInterestRepository goUserInterestRepository;
    private final GoInterestRepository goInterestRepository;
    private final GoAttendingUserRepository goAttendingUserRepository;
    private final AuthUtil authUtil;

    public long calculateTotalAttendingUsers(GoActivity event) {
        return goAttendingUserRepository.countByActivity_Id(event.getId());
    }

    /**
     * Calculate and update popularity based on all users sharing this interest.
     *
     * @param interest The {@link GoInterest} data to calculate with.
     */
    public long calculateAndUpdatePopularity(GoInterest interest) {
        long totalUsers = goUserInterestRepository.countByInterestId(interest.getId());
        interest.setPopularity(totalUsers);
        interest.setUpdatedAt(TimeUtil.now());
        goInterestRepository.save(interest);

        return totalUsers;
    }

    /**
     * Calculate nearby popularity using latitude, longitude, and radius.
     *
     * @param interest The {@link GoInterest} data to calculate with.
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @param radius The radius within which to find nearby users (in kilometers).
     *
     * @return The number of nearby users sharing the interest.
     */
    public long calculateNearbyPopularity(GoInterest interest, double latitude, double longitude, double radius) {
        return goUserInterestRepository.countNearbyUsersWithInterest(interest.getId(), latitude, longitude, radius);
    }

    /**
     * Calculate nearby popularity using latitude, longitude, and radius for the logged-in user.
     *
     * @param searchRadius The search radius to calculate with.
     * @param location The {@link BaseLocation} dat to calculate with.
     * @param interest The {@link GoInterest} data to calculate with.
     *
     * @return The number of nearby users sharing the interest.
     */
    public long calculateNearbyPopularity(GoInterest interest, BaseLocation location, Double searchRadius) {
        return goUserInterestRepository.countNearbyUsersWithInterest(
                interest.getId(),
                location.getLatitude(),
                location.getLongitude(),
                searchRadius
        );
    }

    /**
     * Checks if the {@link GoUser} has selected interests in the account. {@link GoUserInterest}
     *
     * @param user The {@link GoUser} data
     *
     * @return {@code boolean} {@code true} or {@code false}
     */
    public boolean hasInterests(GoUser user) {
        return goUserInterestRepository.existsByUser_Id(user.getId());
    }

    public GoOptional getOptional(double lat, double lng, double rad) {
        GoOptional optional = new GoOptional(lat, lng, rad);

        authUtil.getOptionalGoUser().ifPresent(user -> {
            optional.setUser(user.getId());

            if(user.getLocation() != null) {
                optional.setLat(user.getLocation().getLatitude());
                optional.setLng(user.getLocation().getLongitude());
            }

            optional.setInterests(goUserInterestRepository.findInterestIdsByUserId(user.getId()));
        });

        return optional;
    }
}
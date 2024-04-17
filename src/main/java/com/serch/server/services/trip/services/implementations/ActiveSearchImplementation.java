package com.serch.server.services.trip.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.enums.trip.TripStatus;
import com.serch.server.enums.verified.VerificationStatus;
import com.serch.server.mappers.AccountMapper;
import com.serch.server.mappers.TripMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.repositories.account.SpecialtyRepository;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.shop.ShopRepository;
import com.serch.server.repositories.trip.ActiveRepository;
import com.serch.server.services.account.responses.MoreProfileData;
import com.serch.server.services.account.services.ProfileService;
import com.serch.server.services.company.services.SpecialtyKeywordService;
import com.serch.server.services.trip.responses.ActiveResponse;
import com.serch.server.services.trip.services.ActiveSearchService;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the {@link ActiveSearchService} interface.
 * Provides methods for searching active providers based on various criteria.
 *
 * @see ProfileService
 * @see SpecialtyKeywordService
 * @see ShopRepository
 * @see RatingRepository
 * @see SharedLinkRepository
 * @see SpecialtyRepository
 * @see ActiveRepository
 */
@Service
@RequiredArgsConstructor
public class ActiveSearchImplementation implements ActiveSearchService {
    private final ProfileService profileService;
    private final SpecialtyKeywordService keywordService;
    private final ShopRepository shopRepository;
    private final RatingRepository ratingRepository;
    private final SharedLinkRepository sharedLinkRepository;
    private final SpecialtyRepository specialtyRepository;
    private final ActiveRepository activeRepository;

    @Override
    public ActiveResponse response(Profile profile, TripStatus status, double distance) {
        ActiveResponse response = TripMapper.INSTANCE.response(profile);
        response.setStatus(status);
        response.setName(profile.getFullName());
        response.setDistance(distance + " km");
        if(profile.isAssociate()) {
            response.setBusiness(AccountMapper.INSTANCE.business(profile.getBusiness()));
        }
        response.setSpecializations(
                specialtyRepository.findByProfile_Id(profile.getId())
                        .stream()
                        .map(specialty -> keywordService.getSpecialtyResponse(specialty.getService()))
                        .toList()
        );
        MoreProfileData more = profileService.moreInformation(profile.getUser());
        more.setTotalShared(sharedLinkRepository.findByUserId(profile.getId()).size());
        more.setTotalServiceTrips(0);
        more.setNumberOfShops(shopRepository.findByUser_Id(profile.getUser().getId()).size());
        more.setNumberOfRating(ratingRepository.findByRated(String.valueOf(profile.getId())).size());
        response.setMore(more);
        return response;
    }

    @Override
    public ApiResponse<List<ActiveResponse>> searchByCategory(SerchCategory category, Double longitude, Double latitude, Double radius) {
        List<ActiveResponse> list = activeRepository
                .sortAllWithinDistance(latitude, longitude, radius, category, PlanStatus.ACTIVE)
                .stream()
                .map(activeProvider -> response(
                        activeProvider.getProfile(),
                        activeProvider.getTripStatus(),
                        HelperUtil.getDistance(
                                latitude, longitude,
                                activeProvider.getLatitude(),
                                activeProvider.getLongitude()
                        )
                ))
                .toList();
        return new ApiResponse<>(list);
    }

    @Override
    public ApiResponse<List<ActiveResponse>> searchByVerified(SerchCategory category, Double longitude, Double latitude, Double radius) {
        List<ActiveResponse> list = activeRepository
                .sortByVerificationWithinDistance(latitude, longitude, radius, category, VerificationStatus.VERIFIED, PlanStatus.ACTIVE)
                .stream()
                .map(activeProvider -> response(
                        activeProvider.getProfile(),
                        activeProvider.getTripStatus(),
                        HelperUtil.getDistance(
                                latitude, longitude,
                                activeProvider.getLatitude(),
                                activeProvider.getLongitude()
                        )
                ))
                .toList();
        return new ApiResponse<>(list);
    }

    @Override
    public ApiResponse<List<ActiveResponse>> searchByFree(SerchCategory category, Double longitude, Double latitude, Double radius) {
        List<ActiveResponse> list = activeRepository
                .sortByTripStatusWithinDistance(latitude, longitude, radius, category, PlanStatus.ACTIVE, TripStatus.ONLINE)
                .stream()
                .map(activeProvider -> response(
                        activeProvider.getProfile(),
                        activeProvider.getTripStatus(),
                        HelperUtil.getDistance(
                                latitude, longitude,
                                activeProvider.getLatitude(),
                                activeProvider.getLongitude()
                        )
                ))
                .toList();
        return new ApiResponse<>(list);
    }

    @Override
    public ApiResponse<List<ActiveResponse>> searchByRating(SerchCategory category, Double longitude, Double latitude, Double radius) {
        List<ActiveResponse> list = activeRepository
                .sortByRatingWithinDistance(latitude, longitude, radius, category, PlanStatus.ACTIVE)
                .stream()
                .map(activeProvider -> response(
                        activeProvider.getProfile(),
                        activeProvider.getTripStatus(),
                        HelperUtil.getDistance(
                                latitude, longitude,
                                activeProvider.getLatitude(),
                                activeProvider.getLongitude()
                        )
                ))
                .toList();
        return new ApiResponse<>(list);
    }

    @Override
    public ApiResponse<List<ActiveResponse>> searchBySpecialty(String query, Double longitude, Double latitude, Double radius) {
        List<ActiveResponse> list = activeRepository
                .fullTextSearchWithinDistance(latitude, longitude, query, radius, PlanStatus.ACTIVE.name())
                .stream()
                .map(activeProvider -> response(
                        activeProvider.getProfile(),
                        activeProvider.getTripStatus(),
                        HelperUtil.getDistance(
                                latitude, longitude,
                                activeProvider.getLatitude(),
                                activeProvider.getLongitude()
                        )
                ))
                .toList();
        return new ApiResponse<>(list);
    }
}

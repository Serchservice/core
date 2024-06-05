package com.serch.server.services.trip.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.trip.TripStatus;
import com.serch.server.mappers.AccountMapper;
import com.serch.server.mappers.TripMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.trip.Active;
import com.serch.server.repositories.account.SpecialtyRepository;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.shop.ShopRepository;
import com.serch.server.repositories.trip.ActiveRepository;
import com.serch.server.services.account.responses.MoreProfileData;
import com.serch.server.services.account.services.ProfileService;
import com.serch.server.services.account.services.SpecialtyService;
import com.serch.server.services.shop.responses.SearchShopResponse;
import com.serch.server.services.shop.services.ShopService;
import com.serch.server.services.trip.responses.ActiveResponse;
import com.serch.server.services.trip.responses.SearchResponse;
import com.serch.server.services.trip.services.ActiveSearchService;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the {@link ActiveSearchService} interface.
 * Provides methods for searching active providers based on various criteria.
 *
 * @see ProfileService
 * @see SpecialtyService
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
    private final SpecialtyService specialtyService;
    private final ShopService shopService;
    private final ShopRepository shopRepository;
    private final RatingRepository ratingRepository;
    private final SharedLinkRepository sharedLinkRepository;
    private final SpecialtyRepository specialtyRepository;
    private final ActiveRepository activeRepository;

    @Value("${application.map.search-radius}")
    private String MAP_SEARCH_RADIUS;

    @Override
    public ActiveResponse response(Profile profile, TripStatus status, double distance) {
        ActiveResponse response = TripMapper.INSTANCE.response(profile);
        response.setStatus(status);
        response.setName(profile.getFullName());
        response.setDistanceInKm(distance + " km");
        response.setDistance(distance);
        if(profile.isAssociate()) {
            response.setBusiness(AccountMapper.INSTANCE.business(profile.getBusiness()));
        }
        response.setSpecializations(
                specialtyRepository.findByProfile_Id(profile.getId())
                        .stream()
                        .map(specialtyService::response)
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
    public ApiResponse<SearchResponse> search(SerchCategory category, Double longitude, Double latitude, Double radius, Boolean autoConnect) {
        double searchRadius = radius == null ? Double.parseDouble(MAP_SEARCH_RADIUS) : radius;
        List<Active> actives = activeRepository.sortAllWithinDistance(latitude, longitude, searchRadius, category.name());

        SearchResponse response = prepareResponse(longitude, latitude, actives);
        if(autoConnect != null && autoConnect) {
            Active bestMatch = activeRepository.findBestMatchWithCategory(latitude, longitude, category.name(), searchRadius);
            if(bestMatch != null) {
                response.setBest(response(
                        bestMatch.getProfile(), bestMatch.getTripStatus(),
                        HelperUtil.getDistance(latitude, longitude, bestMatch.getLatitude(), bestMatch.getLongitude())
                ));
            }
        }
        return new ApiResponse<>(response);
    }

    private SearchResponse prepareResponse(Double longitude, Double latitude, List<Active> actives) {
        SearchResponse response = new SearchResponse();
        if(actives != null && !actives.isEmpty()) {
            Set<Active> distinct = new HashSet<>();
            List<ActiveResponse> list = actives.stream()
                    .filter(distinct::add)
                    .map(activeProvider -> response(
                            activeProvider.getProfile(),
                            activeProvider.getTripStatus(),
                            HelperUtil.getDistance(latitude, longitude, activeProvider.getLatitude(), activeProvider.getLongitude())
                    ))
                    .toList();
            response.setProviders(list);
        }
        return response;
    }

    @Override
    public ApiResponse<SearchResponse> search(String query, Double longitude, Double latitude, Double radius, Boolean autoConnect) {
        double searchRadius = radius == null ? Double.parseDouble(MAP_SEARCH_RADIUS) : radius;
        List<Active> actives = activeRepository.fullTextSearchWithinDistance(latitude, longitude, query, searchRadius);
        List<SearchShopResponse> shops = shopService.list(query, null, longitude, latitude, searchRadius);

        SearchResponse response = prepareResponse(longitude, latitude, actives);
        response.setShops(shops);
        if(autoConnect != null && autoConnect) {
            Active bestMatch = activeRepository.findBestMatchWithQuery(latitude, longitude, query, searchRadius);
            if(bestMatch != null) {
                response.setBest(response(
                        bestMatch.getProfile(), bestMatch.getTripStatus(),
                        HelperUtil.getDistance(latitude, longitude, bestMatch.getLatitude(), bestMatch.getLongitude())
                ));
            }
        }
        return new ApiResponse<>(response);
    }
}
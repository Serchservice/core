package com.serch.server.services.trip.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.Gender;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.account.ProviderStatus;
import com.serch.server.enums.shop.DriveScope;
import com.serch.server.enums.verified.VerificationStatus;
import com.serch.server.mappers.AccountMapper;
import com.serch.server.mappers.TripMapper;
import com.serch.server.models.account.AccountSetting;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.verified.Verification;
import com.serch.server.models.trip.Active;
import com.serch.server.repositories.account.SpecialtyRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.certificate.CertificateRepository;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.shop.ShopRepository;
import com.serch.server.repositories.trip.ActiveRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.services.account.responses.MoreProfileData;
import com.serch.server.services.account.services.ProfileService;
import com.serch.server.services.account.services.SpecialtyService;
import com.serch.server.services.shop.responses.SearchShopResponse;
import com.serch.server.services.shop.services.ShopService;
import com.serch.server.services.trip.responses.ActiveResponse;
import com.serch.server.services.trip.responses.SearchResponse;
import com.serch.server.services.trip.services.ActiveSearchService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
    private static final Logger log = LoggerFactory.getLogger(ActiveSearchImplementation.class);
    private final ProfileService profileService;
    private final SpecialtyService specialtyService;
    private final ShopService shopService;
    private final ShopRepository shopRepository;
    private final RatingRepository ratingRepository;
    private final SharedLinkRepository sharedLinkRepository;
    private final SpecialtyRepository specialtyRepository;
    private final ActiveRepository activeRepository;
    private final UserRepository userRepository;
    private final CertificateRepository certificateRepository;

    @Value("${application.map.search-radius}")
    private String MAP_SEARCH_RADIUS;
    private final TripRepository tripRepository;

    @Override
    public Double getSearchRadius(Double radius) {
        return radius == null ? Double.parseDouble(MAP_SEARCH_RADIUS) : radius;
    }

    @Override
    public ActiveResponse response(Profile profile, ProviderStatus status, double distance) {
        ActiveResponse response = TripMapper.INSTANCE.response(profile);
        response.setStatus(status);
        response.setName(profile.getFullName());
        response.setDistanceInKm(distance + " km");
        response.setDistance(distance);
        response.setCategory(profile.getCategory().getType());
        response.setImage(profile.getCategory().getImage());
        if(profile.isAssociate()) {
            response.setBusiness(AccountMapper.INSTANCE.business(profile.getBusiness()));
        }
        response.setSpecializations(
                specialtyRepository.findByProfile_Id(profile.getId())
                        .stream()
                        .map(specialtyService::response)
                        .toList()
        );
        response.setVerificationStatus(profile.getUser().getVerification() != null
                ? profile.getUser().getVerification().getStatus()
                : VerificationStatus.NOT_VERIFIED
        );
        MoreProfileData more = profileService.moreInformation(profile.getUser());
        more.setTotalShared(sharedLinkRepository.findByUserId(profile.getId()).size());
        more.setTotalServiceTrips(tripRepository.findCompletedProviderTrips(profile.getId()).size());
        more.setNumberOfShops(shopRepository.findByUser_Id(profile.getUser().getId()).size());
        more.setNumberOfRating(ratingRepository.findByRated(String.valueOf(profile.getId())).size());
        response.setMore(more);
        return response;
    }

    @Override
    public ApiResponse<SearchResponse> search(SerchCategory category, Double longitude, Double latitude, Double radius, Boolean autoConnect) {
        List<Active> actives = activeRepository.sortAllWithinDistance(latitude, longitude, getSearchRadius(radius), category.name());

        SearchResponse response = prepareResponse(longitude, latitude, actives);
        if(autoConnect != null && autoConnect) {
            Active bestMatch = activeRepository.findBestMatchWithCategory(latitude, longitude, category.name(), getSearchRadius(radius));
            addBestMatch(longitude, latitude, response, bestMatch);
        }
        return new ApiResponse<>(response);
    }

    protected SearchResponse prepareResponse(Double longitude, Double latitude, List<Active> actives) {
        AtomicReference<AccountSetting> setting = new AtomicReference<>();
        userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .ifPresent(user -> setting.set(user.getSetting()));

        SearchResponse response = new SearchResponse();
        if(setting.get() != null) {
            if (actives != null && !actives.isEmpty()) {
                Set<Active> distinct = new HashSet<>();
                List<ActiveResponse> list = actives.stream()
                        // Filter based on whether certificates are required and whether the certificate exists
                        .filter(active -> {
                            boolean showOnlyCertified = Optional.ofNullable(setting.get().getShowOnlyCertified()).orElse(false);
                            boolean hasCertificate = certificateRepository.existsByUser(active.getProfile().getId());
                            log.info(String.format("ACTIVE SEARCH ::: Show Only Certified=%s | Has Certificate=%s", showOnlyCertified, hasCertificate));
                            return !showOnlyCertified || hasCertificate;
                        })
                        // Filter based on whether verification is required and whether the user is verified
                        .filter(active -> {
                            boolean showOnlyVerified = Optional.ofNullable(setting.get().getShowOnlyVerified()).orElse(false);
                            boolean isVerified = Optional.ofNullable(active.getProfile().getUser().getVerification())
                                    .map(Verification::isVerified)
                                    .orElse(false);
                            log.info(String.format("ACTIVE SEARCH ::: Show Only Verified=%s | Is Verified=%s", showOnlyVerified, isVerified));
                            return !showOnlyVerified || isVerified;
                        })
                        // Filter based on gender
                        .filter(active -> {
                            Gender genderSetting = Optional.ofNullable(setting.get().getGender()).orElse(Gender.ANY);
                            Gender profileGender = active.getProfile().getGender();
                            log.info(String.format("ACTIVE SEARCH ::: Selected Trip Gender=%s | User Gender=%s", genderSetting, profileGender));
                            return genderSetting == Gender.ANY || genderSetting.equals(profileGender);
                        })
                        // Ensure distinct results if needed (using some form of uniqueness check)
                        .filter(distinct::add) // Ensure distinct items if you need to avoid duplicates
                        // Map the results to ActiveResponse
                        .map(activeProvider -> response(
                                activeProvider.getProfile(),
                                activeProvider.getStatus(),
                                HelperUtil.getDistance(latitude, longitude, activeProvider.getLatitude(), activeProvider.getLongitude())
                        ))
                        .toList();
                response.setProviders(list);
            }
        } else {
            if(actives != null && !actives.isEmpty()) {
                Set<Active> distinct = new HashSet<>();
                List<ActiveResponse> list = actives.stream()
                        .filter(distinct::add)
                        .map(activeProvider -> response(
                                activeProvider.getProfile(),
                                activeProvider.getStatus(),
                                HelperUtil.getDistance(latitude, longitude, activeProvider.getLatitude(), activeProvider.getLongitude())
                        ))
                        .toList();
                response.setProviders(list);
            }
        }
        return response;
    }

    @Override
    public ApiResponse<SearchResponse> search(String query, Double longitude, Double latitude, Double radius, Boolean autoConnect) {
        List<Active> actives = activeRepository.fullTextSearchWithinDistance(latitude, longitude, query, getSearchRadius(radius));
        List<SearchShopResponse> shops = shopService.list(query, null, longitude, latitude, getSearchRadius(radius), DriveScope.SERCH);

        SearchResponse response = prepareResponse(longitude, latitude, actives);
        response.setShops(shops);
        if(autoConnect != null && autoConnect) {
            Active bestMatch = activeRepository.findBestMatchWithQuery(latitude, longitude, query, getSearchRadius(radius));
            addBestMatch(longitude, latitude, response, bestMatch);
        }
        return new ApiResponse<>(response);
    }

    protected void addBestMatch(Double longitude, Double latitude, SearchResponse response, Active bestMatch) {
        if(bestMatch != null) {
            response.setBest(response(
                    bestMatch.getProfile(), bestMatch.getStatus(),
                    HelperUtil.getDistance(latitude, longitude, bestMatch.getLatitude(), bestMatch.getLongitude())
            ));

            List<ActiveResponse> providers = new ArrayList<>(response.getProviders());
            providers.removeIf(p -> p.getId().equals(bestMatch.getProfile().getId()));
            response.setProviders(providers);
        }
    }
}
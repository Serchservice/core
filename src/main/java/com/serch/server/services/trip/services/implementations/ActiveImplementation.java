package com.serch.server.services.trip.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.enums.trip.TripStatus;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.exceptions.others.TripException;
import com.serch.server.mappers.TripMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.business.BusinessProfile;
import com.serch.server.models.trip.Active;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.business.BusinessProfileRepository;
import com.serch.server.repositories.trip.ActiveRepository;
import com.serch.server.services.trip.requests.OnlineRequest;
import com.serch.server.services.trip.responses.ActiveResponse;
import com.serch.server.services.trip.services.ActiveSearchService;
import com.serch.server.services.trip.services.ActiveService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing the active status of users, such as toggling their trip status
 * and fetching current status.
 * This implements its wrapper class {@link ActiveService}
 *
 * @see ActiveRepository
 * @see ProfileRepository
 * @see UserUtil
 * @see ActiveSearchService
 */
@Service
@RequiredArgsConstructor
public class ActiveImplementation implements ActiveService {
    private final ActiveSearchService service;
    private final UserUtil userUtil;
    private final ActiveRepository activeRepository;
    private final ProfileRepository profileRepository;
    private final BusinessProfileRepository businessProfileRepository;

    @Value("${application.map.search-radius}")
    private String MAP_SEARCH_RADIUS;

    @Override
    public ApiResponse<TripStatus> toggleStatus(OnlineRequest request) {
        if(userUtil.getUser().isProvider()) {
            Optional<Active> existing = activeRepository.findByProfile_Id(userUtil.getUser().getId());
            if(existing.isPresent()) {
                if(existing.get().getTripStatus() == TripStatus.ONLINE) {
                    existing.get().setTripStatus(TripStatus.OFFLINE);
                    existing.get().setUpdatedAt(LocalDateTime.now());
                    updateActive(request, existing.get());
                    activeRepository.save(existing.get());
                    return new ApiResponse<>(TripStatus.OFFLINE);
                } else if(existing.get().getTripStatus() == TripStatus.OFFLINE) {
                    existing.get().setTripStatus(TripStatus.ONLINE);
                    existing.get().setUpdatedAt(LocalDateTime.now());
                    updateActive(request, existing.get());
                    activeRepository.save(existing.get());
                    return new ApiResponse<>(TripStatus.ONLINE);
                } else {
                    throw new TripException("Can't update your trip status");
                }
            } else {
                Profile profile = profileRepository.findById(userUtil.getUser().getId())
                        .orElseThrow(() -> new TripException("Profile not found"));
                Active active = TripMapper.INSTANCE.active(request);
                active.setTripStatus(TripStatus.ONLINE);
                active.setProfile(profile);
                activeRepository.save(active);
                return new ApiResponse<>("Success", TripStatus.ONLINE, HttpStatus.CREATED);
            }
        } else {
            throw new TripException("Access denied. Cannot perform action");
        }
    }

    private void updateActive(OnlineRequest request, Active active) {
        if(request != null) {
            if(request.getCity() != null && !active.getCity().equalsIgnoreCase(request.getCity())) {
                active.setCity(request.getCity());
            }
            if(request.getPlace() != null && !active.getPlace().equalsIgnoreCase(request.getPlace())) {
                active.setPlace(request.getPlace());
            }
            if(request.getCountry() != null && !active.getCountry().equalsIgnoreCase(request.getCountry())) {
                active.setCountry(request.getCountry());
            }
            if(request.getState() != null && !active.getState().equalsIgnoreCase(request.getState())) {
                active.setState(request.getState());
            }
            if(request.getLatitude() != null && !active.getLatitude().equals(request.getLatitude())) {
                active.setLatitude(request.getLatitude());
            }
            if(request.getLongitude() != null && !active.getLongitude().equals(request.getLongitude())) {
                active.setLongitude(request.getLongitude());
            }
        }
    }

    @Override
    public ApiResponse<TripStatus> fetchStatus() {
        return new ApiResponse<>(
                "Success",
                activeRepository.findByProfile_Id(userUtil.getUser().getId())
                        .map(Active::getTripStatus)
                        .orElse(TripStatus.OFFLINE),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<List<ActiveResponse>> activeList() {
        BusinessProfile business = businessProfileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new AccountException("Access denied"));
        if(business.getAssociates() != null && !business.getAssociates().isEmpty()) {
            List<ActiveResponse> list = business.getAssociates()
                    .stream()
                    .map(profile -> {
                        Active active = activeRepository.findByProfile_Id(profile.getId())
                                .orElse(null);
                        ActiveResponse response = new ActiveResponse();
                        response.setName(profile.getFullName());
                        response.setAvatar(profile.getAvatar());
                        response.setStatus(active != null ? active.getTripStatus() : TripStatus.OFFLINE);
                        response.setCategory(profile.getCategory().getType());
                        response.setImage(profile.getCategory().getImage());
                        return response;
                    })
                    .toList();
            return new ApiResponse<>(list);
        } else {
            return new ApiResponse<>(List.of());
        }
    }

    @Override
    public void toggle(User user, TripStatus status, OnlineRequest request) {
        activeRepository.findByProfile_Id(user.getId())
                .ifPresentOrElse(active -> {
                    active.setTripStatus(status);
                    active.setUpdatedAt(LocalDateTime.now());
                    updateActive(request, active);
                    activeRepository.save(active);
                }, () -> profileRepository.findById(user.getId())
                        .ifPresent(profile -> {
                            Active active = TripMapper.INSTANCE.active(request);
                            active.setTripStatus(status);
                            active.setProfile(profile);
                            activeRepository.save(active);
                        }));
    }

    private SerchCategory category(String category) {
        if(category.equalsIgnoreCase(SerchCategory.MECHANIC.getType())) {
            return SerchCategory.MECHANIC;
        } else if(category.equalsIgnoreCase(SerchCategory.CARPENTER.getType())) {
            return SerchCategory.CARPENTER;
        } else if(category.equalsIgnoreCase(SerchCategory.PLUMBER.getType())) {
            return SerchCategory.PLUMBER;
        } else if(category.equalsIgnoreCase(SerchCategory.ELECTRICIAN.getType())) {
            return SerchCategory.ELECTRICIAN;
        } else {
            return SerchCategory.USER;
        }
    }

    @Override
    public ApiResponse<List<ActiveResponse>> search(String query, String category, Double longitude, Double latitude, Double radius) {
        double searchRadius = radius == null ? Double.parseDouble(MAP_SEARCH_RADIUS) : radius;

        return switch (query.toLowerCase()) {
            case "active" -> service.searchByCategory(category(category), longitude, latitude, searchRadius);
            case "free" -> service.searchByFree(category(category), longitude, latitude, searchRadius);
            case "rating" -> service.searchByRating(category(category), longitude, latitude, searchRadius);
            case "verified" -> service.searchByVerified(category(category), longitude, latitude, searchRadius);
            default -> service.searchBySpecialty(query, longitude, latitude, searchRadius);
        };
    }

    @Override
    public ApiResponse<ActiveResponse> auto(String query, String category, Double longitude, Double latitude, Double radius) {
        double searchRadius = radius == null ? Double.parseDouble(MAP_SEARCH_RADIUS) : radius;
        Active provider;

        if(query != null && !query.isEmpty()) {
            provider = activeRepository.findBestMatchWithQuery(
                    latitude, longitude, query, searchRadius, PlanStatus.ACTIVE.name()
            );
        } else {
            provider = activeRepository.findBestMatchWithCategory(
                    latitude, longitude, category(category).name(), searchRadius, PlanStatus.ACTIVE.name()
            );
        }

        if(provider != null) {
            return new ApiResponse<>(service.response(
                    provider.getProfile(),
                    provider.getTripStatus(),
                    HelperUtil.getDistance(
                            latitude, longitude,
                            provider.getLatitude(),
                            provider.getLongitude()
                    )
            ));
        } else {
            throw new TripException("No provider found");
        }
    }
}
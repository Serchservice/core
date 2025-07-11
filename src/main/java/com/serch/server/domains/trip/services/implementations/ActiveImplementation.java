package com.serch.server.domains.trip.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.ProviderStatus;
import com.serch.server.exceptions.others.TripException;
import com.serch.server.mappers.TripMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.trip.Active;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.trip.ActiveRepository;
import com.serch.server.domains.trip.requests.OnlineRequest;
import com.serch.server.domains.trip.responses.ActiveResponse;
import com.serch.server.domains.trip.responses.MapViewResponse;
import com.serch.server.domains.trip.services.ActiveSearchService;
import com.serch.server.domains.trip.services.ActiveService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing the active status of users, such as toggling their trip status
 * and fetching current status.
 * This implements its wrapper class {@link ActiveService}
 *
 * @see ActiveRepository
 * @see ProfileRepository
 * @see AuthUtil
 * @see ActiveSearchService
 */
@Service
@RequiredArgsConstructor
public class ActiveImplementation implements ActiveService {
    private static final Logger log = LoggerFactory.getLogger(ActiveImplementation.class);
    private final AuthUtil authUtil;
    private final SimpMessagingTemplate messaging;
    private final ActiveRepository activeRepository;
    private final ProfileRepository profileRepository;

    @Override
    public ApiResponse<ProviderStatus> toggleStatus(OnlineRequest request) {
        log.info(String.format("ACTIVE TOGGLE STATUS::: %s", request));

        if(authUtil.getUser().isProvider()) {
            Optional<Active> existing = activeRepository.findByProfile_Id(authUtil.getUser().getId());
            if(existing.isPresent()) {
                if(existing.get().getStatus() == ProviderStatus.ONLINE) {
                    existing.get().setStatus(ProviderStatus.OFFLINE);
                    existing.get().setUpdatedAt(TimeUtil.now());
                    updateActive(request, existing.get());
                    activeRepository.save(existing.get());

                    return new ApiResponse<>(ProviderStatus.OFFLINE);
                } else if(existing.get().getStatus() == ProviderStatus.OFFLINE) {
                    existing.get().setStatus(ProviderStatus.ONLINE);
                    existing.get().setUpdatedAt(TimeUtil.now());
                    updateActive(request, existing.get());
                    activeRepository.save(existing.get());

                    return new ApiResponse<>(ProviderStatus.ONLINE);
                } else {
                    throw new TripException("Can't update your trip status");
                }
            } else if(request.getLatitude() == null || request.getLongitude() == null) {
                throw new TripException("There is a missing detail in your location response. Please check your permission settings");
            } else if(request.getAddress() == null || request.getAddress().isEmpty()) {
                throw new TripException("Your address is needed for your online activity. Please try again");
            } else {
                Profile profile = profileRepository.findById(authUtil.getUser().getId())
                        .orElseThrow(() -> new TripException("Profile not found"));
                Active active = TripMapper.INSTANCE.active(request);
                active.setStatus(ProviderStatus.ONLINE);
                active.setProfile(profile);
                activeRepository.save(active);
                return new ApiResponse<>("Success", ProviderStatus.ONLINE, HttpStatus.CREATED);
            }
        } else {
            throw new TripException("Access denied. Cannot perform action");
        }
    }

    private void updateActive(OnlineRequest request, Active active) {
        if(request != null) {
            if(request.getAddress() != null && !active.getAddress().equalsIgnoreCase(request.getAddress())) {
                active.setAddress(request.getAddress());
            }
            if(request.getPlaceId() != null && !active.getPlaceId().equalsIgnoreCase(request.getPlaceId())) {
                active.setPlaceId(request.getPlaceId());
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
    public ApiResponse<ProviderStatus> fetchStatus() {
        return new ApiResponse<>(
                "Success",
                activeRepository.findByProfile_Id(authUtil.getUser().getId())
                        .map(Active::getStatus)
                        .orElse(ProviderStatus.OFFLINE),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<List<ActiveResponse>> activeList(Integer page, Integer size) {
        Page<Profile> associates = profileRepository.findActiveAssociatesByBusinessId(authUtil.getUser().getId(), HelperUtil.getPageable(page, size));

        if(associates != null && associates.hasContent() && !associates.getContent().isEmpty()) {
            List<ActiveResponse> list = associates.getContent()
                    .stream()
                    .map(profile -> {
                        Active active = activeRepository.findByProfile_Id(profile.getId()).orElse(null);
                        ActiveResponse response = new ActiveResponse();
                        response.setName(profile.getFullName());
                        response.setAvatar(profile.getAvatar());
                        response.setStatus(active != null ? active.getStatus() : ProviderStatus.OFFLINE);
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
    public void toggle(User user, ProviderStatus status, OnlineRequest request) {
        activeRepository.findByProfile_Id(user.getId())
                .ifPresentOrElse(active -> {
                    active.setStatus(status);
                    active.setUpdatedAt(TimeUtil.now());

                    if(request != null) {
                        updateActive(request, active);
                    }
                    activeRepository.save(active);
                    messaging.convertAndSend(
                            "/platform/%s/trip/status".formatted(String.valueOf(active.getProfile().getId())),
                            status
                    );
                }, () -> profileRepository.findById(user.getId())
                        .ifPresent(profile -> {
                            if(request != null) {
                                Active active = TripMapper.INSTANCE.active(request);
                                active.setStatus(status);
                                active.setProfile(profile);
                                activeRepository.save(active);

                                messaging.convertAndSend(
                                        "/platform/%s/trip/status".formatted(String.valueOf(profile.getId())),
                                        status
                                );
                            }
                        }));
    }

    @Override
    public MapViewResponse getLocation(User user) {
        Active active = activeRepository.findByProfile_Id(user.getId()).orElse(null);

        if(active != null) {
            return TripMapper.INSTANCE.view(active);
        }

        return null;
    }
}
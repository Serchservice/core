package com.serch.server.domains.trip.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.notification.services.NotificationService;
import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.core.storage.services.StorageService;
import com.serch.server.enums.account.ProviderStatus;
import com.serch.server.exceptions.others.TripException;
import com.serch.server.mappers.TripMapper;
import com.serch.server.models.account.PhoneInformation;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.trip.*;
import com.serch.server.repositories.account.PhoneInformationRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.trip.*;
import com.serch.server.domains.shared.services.SharedService;
import com.serch.server.domains.trip.requests.*;
import com.serch.server.domains.trip.responses.ActiveResponse;
import com.serch.server.domains.trip.responses.MapViewResponse;
import com.serch.server.domains.trip.responses.TripResponse;
import com.serch.server.domains.trip.services.*;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static com.serch.server.enums.account.ProviderStatus.*;
import static com.serch.server.enums.auth.Role.USER;
import static com.serch.server.enums.trip.TripConnectionStatus.*;
import static com.serch.server.enums.trip.TripMode.FROM_USER;
import static com.serch.server.enums.trip.TripStatus.*;
import static com.serch.server.enums.trip.TripType.REQUEST;
import static com.serch.server.enums.trip.TripType.SPEAK_TO;

@Service
@RequiredArgsConstructor
public class TripImplementation implements TripService {
    private final StorageService storageService;
    private final SharedService sharedService;
    private final NotificationService notificationService;
    private final ActiveSearchService activeSearchService;
    private final TripTimelineService timelineService;
    private final TripAuthenticationService authenticationService;
    private final TripPayService payService;
    private final ActiveService activeService;
    private final TripHistoryService historyService;
    private final UserUtil userUtil;
    private final SimpMessagingTemplate messaging;
    private final ProfileRepository profileRepository;
    private final TripRepository tripRepository;
    private final TripAuthenticationRepository tripAuthenticationRepository;
    private final TripShareRepository tripShareRepository;
    private final PhoneInformationRepository phoneInformationRepository;
    private final ActiveRepository activeRepository;
    private final MapViewRepository mapViewRepository;

    @Override
    @Transactional
    public ApiResponse<TripResponse> request(TripInviteRequest request) {
        if(request.getAmount() != null) {
            Profile profile = profileRepository.findById(request.getProvider())
                    .orElseThrow(() -> new TripException("Provider not found"));

            if(profile.getActive() == null) {
                throw new TripException("We cannot place this request because the provider is not ready to accept trips");
            } else if(tripRepository.existsByProviderIdAndTimelineStatus(profile.getId())) {
                throw new TripException("%s is on a trip at the moment".formatted(profile.getFullName()));
            } else {
                Trip trip = create(request, profile);
                sharedService.create(trip.getLinkId(), trip.getAccount(), trip);

                return getRequestResponse(trip, userUtil.getUser());
            }
        } else {
            throw new TripException("There must be an amount in order to create this trip");
        }
    }

    @Transactional
    protected Trip create(TripInviteRequest request, Profile profile) {
        Trip trip = TripMapper.INSTANCE.trip(request);
        trip.setAccount(String.valueOf(userUtil.getUser().getId()));
        trip.setMode(FROM_USER);
        trip.setProvider(profile);
        trip.setCategory(profile.getCategory());
        trip.setType(SPEAK_TO);
        trip.setStatus(WAITING);
        trip.setAmount(BigDecimal.valueOf(request.getAmount()));

        if(!HelperUtil.isUploadEmpty(request.getAudio())) {
            String url = storageService.upload(request.getAudio(), "trip");
            trip.setAudio(url);
        }
        return tripRepository.save(trip);
    }

    @Transactional
    protected ApiResponse<TripResponse> getRequestResponse(Trip trip, User user) {
        timelineService.create(trip, null, REQUESTED);
        notificationService.send(
                String.valueOf(trip.getProvider().getId()),
                String.format("%s is requesting for your %s skill", user.getFullName(), trip.getProvider().getCategory().getType()),
                "New trip request",
                String.valueOf(user.getId()), trip.getId(), false
        );

        return new ApiResponse<>(
                "Request created, waiting for response",
                historyService.response(trip.getId(), String.valueOf(user.getId()), null, true, null),
                HttpStatus.OK
        );
    }

    @Override
    @Transactional
    public ApiResponse<TripResponse> rebook(String id, Boolean withInvited) {
        Trip trip = buildTripFromExisting(id, withInvited);

        return getRequestResponse(trip, userUtil.getUser());
    }

    @Transactional
    protected Trip buildTripFromExisting(String id, Boolean withInvited) {
        Trip existing = tripRepository.findById(id).orElseThrow(() -> new TripException("No trip found"));

        Trip newTrip = TripMapper.INSTANCE.trip(existing);
        if(withInvited != null && withInvited) {
            if(existing.getInvited() != null && existing.getInvited().getProvider() != null) {
                newTrip.setProvider(existing.getInvited().getProvider());
            } else {
                throw new TripException("This trip was not shared to a Serch service provider");
            }
        } else {
            newTrip.setProvider(existing.getProvider());
        }
        newTrip.setStatus(WAITING);
        return tripRepository.save(newTrip);
    }

    @Override
    @Transactional
    public ApiResponse<TripResponse> request(TripInviteRequest request, Profile account, Profile profile) {
        if(request.getAmount() != null) {
            Trip trip = create(request, account.getId().toString(), profile);
            payService.pay(trip, profile.getId());

            return getRequestResponse(trip, account.getUser());
        } else {
            throw new TripException("There must be an amount in order to create this trip");
        }
    }

    @Transactional
    protected Trip create(TripInviteRequest request, String account, Profile profile) {
        Trip trip = TripMapper.INSTANCE.trip(request);
        trip.setAccount(account);
        trip.setMode(FROM_USER);
        trip.setProvider(profile);
        trip.setType(REQUEST);
        trip.setStatus(ACTIVE);
        trip.setAmount(BigDecimal.valueOf(request.getAmount()));
        return tripRepository.save(trip);
    }

    @Override
    @Transactional
    public ApiResponse<TripResponse> accept(TripAcceptRequest request) {
        Trip trip = tripRepository.findByIdAndProviderId(request.getTrip(), userUtil.getUser().getId())
                .orElseThrow(() -> new TripException("Trip not found"));
        trip.setStatus(ACTIVE);
        tripRepository.save(trip);

        MapView mapView = TripMapper.INSTANCE.view(activeService.getLocation(trip.getProvider().getUser()));
        mapView.setTrip(trip);
        mapViewRepository.save(mapView);

        InitializePaymentData data = payService.pay(trip, userUtil.getUser().getId());

        authenticationService.create(trip, null);
        activeService.toggle(trip.getProvider().getUser(), BUSY, null);

        updateOthers(trip);
        return new ApiResponse<>(historyService.response(trip.getId(), String.valueOf(userUtil.getUser().getId()), data, true, null));
    }

    @Override
    @Transactional
    public void updateOthers(Trip trip) {
        if(trip.getProvider() != null) {
            historyService.response(trip.getId(), String.valueOf(trip.getProvider().getId()), null, true, null);
        }
        if(trip.getInvited() != null && trip.getInvited().getProvider() != null) {
            historyService.response(trip.getId(), String.valueOf(trip.getInvited().getProvider().getId()), null, true, null);
        }
        historyService.response(trip.getId(), trip.getAccount(), null, true, null);
    }

    @Override
    @Transactional
    public ApiResponse<List<TripResponse>> end(TripCancelRequest request) {
        Trip trip;
        if(request.getGuest() != null && !request.getGuest().isEmpty()) {
            trip = tripRepository.findByIdAndAccount(request.getTrip(), request.getGuest())
                    .orElseThrow(() -> new TripException("No trip found"));
        } else if(userUtil.getUser().getRole() == USER) {
            trip = tripRepository.findByIdAndAccount(request.getTrip(), String.valueOf(userUtil.getUser().getId()))
                    .orElseThrow(() -> new TripException("No trip found"));
        } else {
            trip = tripRepository.findByIdAndProviderId(request.getTrip(), userUtil.getUser().getId())
                    .orElseThrow(() -> new TripException("No trip found"));
        }

        trip.setIsActive(false);
        trip.setStatus(CLOSED);
        tripRepository.save(trip);

        timelineService.create(trip, null, COMPLETED);
        activeService.toggle(trip.getProvider().getUser(), ONLINE, TripMapper.INSTANCE.request(trip));
        payService.processCredit(trip);

        if(trip.getInvited() != null && trip.getInvited().getProvider() != null) {
            timelineService.create(null, trip.getInvited(), COMPLETED);
            activeService.toggle(trip.getInvited().getProvider().getUser(), ONLINE, TripMapper.INSTANCE.request(trip));
        }

        return historyService.history(request.getGuest(), request.getLinkId(), null, null, true, trip.getId());
    }

    @Override
    @Transactional
    public ApiResponse<List<TripResponse>> cancel(TripCancelRequest request) {
        Trip trip;
        if(request.getGuest() != null && !request.getGuest().isEmpty()) {
            trip = tripRepository.findByIdAndAccount(request.getTrip(), request.getGuest())
                    .orElseThrow(() -> new TripException("Trip not found"));
        } else if(userUtil.getUser().getRole() == USER) {
            trip = tripRepository.findByIdAndAccount(request.getTrip(), String.valueOf(userUtil.getUser().getId()))
                    .orElseThrow(() -> new TripException("Trip not found"));
        } else {
            trip = tripRepository.findByIdAndProviderId(request.getTrip(), userUtil.getUser().getId())
                    .orElseThrow(() -> new TripException("Trip not found"));
        }

        trip.setCancelReason(request.getReason());
        trip.setIsActive(false);
        trip.setStatus(UNFULFILLED);
        tripRepository.save(trip);

        timelineService.create(trip, null, CANCELLED);
        activeService.toggle(trip.getProvider().getUser(), ONLINE, TripMapper.INSTANCE.request(trip));

        if(request.getGuest() != null && !request.getGuest().isEmpty()) {
            notificationService.send(
                    String.valueOf(trip.getProvider().getId()),
                    "We are sorry to notify you that this trip has been cancelled.",
                    "Trip share cancelled",
                    request.getGuest(), null, false
            );
        } else if(userUtil.getUser().getRole() == USER) {
            notificationService.send(
                    String.valueOf(trip.getProvider().getId()),
                    "We are sorry to notify you that this trip has been cancelled.",
                    "Trip share cancelled",
                    String.valueOf(userUtil.getUser().getId()), null, false
            );
        } else {
            notificationService.send(
                    trip.getAccount(),
                    "We are sorry to notify you that this trip has been cancelled.",
                    "Trip share cancelled",
                    String.valueOf(userUtil.getUser().getId()), null, false
            );
        }

        return historyService.history(request.getGuest(), request.getLinkId(), null, null, true, trip.getId());
    }

    @Override
    @Transactional
    public ApiResponse<TripResponse> payServiceFee(String id) {
        Trip trip = tripRepository.findByIdAndProviderId(id, userUtil.getUser().getId())
                .orElseThrow(() -> new TripException("Trip not found"));

        if(!trip.getIsServiceFeePaid()) {
            Boolean isPaid = payService.processPayment(trip);
            if(isPaid) {
                timelineService.create(trip, null, CONNECTED);
            } else {
                timelineService.create(trip, null, REQUESTED);
            }

            trip.setIsServiceFeePaid(isPaid);
            trip.setUpdatedAt(TimeUtil.now());
            tripRepository.save(trip);

            if(isPaid) {
                updateOthers(trip);
                return new ApiResponse<>(historyService.response(trip.getId(), String.valueOf(userUtil.getUser().getId()), null, true, null));
            } else {
                throw new TripException("Unable to process payment. Check wallet balance");
            }
        }
        throw new TripException("You've already paid for the necessary fee demanded.");
    }

    @Override
    @Transactional
    public ApiResponse<TripResponse> verify(String id, String guest, String reference) {
        Trip trip;
        if(guest != null && !guest.isEmpty()) {
            trip = tripRepository.findByIdAndAccount(id, guest)
                    .orElseThrow(() -> new TripException("Trip not found"));
        } else {
            trip = tripRepository.findByIdAndAccount(id, String.valueOf(userUtil.getUser().getId()))
                    .orElseThrow(() -> new TripException("Trip not found"));
        }

        Boolean isPaid = payService.verify(reference);
        if(isPaid) {
            timelineService.create(trip, null, CONNECTED);
        } else {
            timelineService.create(trip, null, REQUESTED);
        }

        updateOthers(trip);
        return new ApiResponse<>(historyService.response(trip.getId(), String.valueOf(userUtil.getUser().getId()), null, true, null));
    }

    @Override
    @Transactional
    public ApiResponse<List<TripResponse>> leave(String id) {
        Trip trip = tripRepository.findByIdAndProviderId(id, userUtil.getUser().getId())
                .orElseThrow(() -> new TripException("No trip found for trip " + id));

        if(trip.getInvited() != null && trip.getInvited().getTimelines() != null && !trip.getInvited().getTimelines().isEmpty()) {
            if(trip.getInvited().getTimelines().stream().anyMatch(time -> time.getStatus() == LEFT)) {
                throw new TripException("You cannot leave trip unless there is another provider on the trip");
            }
            timelineService.create(trip, null, LEFT);
            activeService.toggle(trip.getProvider().getUser(), ONLINE, TripMapper.INSTANCE.request(trip));

            notificationService.send(
                    trip.getAccount(),
                    String.format("%s has left the trip", userUtil.getUser().getFullName()),
                    "Trip Update - Provider left",
                    String.valueOf(userUtil.getUser().getId()), null, false
            );

            notificationService.send(
                    String.valueOf(trip.getInvited().getProvider().getId()),
                    String.format("%s has left the trip", userUtil.getUser().getFullName()),
                    "Trip Update - Provider left",
                    String.valueOf(userUtil.getUser().getId()), null, false
            );

            return historyService.history(null, null, null, null, true, trip.getId());
        }
        throw new TripException("You cannot leave trip unless there is another provider on the trip");
    }

    @Override
    @Transactional
    public ApiResponse<List<ActiveResponse>> search(String phoneNumber, Double lat, Double lng, Integer page, Integer size) {
        Page<PhoneInformation> list = phoneInformationRepository.findByPhoneNumber(phoneNumber, HelperUtil.getPageable(page, size));

        if(list == null || !list.hasContent()) {
            return new ApiResponse<>(List.of());
        } else {
            List<ActiveResponse> responses = list.getContent().stream()
                    .map(phone -> {
                        Profile profile = profileRepository.findById(phone.getUser().getId()).orElse(null);
                        ProviderStatus status = activeRepository.findByProfile_Id(phone.getUser().getId())
                                .map(Active::getStatus).orElse(OFFLINE);
                        Double latitude = activeRepository.findByProfile_Id(phone.getUser().getId())
                                .map(Active::getLatitude).orElse(0.0);
                        Double longitude = activeRepository.findByProfile_Id(phone.getUser().getId())
                                .map(Active::getLongitude).orElse(0.0);

                        if(profile != null) {
                            return activeSearchService.response(profile, status, HelperUtil.getDistance(lat, lng, latitude, longitude));
                        } else {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .sorted()
                    .toList();

            return new ApiResponse<>(responses);
        }
    }

    @Override
    @Transactional
    public ApiResponse<TripResponse> auth(TripAuthRequest request) {
        Trip trip;
        if(request.getGuest() != null && !request.getGuest().isEmpty()) {
            trip = tripRepository.findByIdAndAccount(request.getTrip(), request.getGuest())
                    .orElseThrow(() -> new TripException("Trip not found"));
        } else {
            trip = tripRepository.findByIdAndAccount(request.getTrip(), String.valueOf(userUtil.getUser().getId()))
                    .orElseThrow(() -> new TripException("Trip not found"));
        }

        if(trip.getAuthentication() != null) {
            if(authenticationService.verify(request, trip.getAuthentication().getCode())) {
                TripAuthentication authentication = trip.getAuthentication();
                authentication.setIsVerified(true);
                authentication.setUpdatedAt(TimeUtil.now());
                tripAuthenticationRepository.save(authentication);
                timelineService.create(trip, null, AUTHENTICATED);
                timelineService.create(trip, null, ON_TRIP);

                notificationService.send(
                        String.valueOf(trip.getProvider().getId()),
                        "This trip is now verified",
                        "Trip authentication successful",
                        trip.getAccount(), null, false
                );

                updateOthers(trip);
                return new ApiResponse<>(historyService.response(
                        trip.getId(),
                        request.getGuest() != null ? request.getGuest() : String.valueOf(userUtil.getUser().getId()),
                        null,
                        true,
                        null
                ));
            } else {
                throw new TripException("Wrong authentication code");
            }
        } else {
            throw new TripException("No authentication found for trip " + request.getTrip());
        }
    }

    @Override
    @Transactional
    public ApiResponse<TripResponse> update(TripUpdateRequest request) {
        if(request.getIsShared()) {
            TripShare share = tripShareRepository.findByTrip_IdAndProvider_Id(request.getTrip(), userUtil.getUser().getId())
                    .orElseThrow(() -> new TripException("Trip not found"));

            timelineService.create(null, share, request.getStatus());

            updateOthers(share.getTrip());
            return new ApiResponse<>(historyService.response(share.getTrip().getId(), String.valueOf(userUtil.getUser().getId()), null, true, null));
        } else if(request.getGuest() != null && !request.getGuest().isEmpty()) {
            Trip trip = tripRepository.findByIdAndAccount(request.getTrip(), request.getGuest())
                    .orElseThrow(() -> new TripException("Trip not found"));

            timelineService.create(trip, null, request.getStatus());

            updateOthers(trip);
            return new ApiResponse<>(historyService.response(trip.getId(), request.getGuest(), null, true, null));
        } else if(userUtil.getUser().getRole() == USER) {
            Trip trip = tripRepository.findByIdAndAccount(request.getTrip(), String.valueOf(userUtil.getUser().getId()))
                    .orElseThrow(() -> new TripException("Trip not found"));

            timelineService.create(trip, null, request.getStatus());

            updateOthers(trip);
            return new ApiResponse<>(historyService.response(trip.getId(), String.valueOf(userUtil.getUser().getId()), null, true, null));
        } else {
            Trip trip = tripRepository.findByIdAndProviderId(request.getTrip(), userUtil.getUser().getId())
                    .orElseThrow(() -> new TripException("Trip not found"));

            timelineService.create(trip, null, request.getStatus());

            updateOthers(trip);
            return new ApiResponse<>(historyService.response(trip.getId(), String.valueOf(userUtil.getUser().getId()), null, true, null));
        }
    }

    @Override
    @Transactional
    public void update(MapViewRequest request) {
        if(request.getIsShared()) {
            TripShare share = tripShareRepository.findByTrip_IdAndProvider_Id(request.getTrip(), userUtil.getUser().getId())
                    .orElseThrow(() -> new TripException("Trip not found"));

            if(share != null) {
                MapView view = share.getMapView();
                if(view != null) {
                    updateView(request, view);
                } else {
                    view = TripMapper.INSTANCE.view(request);
                    view.setSharing(share);
                    mapViewRepository.save(view);
                }

                sendViewUpdate(view, share.getTrip());
            }
        } else {
            Trip trip = tripRepository.findByIdAndProviderId(request.getTrip(), userUtil.getUser().getId()).orElse(null);

            if(trip != null) {
                MapView view = trip.getMapView();
                if(view != null) {
                    updateView(request, view);
                } else {
                    view = TripMapper.INSTANCE.view(request);
                    view.setTrip(trip);
                    mapViewRepository.save(view);
                }

                sendViewUpdate(view, trip);
            }
        }
    }

    private void sendViewUpdate(MapView view, Trip share) {
        MapViewResponse response = TripMapper.INSTANCE.view(view);

        messaging.convertAndSend("/platform/%s/trip/%s/location".formatted(share.getAccount(), share.getId()), response);
        historyService.response(share.getId(), String.valueOf(userUtil.getUser().getId()), null, true, null);
    }

    @Transactional
    protected void updateView(MapViewRequest request, MapView view) {
        view.setPlace(request.getPlace());
        view.setLongitude(request.getLongitude());
        view.setLatitude(request.getLatitude());
        view.setBearing(request.getBearing());
        view.setUpdatedAt(TimeUtil.now());

        mapViewRepository.save(view);
    }
}

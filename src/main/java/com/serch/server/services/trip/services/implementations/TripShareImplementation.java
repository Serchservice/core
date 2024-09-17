package com.serch.server.services.trip.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.notification.core.NotificationService;
import com.serch.server.exceptions.others.TripException;
import com.serch.server.mappers.TripMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.trip.*;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.trip.*;
import com.serch.server.services.trip.requests.*;
import com.serch.server.services.trip.responses.TripResponse;
import com.serch.server.services.trip.services.*;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.serch.server.enums.auth.Role.USER;
import static com.serch.server.enums.trip.TripConnectionStatus.*;
import static com.serch.server.enums.trip.TripConnectionStatus.REQUESTED;
import static com.serch.server.enums.trip.TripShareAccess.DENIED;
import static com.serch.server.enums.trip.TripShareAccess.GRANTED;
import static com.serch.server.enums.account.ProviderStatus.ONLINE;
import static com.serch.server.enums.account.ProviderStatus.REQUESTSHARING;
import static com.serch.server.enums.trip.TripStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.NESTED)
public class TripShareImplementation implements TripShareService {
    private static final Logger log = LoggerFactory.getLogger(TripShareImplementation.class);
    private final NotificationService notificationService;
    private final TripTimelineService timelineService;
    private final TripAuthenticationService authenticationService;
    private final TripHistoryService historyService;
    private final TripPayService payService;
    private final ActiveService activeService;
    private final TripService tripService;
    private final UserUtil userUtil;
    private final SimpMessagingTemplate messaging;
    private final TripRepository tripRepository;
    private final TripShareRepository tripShareRepository;
    private final GuestRepository guestRepository;
    private final ProfileRepository profileRepository;
    private final TripAuthenticationRepository tripAuthenticationRepository;
    private final ActiveRepository activeRepository;
    private final MapViewRepository mapViewRepository;

    @Value("${application.map.search-radius}")
    private String MAP_SEARCH_RADIUS;

    @Override
    @Transactional
    public ApiResponse<TripResponse> access(String guest, String id) {
        String account;
        String name;
        if(guest != null && !guest.isEmpty()) {
            Guest guestProfile = guestRepository.findById(guest)
                    .orElseThrow(() -> new TripException("Guest not found"));
            account = guestProfile.getId();
            name = guestProfile.getFullName();
        } else {
            account = String.valueOf(userUtil.getUser().getId());
            name = userUtil.getUser().getFullName();
        }

        Trip trip = tripRepository.findByIdAndAccount(id, account)
                .orElseThrow(() -> new TripException("No trip found for trip " + id));

        if(trip.getAccess() == GRANTED) {
            trip.setAccess(DENIED);
            notificationService.send(
                    String.valueOf(trip.getProvider().getId()),
                    String.format("%s has denied you share access. You cannot invite another provider to the trip", name),
                    "Share access denied",
                    account, trip.getId(), false
            );
        } else {
            trip.setAccess(GRANTED);
            notificationService.send(
                    String.valueOf(trip.getProvider().getId()),
                    String.format("%s has granted you share access. You can now invite another provider to the trip", name),
                    "Share access granted",
                    account, trip.getId(), false
            );
        }
        trip.setUpdatedAt(TimeUtil.now());
        tripRepository.save(trip);
        timelineService.create(trip, null, trip.getAccess() == GRANTED ? SHARE_ACCESS_GRANTED : SHARE_ACCESS_DENIED);

        tripService.updateOthers(trip);
        return new ApiResponse<>(historyService.response(trip.getId(), account, null, true, null));
    }

    @Override
    @Transactional
    public ApiResponse<TripResponse> share(TripShareRequest request) {
        Trip trip = tripRepository.findByIdAndProviderId(request.getTrip(), userUtil.getUser().getId())
                .orElseThrow(() -> new TripException("No trip found for trip " + request.getTrip()));

        if(trip.getAccess() == GRANTED) {
            if(trip.getInvited() == null) {
                TripShare share = create(request, trip);

                if(request.isOnline()) {
                    timelineService.create(null, share, REQUESTED);
                    authenticationService.create(null, share);
                }

                notificationService.send(
                        trip.getAccount(),
                        String.format(
                                "%s has shared this trip on the Serch platform. Waiting for responses... should be quick.",
                                userUtil.getUser().getFullName()
                        ),
                        "Another provider is invited!",
                        String.valueOf(userUtil.getUser().getId()), null, false
                );

                trip.setInvited(share);
                trip.setUpdatedAt(TimeUtil.now());
                tripRepository.save(trip);

                if(request.isOnline()) {
                    pingServiceProviders(trip, request);
                }

                tripService.updateOthers(trip);
                return new ApiResponse<>(historyService.response(trip.getId(), String.valueOf(userUtil.getUser().getId()), null, true, null));
            } else {
                throw new TripException("You cannot share a trip more than once.");
            }
        } else {
            throw new TripException("You cannot share this trip until user has granted you access to sharing.");
        }
    }

    @Transactional
    protected TripShare create(TripShareRequest request, Trip trip) {
        TripShare share = TripMapper.INSTANCE.share(request);
        share.setTrip(trip);
        share.setStatus(request.isOnline() ? WAITING : ACTIVE);

        if(request.getSerchCategory() != null) {
            share.setCategory(request.getSerchCategory().getType());
        }
        return tripShareRepository.save(share);
    }

    @Transactional
    protected void pingServiceProviders(Trip trip, TripShareRequest request) {
        String category = request.getSerchCategory() != null ? request.getSerchCategory().getType() : "";
        String filters = String.join(" | ", request.getFilters());

        log.info(String.format("SERCH SHARE TRIP ONLINE PING::: Searching for %s category with filters [%s]", category, filters));
        List<Active> actives = activeRepository.searchWithinDistance(trip.getLatitude(), trip.getLongitude(), Double.parseDouble(MAP_SEARCH_RADIUS), category, filters);

        if(actives != null && !actives.isEmpty()) {
            log.info(String.format("SERCH SHARE TRIP ONLINE PING::: Found %s active providers", actives.size()));

            actives.forEach(active -> {
                if(!active.getProfile().isSameAs(trip.getProvider().getId())) {
                    messaging.convertAndSend(
                            "/platform/%s".formatted(String.valueOf(active.getProfile().getId())),
                            historyService.response(trip.getId(), String.valueOf(active.getProfile().getId()), null, false, null)
                    );
                    notificationService.send(
                            String.valueOf(active.getProfile().getId()),
                            String.format("%s wants your service now", userUtil.getUser().getFullName()),
                            "You have a new shared trip request. Tap to view details",
                            String.valueOf(userUtil.getUser().getId()), null, false
                    );
                }
            });
        } else {
            log.info(String.format("SERCH SHARE TRIP ONLINE PING::: Found 0 active providers and is null %s", actives == null));
        }
    }

    @Override
    @Transactional
    public ApiResponse<String> cancel(TripCancelRequest request) {
        TripShare share;

        String account;
        if(request.getGuest() != null && !request.getGuest().isEmpty()) {
            share = tripShareRepository.findByTrip_IdAndTrip_Account(request.getTrip(), request.getGuest())
                    .orElseThrow(() -> new TripException("Shared trip not found"));
            account = request.getGuest();
        } else if(userUtil.getUser().getRole() == USER) {
            share = tripShareRepository.findByTrip_IdAndTrip_Account(request.getTrip(), String.valueOf(userUtil.getUser().getId()))
                    .orElseThrow(() -> new TripException("Shared trip not found"));
            account = String.valueOf(userUtil.getUser().getId());
        } else {
            share = tripShareRepository.findByTrip_IdAndProvider_Id(request.getTrip(), userUtil.getUser().getId())
                    .orElseThrow(() -> new TripException("Shared trip not found"));
            account = String.valueOf(userUtil.getUser().getId());
        }

        notificationService.send(
                share.getTrip().getAccount(),
                String.format(
                        "%s has cancelled the shared trip request from %s",
                        userUtil.getUser().getFullName(),
                        share.getTrip().getProvider().getFullName()
                ),
                "Trip share cancelled",
                String.valueOf(userUtil.getUser().getId()), null, false
        );

        notificationService.send(
                String.valueOf(share.getTrip().getProvider().getId()),
                String.format("%s has cancelled your shared trip request", userUtil.getUser().getFullName()),
                "Trip share cancelled",
                String.valueOf(userUtil.getUser().getId()), null, false
        );

        tripService.updateOthers(share.getTrip());
        historyService.response(share.getTrip().getId(), account, null, true, null);

        tripShareRepository.delete(share);
        return new ApiResponse<>("Shared trip cancelled", HttpStatus.OK);
    }

    @Override
    @Transactional
    public ApiResponse<TripResponse> accept(TripAcceptRequest request) {
        TripShare share = tripShareRepository.findByIdAndTrip_Id(request.getQuoteId(), request.getTrip())
                .orElseThrow(() -> new TripException("No shared found for trip " + request.getTrip()));

        if(share.getProvider() == null) {
            Profile profile = profileRepository.findById(userUtil.getUser().getId())
                    .orElseThrow(() -> new TripException("User not found"));

            share.getTrip().setCategory(profile.getCategory());
            share.getTrip().setUpdatedAt(TimeUtil.now());
            tripRepository.save(share.getTrip());

            share.setProvider(profile);
            share.setStatus(ACTIVE);
            share.setUpdatedAt(TimeUtil.now());
            tripShareRepository.save(share);

            MapView mapView = TripMapper.INSTANCE.view(activeService.getLocation(share.getProvider().getUser()));
            mapView.setSharing(share);
            mapViewRepository.save(mapView);

            timelineService.create(null, share, CONNECTED);
            authenticationService.create(null, share);
            activeService.toggle(share.getTrip().getProvider().getUser(), REQUESTSHARING, null);
            activeService.toggle(userUtil.getUser(), REQUESTSHARING, null);

            notificationService.send(
                    share.getTrip().getAccount(),
                    String.format("%s has accepted the shared trip invite", userUtil.getUser().getFullName()),
                    "Trip Update - Shared invite accepted",
                    String.valueOf(userUtil.getUser().getId()), null, false
            );

            notificationService.send(
                    String.valueOf(share.getTrip().getProvider().getId()),
                    String.format("%s has accepted the shared trip invite", userUtil.getUser().getFullName()),
                    "Trip Update - Shared invite accepted",
                    String.valueOf(userUtil.getUser().getId()), null, false
            );

            tripService.updateOthers(share.getTrip());
            return new ApiResponse<>(historyService.response(share.getTrip().getId(), String.valueOf(userUtil.getUser().getId()),  null, true, null));
        } else {
            throw new TripException("This trip has been accepted by another provider");
        }
    }

    @Override
    @Transactional
    public ApiResponse<TripResponse> auth(TripAuthRequest request) {
        TripShare share;
        String account;

        if(request.getGuest() != null && !request.getGuest().isEmpty()) {
            share = tripShareRepository.findByTrip_IdAndTrip_Account(request.getTrip(), request.getGuest())
                    .orElseThrow(() -> new TripException("Trip not found"));
            account = request.getGuest();
        } else {
            share = tripShareRepository.findByTrip_IdAndTrip_Account(request.getTrip(), String.valueOf(userUtil.getUser().getId()))
                    .orElseThrow(() -> new TripException("Trip not found"));
            account = String.valueOf(userUtil.getUser().getId());
        }

        if(share.getAuthentication() != null) {
            if(authenticationService.verify(request, share.getAuthentication().getCode())) {
                TripAuthentication authentication = share.getAuthentication();
                authentication.setIsVerified(true);
                authentication.setUpdatedAt(TimeUtil.now());
                tripAuthenticationRepository.save(authentication);

                notificationService.send(
                        String.valueOf(userUtil.getUser().getId()),
                        "Shared trip is now verified",
                        "Trip authentication successful",
                        share.getTrip().getAccount(), null, false
                );

                notificationService.send(
                        String.valueOf(share.getTrip().getProvider().getId()),
                        "Shared trip is now verified",
                        "Trip authentication successful",
                        share.getTrip().getAccount(), null, false
                );

                timelineService.create(null, share, AUTHENTICATED);
                timelineService.create(null, share, ON_TRIP);

                tripService.updateOthers(share.getTrip());
                return new ApiResponse<>(historyService.response(share.getTrip().getId(), account, null, true, null));
            } else {
                throw new TripException("Wrong authentication code");
            }
        } else {
            throw new TripException("No authentication found for shared trip " + request.getTrip());
        }
    }

    @Override
    @Transactional
    public ApiResponse<List<TripResponse>> end(String id) {
        TripShare share = tripShareRepository.findByTrip_IdAndProvider_Id(id, userUtil.getUser().getId())
                .orElseThrow(() -> new TripException("No shared found for trip " + id));

        OnlineRequest request = TripMapper.INSTANCE.request(share.getTrip());

        Trip trip = share.getTrip();
        trip.setIsActive(false);
        trip.setStatus(CLOSED);
        tripRepository.save(trip);

        share.setStatus(CLOSED);
        tripShareRepository.save(share);

        payService.processCredit(trip);
        timelineService.create(trip, null, COMPLETED);
        activeService.toggle(trip.getProvider().getUser(), ONLINE, request);
        timelineService.create(null, share, COMPLETED);
        activeService.toggle(userUtil.getUser(), ONLINE, request);

        return historyService.history(null, null, true, share.getTrip().getId());
    }

    @Override
    @Transactional
    public ApiResponse<List<TripResponse>> leave(String id) {
        TripShare share = tripShareRepository.findByTrip_IdAndProvider_Id(id, userUtil.getUser().getId())
                .orElseThrow(() -> new TripException("No shared found for trip " + id));

        if(share.getTrip().getTimelines().stream().anyMatch(time -> time.getStatus() == LEFT)) {
            throw new TripException("You cannot leave this trip when you are the only provider. You have to end it");
        }

        share.getTrip().setCategory(share.getProvider().getCategory());
        share.getTrip().setUpdatedAt(TimeUtil.now());
        tripRepository.save(share.getTrip());

        timelineService.create(null, share, LEFT);
        OnlineRequest request = TripMapper.INSTANCE.request(share.getTrip());
        activeService.toggle(userUtil.getUser(), ONLINE, request);

        notificationService.send(
                share.getTrip().getAccount(),
                String.format("%s has left the trip", userUtil.getUser().getFullName()),
                "Trip Update - Invited Provider left",
                String.valueOf(userUtil.getUser().getId()), null, false
        );

        notificationService.send(
                String.valueOf(share.getTrip().getProvider().getId()),
                String.format("%s has left the trip", userUtil.getUser().getFullName()),
                "Trip Update - Invited Provider left",
                String.valueOf(userUtil.getUser().getId()), null, false
        );

        return historyService.history(null, null, true, share.getTrip().getId());
    }
}
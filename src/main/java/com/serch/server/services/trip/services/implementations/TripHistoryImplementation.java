package com.serch.server.services.trip.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.trip.TripConnectionStatus;
import com.serch.server.exceptions.others.TripException;
import com.serch.server.mappers.TripMapper;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.PhoneInformation;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.bookmark.Bookmark;
import com.serch.server.models.rating.Rating;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.trip.*;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.PhoneInformationRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.bookmark.BookmarkRepository;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.trip.TripInviteRepository;
import com.serch.server.repositories.trip.TripPaymentRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.repositories.trip.TripTimelineRepository;
import com.serch.server.services.trip.responses.*;
import com.serch.server.services.trip.services.TripHistoryService;
import com.serch.server.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.serch.server.enums.account.SerchCategory.GUEST;
import static com.serch.server.enums.transaction.TransactionStatus.SUCCESSFUL;
import static com.serch.server.enums.trip.TripConnectionStatus.*;
import static com.serch.server.enums.trip.TripStatus.WAITING;
import static com.serch.server.enums.trip.TripType.REQUEST;

/**
 * This is the implementation of the wrapper class {@link TripHistoryService}.
 * It contains the logic and implementation of all the methods in its service layer.
 *
 * @see UserUtil
 * @see ProfileRepository
 * @see TripRepository
 * @see GuestRepository
 */
@Service
@RequiredArgsConstructor
public class TripHistoryImplementation implements TripHistoryService {
    private final UserUtil userUtil;
    private final SimpMessagingTemplate messaging;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d ' | ' h:mma");
    private final ProfileRepository profileRepository;
    private final TripRepository tripRepository;
    private final GuestRepository guestRepository;
    private final TripInviteRepository tripInviteRepository;
    private final UserRepository userRepository;
    private final TripPaymentRepository tripPaymentRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final PhoneInformationRepository phoneInformationRepository;
    private final RatingRepository ratingRepository;
    private final BookmarkRepository bookmarkRepository;
    private final TripTimelineRepository tripTimelineRepository;

    @Value("${application.map.search-radius}")
    private String MAP_SEARCH_RADIUS;

    @Override
    @Transactional
    public TripResponse response(String id, String userId) {
        TripInvite invite = tripInviteRepository.findById(id).orElseThrow(() -> new TripException("Invite not found"));

        return buildInviteResponse(userId, invite);
    }

    @Transactional
    protected TripResponse buildInviteResponse(String userId, TripInvite invite) {
        TripResponse response = TripMapper.INSTANCE.response(invite);

        if(invite.getShoppingItems() != null && !invite.getShoppingItems().isEmpty()) {
            response.setShoppingItems(invite.getShoppingItems().stream().map(shoppingItem -> {
                ShoppingItemResponse item = TripMapper.INSTANCE.response(shoppingItem);
                item.setAmount(MoneyUtil.formatToNaira(shoppingItem.getAmount()));
                return item;
            }).toList());
        }

        UserResponse userResponse = buildUserResponse(invite.getAccount(), invite.getId());
        response.setUser(userResponse);

        try {
            User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new TripException("User not found"));

            if(user.isUser()) {
                if(invite.getQuotes() != null && !invite.getQuotes().isEmpty()) {
                    response.setQuotations(buildQuotationList(invite));
                }
            } else {
                if(invite.getQuotes() != null && !invite.getQuotes().isEmpty()) {
                    response.setQuotations(buildQuotationList(invite, user, userResponse));
                    response.setWaitingForQuotationResponse(invite.getQuotes()
                            .stream()
                            .anyMatch(q -> q.getAccount() == null && (q.getProvider().isSameAs(user.getId()) || q.getProvider().belongsToBusiness(user.getId())))
                    );
                }
            }
        } catch (Exception ignored) {
            if(invite.getQuotes() != null && !invite.getQuotes().isEmpty()) {
                response.setQuotations(buildQuotationList(invite));
            }
        }

        response.setStatus(WAITING);
        response.setSnt("TRIP");
        response.setType(REQUEST);
        response.setMode(String.format("%s | %s", invite.getMode().getType(), invite.getAudio() == null || invite.getAudio().isEmpty() ? "Description" : "Audio"));
        response.setImage(invite.getCategory().getImage());
        response.setLabel(invite.getUpdatedAt().format(formatter));
        response.setCategory(invite.getCategory().getType());

        return response;
    }

    private List<QuotationResponse> buildQuotationList(TripInvite invite, User user, UserResponse userResponse) {
        return invite.getQuotes()
                .stream()
                .filter(q -> q.getAccount() != null && (q.getProvider().isSameAs(user.getId()) || q.getProvider().belongsToBusiness(user.getId())))
                .sorted(Comparator.comparing(TripInviteQuotation::getUpdatedAt).reversed())
                .map(quote -> getQuotationResponse(invite, quote, userResponse.getName(), userResponse.getAvatar(), userResponse.getRating()))
                .toList();
    }

    private List<QuotationResponse> buildQuotationList(TripInvite invite) {
        return invite.getQuotes()
                .stream()
                .filter(q -> q.getAccount() == null)
                .sorted(Comparator.comparing(TripInviteQuotation::getUpdatedAt).reversed())
                .map(quote -> getQuotationResponse(invite, quote, quote.getProvider().getFullName(), quote.getProvider().getAvatar(), quote.getProvider().getRating()))
                .toList();
    }

    private QuotationResponse getQuotationResponse(TripInvite invite, TripInviteQuotation quote, String name, String avatar, Double rating) {
        double latitude = quote.getProvider().getActive() != null ? quote.getProvider().getActive().getLatitude() : 0.0;
        double longitude = quote.getProvider().getActive() != null ? quote.getProvider().getActive().getLongitude() : 0.0;

        QuotationResponse quotation = TripMapper.INSTANCE.response(quote);
        quotation.setAmount(MoneyUtil.formatToNaira(quote.getAmount()));
        quotation.setName(name);
        quotation.setAvatar(avatar);
        quotation.setRating(rating);
        quotation.setDistance(String.format("%s km", HelperUtil.getDistance(invite.getLatitude(), invite.getLongitude(), latitude, longitude)));

        return quotation;
    }

    @Transactional
    protected UserResponse buildUserResponse(String id, String trip) {
        AtomicReference<UserResponse> response = new AtomicReference<>(new UserResponse());

        try {
            profileRepository.findById(UUID.fromString(id)).ifPresent(profile -> response.set(buildUserResponse(profile, trip)));
        } catch (Exception ignored) {
            Guest guest = guestRepository.findById(id).orElse(null);

            if(guest != null) {
                response.get().setAvatar(guest.getAvatar());
                response.get().setName(guest.getFullName());
                response.get().setRating(guest.getRating());
                response.get().setId(guest.getId());
                response.get().setImage(GUEST.getImage());
                response.get().setCategory(GUEST.getType());
                response.get().setRole(GUEST.name());
                response.get().setBookmark("");
                response.get().setTripRating(ratingRepository.findByEventAndRated(trip, id).map(Rating::getRating).orElse(null));
                response.get().setPhone(guest.getPhoneNumber());
            }
        }

        return response.get();
    }

    @Transactional
    protected UserResponse buildUserResponse(Profile profile, String trip) {
        UserResponse response = new UserResponse();
        response.setAvatar(profile.getAvatar());
        response.setName(profile.getFullName());
        response.setRating(profile.getRating());
        response.setId(profile.getId().toString());
        response.setImage(profile.getCategory().getImage());
        response.setCategory(profile.getCategory().getType());
        response.setRole(profile.getUser().getRole().getType());
        response.setPhone(phoneInformationRepository.findByUser_Id(profile.getId()).map(PhoneInformation::getPhoneNumber).orElse(""));
        response.setTripRating(ratingRepository.findByEventAndRated(trip, String.valueOf(profile.getId())).map(Rating::getRating).orElse(null));

        try {
            User user = userUtil.getUser();
            if(user.isUser()) {
                response.setBookmark(bookmarkRepository.findByUser_IdAndProvider_Id(user.getId(), profile.getId()).map(Bookmark::getBookmarkId).orElse(""));
            } else {
                response.setBookmark("");
            }
        } catch (Exception ignored) {
            response.setBookmark("");
        }

        return response;
    }

    @Override
    @Transactional
    public TripResponse response(String id, String userId, @Nullable InitializePaymentData payment, boolean sendUpdate, String requestedId) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new TripException("Trip not found"));

        return buildTripResponse(userId, payment, sendUpdate, requestedId, trip);
    }

    @Transactional
    protected TripResponse buildTripResponse(String userId, InitializePaymentData payment, boolean sendUpdate, String requestedId, Trip trip) {
        TripResponse response = TripMapper.INSTANCE.response(trip);
        String business = null;

        if(payment != null) {
            response.setPendingPaymentData(payment);
            if(trip.getPayment() != null) {
                trip.getPayment().setUrl(DatabaseUtil.encodeData(payment.getAuthorization_url()));
                tripPaymentRepository.save(trip.getPayment());
            }
        } else if(trip.getPayment() != null && trip.getPayment().getStatus() != SUCCESSFUL) {
            InitializePaymentData paymentData = new InitializePaymentData();
            paymentData.setAuthorization_url(DatabaseUtil.decodeData(trip.getPayment().getUrl()));
            paymentData.setReference(trip.getPayment().getReference());
            response.setPendingPaymentData(paymentData);
        } else {
            if(trip.getPayment() != null) {
                trip.getPayment().setUrl(null);
                tripPaymentRepository.save(trip.getPayment());
            }
        }

        UserResponse userResponse = buildUserResponse(trip.getAccount(), trip.getId());
        response.setUser(userResponse);

        try {
            User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new TripException("User not found"));
            if (!user.isUser()) {
                if(trip.getInvited() != null) {
                    TripShareResponse share = TripMapper.INSTANCE.share(trip.getInvited());

                    share.setIsProvider(trip.getInvited().getProvider().isSameAs(user.getId()));

                    if(trip.getInvited().getAuthentication() != null && share.getIsProvider()) {
                        share.setAuthentication(DatabaseUtil.decodeData(trip.getInvited().getAuthentication().getCode()));
                    }
                    response.setProvider(buildUserResponse(trip.getProvider(), trip.getId()));
                    response.setUser(buildUserResponse(trip.getAccount(), trip.getId()));

                    List<TripTimeline> timelines = tripTimelineRepository.findBySharing_Id(trip.getInvited().getId());
                    if(timelines != null && !timelines.isEmpty()) {
                        share.setTimelines(timeline(timelines, share, null));
                    }

                    if(trip.getInvited().getMapView() != null) {
                        share.setLocation(TripMapper.INSTANCE.view(trip.getInvited().getMapView()));
                    }

                    if(trip.getInvited().getProvider().isAssociate()) {
                        business = trip.getInvited().getProvider().getBusiness().getId().toString();
                    }
                }

                response.setIsProvider(trip.getProvider().isSameAs(user.getId()));

                if(trip.getAuthentication() != null && response.getIsProvider()) {
                    response.setAuthentication(DatabaseUtil.decodeData(trip.getAuthentication().getCode()));
                }

                if(response.getIsProvider() || trip.getProvider().isAssociate()) {
                    addInvitedInformation(trip, response);
                    response.setServiceFee(MoneyUtil.formatToNaira(trip.getServiceFee()));
                }

                if(trip.getProvider().isAssociate()) {
                    business = trip.getProvider().getBusiness().getId().toString();
                }
            } else {
                response.setProvider(buildUserResponse(trip.getProvider(), trip.getId()));
                addInvitedInformation(trip, response);
            }
        } catch (Exception ignored) {
            response.setProvider(buildUserResponse(trip.getProvider(), trip.getId()));
            addInvitedInformation(trip, response);
        }

        response.setSnt("TRIP");
        response.setImage(trip.getCategory().getImage());
        response.setTryServicePaymentAgain(!trip.getIsServiceFeePaid());
        response.setLabel(trip.getUpdatedAt().format(formatter));
        response.setAmount(MoneyUtil.formatToNaira(trip.getAmount()));
        response.setMode(String.format("%s | %s", trip.getMode().getType(), trip.getAudio() == null || trip.getAudio().isEmpty() ? "Description" : "Audio"));
        response.setCategory(trip.getCategory().getType());

        List<TripTimeline> timelines = tripTimelineRepository.findByTrip_Id(trip.getId());
        if(timelines != null && !timelines.isEmpty()) {
            response.setTimelines(timeline(timelines, response, trip.getInvited()));
        }

        if(trip.getMapView() != null) {
            response.setLocation(TripMapper.INSTANCE.view(trip.getMapView()));
        }

        if(sendUpdate) {
            if(trip.isActive()) {
                messaging.convertAndSend("/platform/%s/trip/active".formatted(userId), response);
                messaging.convertAndSend("/platform/%s/trip/active/%s".formatted(userId, response.getId()), response);

                if(business != null) {
                    messaging.convertAndSend("/platform/%s/trip/active".formatted(business), response);
                    messaging.convertAndSend("/platform/%s/trip/active/%s".formatted(business, response.getId()), response);
                }
            } else if(trip.isRequested()) {
                messaging.convertAndSend("/platform/%s/trip/requested".formatted(userId), response);
                messaging.convertAndSend("/platform/%s/trip/requested/%s".formatted(userId, response.getId()), response);

                if(business != null) {
                    messaging.convertAndSend("/platform/%s/trip/requested".formatted(business), response);
                    messaging.convertAndSend("/platform/%s/trip/requested/%s".formatted(business, response.getId()), response);
                }
            } else {
                messaging.convertAndSend("/platform/%s/trip/history".formatted(userId), response);
                messaging.convertAndSend("/platform/%s/trip/history/%s".formatted(userId, response.getId()), response);

                if(business != null) {
                    messaging.convertAndSend("/platform/%s/trip/history".formatted(business), response);
                    messaging.convertAndSend("/platform/%s/trip/history/%s".formatted(business, response.getId()), response);
                }
            }
        }

        if(requestedId != null) {
            response.setRequestedId(requestedId);
        }

        return response;
    }

    @Transactional
    protected void addInvitedInformation(Trip trip, TripResponse response) {
        if(trip.getInvited() != null) {
            TripShareResponse share = TripMapper.INSTANCE.share(trip.getInvited());
            if(trip.getInvited().getProvider() != null) {
                share.setProfile(buildUserResponse(trip.getInvited().getProvider(), trip.getId()));
            } else if(trip.getInvited().getFirstName() != null) {
                share.setProfile(buildOfflineProfile(trip.getInvited()));
            }

            if(trip.getInvited().getFirstName() != null) {
                boolean showAuth = trip.getInvited().getAuthentication() == null || !trip.getInvited().getAuthentication().getIsVerified();
                share.setShowAuth(showAuth);
            } else {
                List<TripTimeline> timelines = tripTimelineRepository.findBySharing_Id(trip.getInvited().getId());
                if(timelines != null && !timelines.isEmpty()) {
                    share.setTimelines(timeline(timelines, share, null));
                }
            }

            response.setUserShareFee(MoneyUtil.formatToNaira(trip.getUserShare()));
            response.setShared(share);
        }
    }

    private UserResponse buildOfflineProfile(TripShare trip) {
        UserResponse profile = new UserResponse();

        profile.setPhone(trip.getPhoneNumber());
        profile.setRole("Offline Provider");
        profile.setCategory(trip.getCategory());
        profile.setAvatar("");
        profile.setRating(0.0);
        profile.setId("");
        profile.setName(trip.fullName());

        return profile;
    }

    @Transactional
    protected List<TripTimelineResponse> timeline(List<TripTimeline> timelines, TripActionResponse action, TripShare share) {
        boolean containsCancelled = matchesTimelineStatus(CANCELLED, timelines);
        boolean containsShareAccessGranted = matchesTimelineStatus(SHARE_ACCESS_GRANTED, timelines);
        boolean containsShareAccessDenied = matchesTimelineStatus(SHARE_ACCESS_DENIED, timelines);
        boolean containsArrived = matchesTimelineStatus(ARRIVED, timelines);
        boolean containsAuthenticated = matchesTimelineStatus(AUTHENTICATED, timelines);
        boolean containsOnTrip = matchesTimelineStatus(ON_TRIP, timelines);
        boolean containsLeft = matchesTimelineStatus(LEFT, timelines);
        boolean containsCompleted = matchesTimelineStatus(COMPLETED, timelines);
        boolean containsRequested = matchesTimelineStatus(REQUESTED, timelines);
        boolean containsRequestedOnly = timelines.stream().allMatch(timeline -> timeline.getStatus() == REQUESTED);
        boolean showNotifyOnMyWay = statusSet(timelines).equals(Set.of(REQUESTED, CONNECTED)) || statusSet(timelines).equals(Set.of(CONNECTED));
        boolean showNotifyArrival = statusSet(timelines).equals(Set.of(REQUESTED, CONNECTED, ON_THE_WAY)) || statusSet(timelines).equals(Set.of(CONNECTED, ON_THE_WAY));

        List<TripTimelineResponse> responses = Arrays.stream(TripConnectionStatus.values())
                .filter(status -> {
                    if (status == CANCELLED && !containsCancelled) {
                        return false;
                    }
                    if (status == SHARE_ACCESS_GRANTED && !containsShareAccessGranted) {
                        return false;
                    }
                    if (status == SHARE_ACCESS_DENIED && !containsShareAccessDenied) {
                        return false;
                    }
                    if (status == LEFT && !containsLeft) {
                        return false;
                    }
                    if (status == REQUESTED && !containsRequested) {
                        return false;
                    }
                    if (containsCancelled && status != REQUESTED && status != CANCELLED) {
                        return false;
                    }
                    return !containsLeft || status != COMPLETED;
                })
                .map(status -> {
                    TripTimelineResponse response = new TripTimelineResponse();
                    response.setStatus(status);
                    response.setHeader(status.getType());
                    response.setDescription(status.getDescription());

                    TripTimeline matchingTimeline = timelines.stream()
                            .filter(timeline -> timeline.getStatus() == status)
                            .findFirst()
                            .orElse(null);

                    response.setIsOver(matchingTimeline != null);
                    response.setCreatedAt(matchingTimeline != null ? matchingTimeline.getCreatedAt() : null);
                    response.setUpdatedAt(matchingTimeline != null ? matchingTimeline.getUpdatedAt() : null);
                    response.setLabel(matchingTimeline != null
                            ? TimeUtil.formatTime(matchingTimeline.getCreatedAt(), matchingTimeline.getTrip().getProvider().getUser().getTimezone())
                            : ""
                    );

                    return response;
                })
                .sorted(Comparator.comparing(response -> response.getStatus().getValue()))
                .toList();

        action.setShowCancel(containsRequestedOnly);
        action.setShowAuth(containsArrived && !containsAuthenticated);
        action.setShowEnd(containsOnTrip && !containsLeft && !containsCompleted);
        action.setShowShare(containsOnTrip && !containsLeft && !containsCompleted && containsShareAccessGranted && !containsShareAccessDenied && share == null);
        action.setShowGrant(containsOnTrip && !containsLeft && !containsCompleted && !containsShareAccessGranted && !containsShareAccessDenied && share == null);
        action.setShowDeny(containsOnTrip && !containsLeft && !containsCompleted && containsShareAccessGranted && !containsShareAccessDenied);
        action.setShowLeave(containsOnTrip && !containsLeft);
        action.setShowNotifyOnMyWay(showNotifyOnMyWay);
        action.setShowNotifyArrival(showNotifyArrival);

        return responses;
    }

    private boolean matchesTimelineStatus(TripConnectionStatus status, List<TripTimeline> timelines) {
        return timelines.stream().anyMatch(timeline -> timeline.getStatus() == status);
    }

    private Set<TripConnectionStatus> statusSet(List<TripTimeline> timelines) {
        return timelines.stream()
                .map(TripTimeline::getStatus)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public ApiResponse<List<TripResponse>> history(String id, String linkId, Integer page, Integer size, boolean sendUpdate, String tripId) {
        return new ApiResponse<>(history(id, linkId, sendUpdate, tripId, page, size, false, false, false));
    }

    @Override
    @Transactional
    public ApiResponse<List<TripResponse>> active(String id, String linkId, Integer page, Integer size, boolean sendUpdate, String tripId) {
        return new ApiResponse<>(history(id, linkId, sendUpdate, tripId, page, size, true, false, false));
    }

    @Override
    @Transactional
    public ApiResponse<List<TripResponse>> requested(String id, String linkId, Integer page, Integer size, boolean sendUpdate, String tripId) {
        return new ApiResponse<>(history(id, linkId, sendUpdate, tripId, page, size, false, true, false));
    }

    @Override
    @Transactional
    public ApiResponse<List<TripResponse>> shared(String id, String linkId, Integer page, Integer size, boolean sendUpdate, String tripId) {
        return new ApiResponse<>(history(id, linkId, sendUpdate, tripId, page, size, false, false, true));
    }

    @Override
    @Transactional
    public ApiResponse<List<TripResponse>> history(String id, String linkId, Integer page, Integer size, Boolean isShared, String category, ZonedDateTime dateTime) {
        List<TripResponse> list;
        boolean isGuestAccount = id != null && !id.isEmpty() && linkId != null && !linkId.isEmpty();

        if(dateTime == null) {
            if(isGuestAccount) {
                list = buildTrips(tripRepository.findByGuestHistoryTrips(id, linkId, category, TimeUtil.now(), isShared, HelperUtil.getPageable(page, size)), id, false);
            } else if(userUtil.getUser().isUser()) {
                list = buildTrips(tripRepository.findByUserHistoryTrips(String.valueOf(userUtil.getUser().getId()), category, TimeUtil.now(), isShared, HelperUtil.getPageable(page, size)), String.valueOf(userUtil.getUser().getId()), false);
            } else {
                list = buildTrips(tripRepository.findByProviderHistoryTrips(userUtil.getUser().getId(), category, TimeUtil.now(), isShared, HelperUtil.getPageable(page, size)), String.valueOf(userUtil.getUser().getId()), false);
            }
        } else {
            if(isGuestAccount) {
                list = buildTrips(tripRepository.findByGuestHistoryTrips(id, linkId, category, dateTime, isShared, HelperUtil.getPageable(page, size)), id, false);
            } else if(userUtil.getUser().isUser()) {
                list = buildTrips(tripRepository.findByUserHistoryTrips(String.valueOf(userUtil.getUser().getId()), category, dateTime, isShared, HelperUtil.getPageable(page, size)), String.valueOf(userUtil.getUser().getId()), false);
            } else {
                list = buildTrips(tripRepository.findByProviderHistoryTrips(userUtil.getUser().getId(), category, dateTime, isShared, HelperUtil.getPageable(page, size)), String.valueOf(userUtil.getUser().getId()), false);
            }
        }

        return new ApiResponse<>(list);
    }

    @Transactional
    protected List<TripResponse> history(String id, String linkId, boolean sendUpdate, String tripId, Integer page, Integer size, boolean isActive, boolean isRequested, boolean isShared) {
        if(isRequested) {
            List<TripResponse> list = new ArrayList<>();

            if (id != null && !id.isEmpty()) {
                try {
                    User user = userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new TripException("User not found"));

                    if (user.isUser()) {
                        buildUserList(list, user, page, size, sendUpdate);
                    } else {
                        buildProviderWaitingList(list, user, page, size, sendUpdate);
                    }
                } catch (Exception ignored) {
                    buildGuestInviteHistory(id, linkId, page, size, list, sendUpdate);
                }
            } else if (userUtil.getUser().isUser()) {
                buildUserList(list, userUtil.getUser(), page, size, sendUpdate);
            } else {
                buildProviderWaitingList(list, userUtil.getUser(), page, size, sendUpdate);
            }

            return list.stream()
                    .collect(Collectors.toMap(TripResponse::getId, response -> response, (existing, replacement) -> existing))
                    .values()
                    .stream()
                    .sorted(Comparator.comparing(TripResponse::getUpdatedAt).reversed())
                    .collect(Collectors.toList());
        } else {
            if(tripId != null && !tripId.isEmpty()) {
                Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripException("Trip not found"));
                buildTripResponse(trip.getAccount(), null, sendUpdate, null, trip);
                buildTripResponse(trip.getProvider().getId().toString(), null, sendUpdate, null, trip);

                if(trip.getInvited() != null && trip.getInvited().getProvider() != null) {
                    buildTripResponse(trip.getInvited().getProvider().getId().toString(), null, sendUpdate, null, trip);
                }
            }

            return getHistory(id, linkId, sendUpdate, page, size, isActive, isShared);
        }
    }

    @Transactional
    protected void buildGuestInviteHistory(String guestId, String linkId, Integer page, Integer size, List<TripResponse> invites, boolean sendUpdate) {
        Guest guest = guestRepository.findById(guestId).orElseThrow(() -> new TripException("Guest not found"));

        addTripToHistory(tripRepository.findByRequestedGuestTrips(guest.getId(), linkId, HelperUtil.getPageable(page, size)), invites, guestId, sendUpdate);
        addInviteToHistory(invites, guest.getId(), tripInviteRepository.findByAccountAndLinkId(guest.getId(), linkId, HelperUtil.getPageable(page, size)));
    }

    @Transactional
    protected void buildUserList(List<TripResponse> invites, User user, Integer page, Integer size, boolean sendUpdate) {
        addInviteToHistory(invites, String.valueOf(user.getId()), tripInviteRepository.findByAccount(String.valueOf(user.getId()), HelperUtil.getPageable(page, size)));
        addTripToHistory(tripRepository.findByRequestedUserTrips(String.valueOf(user.getId()), HelperUtil.getPageable(page, size)), invites, String.valueOf(user.getId()), sendUpdate);
    }

    @Transactional
    protected void buildProviderWaitingList(List<TripResponse> invites, User user, Integer page, Integer size, boolean sendUpdate) {
        SerchCategory category = profileRepository.findById(user.getId())
                .map(Profile::getCategory)
                .orElse(businessProfileRepository.findById(user.getId())
                        .map(BusinessProfile::getCategory)
                        .orElse(SerchCategory.USER));

        addInviteToHistory(invites, String.valueOf(user.getId()), tripInviteRepository.sortWithinDistanceWithNoneSelected(Double.parseDouble(MAP_SEARCH_RADIUS), category.name(), HelperUtil.getPageable(page, size)));
        addInviteToHistory(invites, String.valueOf(user.getId()), tripInviteRepository.findBySelected(user.getId(), HelperUtil.getPageable(page, size)));
        addTripToHistory(tripRepository.findByRequestedProviderTrips(user.getId(), HelperUtil.getPageable(page, size)), invites, String.valueOf(user.getId()), sendUpdate);
    }

    @Transactional
    protected void addInviteToHistory(List<TripResponse> invites, String userId, Page<TripInvite> list) {
        if(list != null && list.hasContent()) {
            invites.addAll(list.getContent()
                    .stream()
                    .sorted(Comparator.comparing(TripInvite::getUpdatedAt).reversed())
                    .map(invite -> buildInviteResponse(userId, invite))
                    .toList());
        }
    }

    @Transactional
    protected void addTripToHistory(Page<Trip> trips, List<TripResponse> invites, String userId, boolean sendUpdate) {
        if (trips != null && trips.hasContent()) {
            invites.addAll(trips.getContent()
                    .stream()
                    .sorted(Comparator.comparing(Trip::getUpdatedAt).reversed())
                    .map(trip -> buildTripResponse(userId, null, sendUpdate, null, trip))
                    .toList());
        }
    }

    @Transactional
    protected List<TripResponse> getHistory(String id, String linkId, boolean sendUpdate, Integer page, Integer size, boolean isActive, boolean isShared) {
        boolean isGuestAccount = id != null && !id.isEmpty() && linkId != null && !linkId.isEmpty();

        if(isActive) {
            if(isGuestAccount) {
                return buildTrips(tripRepository.findByActiveGuestTrips(id, linkId, HelperUtil.getPageable(page, size)), id, sendUpdate);
            } else if(userUtil.getUser().isUser()) {
                return buildTrips(tripRepository.findByActiveUserTrips(String.valueOf(userUtil.getUser().getId()), HelperUtil.getPageable(page, size)), String.valueOf(userUtil.getUser().getId()), sendUpdate);
            } else {
                return buildTrips(tripRepository.findByActiveProviderTrips(userUtil.getUser().getId(), HelperUtil.getPageable(page, size)), String.valueOf(userUtil.getUser().getId()), sendUpdate);
            }
        } else if(isShared) {
            if(isGuestAccount) {
                return buildTrips(tripRepository.findBySharedGuestTrips(id, linkId, HelperUtil.getPageable(page, size)), id, sendUpdate);
            } else if(userUtil.getUser().isUser()) {
                return buildTrips(tripRepository.findBySharedUserTrips(String.valueOf(userUtil.getUser().getId()), HelperUtil.getPageable(page, size)), String.valueOf(userUtil.getUser().getId()), sendUpdate);
            } else {
                return buildTrips(tripRepository.findBySharedProviderTrips(userUtil.getUser().getId(), HelperUtil.getPageable(page, size)), String.valueOf(userUtil.getUser().getId()), sendUpdate);
            }
        } else {
            if(isGuestAccount) {
                return buildTrips(tripRepository.findByGuestHistoryTrips(id, linkId, null, null, null, HelperUtil.getPageable(page, size)), id, sendUpdate);
            } else if(userUtil.getUser().isUser()) {
                return buildTrips(tripRepository.findByUserHistoryTrips(String.valueOf(userUtil.getUser().getId()), null, null, null, HelperUtil.getPageable(page, size)), String.valueOf(userUtil.getUser().getId()), sendUpdate);
            } else {
                return buildTrips(tripRepository.findByProviderHistoryTrips(userUtil.getUser().getId(), null, null, null, HelperUtil.getPageable(page, size)), String.valueOf(userUtil.getUser().getId()), sendUpdate);
            }
        }
    }

    @Transactional
    protected List<TripResponse> buildTrips(Page<Trip> trips, String user, boolean update) {
        if(trips != null && trips.hasContent()) {
            return trips.getContent().stream()
                    .sorted(Comparator.comparing(Trip::getUpdatedAt).reversed())
                    .map(trip -> buildTripResponse(user, null, update, null, trip))
                    .toList();
        } else {
            return List.of();
        }
    }
}
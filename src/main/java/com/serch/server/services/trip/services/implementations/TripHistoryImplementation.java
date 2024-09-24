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
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(propagation = Propagation.NESTED)
    public TripResponse response(String id, String userId) {
        TripInvite invite = tripInviteRepository.findById(id).orElseThrow(() -> new TripException("Invite not found"));
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
        return invite.getQuotes().stream()
                .filter(q -> q.getAccount() != null && (q.getProvider().isSameAs(user.getId()) || q.getProvider().belongsToBusiness(user.getId())))
                .sorted(Comparator.comparing(TripInviteQuotation::getUpdatedAt).reversed())
                .map(quote -> {
                    double longitude = quote.getProvider().getActive() != null
                            ? quote.getProvider().getActive().getLongitude() : 0.0;
                    double latitude = quote.getProvider().getActive() != null
                            ? quote.getProvider().getActive().getLatitude() : 0.0;

                    QuotationResponse quotation = TripMapper.INSTANCE.response(quote);
                    quotation.setAmount(MoneyUtil.formatToNaira(quote.getAmount()));
                    quotation.setName(userResponse.getName());
                    quotation.setAvatar(userResponse.getAvatar());
                    quotation.setRating(userResponse.getRating());
                    quotation.setDistance(String.format("%s km", HelperUtil.getDistance(invite.getLatitude(), invite.getLongitude(), latitude, longitude)));
                    return quotation;
                })
                .toList();
    }

    private List<QuotationResponse> buildQuotationList(TripInvite invite) {
        return invite.getQuotes().stream()
                .filter(q -> q.getAccount() == null)
                .sorted(Comparator.comparing(TripInviteQuotation::getUpdatedAt).reversed())
                .map(quote -> {
                    double latitude = quote.getProvider().getActive() != null
                            ? quote.getProvider().getActive().getLatitude() : 0.0;
                    double longitude = quote.getProvider().getActive() != null
                            ? quote.getProvider().getActive().getLongitude() : 0.0;

                    QuotationResponse quotation = TripMapper.INSTANCE.response(quote);
                    quotation.setAmount(MoneyUtil.formatToNaira(quote.getAmount()));
                    quotation.setName(quote.getProvider().getFullName());
                    quotation.setAvatar(quote.getProvider().getAvatar());
                    quotation.setRating(quote.getProvider().getRating());
                    quotation.setDistance(String.format("%s km", HelperUtil.getDistance(invite.getLatitude(), invite.getLongitude(), latitude, longitude)));
                    return quotation;
                })
                .toList();
    }

    @Transactional(propagation = Propagation.NESTED)
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

    @Transactional(propagation = Propagation.NESTED)
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
    @Transactional(propagation = Propagation.NESTED)
    public TripResponse response(String id, String userId, @Nullable InitializePaymentData payment, boolean sendUpdate, String requestedId) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new TripException("Trip not found"));

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
            messaging.convertAndSend("/platform/%s".formatted(userId), response);
            messaging.convertAndSend("/platform/%s/%s".formatted(response.getId(), userId), response);

            if(business != null) {
                messaging.convertAndSend("/platform/%s".formatted(business), response);
                messaging.convertAndSend("/platform/%s/%s".formatted(response.getId(), business), response);
            }
        }

        if(requestedId != null) {
            response.setRequestedId(requestedId);
        }

        return response;
    }

    @Transactional(propagation = Propagation.NESTED)
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

    @Transactional(propagation = Propagation.NESTED)
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
    @Transactional(propagation = Propagation.NESTED)
    public List<TripResponse> inviteHistory(String guestId, UUID userId, String linkId) {
        List<TripResponse> invites = new ArrayList<>();

        if (guestId != null && !guestId.isEmpty()) {
            Guest guest = guestRepository.findById(guestId)
                    .orElseThrow(() -> new TripException("Guest not found"));

            invites.addAll(tripInviteRepository.findByAccountAndLinkId(guest.getId(), linkId)
                    .stream()
                    .sorted(Comparator.comparing(TripInvite::getUpdatedAt).reversed())
                    .map(invite -> response(invite.getId(), guest.getId()))
                    .toList());

            invites.addAll(tripRepository.findByAccount(guest.getId(), linkId)
                    .stream()
                    .filter(trip -> trip.getStatus() == WAITING)
                    .sorted(Comparator.comparing(Trip::getUpdatedAt).reversed())
                    .map(trip -> response(trip.getId(), guestId, null, false, null))
                    .toList());

        } else if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new TripException("User not found"));

            if (user.isUser()) {
                buildUserList(invites, user);
            } else {
                SerchCategory category = profileRepository.findById(userId)
                        .map(Profile::getCategory)
                        .orElse(businessProfileRepository.findById(userId)
                                .map(BusinessProfile::getCategory)
                                .orElse(SerchCategory.USER));

                buildProviderWaitingList(category, invites, user);
            }
        } else {
            User currentUser = userUtil.getUser();

            if (currentUser.isUser()) {
                buildUserList(invites, currentUser);
            } else {
                SerchCategory category = profileRepository.findById(currentUser.getId())
                        .map(Profile::getCategory)
                        .orElse(businessProfileRepository.findById(currentUser.getId())
                                .map(BusinessProfile::getCategory)
                                .orElse(SerchCategory.USER));

                buildProviderWaitingList(category, invites, currentUser);
            }
        }

        return invites.stream()
                .collect(Collectors.toMap(TripResponse::getId, response -> response, (existing, replacement) -> existing))
                .values()
                .stream()
                .sorted(Comparator.comparing(TripResponse::getUpdatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.NESTED)
    protected void buildUserList(List<TripResponse> invites, User user) {
        invites.addAll(tripInviteRepository.findByAccount(String.valueOf(user.getId()))
                .stream()
                .sorted(Comparator.comparing(TripInvite::getUpdatedAt).reversed())
                .map(invite -> response(invite.getId(), String.valueOf(user.getId())))
                .toList());

        invites.addAll(tripRepository.findByAccount(String.valueOf(user.getId()))
                .stream()
                .filter(trip -> trip.getStatus() == WAITING)
                .sorted(Comparator.comparing(Trip::getUpdatedAt).reversed())
                .map(trip -> response(trip.getId(), String.valueOf(user.getId()), null, false, null))
                .toList());
    }

    @Transactional(propagation = Propagation.NESTED)
    protected void buildProviderWaitingList(SerchCategory category, List<TripResponse> invites, User user) {
        invites.addAll(tripInviteRepository.sortAllWithinDistance(Double.parseDouble(MAP_SEARCH_RADIUS), category.name())
                .stream()
                .filter(invite -> invite.getSelected() == null)
                .sorted(Comparator.comparing(TripInvite::getUpdatedAt).reversed())
                .map(invite -> response(invite.getId(), String.valueOf(user.getId())))
                .toList());
        invites.addAll(tripInviteRepository.findBySelected(userUtil.getUser().getId())
                .stream()
                .sorted(Comparator.comparing(TripInvite::getUpdatedAt).reversed())
                .map(invite -> response(invite.getId(), String.valueOf(user.getId())))
                .toList());

        invites.addAll(tripRepository.findByProviderId(userUtil.getUser().getId())
                .stream()
                .filter(trip -> trip.getStatus() == WAITING)
                .sorted(Comparator.comparing(Trip::getUpdatedAt).reversed())
                .map(trip -> response(trip.getId(), String.valueOf(user.getId()), null, false, null))
                .toList());
    }


    @Override
    @Transactional(propagation = Propagation.NESTED)
    public ApiResponse<List<TripResponse>> history(String guest, String linkId, boolean sendUpdate, String tripId) {
        List<TripResponse> list;

        if(guest != null && !guest.isEmpty() && linkId != null && !linkId.isEmpty()) {
            list = tripRepository.findByAccount(guest, linkId)
                    .stream()
                    .sorted(Comparator.comparing(Trip::getUpdatedAt).reversed())
                    .map(trip -> response(trip.getId(), guest, null, sendUpdate, null))
                    .toList();
        } else if(userUtil.getUser().isUser()) {
            list = tripRepository.findByAccount(String.valueOf(userUtil.getUser().getId()))
                    .stream()
                    .sorted(Comparator.comparing(Trip::getUpdatedAt).reversed())
                    .map(trip -> response(trip.getId(), String.valueOf(userUtil.getUser().getId()), null, sendUpdate, null))
                    .toList();
        } else {
            list = tripRepository.findByProviderId(userUtil.getUser().getId())
                    .stream()
                    .sorted(Comparator.comparing(Trip::getUpdatedAt).reversed())
                    .map(trip -> response(trip.getId(), String.valueOf(userUtil.getUser().getId()), null, sendUpdate, null))
                    .toList();
        }

        if(tripId != null && !tripId.isEmpty()) {
            Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new TripException("Trip not found"));
            response(tripId, trip.getAccount(), null, sendUpdate, null);
            response(tripId, trip.getProvider().getId().toString(), null, sendUpdate, null);

            if(trip.getInvited() != null && trip.getInvited().getProvider() != null) {
                response(tripId, trip.getInvited().getProvider().getId().toString(), null, sendUpdate, null);
            }
        }
        return new ApiResponse<>(list);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public ApiResponse<List<TripResponse>> history(String guest, String linkId) {
        return history(guest, linkId, false, null);
    }
}
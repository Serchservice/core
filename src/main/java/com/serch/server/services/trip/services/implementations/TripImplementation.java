package com.serch.server.services.trip.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.transaction.TransactionType;
import com.serch.server.exceptions.others.TripException;
import com.serch.server.mappers.TripMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.transaction.Transaction;
import com.serch.server.models.transaction.Wallet;
import com.serch.server.models.trip.Trip;
import com.serch.server.models.trip.TripAuthentication;
import com.serch.server.models.trip.TripTime;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.transaction.TransactionRepository;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.repositories.trip.TripAuthenticationRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.repositories.trip.TripTimeRepository;
import com.serch.server.services.auth.services.TokenService;
import com.serch.server.services.transaction.requests.BalanceUpdateRequest;
import com.serch.server.services.trip.requests.*;
import com.serch.server.services.trip.responses.TripHistoryResponse;
import com.serch.server.services.trip.services.ActiveService;
import com.serch.server.services.trip.services.TripService;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import com.serch.server.utils.WalletUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.serch.server.enums.account.SerchCategory.GUEST;
import static com.serch.server.enums.auth.Role.*;
import static com.serch.server.enums.transaction.TransactionStatus.SUCCESSFUL;
import static com.serch.server.enums.trip.TripConnectionStatus.*;
import static com.serch.server.enums.trip.TripStatus.*;

/**
 * This is the implementation of the wrapper class {@link TripService}.
 * It contains the logic and implementation of all the methods in its service layer.
 *
 * @see TokenService
 * @see ActiveService
 * @see UserUtil
 * @see PasswordEncoder
 * @see ProfileRepository
 * @see TripRepository
 * @see TripTimeRepository
 * @see TripAuthenticationRepository
 * @see GuestRepository
 * @see WalletRepository
 * @see TransactionRepository
 */
@Service
@RequiredArgsConstructor
public class TripImplementation implements TripService {
    private final TokenService tokenService;
    private final ActiveService activeService;
    private final UserUtil userUtil;
    private final WalletUtil walletUtil;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;
    private final TripRepository tripRepository;
    private final TripTimeRepository tripTimeRepository;
    private final TripAuthenticationRepository tripAuthenticationRepository;
    private final GuestRepository guestRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public ApiResponse<String> request(TripRequest request) {
        Profile profile = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new TripException("Profile not found"));
        Profile provider = profileRepository.findById(request.getProvider())
                .orElseThrow(() -> new TripException("Provider not found"));

        if(profile.getUser().getRole() != USER) {
            throw new TripException("Access denied. Cannot request trip");
        } else {
            if(tripRepository.existsByStatusAndAccount(ON_TRIP, ACCEPTED, String.valueOf(profile.getId()))) {
                throw new TripException("You have an active trip");
            } else if(tripRepository.existsByStatusAndProvider(ON_TRIP, ACCEPTED, provider.getId())) {
                throw new TripException("%s is currently on a trip".formatted(provider.getFullName()));
            } else {
                Trip trip = new Trip();
                trip.setAccount(String.valueOf(profile.getId()));
                trip.setProvider(provider);
                trip.setAddress(TripMapper.INSTANCE.address(request.getAddress()));
                tripRepository.save(trip);
                return new ApiResponse<>("Success", HttpStatus.CREATED);
            }
        }
    }

    @Override
    public ApiResponse<String> accept(TripAcceptRequest request) {
        Profile profile = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new TripException("Profile not found"));
        Trip trip = tripRepository.findByIdAndProviderId(request.getTrip(), profile.getId())
                .orElseThrow(() -> new TripException("Trip not found"));

        if(profile.getUser().getRole() == ASSOCIATE_PROVIDER || profile.getUser().getRole() == PROVIDER) {
            if(tripRepository.existsByStatusAndProvider(ON_TRIP, ACCEPTED, profile.getId())) {
                closeTripRequest(trip, profile);
                throw new TripException("You have an active trip");
            } else if(tripRepository.existsByStatusAndAccount(ON_TRIP, ACCEPTED, trip.getAccount())) {
                closeTripRequest(trip, profile);
                throw new TripException("User/Guest has an active trip at the moment");
            } else {
                try {
                    Optional<Profile> user = profileRepository.findById(UUID.fromString(trip.getAccount()));
                    if(user.isPresent()) {
                        return acceptTrip(request, trip, profile);
                    }
                } catch (Exception e) {
                    Optional<Guest> guest = guestRepository.findById(trip.getAccount());
                    if(guest.isPresent()) {
                        if(trip.getShared() != null) {
//                            if(trip.getTransaction() != null && trip.getTransaction().getStatus() == SUCCESSFUL) {
//                                return acceptTrip(request, trip, profile);
//                            } else {
//                                return payAndAcceptTrip(request, trip, profile);
//                            }
                        } else {
                            throw new TripException("Cannot accept trip till you pay the required commission");
                        }
                    }
                    return payAndAcceptTrip(request, trip, profile);
                }
            }
        } else {
            throw new TripException("Access denied. Cannot accept trips");
        }
        throw new TripException("Couldn't find the user or guest that made trip request");
    }

    private ApiResponse<String> payAndAcceptTrip(TripAcceptRequest request, Trip trip, Profile profile) {
        BalanceUpdateRequest update = BalanceUpdateRequest.builder()
                .type(TransactionType.TRIP_WITHDRAW)
                .amount(trip.getShared().getAmount().subtract(trip.getShared().getProvider()))
                .user(profile.getId())
                .build();

        UUID userId = trip.getShared().getSharedLogin().getSharedLink().getUser().getId();
        Wallet sender = walletRepository.findByUser_Id(profile.getId())
                .orElseThrow(() -> new TripException("Wallet not found"));
        String account = walletRepository.findByUser_Id(userId)
                .map(Wallet::getId)
                .orElseThrow(() -> new TripException("Wallet not found"));

        if(walletUtil.isBalanceSufficient(update)) {
            walletUtil.updateBalance(update);

            update.setUser(userId);
            update.setType(TransactionType.TRIP);
            update.setAmount(trip.getShared().getUser());
            walletUtil.updateBalance(update);

            trip.getShared().setUpdatedAt(LocalDateTime.now());
//            sharedPricingRepository.save(trip.getShared());

            Transaction transaction = new Transaction();
            transaction.setReference("S-SHARED-TRIP-%s".formatted(UUID.randomUUID().toString().substring(0, 8)));
//            transaction.setTrip(trip);
            transaction.setVerified(true);
            transaction.setStatus(SUCCESSFUL);
//            transaction.setSender(sender);
            transaction.setAccount(account);
            transaction.setType(TransactionType.TRIP);
            transactionRepository.save(transaction);
            return acceptTrip(request, trip, profile);
        } else {
            throw new TripException("Insufficient balance to pay for trip");
        }
    }

    private void closeTripRequest(Trip trip, Profile profile) {
        if(trip.getInvitedProvider().isSameAs(profile.getId())) {
            trip.setInviteStatus(CLOSED);
            /// TODO:: Send notification to account (user/guest and provider)
        } else {
            trip.setStatus(CLOSED);
            trip.setCancelReason("%s had an active trip when this trip request was received".formatted(profile.getFullName()));
            /// TODO:: Send notification to account (user/guest)
        }
        trip.setUpdatedAt(LocalDateTime.now());
        tripRepository.save(trip);
    }

    private ApiResponse<String> acceptTrip(TripAcceptRequest request, Trip trip, Profile profile) {
        String otp = tokenService.generateCode(8);
        if(trip.getInvitedProvider().isSameAs(profile.getId())) {
            acceptInvitedTrip(trip, otp, profile.getUser(), request.getAddress());
        } else {
            acceptTrip(trip, otp, profile.getUser(), request.getAddress());
        }
        trip.setUpdatedAt(LocalDateTime.now());
        tripRepository.save(trip);
        return new ApiResponse<>("Trip accepted", otp, HttpStatus.OK);
    }

    private void acceptInvitedTrip(Trip trip, String otp, User user, OnlineRequest request) {
        trip.setInviteStatus(ACCEPTED);

        trip.getTime().setInviteAcceptedAt(LocalDateTime.now());
        tripTimeRepository.save(trip.getTime());

        trip.getAuthentication().setCode(passwordEncoder.encode(otp));
        tripAuthenticationRepository.save(trip.getAuthentication());

        activeService.toggle(user, REQUESTSHARING, request);
        /// TODO:: Send notification to account (user/guest and provider)
    }

    private void acceptTrip(Trip trip, String otp, User user, OnlineRequest request) {
        trip.setStatus(ACCEPTED);

        TripTime time = new TripTime();
        time.setAcceptedAt(LocalDateTime.now());
        time.setTrip(trip);
        tripTimeRepository.save(time);

        TripAuthentication auth = new TripAuthentication();
        auth.setCode(passwordEncoder.encode(otp));
        auth.setTrip(trip);
        tripAuthenticationRepository.save(auth);

        activeService.toggle(user, BUSY, request);
        /// TODO:: Send notification to account (user/guest)
    }

    @Override
    public ApiResponse<String> decline(TripDeclineRequest request) {
        Profile profile = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new TripException("Profile not found"));
        Trip trip = tripRepository.findByIdAndProviderId(request.getTrip(), profile.getId())
                .orElseThrow(() -> new TripException("Trip not found"));

        if(profile.getUser().getRole() == ASSOCIATE_PROVIDER || profile.getUser().getRole() == PROVIDER) {
            if(trip.getInvitedProvider().isSameAs(profile.getId())) {
                trip.setInviteStatus(DECLINED);
                trip.setInviteCancelReason(request.getReason());

                /// TODO:: Send notification to account (user/guest and provider)
            } else {
                trip.setStatus(DECLINED);
                trip.setCancelReason(request.getReason());

                /// TODO:: Send notification to account (user/guest)
            }
            trip.setUpdatedAt(LocalDateTime.now());
            tripRepository.save(trip);
            return new ApiResponse<>("Trip declined", HttpStatus.OK);
        } else {
            throw new TripException("Access denied. Cannot decline trips");
        }
    }

    @Override
    public ApiResponse<String> cancel(TripCancelRequest request) {
        Profile profile = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new TripException("Profile not found"));
        Trip trip = tripRepository.findByIdAndAccount(request.getTrip(), String.valueOf(profile.getId()))
                .orElseThrow(() -> new TripException("Trip not found"));

        return getCancelResponse(trip, request.getReason());
    }

    @Override
    public ApiResponse<String> getCancelResponse(Trip trip, String reason) {
        if(trip.getStatus() == ON_TRIP || trip.getStatus() == ACCEPTED) {
            throw new TripException("Cannot cancel an active trip");
        } else {
            trip.setStatus(CANCELLED);
            trip.setCancelReason(reason);
            trip.setUpdatedAt(LocalDateTime.now());
            tripRepository.save(trip);
            return new ApiResponse<>("Trip cancelled", HttpStatus.OK);
        }
    }

    @Override
    public ApiResponse<String> permitSharing(String tripId) {
        Profile profile = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new TripException("Profile not found"));
        Trip trip = tripRepository.findByIdAndAccount(tripId, String.valueOf(profile.getId()))
                .orElseThrow(() -> new TripException("Trip not found"));

        if(trip.getCanShare()) {
            return new ApiResponse<>("Permission already granted", HttpStatus.OK);
        } else if(trip.getStatus() != ACCEPTED && trip.getStatus() != ON_TRIP) {
            throw new TripException("Cannot grant sharing permission till trip is accepted");
        } else {
            trip.setCanShare(true);
            trip.setUpdatedAt(LocalDateTime.now());
            tripRepository.save(trip);
            return new ApiResponse<>("Sharing permission granted", HttpStatus.OK);
        }
    }

    @Override
    public ApiResponse<String> invite(TripInviteRequest request) {
        Profile profile = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new TripException("Profile not found"));
        Profile provider = profileRepository.findById(request.getProvider())
                .orElseThrow(() -> new TripException("Provider not found"));
        Trip trip = tripRepository.findByIdAndProviderId(request.getTrip(), profile.getId())
                .orElseThrow(() -> new TripException("Trip not found"));

        if(!trip.getCanShare()) {
            throw new TripException("Cannot invite provider without user's permission");
        } else if(trip.getInviteStatus() == ACCEPTED || trip.getInviteStatus() == ON_TRIP) {
            throw new TripException("Cannot invite another provider when another on is active");
        } else {
            trip.setInvitedProvider(provider);
            trip.setUpdatedAt(LocalDateTime.now());
            tripRepository.save(trip);

            trip.getTime().setInvitedAt(LocalDateTime.now());
            tripTimeRepository.save(trip.getTime());

            /// TODO:: Send notification to account (user/guest and invited provider)
            return new ApiResponse<>(
                    "%s has been invited".formatted(provider.getFullName()),
                    HttpStatus.OK
            );
        }
    }

    @Override
    public ApiResponse<String> cancelInvite(TripCancelRequest request) {
        Profile profile = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new TripException("Profile not found"));
        Trip trip = tripRepository.findByIdAndAccount(request.getTrip(), String.valueOf(profile.getId()))
                .orElseThrow(() -> new TripException("Trip not found"));

        if(trip.getInviteStatus() == ON_TRIP || trip.getInviteStatus() == ACCEPTED) {
            throw new TripException("Cannot cancel an active invited trip");
        } else {
            trip.setInviteStatus(CANCELLED);
            trip.setInviteCancelReason(request.getReason());
            trip.setUpdatedAt(LocalDateTime.now());
            tripRepository.save(trip);
            return new ApiResponse<>("Invited Trip cancelled", HttpStatus.OK);
        }
    }

    @Override
    public ApiResponse<String> announceArrival(String code, String tripId) {
        Profile profile = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new TripException("Profile not found"));
        Trip trip = tripRepository.findByIdAndProviderId(tripId, profile.getId())
                .orElseThrow(() -> new TripException("Trip not found"));

        if(trip.getInvitedProvider().isSameAs(profile.getId())) {
            if(trip.getInviteStatus() != ACCEPTED) {
                throw new TripException("Cannot start unaccepted trip invite");
            }

            if(passwordEncoder.matches(code, trip.getAuthentication().getCode())) {
                trip.setInviteStatus(ON_TRIP);
                trip.setUpdatedAt(LocalDateTime.now());
                tripRepository.save(trip);

                trip.getTime().setInviteVerifiedAt(LocalDateTime.now());
                tripTimeRepository.save(trip.getTime());
                return new ApiResponse<>("Trip is now active", HttpStatus.OK);
            }
        } else if(trip.getProvider().isSameAs(profile.getId())) {
            if(trip.getStatus() != ACCEPTED) {
                throw new TripException("Cannot start unaccepted trip");
            }

            if(passwordEncoder.matches(code, trip.getAuthentication().getCode())) {
                trip.setStatus(ON_TRIP);
                trip.setUpdatedAt(LocalDateTime.now());
                tripRepository.save(trip);

                trip.getTime().setVerifiedAt(LocalDateTime.now());
                tripTimeRepository.save(trip.getTime());
                return new ApiResponse<>("Trip is now active", HttpStatus.OK);
            }
        } else {
            throw new TripException("Access denied. Cannot perform action");
        }

        throw new TripException("Code does not match with trip authentication code");
    }

    @Override
    public ApiResponse<String> leave(String tripId) {
        Profile profile = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new TripException("Profile not found"));
        Trip trip = tripRepository.findByIdAndProviderId(tripId, profile.getId())
                .orElseThrow(() -> new TripException("Trip not found"));

        if(trip.getInvitedProvider().isSameAs(profile.getId())) {
            if(trip.getStatus() == ON_TRIP) {
                trip.setInviteStatus(LEFT);
                trip.setUpdatedAt(LocalDateTime.now());
                tripRepository.save(trip);

                trip.getTime().setLeftAt(LocalDateTime.now());
                tripTimeRepository.save(trip.getTime());
                return new ApiResponse<>("You've left trip %s".formatted(trip.getId()), HttpStatus.OK);
            }
        } else if(trip.getProvider().isSameAs(profile.getId())) {
            if(trip.getInviteStatus() == ON_TRIP) {
                trip.setStatus(LEFT);
                trip.setUpdatedAt(LocalDateTime.now());
                tripRepository.save(trip);

                trip.getTime().setLeftAt(LocalDateTime.now());
                tripTimeRepository.save(trip.getTime());
                return new ApiResponse<>("You've left trip %s".formatted(trip.getId()), HttpStatus.OK);
            }
        } else {
            throw new TripException("Access denied. Cannot perform action");
        }

        throw new TripException("You can't leave this trip when you are the only provider here");
    }

    @Override
    public ApiResponse<String> end(String tripId, Integer amount) {
        Profile profile = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new TripException("Profile not found"));
        Trip trip = tripRepository.findByIdAndAccount(tripId, String.valueOf(profile.getId()))
                .orElse(
                        tripRepository.findByIdAndProviderId(tripId, profile.getId())
                                .orElseThrow(() -> new TripException("Trip not found"))
                );

        updateTripWhenEnded(amount, trip);
        return new ApiResponse<>("Trip ended", trip.getId(), HttpStatus.OK);
    }

    @Override
    public void updateTripWhenEnded(Integer amount, Trip trip) {
        if(trip.getStatus() != LEFT) {
            trip.setStatus(COMPLETED);
        }
        if(trip.getInviteStatus() != LEFT && trip.getInviteStatus() != PENDING) {
            trip.setInviteStatus(COMPLETED);
        }
        trip.setAmount(amount);
        trip.setUpdatedAt(LocalDateTime.now());
        tripRepository.save(trip);

        trip.getTime().setStoppedAt(LocalDateTime.now());
        tripTimeRepository.save(trip.getTime());

        OnlineRequest request = TripMapper.INSTANCE.request(trip.getAddress());
        if(trip.getInvitedProvider() != null) {
            activeService.toggle(trip.getInvitedProvider().getUser(), ONLINE, request);
        }
        activeService.toggle(trip.getProvider().getUser(), ONLINE, request);

        /// TODO::: Send notification to all involved
    }

    @Override
    public ApiResponse<List<TripHistoryResponse>> history() {
        Profile profile = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new TripException("Profile not found"));
        List<TripHistoryResponse> trips = tripRepository.findByAccount(String.valueOf(profile.getId()), profile.getId())
                .stream()
                .map(trip -> {
                    TripHistoryResponse response = new TripHistoryResponse();
                    String name = "";
                    String avatar = "";
                    SerchCategory category = SerchCategory.USER;

                    try {
                        Optional<Profile> user = profileRepository.findById(UUID.fromString(trip.getAccount()));
                        if(user.isPresent()) {
                            name = user.get().getFullName();
                            avatar = user.get().getAvatar();
                            category = user.get().getCategory();
                        }
                    } catch (Exception e) {
                        Optional<Guest> guest = guestRepository.findById(trip.getAccount());
                        if(guest.isPresent()) {
                            name = guest.get().getFullName();
                            avatar = guest.get().getAvatar();
                            category = GUEST;
                        }
                    }

                    response.setId(trip.getId());
                    response.setCategory(category);
                    response.setName(name);
                    response.setAvatar(avatar);
                    response.setAmount(MoneyUtil.formatToNaira(BigDecimal.valueOf(trip.getAmount())));

                    response.setStoppedAt(TimeUtil.formatDay(trip.getTime().getStoppedAt()));
                    response.setRequestedAt(TimeUtil.formatDay(trip.getCreatedAt()));
                    response.setLeftAt(TimeUtil.formatDay(trip.getTime().getLeftAt()));

                    response.setReason(trip.getCancelReason());
                    response.setProviderAvatar(trip.getProvider().getAvatar());
                    response.setProviderAcceptedAt(TimeUtil.formatDay(trip.getTime().getAcceptedAt()));
                    response.setProviderCategory(trip.getProvider().getCategory());
                    response.setProviderName(trip.getProvider().getFullName());
                    response.setProviderStatus(trip.getStatus());
                    response.setProviderVerifiedAt(TimeUtil.formatDay(trip.getTime().getVerifiedAt()));

                    response.setInviteReason(trip.getInviteCancelReason());
                    response.setInvitedProviderAvatar(trip.getInvitedProvider().getAvatar());
                    response.setInvitedProviderAcceptedAt(TimeUtil.formatDay(trip.getTime().getInviteAcceptedAt()));
                    response.setInvitedProviderCategory(trip.getInvitedProvider().getCategory());
                    response.setInvitedProviderInvitedAt(TimeUtil.formatDay(trip.getTime().getInvitedAt()));
                    response.setInvitedProviderName(trip.getInvitedProvider().getFullName());
                    response.setInvitedProviderStatus(trip.getInviteStatus());
                    response.setInvitedProviderVerifiedAt(TimeUtil.formatDay(trip.getTime().getInviteVerifiedAt()));

                    if(trip.getShared() != null) {
                        response.setSharedAmount(MoneyUtil.formatToNaira(trip.getShared().getAmount()));
                        response.setUserShare(MoneyUtil.formatToNaira(trip.getShared().getUser()));
                        response.setProviderShare(MoneyUtil.formatToNaira(trip.getShared().getProvider()));
                    }
                    return response;
                })
                .toList();
        return new ApiResponse<>(trips);
    }
}
package com.serch.server.domains.shared.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.token.TokenService;
import com.serch.server.domains.account.responses.AccountResponse;
import com.serch.server.domains.shared.responses.GuestProfileData;
import com.serch.server.domains.shared.responses.SharedLinkData;
import com.serch.server.domains.shared.responses.SharedLinkResponse;
import com.serch.server.domains.shared.responses.SharedStatusData;
import com.serch.server.domains.shared.services.SharedService;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.exceptions.others.SharedException;
import com.serch.server.exceptions.others.TripException;
import com.serch.server.mappers.SharedMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.rating.Rating;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shared.SharedLogin;
import com.serch.server.models.shared.SharedStatus;
import com.serch.server.models.trip.Trip;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.shared.SharedLoginRepository;
import com.serch.server.repositories.shared.SharedStatusRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * This is the class that contains the logic and implementation for its wrapper class {@link SharedService}
 *
 * @see UserUtil
 * @see SharedLinkRepository
 * @see GuestRepository
 * @see UserRepository
 */
@Service
@RequiredArgsConstructor
public class SharedImplementation implements SharedService {
    private final UserUtil util;
    private final TokenService tokenService;
    private final SharedLinkRepository sharedLinkRepository;
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final ProfileRepository profileRepository;
    private final SharedLoginRepository sharedLoginRepository;
    private final TripRepository tripRepository;
    private final UserUtil userUtil;
    private final SharedStatusRepository sharedStatusRepository;

    @Value("${application.trip.count.min}")
    private Integer TRIP_MIN_COUNT_BEFORE_CHARGE;

    @Override
    public ApiResponse<List<AccountResponse>> accounts(String id) {
        Guest guest = guestRepository.findById(id).orElseThrow(() -> new SharedException("Guest not found"));
        User user = userRepository.findByEmailAddressIgnoreCase(guest.getEmailAddress()).orElse(null);

        return buildAccountResponse(guest, user);
    }

    @Override
    public ApiResponse<List<SharedLinkResponse>> create(String id, Boolean withInvited) {
        Trip trip = tripRepository.findByIdAndAccount(id, String.valueOf(userUtil.getUser().getId()))
                .orElseThrow(() -> new SharedException("Trip not found"));
        Profile profile = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new SharedException("User not found"));

        if(withInvited != null && withInvited) {
            if(trip.getInvited() != null && trip.getInvited().getProvider() != null) {
                verifyShareEligibility(trip.getInvited().getProvider());
                saveSharedLInk(profile, trip, trip.getInvited().getProvider());
            } else {
                throw new TripException("There is no invited provider in this trip");
            }
        } else {
            verifyShareEligibility(trip.getProvider());
            saveSharedLInk(profile, trip, trip.getProvider());
        }

        return links(null, null);
    }

    private void verifyShareEligibility(Profile profile) {
        List<Trip> trips = tripRepository.findByProviderId(profile.getUser().getId());
        if(trips.size() < TRIP_MIN_COUNT_BEFORE_CHARGE) {
            int remains = TRIP_MIN_COUNT_BEFORE_CHARGE - trips.size();
            throw new SharedException(
                    "We are sorry to inform you that you cannot share this provider at the moment." +
                            " As per our policy, providers can be shared once they have surpassed a certain amount of trips." +
                            " This assures us of their efficiency in getting the job done. As at the moment, " +
                            String.format("%s has %s trips to go before this becomes possible.", profile.getFullName(), remains)
            );
        }
    }

    private void saveSharedLInk(Profile profile, Trip trip, Profile provider) {
        String from = String.format("%s-%s", profile.getFullName(), provider.getFullName());

        SharedLink shared = new SharedLink();
        shared.setSecret(tokenService.generate(from, 10));
        shared.setAmount(trip.getAmount());
        shared.setUser(profile);
        shared.setProvider(provider);
        sharedLinkRepository.save(shared);
    }

    @Override
    public ApiResponse<List<SharedLinkResponse>> links(Integer page, Integer size) {
        List<SharedLinkResponse> list = new ArrayList<>();

        Page<SharedLink> links = sharedLinkRepository.findByUserId(util.getUser().getId(), HelperUtil.getPageable(page, size));
        if(links != null && !links.getContent().isEmpty()) {
            list = links.getContent()
                    .stream()
                    .sorted(Comparator.comparing(SharedLink::getUpdatedAt).reversed())
                    .map(this::buildLink)
                    .toList();
        }

        return new ApiResponse<>(list);
    }

    @Override
    public SharedLinkResponse buildLink(SharedLink link) {
        SharedLinkResponse response = new SharedLinkResponse();
        response.setData(data(link, getCurrentStatus(link)));
        response.setTotalGuests(link.getLogins() != null ? link.getLogins().size() : 0);

        if(link.getLogins() != null && !link.getLogins().isEmpty()) {
            response.setGuests(link.getLogins().stream()
                    .sorted(Comparator.comparing(login -> getCurrentStatusForAccount(login).getCreatedAt()))
                    .map(login -> {
                        GuestProfileData data = SharedMapper.INSTANCE.response(login.getGuest());
                        data.setJoinedAt(TimeUtil.formatDay(login.getCreatedAt(), ""));
                        data.setStatus(login.getStatus().getType());
                        if(login.getStatuses() != null && !login.getStatuses().isEmpty()) {
                            data.setStatuses(login.getStatuses().stream().map(stat -> getStatusData(login.getSharedLink(), stat)).toList());
                        }

                        return data;
                    })
                    .toList()
            );
        }

        return response;
    }

    @Override
    public SharedStatus getCurrentStatus(SharedLink link) {
        if (link.getLogins() != null && !link.getLogins().isEmpty()) {
            return link.getLogins().stream()
                    .filter(login -> Objects.nonNull(login.getStatuses()))
                    .flatMap(login -> login.getStatuses().stream())
                    .max(Comparator.comparing(SharedStatus::getCreatedAt))
                    .orElse(null);
        } else {
            return null;
        }
    }


    @Override
    public SharedStatus getCurrentStatusForAccount(SharedLogin login) {
        if(login.getStatuses() != null && !login.getStatuses().isEmpty()) {
            List<SharedStatus> statusesForAccount = login.getStatuses().stream()
                    .filter(status -> status.getSharedLogin().getId().equals(login.getId()))
                    .toList();

            Optional<SharedStatus> latestStatus = statusesForAccount.stream()
                    .max(Comparator.comparing(SharedStatus::getCreatedAt));

            return latestStatus.orElse(null);
        } else {
            return null;
        }
    }

    @Override
    public SharedLinkData data(SharedLink link, SharedStatus status) {
        SharedLinkData data = SharedMapper.INSTANCE.shared(link);

        data.setProvider(SharedMapper.INSTANCE.response(link.getProvider()));
        data.getProvider().setName(link.getProvider().getFullName());
        data.getProvider().setCategory(link.getProvider().getCategory().getType());

        data.setUser(SharedMapper.INSTANCE.response(link.getUser()));
        data.getUser().setName(link.getUser().getFullName());

        data.setImage(link.getProvider().getCategory().getImage());
        data.setCategory(link.getProvider().getCategory().getType());
        data.setLabel(TimeUtil.formatDay(link.getCreatedAt(), ""));
        data.setAmount(MoneyUtil.formatToNaira(link.getAmount()));
        data.setLink(LinkUtils.instance.shared(link.getProvider().getCategory().name(), link.getSecret()));
        data.setStatus(status != null ? status.getUseStatus().getType() : "No usage");

        return data;
    }

    @Override
    public SharedStatusData getStatusData(SharedLink link, SharedStatus status) {
        SharedStatusData data = SharedMapper.INSTANCE.data(status);
        data.setLabel(TimeUtil.formatDay(status.getCreatedAt(), ""));
        data.setStatus(status.getUseStatus().getType());
        data.setRating(ratingRepository.getByRated(status.getTrip().getId()).map(Rating::getRating).orElse(0.0));
        data.setAmount(MoneyUtil.formatToNaira(status.getTrip().getAmount()));
        data.setUser(MoneyUtil.formatToNaira(status.getTrip().getUserShare()));

        if(status.getTrip() != null) {
            data.setTrip(status.getTrip().getId());
            data.setRating(ratingRepository.getByRated(status.getTrip().getId()).map(Rating::getRating).orElse(0.0));
        } else {
            data.setRating(0.0);
        }
        data.setMore(status.getTrip().getStatus().name());

        return data;
    }

    @Override
    public void create(String linkId, String account, Trip trip) {
        if(linkId != null && !linkId.isEmpty()) {
            SharedLogin login = sharedLoginRepository.findBySharedLink_IdAndGuest_Id(linkId, account)
                    .orElseThrow(() -> new TripException("Guest not found. Check the link you are using."));

            if(login.getSharedLink().isExpired()) {
                throw new TripException("Link has expired");
            } else {
                SharedStatus status = new SharedStatus();
                status.setSharedLogin(login);
                status.setTrip(trip);
                status.setUseStatus(login.nextUsageStatus());
                status.setSharedLink(login.getSharedLink());
                sharedStatusRepository.save(status);
            }
        }
    }

    @Override
    public <T extends AccountResponse> T buildWithUser(User user, T response) {
        SerchCategory category = profileRepository.findById(user.getId())
                .map(Profile::getCategory).orElse(SerchCategory.USER);

        response.setAvatar(profileRepository.findById(user.getId()).map(Profile::getAvatar).orElse(""));
        response.setName(user.getFullName());
        response.setCategory(category.getType());
        response.setCategoryImage(category.getImage());
        response.setId(user.getId().toString());
        response.setEmailAddress(user.getEmailAddress());

        return response;
    }

    @Override
    public <T extends AccountResponse> T buildWithLogin(SharedLogin login, T response) {
        response.setId(login.getGuest().getId());
        response.setAvatar(login.getGuest().getAvatar());
        response.setName(login.getGuest().getFullName());
        response.setEmailAddress(login.getGuest().getEmailAddress());
        response.setLinkId(login.getSharedLink().getId());
        response.setCategory(SerchCategory.GUEST.getType());
        response.setCategoryImage(SerchCategory.GUEST.getImage());

        return response;
    }

    @Override
    public ApiResponse<List<AccountResponse>> buildAccountResponse(Guest guest, User user) {
        List<AccountResponse> list = new ArrayList<>();
        if(guest != null) {
            List<SharedLogin> logins = sharedLoginRepository.findByGuest_Id(guest.getId());
            if(logins != null && !logins.isEmpty()) {
                List<AccountResponse> guests = logins.stream()
                        .filter(login -> !login.getSharedLink().cannotLogin())
                        .sorted(Comparator.comparing(SharedLogin::getCreatedAt))
                        .map(login -> buildWithLogin(login, new AccountResponse()))
                        .toList();
                list.addAll(guests);
            }
        }

        if(user != null) {
            list.add(buildWithUser(user, new AccountResponse()));
        }

        return new ApiResponse<>(list);
    }
}
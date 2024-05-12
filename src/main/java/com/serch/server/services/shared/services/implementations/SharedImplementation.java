package com.serch.server.services.shared.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.shared.UseStatus;
import com.serch.server.exceptions.others.SharedException;
import com.serch.server.mappers.SharedMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.rating.Rating;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shared.SharedLogin;
import com.serch.server.models.shared.SharedStatus;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.shared.SharedLoginRepository;
import com.serch.server.services.account.responses.AccountResponse;
import com.serch.server.services.shared.responses.GuestProfileData;
import com.serch.server.services.shared.responses.SharedLinkData;
import com.serch.server.services.shared.responses.SharedLinkResponse;
import com.serch.server.services.shared.responses.SharedStatusData;
import com.serch.server.services.shared.services.SharedService;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
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
    private final SharedLinkRepository sharedLinkRepository;
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final ProfileRepository profileRepository;
    private final SharedLoginRepository sharedLoginRepository;

    @Override
    public ApiResponse<List<AccountResponse>> accounts(String id) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new SharedException("Guest not found"));
        User user = userRepository.findByEmailAddressIgnoreCase(guest.getEmailAddress())
                .orElse(null);

        return buildAccountResponse(guest, user);
    }

    @Override
    public ApiResponse<List<SharedLinkResponse>> links() {
        List<SharedLinkResponse> list = sharedLinkRepository.findByUserId(util.getUser().getId())
                .stream()
                .sorted(Comparator.comparing(SharedLink::getUpdatedAt))
                .map(link -> {
                    SharedLinkResponse response = new SharedLinkResponse();
                    response.setData(data(link, getCurrentStatus(link)));
                    response.setTotalGuests(link.getLogins().size());
                    if(link.getLogins() != null && !link.getLogins().isEmpty()) {
                        response.setGuests(
                                link.getLogins().stream()
                                        .sorted(Comparator.comparing(login ->
                                                getCurrentStatusForAccount(login).getCreatedAt()
                                        ))
                                        .map(login -> {
                                            GuestProfileData data = new GuestProfileData();
                                            data.setId(login.getGuest().getId());
                                            data.setGender(login.getGuest().getGender());
                                            data.setAvatar(login.getGuest().getAvatar());
                                            data.setName(login.getGuest().getFullName());
                                            data.setJoinedAt(TimeUtil.formatDay(login.getCreatedAt()));
                                            data.setStatus(login.getStatus().getType());
                                            if(login.getStatuses() != null && !login.getStatuses().isEmpty()) {
                                                data.setStatuses(
                                                        login.getStatuses()
                                                                .stream()
                                                                .map(stat -> getStatusData(login.getSharedLink(), stat))
                                                                .toList()
                                                );
                                            }
                                            return data;
                                        })
                                        .toList()
                        );
                    }
                    return response;
                })
                .toList();
        return new ApiResponse<>(list);
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
        data.setUser(SharedMapper.INSTANCE.response(link.getUser()));
        data.getUser().setName(link.getUser().getFullName());
        data.setImage(link.getProvider().getCategory().getImage());
        data.setCategory(link.getProvider().getCategory().getType());
        data.setLabel(TimeUtil.formatDay(link.getCreatedAt()));
        data.setAmount(MoneyUtil.formatToNaira(link.getAmount()));
        data.setStatus(status != null ? status.getStatus().getType() : UseStatus.NOT_USED.getType());
        return data;
    }

    @Override
    public SharedStatusData getStatusData(SharedLink link, SharedStatus status) {
        SharedStatusData data = SharedMapper.INSTANCE.data(status);
        data.setLabel(TimeUtil.formatDay(status.getCreatedAt()));
        data.setStatus(status.getStatus().getType());
        data.setMore(
                MoneyUtil.getSharedInfo(
                        link.getProvider().getFullName(),
                        link.getUser().getFullName(),
                        link.getId(), status.getAmount(),
                        status.getUser(), status.getProvider()
                )
        );
        data.setRating(
                ratingRepository.getByRated(status.getTrip().getId())
                        .map(Rating::getRating)
                        .orElse(0.0)
        );
        data.setAmount(MoneyUtil.formatToNaira(status.getAmount()));
        data.setProvider(MoneyUtil.formatToNaira(status.getProvider()));
        data.setUser(MoneyUtil.formatToNaira(status.getUser()));
        if(status.getTrip() != null) {
            data.setTrip(status.getTrip().getId());
            data.setRating(ratingRepository.getByRated(status.getTrip().getId()).map(Rating::getRating).orElse(0.0));
        } else {
            data.setRating(0.0);
        }
        return data;
    }

    @Override
    public ApiResponse<List<AccountResponse>> buildAccountResponse(Guest guest, User user) {
        List<AccountResponse> list = new ArrayList<>();
        if(guest != null) {
            List<SharedLogin> logins = sharedLoginRepository.findByGuest_Id(guest.getId());
            if(logins != null && !logins.isEmpty()) {
                List<AccountResponse> guests = logins.stream()
                        .sorted(Comparator.comparing(SharedLogin::getCreatedAt))
                        .map(login -> {
                            AccountResponse response = new AccountResponse();
                            response.setId(login.getGuest().getId());
                            response.setAvatar(login.getGuest().getAvatar());
                            response.setName(login.getGuest().getFullName());
                            response.setEmailAddress(login.getGuest().getEmailAddress());
                            response.setLinkId(login.getSharedLink().getId());
                            response.setCategory(SerchCategory.GUEST.getType());
                            response.setCategoryImage(SerchCategory.GUEST.getImage());
                            return response;
                        })
                        .toList();
                list.addAll(guests);
            }
        }

        if(user != null) {
            SerchCategory category = profileRepository.findById(user.getId())
                    .map(Profile::getCategory).orElse(SerchCategory.USER);
            AccountResponse response = new AccountResponse();
            response.setAvatar(
                    profileRepository.findById(user.getId())
                            .map(Profile::getAvatar)
                            .orElse("")
            );
            response.setName(user.getFullName());
            response.setCategory(category.getType());
            response.setCategoryImage(category.getImage());
            response.setId(user.getId().toString());
            response.setAvatar(profileRepository.findById(user.getId()).map(Profile::getAvatar).orElse(""));
            response.setEmailAddress(user.getEmailAddress());
            list.add(response);
        }
        return new ApiResponse<>(list);
    }
}
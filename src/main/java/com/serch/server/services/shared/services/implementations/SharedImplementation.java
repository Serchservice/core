package com.serch.server.services.shared.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.others.SharedException;
import com.serch.server.mappers.SharedMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shared.SharedPricing;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.services.account.responses.AccountResponse;
import com.serch.server.services.shared.responses.GuestResponse;
import com.serch.server.services.shared.responses.SharedAccountResponse;
import com.serch.server.services.shared.services.GuestAuthService;
import com.serch.server.services.shared.services.SharedService;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SharedImplementation implements SharedService {
    private final GuestAuthService authService;
    private final UserUtil util;
    private final SharedLinkRepository sharedLinkRepository;
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Override
    public ApiResponse<AccountResponse> accounts(String id) {
        List<SharedAccountResponse> list;
        Guest guest;
        User user;

        if(id != null && !id.isEmpty()) {
            guest = guestRepository.findById(id)
                    .orElseThrow(() -> new SharedException("Guest not found"));
        } else {
            guest = guestRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                    .orElse(new Guest());
        }

        if(id != null && !id.isEmpty()) {
            user = userRepository.findByEmailAddressIgnoreCase(guest.getEmailAddress())
                    .orElse(new User());
        } else {
            user = util.getUser();
        }

        list = guest.getSharedLinks()
                .stream()
                .sorted(Comparator.comparing(SharedLink::getCreatedAt))
                .map(link -> {
                    SharedAccountResponse response = new SharedAccountResponse();
                    response.setId(guest.getId());
                    response.setAvatar(guest.getAvatar());
                    response.setCount(link.getStatus().getCount());
                    response.setLink(link.getLink());
                    response.setName(guest.getFullName());
                    response.setMode("GUEST");
                    response.setEmailAddress(guest.getEmailAddress());
                    response.setLinkId(link.getId());
                    return response;
                })
                .toList();

        AccountResponse response = new AccountResponse();
        response.setAvatar(
                profileRepository.findById(user.getId())
                        .map(Profile::getAvatar)
                        .orElse("")
        );
        response.setName(user.getFullName());
        response.setMode("USER");
        response.setId(user.getId().toString());
        response.setGuests(list);
        response.setEmailAddress(user.getEmailAddress());
        return new ApiResponse<>(response);
    }

    @Override
    public ApiResponse<List<GuestResponse>> links() {
        List<GuestResponse> list = sharedLinkRepository.findByUser_SerchIdOrProvider_SerchId(util.getUser().getId())
                .stream()
                .sorted(Comparator.comparing(SharedLink::getUpdatedAt))
                .map(link -> {
                    GuestResponse response = new GuestResponse();
                    response.setData(SharedMapper.INSTANCE.response(link));
                    response.setProvider(SharedMapper.INSTANCE.response(link.getProvider()));
                    response.getProvider().setName(link.getProvider().getFullName());
                    response.setUser(SharedMapper.INSTANCE.response(link.getUser()));
                    response.getUser().setName(link.getUser().getFullName());

                    if(!link.getPricing().isEmpty()) {
                        response.setPricing(
                                link.getPricing()
                                        .stream()
                                        .sorted(Comparator.comparing(SharedPricing::getCreatedAt))
                                        .map(pricing -> authService.getSharedPricingData(link, pricing))
                                        .toList()
                        );
                    }
                    return response;
                })
                .toList();
        return new ApiResponse<>(list);
    }
}

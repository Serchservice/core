package com.serch.server.services.shared.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.others.SharedException;
import com.serch.server.mappers.SharedMapper;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shared.SharedPricing;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.services.account.responses.AccountResponse;
import com.serch.server.services.account.services.AccountService;
import com.serch.server.services.shared.responses.GuestResponse;
import com.serch.server.services.shared.services.GuestAuthService;
import com.serch.server.services.shared.services.SharedService;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * This is the class that contains the logic and implementation for its wrapper class {@link SharedService}
 *
 * @see GuestAuthService
 * @see UserUtil
 * @see SharedLinkRepository
 * @see GuestRepository
 * @see UserRepository
 */
@Service
@RequiredArgsConstructor
public class SharedImplementation implements SharedService {
    private final GuestAuthService authService;
    private final AccountService accountService;
    private final UserUtil util;
    private final SharedLinkRepository sharedLinkRepository;
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<AccountResponse> accounts(String id) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new SharedException("Guest not found"));
        User user = userRepository.findByEmailAddressIgnoreCase(guest.getEmailAddress())
                .orElse(new User());

        return accountService.buildAccountResponse(guest, user);
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

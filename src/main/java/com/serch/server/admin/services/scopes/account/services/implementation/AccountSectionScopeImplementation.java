package com.serch.server.admin.services.scopes.account.services.implementation;

import com.serch.server.admin.services.responses.Metric;
import com.serch.server.admin.services.scopes.account.responses.AccountSectionResponse;
import com.serch.server.admin.services.scopes.account.services.AccountSectionScopeService;
import com.serch.server.admin.services.scopes.common.services.CommonProfileService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.utils.AdminUtil;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountSectionScopeImplementation implements AccountSectionScopeService {
    private final CommonProfileService profileService;
    private final UserRepository userRepository;
    private final GuestRepository guestRepository;

    @Override
    public ApiResponse<Metric> fetchMetric(Role role) {
        Metric metric = new Metric();

        if(role != null) {
            metric.setHeader(String.format(
                    "Total number of %s",
                    String.format("%s%s", role.getType().toLowerCase(), role == Role.BUSINESS ? "es" : "s")
            ));
            metric.setCount(AdminUtil.formatCount(userRepository.countByRole(role)));
        } else {
            metric.setHeader("Total number of guests");
            metric.setCount(AdminUtil.formatCount(guestRepository.count()));
        }

        return new ApiResponse<>(metric);
    }

    @Override
    public ApiResponse<AccountSectionResponse> fetchAccounts(Role role, Integer page, Integer size, String alphabet) {
        AccountSectionResponse response = new AccountSectionResponse();

        if(role != null) {
            Page<User> users = userRepository.findByRoleAndLastNameStartingWithIgnoreCase(role, alphabet, HelperUtil.getPageable(page, size));

            response.setTotal(userRepository.countByRoleAndLastNameStartingWithIgnoreCase(role, alphabet));
            response.setAccounts(users.getContent().stream().map(user -> profileService.fromId(user.getId())).toList());
            response.setTotalPages(users.getTotalPages());
        } else {
            Page<Guest> guests = guestRepository.findByLastNameStartingWithIgnoreCase(alphabet, HelperUtil.getPageable(page, size));

            response.setTotal(guestRepository.countByLastNameStartingWithIgnoreCase(alphabet));
            response.setAccounts(guests.getContent().stream().map(user -> profileService.fromId(user.getId())).toList());
            response.setTotalPages(guests.getTotalPages());
        }

        return new ApiResponse<>(response);
    }

    @Override
    public ApiResponse<AccountSectionResponse> search(Role role, Integer page, Integer size, String query) {
        AccountSectionResponse response = new AccountSectionResponse();

        if(role != null) {
            Page<User> users = userRepository.searchByRoleAndQuery(role, query, HelperUtil.getPageable(page, size));

            response.setTotal(userRepository.countByRoleAndQuery(role, query));
            response.setAccounts(users.getContent().stream().map(user -> profileService.fromId(user.getId())).toList());
            response.setTotalPages(users.getTotalPages());
        } else {
            Page<Guest> guests = guestRepository.searchByQuery(query, HelperUtil.getPageable(page, size));

            response.setTotal(guestRepository.countByQuery(query));
            response.setAccounts(guests.getContent().stream().map(user -> profileService.fromId(user.getId())).toList());
            response.setTotalPages(guests.getTotalPages());
        }

        return new ApiResponse<>(response);
    }
}
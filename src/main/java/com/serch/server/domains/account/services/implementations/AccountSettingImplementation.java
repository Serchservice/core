package com.serch.server.domains.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.account.responses.AccountSettingResponse;
import com.serch.server.domains.account.services.AccountSettingService;
import com.serch.server.mappers.AccountMapper;
import com.serch.server.models.account.AccountSetting;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.account.AccountSettingRepository;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This holds the implementation for its wrapper class {@link AccountSettingService}
 *
 * @see UserUtil
 * @see AccountSettingRepository
 */
@Service
@RequiredArgsConstructor
public class AccountSettingImplementation implements AccountSettingService {
    private final UserUtil userUtil;
    private final AccountSettingRepository accountSettingRepository;

    @Override
    public void create(User user) {
        Optional<AccountSetting> existing = accountSettingRepository.findByUser_Id(user.getId());

        if(existing.isEmpty()) {
            AccountSetting setting = new AccountSetting();
            setting.setUser(user);
            accountSettingRepository.save(setting);
        }
    }

    @Override
    public ApiResponse<AccountSettingResponse> update(AccountSettingResponse request) {
        AccountSetting setting = accountSettingRepository.findByUser_Id(userUtil.getUser().getId())
                .orElse(new AccountSetting());

        AccountMapper.INSTANCE.updateAccountSetting(request, setting);
        accountSettingRepository.save(setting);

        return new ApiResponse<>("Settings updated", AccountMapper.INSTANCE.response(setting), HttpStatus.OK);
    }

    @Override
    public ApiResponse<AccountSettingResponse> settings() {
        AccountSetting setting = accountSettingRepository.findByUser_Id(userUtil.getUser().getId())
                .orElse(new AccountSetting());

        AccountSettingResponse response = AccountMapper.INSTANCE.response(setting);
        return new ApiResponse<>(response);
    }
}

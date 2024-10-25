package com.serch.server.admin.services.scopes.common;

import com.serch.server.admin.mappers.CommonMapper;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.bases.BaseProfile;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class CommonProfileImplementation implements CommonProfileService {
    private final ProfileRepository profileRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final WalletRepository walletRepository;
    private final GuestRepository guestRepository;
    private final AdminRepository adminRepository;

    @Override
    public CommonProfileResponse fromTransaction(String id) {
        AtomicReference<CommonProfileResponse> response = new AtomicReference<>(new CommonProfileResponse());

        UUID uuid = HelperUtil.parseUUID(id);
        if (uuid != null) {
            profileRepository.findById(uuid).ifPresentOrElse(
                    profile -> response.set(CommonMapper.instance.response(profile)),
                    () -> businessProfileRepository.findById(uuid)
                            .ifPresent(profile -> response.set(CommonMapper.instance.response(profile)))
            );
        } else {
            walletRepository.findById(id).ifPresentOrElse(
                    wallet -> response.set(CommonMapper.instance.response(wallet.getUser())),
                    () -> guestRepository.findById(id)
                            .ifPresent(profile -> {
                                CommonProfileResponse pro = CommonMapper.instance.response(profile);
                                pro.setImage(SerchCategory.GUEST.getImage());
                                pro.setCategory(SerchCategory.GUEST.getType());
                                response.set(pro);
                            })
            );
        }

        return response.get();
    }

    @Override
    public CommonProfileResponse fromUser(User user) {
        CommonProfileResponse profile = CommonMapper.instance.response(user);
        profile.setAvatar(
                profileRepository.findById(user.getId()).map(BaseProfile::getAvatar)
                        .orElse(businessProfileRepository.findById(user.getId())
                                .map(BaseProfile::getAvatar)
                                .orElse(adminRepository.findById(user.getId())
                                        .map(Admin::getAvatar).orElse(null)))
        );

        return profile;
    }
}

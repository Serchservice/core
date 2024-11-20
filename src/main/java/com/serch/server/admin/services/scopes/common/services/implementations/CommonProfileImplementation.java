package com.serch.server.admin.services.scopes.common.services.implementations;

import com.serch.server.admin.mappers.CommonMapper;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.admin.services.scopes.common.services.CommonProfileService;
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
        UUID uuid = HelperUtil.parseUUID(id);
        if (uuid != null) {
            return fromId(uuid);
        } else {
            return walletRepository.findById(id).map(wallet -> CommonMapper.instance.response(wallet.getUser()))
                    .or(() -> guestRepository.findById(id).map(profile -> {
                        CommonProfileResponse response = CommonMapper.instance.response(profile);
                        response.setImage(SerchCategory.GUEST.getImage());
                        response.setCategory(SerchCategory.GUEST.getType());

                        return response;
                    }))
                    .orElseGet(CommonProfileResponse::new);
        }
    }

    @Override
    public CommonProfileResponse fromId(UUID id) {
        return profileRepository.findById(id).map(CommonMapper.instance::response)
                .or(() -> businessProfileRepository.findById(id).map(CommonMapper.instance::response))
                .or(() -> adminRepository.findById(id).map(CommonMapper.instance::response))
                .orElseGet(CommonProfileResponse::new);
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
        profile.setRating(
                profileRepository.findById(user.getId()).map(BaseProfile::getRating)
                        .orElse(businessProfileRepository.findById(user.getId())
                                .map(BaseProfile::getRating)
                                .orElse(0.0))
        );

        return profile;
    }

    @Override
    public CommonProfileResponse fromId(String id) {
        return guestRepository.findById(id).map(profile -> {
            CommonProfileResponse response = CommonMapper.instance.response(profile);
            response.setImage(SerchCategory.GUEST.getImage());
            response.setCategory(SerchCategory.GUEST.getType());

            return response;
        })
        .orElseGet(CommonProfileResponse::new);
    }
}

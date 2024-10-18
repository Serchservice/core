package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.models.account.AccountDelete;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.account.*;
import com.serch.server.repositories.auth.AccountStatusTrackerRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.referral.ReferralProgramRepository;
import com.serch.server.services.account.services.AccountDeleteService;
import com.serch.server.services.referral.services.ReferralService;
import com.serch.server.services.transaction.services.WalletService;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for deleting user accounts.
 * It implements the wrapper class {@link AccountDeleteService}
 *
 * @see UserUtil
 * @see AccountDeleteRepository
 * @see ProfileRepository
 * @see UserRepository
 */
@Service
@RequiredArgsConstructor
public class AccountDeleteImplementation implements AccountDeleteService {
    private final ReferralService referralService;
    private final WalletService walletService;
    private final UserUtil userUtil;
    private final AccountDeleteRepository accountDeleteRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final PhoneInformationRepository phoneInformationRepository;
    private final ReferralProgramRepository referralProgramRepository;
    private final AccountStatusTrackerRepository accountStatusTrackerRepository;
    private final AccountSettingRepository accountSettingRepository;
    private final BusinessProfileRepository businessProfileRepository;

    @Override
    public ApiResponse<String> delete(UUID id) {
        if(userUtil.getUser().isProfile()) {
            throw new AccountException("Cannot access this resource. Access denied");
        } else {
            User user = profileRepository.findById(id)
                    .orElseThrow(() -> new AccountException("Provider not found"))
                    .getUser();
            if(user.getStatus() != AccountStatus.BUSINESS_DELETED) {
                user.setStatus(AccountStatus.BUSINESS_DELETED);
                user.setUpdatedAt(TimeUtil.now());
                userRepository.save(user);
            }
            return getDeleteResponse(user);
        }
    }

    @Override
    public ApiResponse<String> delete() {
        return getDeleteResponse(userUtil.getUser());
    }

    private ApiResponse<String> getDeleteResponse(User user) {
        Optional<AccountDelete> del = accountDeleteRepository.findByUser_EmailAddress(user.getEmailAddress());

        if(del.isPresent()) {
            throw new AccountException("Cannot request delete multiple times.");
        } else {
            AccountDelete delete = new AccountDelete();
            delete.setUser(user);
            delete.setStatus(IssueStatus.PENDING);
            accountDeleteRepository.save(delete);

            return new ApiResponse<>("Account delete requested", HttpStatus.OK);
        }
    }

    @Override
    @Transactional
    public void undo(String emailAddress) {
        profileRepository.findByUser_EmailAddress(emailAddress).ifPresent(profile -> handleUndo(profile.getUser()));
        businessProfileRepository.findByUser_EmailAddress(emailAddress).ifPresent(profile -> handleUndo(profile.getUser()));
    }

    @Transactional
    protected void handleUndo(User user) {
        phoneInformationRepository.findByUser_Id(user.getId()).ifPresent(phone -> {
            phoneInformationRepository.delete(phone);
            phoneInformationRepository.flush();
        });

        referralService.undo(user);
        referralProgramRepository.delete(user.getProgram());
        referralProgramRepository.flush();

        accountStatusTrackerRepository.deleteByUser(user);
        accountStatusTrackerRepository.flush();

        accountSettingRepository.delete(user.getSetting());
        accountSettingRepository.flush();

        walletService.undo(user);

        profileRepository.findById(user.getId()).ifPresent(profile -> {
            profileRepository.delete(profile);
            profileRepository.flush();
        });

        businessProfileRepository.findById(user.getId()).ifPresent(profile -> {
            businessProfileRepository.delete(profile);
            businessProfileRepository.flush();
        });

        userRepository.delete(user);
        userRepository.flush();
    }
}

package com.serch.server.domains.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.models.account.AccountDelete;
import com.serch.server.models.auth.AccountStatusTracker;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.account.*;
import com.serch.server.repositories.auth.AccountStatusTrackerRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.referral.ReferralProgramRepository;
import com.serch.server.domains.account.services.AccountDeleteService;
import com.serch.server.domains.referral.services.ReferralService;
import com.serch.server.domains.transaction.services.WalletService;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for deleting user accounts.
 * It implements the wrapper class {@link AccountDeleteService}
 *
 * @see AuthUtil
 * @see AccountDeleteRepository
 * @see ProfileRepository
 * @see UserRepository
 */
@Service
@RequiredArgsConstructor
public class AccountDeleteImplementation implements AccountDeleteService {
    private static final Logger log = LoggerFactory.getLogger(AccountDeleteImplementation.class);

    private final ReferralService referralService;
    private final WalletService walletService;
    private final AuthUtil authUtil;
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
        if(authUtil.getUser().isProfile()) {
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
        return getDeleteResponse(authUtil.getUser());
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
        profileRepository.findByUser_EmailAddress(emailAddress).ifPresent(profile -> {
            log.info(String.format("SERCH::: Running account create undo for %s with id %s", emailAddress, profile.getId()));
            handleUndo(profile.getUser());
        });

        businessProfileRepository.findByUser_EmailAddress(emailAddress).ifPresent(profile -> {
            log.info(String.format("SERCH::: Running business account create undo for %s with id %s", emailAddress, profile.getId()));
            handleUndo(profile.getUser());
        });
    }

    @Transactional
    protected void handleUndo(User user) {
        phoneInformationRepository.findByUser_Id(user.getId()).ifPresent(phone -> {
            log.info(String.format("SERCH::: Running phone information create undo for %s with phone number %s", user.getEmailAddress(), phone.getPhoneNumber()));
            phoneInformationRepository.delete(phone);
            phoneInformationRepository.flush();
        });

        log.info(String.format("SERCH::: Running referral create undo for %s", user.getEmailAddress()));
        referralService.undo(user);

        log.info(String.format("SERCH::: Running referral program create undo for %s with program id %s", user.getEmailAddress(), user.getProgram().getId()));
        referralProgramRepository.delete(user.getProgram());
        referralProgramRepository.flush();

        List<AccountStatusTracker> trackers = accountStatusTrackerRepository.findByUser_Id(user.getId());
        if(trackers != null && !trackers.isEmpty()) {
            trackers.removeIf(tracker -> tracker.getId() == null);
            if (!trackers.isEmpty()) {
                log.info(String.format("SERCH::: Running account status tracker create undo for %s", user.getEmailAddress()));
                accountStatusTrackerRepository.deleteAll(trackers);
                accountStatusTrackerRepository.flush();
            }
        }

        log.info(String.format("SERCH::: Running account setting create undo for %s with setting id %s", user.getEmailAddress(), user.getSetting().getId()));
        accountSettingRepository.delete(user.getSetting());
        accountSettingRepository.flush();

        log.info(String.format("SERCH::: Running wallet create undo for %s", user.getEmailAddress()));
        walletService.undo(user);

        profileRepository.findById(user.getId()).ifPresent(profile -> {
            log.info(String.format("SERCH::: Running profile create undo for %s with id %s and category %s", user.getEmailAddress(), profile.getId(), profile.getCategory()));
            profileRepository.delete(profile);
            profileRepository.flush();
        });

        businessProfileRepository.findById(user.getId()).ifPresent(profile -> {
            log.info(String.format("SERCH::: Running business profile create undo for %s with id %s and category %s", user.getEmailAddress(), profile.getId(), profile.getCategory()));
            businessProfileRepository.delete(profile);
            businessProfileRepository.flush();
        });

        log.info(String.format("SERCH::: Running user create undo for %s with id %s and role %s", user.getEmailAddress(), user.getId(), user.getRole()));
        userRepository.delete(user);
        userRepository.flush();
    }
}

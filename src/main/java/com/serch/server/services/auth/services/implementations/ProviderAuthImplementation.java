package com.serch.server.services.auth.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.mappers.AuthMapper;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.*;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.auth.incomplete.*;
import com.serch.server.services.account.services.ReferralService;
import com.serch.server.services.auth.requests.*;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.AuthService;
import com.serch.server.services.auth.services.ProviderAuthService;
import com.serch.server.services.auth.services.SessionService;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for implementing provider authentication.
 * It implements its wrapper class {@link ProviderAuthService}
 * <p></p>
 * It interacts with {@link AuthService}, {@link ReferralService}, {@link SessionService}, and others.
 *
 * @see AuthService
 * @see ReferralService
 * @see SessionService
 * @see PasswordEncoder
 * @see UserRepository
 * @see IncompleteRepository
 * @see IncompleteReferralRepository
 * @see IncompleteProfileRepository
 * @see IncompletePhoneInformationRepository
 * @see IncompleteCategoryRepository
 * @see SpecialtyServiceRepository
 * @see IncompleteSpecialtyRepository
 * @see IncompleteAdditionalRepository
 */
@Service
@RequiredArgsConstructor
public class ProviderAuthImplementation implements ProviderAuthService {
    private final AuthService authService;
    private final ReferralService referralService;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final IncompleteRepository incompleteRepository;
    private final IncompleteReferralRepository incompleteReferralRepository;
    private final IncompleteProfileRepository incompleteProfileRepository;
    private final IncompletePhoneInformationRepository incompletePhoneInformationRepository;
    private final IncompleteCategoryRepository incompleteCategoryRepository;
    private final SpecialtyServiceRepository specialtyServiceRepository;
    private final IncompleteSpecialtyRepository incompleteSpecialtyRepository;
    private final IncompleteAdditionalRepository incompleteAdditionalRepository;

    @Value("${application.account.specialty-limit}")
    private Integer SPECIALTY_LIMIT;

    @Override
    public ApiResponse<AuthResponse> login(RequestLogin request) {
        var user = userRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.check();
        if(user.getRole() == Role.PROVIDER || user.getRole() == Role.ASSOCIATE_PROVIDER) {
            return authService.authenticate(request, user);
        } else {
            throw new AuthException(
                    "This email does not belong to a Serch Provider",
                    ExceptionCodes.ACCESS_DENIED
            );
        }
    }

    @Override
    public ApiResponse<String> saveProfile(RequestProviderProfile request) {
        var incomplete = incompleteRepository.findByEmailAddress(request.getEmailAddress())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(incomplete.isEmailConfirmed()) {
            if(incomplete.hasProfile()) {
                throw new AuthException(
                        "You already have a profile. Confirm your email again",
                        ExceptionCodes.ACCESS_DENIED
                );
            }
            if(HelperUtil.validatePassword(request.getPassword())) {
                saveProfile(request, incomplete);
                return new ApiResponse<>("Profile saved successfully", HttpStatus.OK);
            } else {
                throw new AuthException(
                        "Password must contain a lowercase, uppercase, special character and number",
                        ExceptionCodes.INCORRECT_TOKEN
                );
            }
        } else {
            throw new AuthException("You have not confirmed your email", ExceptionCodes.EMAIL_NOT_VERIFIED);
        }
    }

    @Override
    public void saveProfile(RequestProviderProfile request, Incomplete incomplete) {
        if(request.getReferral() != null && !request.getReferral().isEmpty()) {
            saveReferral(request.getReferral(), incomplete);
        }
        IncompleteProfile incompleteProfile = AuthMapper.INSTANCE.profile(request);
        incompleteProfile.setIncomplete(incomplete);
        incompleteProfile.setPassword(passwordEncoder.encode(request.getPassword()));
        incompleteProfileRepository.save(incompleteProfile);

        IncompletePhoneInformation phone = AuthMapper.INSTANCE.phoneInformation(request.getPhoneInformation());
        phone.setIncomplete(incomplete);
        incompletePhoneInformationRepository.save(phone);
    }

    @Override
    public void saveReferral(String code, Incomplete incomplete) {
        User referredBy = referralService.verifyCode(code);
        IncompleteReferral referral = new IncompleteReferral();
        referral.setIncomplete(incomplete);
        referral.setReferredBy(referredBy);
        incompleteReferralRepository.save(referral);
    }

    @Override
    public ApiResponse<String> saveCategory(RequestSerchCategory category) {
        var incomplete = incompleteRepository.findByEmailAddress(category.getEmailAddress())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(category.getCategory() == SerchCategory.USER) {
            throw new AuthException("You cannot be a Serch User in the Provider platform");
        } else {
            if(incomplete.isEmailConfirmed()) {
                if(incomplete.hasProfile()) {
                    if(incomplete.hasCategory()) {
                        throw new AuthException(
                                "You already have a Serch category",
                                ExceptionCodes.ACCESS_DENIED
                        );
                    }
                    saveCategory(category.getCategory(), incomplete);
                    return new ApiResponse<>("Serch Category saved successfully", HttpStatus.OK);
                } else {
                    throw new AuthException("You don't have any profile", ExceptionCodes.PROFILE_NOT_SET);
                }
            } else {
                throw new AuthException("You have not confirmed your email", ExceptionCodes.EMAIL_NOT_VERIFIED);
            }
        }
    }

    @Override
    public void saveCategory(SerchCategory category, Incomplete incomplete) {
        IncompleteCategory incompleteCategory = new IncompleteCategory();
        incompleteCategory.setCategory(category);
        incompleteCategory.setIncomplete(incomplete);
        incompleteCategoryRepository.save(incompleteCategory);
    }

    @Override
    public ApiResponse<String> saveSpecialties(RequestAuthSpecialty specialty) {
        if(specialty.getSpecialties().size() > SPECIALTY_LIMIT) {
            throw new AuthException("Providers can only add %s specialties".formatted(SPECIALTY_LIMIT));
        } else {
            var incomplete = incompleteRepository.findByEmailAddress(specialty.getEmailAddress())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            if(incomplete.isEmailConfirmed()) {
                if(incomplete.hasProfile()) {
                    if(incomplete.hasCategory()) {
                        if(incomplete.hasSpecialty() && incomplete.getSpecializations().size() >= SPECIALTY_LIMIT) {
                            throw new AuthException(
                                    "You have reached the maximum limit for specialties",
                                    ExceptionCodes.ACCESS_DENIED
                            );
                        } else {
                            addSpecialtiesToIncompleteProfile(specialty.getSpecialties(), incomplete);
                            return new ApiResponse<>("Specialty saved successfully", HttpStatus.OK);
                        }
                    } else {
                        throw new AuthException("You don't have a Serch category", ExceptionCodes.CATEGORY_NOT_SET);
                    }
                } else {
                    throw new AuthException("You don't have any profile", ExceptionCodes.PROFILE_NOT_SET);
                }
            } else {
                throw new AuthException("You have not confirmed your email", ExceptionCodes.EMAIL_NOT_VERIFIED);
            }
        }
    }

    @Override
    public void addSpecialtiesToIncompleteProfile(List<Long> specialties, Incomplete incomplete) {
        specialties.forEach(id -> specialtyServiceRepository.findById(id)
                .ifPresent(serviceKeyword -> {
                    IncompleteSpecialty incompleteSpecialty = new IncompleteSpecialty();
                    incompleteSpecialty.setService(serviceKeyword);
                    incompleteSpecialty.setIncomplete(incomplete);
                    incompleteSpecialtyRepository.save(incompleteSpecialty);
                }));
    }

    @Override
    public ApiResponse<String> saveAdditional(RequestAdditionalInformation request) {
        var incomplete = incompleteRepository.findByEmailAddress(request.getEmailAddress())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(incomplete.isEmailConfirmed()) {
            if(incomplete.hasProfile()) {
                if(incomplete.hasCategory()) {
                    if(incomplete.hasAdditional()) {
                        throw new AuthException("You already have a additional profile", ExceptionCodes.ACCESS_DENIED);
                    } else {
                        IncompleteAdditional additional = AuthMapper.INSTANCE.additional(request);
                        additional.setIncomplete(incomplete);
                        incompleteAdditionalRepository.save(additional);
                        return new ApiResponse<>("Profile saved successfully", HttpStatus.OK);
                    }
                } else {
                    throw new AuthException("You don't have a Serch category", ExceptionCodes.CATEGORY_NOT_SET);
                }
            } else {
                throw new AuthException("You don't have any profile", ExceptionCodes.PROFILE_NOT_SET);
            }
        } else {
            throw new AuthException("You have not confirmed your email", ExceptionCodes.EMAIL_NOT_VERIFIED);
        }
    }

    @Override
    public ApiResponse<String> checkStatus(String emailAddress) {
        var incomplete = incompleteRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(incomplete.isEmailConfirmed()) {
            if(incomplete.hasProfile()) {
                if(incomplete.hasCategory()) {
                    if(incomplete.hasAdditional()) {
                        return new ApiResponse<>("Success", HttpStatus.OK);
                    } else {
                        throw new AuthException("You don't have a additional profile", ExceptionCodes.ADDITIONAL_NOT_SET);
                    }
                } else {
                    throw new AuthException("You don't have a Serch category", ExceptionCodes.CATEGORY_NOT_SET);
                }
            } else {
                throw new AuthException("You don't have any profile", ExceptionCodes.PROFILE_NOT_SET);
            }
        } else {
            throw new AuthException("You have not confirmed your email", ExceptionCodes.EMAIL_NOT_VERIFIED);
        }
    }

    @Override
    public ApiResponse<AuthResponse> finishSignup(RequestAuth auth) {
        User user = userRepository.findByEmailAddressIgnoreCase(auth.getEmailAddress())
                .orElseThrow(() -> new AuthException("User not found"));
        RequestSession requestSession = new RequestSession();
        requestSession.setPlatform(auth.getPlatform());
        requestSession.setMethod(AuthMethod.PASSWORD);
        requestSession.setUser(user);
        requestSession.setDevice(auth.getDevice());
        var session = sessionService.generateSession(requestSession);

        return new ApiResponse<>(AuthResponse.builder()
                .mfaEnabled(user.getMfaEnabled())
                .session(session.getData())
                .role(user.getRole().getType())
                .firstName(user.getFirstName())
                .recoveryCodesEnabled(user.getRecoveryCodeEnabled())
                .build()
        );
    }
}

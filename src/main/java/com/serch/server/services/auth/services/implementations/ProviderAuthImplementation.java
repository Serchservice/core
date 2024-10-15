package com.serch.server.services.auth.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.mappers.AuthMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.*;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.auth.incomplete.*;
import com.serch.server.services.account.services.AdditionalService;
import com.serch.server.services.account.services.ProfileService;
import com.serch.server.services.account.services.SpecialtyService;
import com.serch.server.services.referral.services.ReferralProgramService;
import com.serch.server.services.auth.requests.*;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.AuthService;
import com.serch.server.services.auth.services.ProviderAuthService;
import com.serch.server.core.session.SessionService;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for implementing provider authentication.
 * It implements its wrapper class {@link ProviderAuthService}
 * <p></p>
 * It interacts with {@link AuthService}, {@link ReferralProgramService}, {@link SessionService}, and others.
 *
 * @see AuthService
 * @see SessionService
 * @see ReferralProgramService
 * @see AdditionalService
 * @see SpecialtyService
 * @see ProfileService
 * @see PasswordEncoder
 * @see UserRepository
 * @see IncompleteRepository
 * @see IncompleteReferralRepository
 * @see IncompleteProfileRepository
 * @see IncompletePhoneInformationRepository
 * @see IncompleteCategoryRepository
 * @see IncompleteSpecialtyRepository
 */
@Service
@RequiredArgsConstructor
public class ProviderAuthImplementation implements ProviderAuthService {
    private final AuthService authService;
    private final SessionService sessionService;
    private final ReferralProgramService referralProgramService;
    private final SpecialtyService specialtyService;
    private final ProfileService profileService;
    private final AdditionalService additionalService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final IncompleteRepository incompleteRepository;
    private final IncompleteReferralRepository incompleteReferralRepository;
    private final IncompleteProfileRepository incompleteProfileRepository;
    private final IncompletePhoneInformationRepository incompletePhoneInformationRepository;
    private final IncompleteCategoryRepository incompleteCategoryRepository;
    private final IncompleteSpecialtyRepository incompleteSpecialtyRepository;

    @Value("${application.account.limit.specialty}")
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
                throw new AuthException("You already have a profile. Confirm your email again", ExceptionCodes.ACCESS_DENIED);
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
        User referredBy = referralProgramService.verify(code);
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
            throw new AuthException("You cannot be a Serch User in this platform");
        } else {
            if(incomplete.isEmailConfirmed()) {
                if(incomplete.hasProfile()) {
                    if(incomplete.hasCategory()) {
                        throw new AuthException("You already have a Serch category", ExceptionCodes.ACCESS_DENIED);
                    } else if(category.getSpecialties() != null && category.getSpecialties().size() > SPECIALTY_LIMIT) {
                        throw new AuthException("Specialties cannot be more than %s".formatted(SPECIALTY_LIMIT), ExceptionCodes.ACCESS_DENIED);
                    } else {
                        saveCategory(category, incomplete);
                        return new ApiResponse<>("Serch Category saved successfully", HttpStatus.OK);
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
    public void saveCategory(RequestSerchCategory category, Incomplete incomplete) {
        IncompleteCategory incompleteCategory = new IncompleteCategory();
        incompleteCategory.setCategory(category.getCategory());
        incompleteCategory.setIncomplete(incomplete);
        incompleteCategoryRepository.save(incompleteCategory);

        if(category.getSpecialties() != null) {
            category.getSpecialties().forEach(keyword -> {
                IncompleteSpecialty incompleteSpecialty = new IncompleteSpecialty();
                incompleteSpecialty.setSpecialty(keyword);
                incompleteSpecialty.setIncomplete(incomplete);
                incompleteSpecialtyRepository.save(incompleteSpecialty);
            });
        }
    }

    @Override
    public ApiResponse<AuthResponse> signup(RequestAdditionalInformation request) {
        var incomplete = incompleteRepository.findByEmailAddress(request.getEmailAddress())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(incomplete.isEmailConfirmed()) {
            if(incomplete.hasProfile()) {
                if(incomplete.hasCategory()) {
                    User user = authService.getUserFromIncomplete(incomplete, Role.PROVIDER);
                    user.setCountry(request.getCountry());
                    user.setState(request.getState());
                    userRepository.save(user);
                    ApiResponse<Profile> response = profileService.createProviderProfile(incomplete, user);
                    if(response.getStatus().is2xxSuccessful()) {
                        additionalService.createAdditional(request, response.getData());
                        specialtyService.createSpecialties(incomplete, response.getData());
                        incompleteRepository.delete(incomplete);

                        RequestSession requestSession = new RequestSession();
                        requestSession.setMethod(AuthMethod.PASSWORD);
                        requestSession.setUser(user);
                        requestSession.setDevice(request.getDevice());

                        return sessionService.generateSession(requestSession);
                    } else {
                        profileService.undo(incomplete.getEmailAddress());
                        return new ApiResponse<>(response.getMessage());
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

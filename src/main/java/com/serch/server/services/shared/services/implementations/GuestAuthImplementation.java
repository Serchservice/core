package com.serch.server.services.shared.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.others.SharedException;
import com.serch.server.mappers.SharedMapper;
import com.serch.server.models.account.PhoneInformation;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.*;
import com.serch.server.repositories.account.PhoneInformationRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.shared.SharedLoginRepository;
import com.serch.server.services.auth.services.AuthService;
import com.serch.server.services.auth.services.TokenService;
import com.serch.server.services.shared.requests.CreateGuestFromUserRequest;
import com.serch.server.services.shared.requests.CreateGuestRequest;
import com.serch.server.services.shared.requests.VerifyEmailRequest;
import com.serch.server.services.shared.responses.GuestResponse;
import com.serch.server.services.shared.responses.SharedLinkData;
import com.serch.server.services.shared.services.GuestAuthService;
import com.serch.server.services.shared.services.GuestService;
import com.serch.server.services.shared.services.SharedService;
import com.serch.server.core.storage.core.StorageService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * This is the class that contains the logic and implementation of its wrapper class {@link GuestAuthService}
 *
 * @see TokenService
 * @see StorageService
 * @see PasswordEncoder
 * @see SharedLinkRepository
 * @see GuestRepository
 * @see UserRepository
 * @see ProfileRepository
 */
@Service
@RequiredArgsConstructor
public class GuestAuthImplementation implements GuestAuthService {
    private final TokenService tokenService;
    private final SharedService sharedService;
    private final AuthService authService;
    private final GuestService guestService;
    private final StorageService supabase;
    private final PasswordEncoder passwordEncoder;
    private final SharedLinkRepository sharedLinkRepository;
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final SharedLoginRepository sharedLoginRepository;
    private final PhoneInformationRepository phoneInformationRepository;

    @Value("${application.security.otp-expiration-time}")
    protected Integer OTP_EXPIRATION_TIME;

    @Override
    public ApiResponse<SharedLinkData> verifyLink(String link) {
        SharedLink sharedLink = sharedLinkRepository.findByLink(link.trim())
                .orElseThrow(() -> new SharedException("Link not found"));
        return new ApiResponse<>(sharedService.data(sharedLink, sharedService.getCurrentStatus(sharedLink)));
    }

    @Override
    public ApiResponse<String> askToVerifyEmail(VerifyEmailRequest request) {
        Guest guest = guestRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new SharedException("Guest not found"));
        if(guest.isEmailConfirmed()) {
            throw new SharedException("Email address is already verified");
        } else {
            String otp = tokenService.generateOtp();
            guest.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_TIME));
            guest.setToken(passwordEncoder.encode(otp));
            guest.setEmailAddress(guest.getEmailAddress());
            guestRepository.save(guest);
            authService.sendEmail(guest.getEmailAddress(), otp);
            return new ApiResponse<>("OTP Sent", guest.getFirstName(), HttpStatus.OK);
        }
    }

    @Override
    public ApiResponse<GuestResponse> verifyEmailWithToken(VerifyEmailRequest request) {
        Guest guest = guestRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new SharedException("Guest not found"));
        if(guest.isEmailConfirmed()) {
            throw new SharedException("Email address is already verified");
        } else if(TimeUtil.isOtpExpired(guest.getExpiresAt(), OTP_EXPIRATION_TIME)) {
            throw new SharedException("OTP is expired. Request for another.");
        } else {
            if(passwordEncoder.matches(request.getToken(), guest.getToken())) {
                guest.setConfirmedAt(LocalDateTime.now());
                guest.setUpdatedAt(LocalDateTime.now());
                guest.setToken(null);
                guest.setExpiresAt(null);
                guestRepository.save(guest);
                return new ApiResponse<>("Email confirmed", HttpStatus.OK);
            } else {
                throw new SharedException("Incorrect token");
            }
        }
    }

    @Override
    public ApiResponse<GuestResponse> login(VerifyEmailRequest request) {
        Guest guest = guestRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new SharedException("Guest not found"));
        SharedLink sharedLink = sharedLinkRepository.findByLink(request.getLink())
                .orElseThrow(() -> new SharedException("Link not found"));

        sharedLink.validate(request.getEmailAddress());

        if(sharedLink.cannotLogin()) {
            throw new SharedException("This link has run out of its usage. You cannot access this link again");
        } else {
            SharedLogin login;
            if(request.getLinkId() != null) {
                login = sharedLoginRepository.findBySharedLink_IdAndGuest_Id(request.getLinkId(), guest.getId())
                        .orElseGet(() -> getNewLogin(sharedLink, guest));
            } else {
                login = sharedLoginRepository.findBySharedLink_LinkAndGuest_Id(request.getLink(), guest.getId())
                        .orElseThrow(() -> new SharedException("An error occurred while performing this action. Try clicking on the link in order to redirect you to the Serch app."));
            }

            guest.setState(request.getState());
            guest.setCountry(request.getCountry());
            guestRepository.save(guest);
            return new ApiResponse<>(guestService.response(login));
        }
    }

    @Override
    public ApiResponse<GuestResponse> create(CreateGuestRequest request) {
        Optional<User> user = userRepository.findByEmailAddressIgnoreCase(request.getEmailAddress());
        SharedLink sharedLink = sharedLinkRepository.findByLink(request.getLink())
                .orElseThrow(() -> new SharedException("Link not found"));

        sharedLink.validate(request.getEmailAddress());

        if(sharedLink.cannotLogin()) {
            throw new SharedException("This link has run out of its usage, so you cannot access it.");
        } else if(user.isPresent()) {
            throw new SharedException(
                    "Email address exists as a user in the Serch platform. If this is your email address, " +
                            "you need to sign into your user account to use your email as a guest"
            );
        } else {
            Optional<Guest> existing = guestRepository.findByEmailAddressIgnoreCase(request.getEmailAddress());
            if(existing.isPresent()) {
                SharedLogin login = sharedLoginRepository.findBySharedLink_IdAndGuest_Id(request.getLinkId(), existing.get().getId())
                        .orElseGet(() -> getNewLogin(sharedLink, existing.get()));

                existing.get().setState(request.getState());
                existing.get().setCountry(request.getCountry());
                guestRepository.save(existing.get());
                return new ApiResponse<>(guestService.response(login));
            } else {
                if(HelperUtil.isUploadEmpty(request.getUpload())) {
                    throw new SharedException("A picture is needed to facilitate your trips. Upload one");
                }
                Guest guest = getNewGuest(request, null);
                SharedLogin login = getNewLogin(sharedLink, guest);

                return new ApiResponse<>(guestService.response(login));
            }
        }
    }

    protected Guest getNewGuest(CreateGuestRequest request, String image) {
        String avatar;
        if(image != null) {
            avatar = image;
        } else {
            avatar = supabase.upload(request.getUpload(), UserUtil.getBucket(null));
        }

        Guest guest = SharedMapper.INSTANCE.guest(request);
        guest.setAvatar(avatar);
        guest.setName(request.getDevice().getName());
        guest.setDevice(request.getDevice().getDevice());
        guest.setPlatform(request.getDevice().getPlatform());
        guest.setHost(request.getDevice().getHost());
        guest.setIpAddress(request.getDevice().getIpAddress());
        guest.setLocalHost(request.getDevice().getLocalHost());
        guest.setOperatingSystem(request.getDevice().getOperatingSystem());
        guest.setOperatingSystemVersion(request.getDevice().getOperatingSystemVersion());
        return guestRepository.save(guest);
    }

    protected SharedLogin getNewLogin(SharedLink sharedLink, Guest guest) {
        SharedLogin newLogin = new SharedLogin();
        newLogin.setGuest(guest);
        newLogin.setSharedLink(sharedLink);
        newLogin.setStatus(sharedLink.nextLoginStatus());
        return sharedLoginRepository.save(newLogin);
    }

    @Override
    public ApiResponse<GuestResponse> createFromExistingUser(CreateGuestFromUserRequest request) {
        User user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new SharedException("User not found"));
        Profile profile = profileRepository.findById(user.getId())
                .orElseThrow(() -> new SharedException("User has not profile"));
        SharedLink sharedLink = sharedLinkRepository.findByLink(request.getLink())
                .orElseThrow(() -> new SharedException("Link not found"));
        Optional<Guest> existing = guestRepository.findByEmailAddressIgnoreCase(profile.getUser().getEmailAddress());

        sharedLink.validate(request.getEmailAddress());

        if(sharedLink.cannotLogin()) {
            throw new SharedException("This link has run out of its usage, so you cannot access it.");
        } else if(existing.isPresent()) {
            throw new SharedException("Email address already exists as a guest");
        } else if((profile.getAvatar() == null || profile.getAvatar().isEmpty()) && HelperUtil.isUploadEmpty(request.getUpload())) {
            throw new SharedException("A picture is needed to facilitate your trips. Upload one");
        } else {
            if(passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String avatar = getProfileImage(request, profile);
                CreateGuestRequest guestRequest = SharedMapper.INSTANCE.request(profile);
                guestRequest.setPhoneNumber(phoneInformationRepository.findByUser_Id(profile.getId()).map(PhoneInformation::getPhoneNumber).orElse(""));
                guestRequest.setDevice(request.getDevice());
                guestRequest.setCountry(request.getCountry());
                guestRequest.setState(request.getState());

                Guest guest = getNewGuest(guestRequest, avatar);
                guest.setConfirmedAt(profile.getUser().getEmailConfirmedAt());
                guestRepository.save(guest);

                SharedLogin login = getNewLogin(sharedLink, guest);

                return new ApiResponse<>(guestService.response(login));
            } else {
                throw new SharedException("Incorrect password or email address");
            }
        }
    }

    protected String getProfileImage(CreateGuestFromUserRequest request, Profile profile) {
        if(profile.getAvatar() != null && !profile.getAvatar().isEmpty()) {
            return profile.getAvatar();
        } else {
            String image = supabase.upload(request.getUpload(), UserUtil.getBucket(null));
            profile.setAvatar(image);
            profile.setUpdatedAt(LocalDateTime.now());
            profileRepository.save(profile);
            return image;
        }
    }
}
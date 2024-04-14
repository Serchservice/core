package com.serch.server.services.shared.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.others.SharedException;
import com.serch.server.mappers.SharedMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.GuestAuth;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shared.SharedPricing;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.shared.GuestAuthRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.services.auth.services.TokenService;
import com.serch.server.services.shared.requests.CreateGuestRequest;
import com.serch.server.services.shared.requests.VerifyEmailRequest;
import com.serch.server.services.shared.responses.GuestAuthResponse;
import com.serch.server.services.shared.responses.GuestResponse;
import com.serch.server.services.shared.responses.SharedPricingData;
import com.serch.server.services.shared.services.GuestAuthService;
import com.serch.server.services.storage.core.StorageService;
import com.serch.server.services.storage.requests.UploadRequest;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.TimeUtil;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

/**
 * This is the class that contains the logic and implementation of its wrapper class {@link GuestAuthService}
 *
 * @see TokenService
 * @see StorageService
 * @see PasswordEncoder
 * @see SharedLinkRepository
 * @see GuestAuthRepository
 * @see GuestRepository
 * @see UserRepository
 * @see ProfileRepository
 */
@Service
@RequiredArgsConstructor
public class GuestAuthImplementation implements GuestAuthService {
    private final TokenService tokenService;
    private final StorageService storageService;
    private final PasswordEncoder passwordEncoder;
    private final SharedLinkRepository sharedLinkRepository;
    private final GuestAuthRepository guestAuthRepository;
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Value("${application.security.otp-expiration-time}")
    protected Integer OTP_EXPIRATION_TIME;

    @Override
    public ApiResponse<GuestAuthResponse> verifyLink(String link) {
        SharedLink sharedLink = sharedLinkRepository.findByLink(link)
                .orElseThrow(() -> new SharedException("Link not found"));

        GuestAuthResponse response = new GuestAuthResponse();
        response.setProvider(SharedMapper.INSTANCE.response(sharedLink.getProvider()));
        response.getProvider().setName(sharedLink.getProvider().getFullName());
        response.setData(SharedMapper.INSTANCE.response(sharedLink));
        response.setUser(SharedMapper.INSTANCE.response(sharedLink.getUser()));
        response.getUser().setName(sharedLink.getUser().getFullName());

        return new ApiResponse<>(response);
    }

    @Override
    public ApiResponse<String> askToVerifyEmail(VerifyEmailRequest request) {
        Optional<Guest> guest = guestRepository.findByEmailAddressIgnoreCase(request.getEmailAddress());
        if(guest.isPresent()) {
            throw new SharedException("Access denied. Unregistered guests can access this resource");
        } else {
            Optional<GuestAuth> guestAuth = guestAuthRepository.findByEmailAddressIgnoreCase(request.getEmailAddress());

            if(guestAuth.isPresent()) {
                if(guestAuth.get().isEmailConfirmed()) {
                    throw new SharedException("Email is already confirmed");
                } else if(guestAuth.get().isTokenExpired()) {
                    String otp = tokenService.generateOtp();
                    guestAuth.get().setToken(passwordEncoder.encode(otp));
                    guestAuth.get().setExpiredAt(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_TIME));
                    guestAuth.get().setUpdatedAt(LocalDateTime.now());
                    guestAuthRepository.save(guestAuth.get());
                    /// TODO::: Send pricing email
                    return new ApiResponse<>("OTP Sent", HttpStatus.OK);
                } else {
                    throw new SharedException("You can request a new token in %s".formatted(
                            TimeUtil.formatFutureTime(guestAuth.get().getExpiredAt())
                    ));
                }
            } else {
                return getSendTokenResponse(new GuestAuth(), request.getEmailAddress());
            }
        }
    }

    @Override
    public ApiResponse<String> verifyEmailWithToken(VerifyEmailRequest request) {
        GuestAuth auth = guestAuthRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new SharedException("Email address not found"));

        return getVerifyResponse(request, auth);
    }

    @Override
    public ApiResponse<String> askToConfirmExistingEmailIdentity(VerifyEmailRequest request) {
        Guest guest = guestRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new SharedException("Guest not found"));

        Optional<GuestAuth> guestAuth = guestAuthRepository.findByEmailAddressIgnoreCase(guest.getEmailAddress());
        return guestAuth
                .map(auth -> getSendTokenResponse(auth, guest.getEmailAddress()))
                .orElseGet(() -> getSendTokenResponse(new GuestAuth(), guest.getEmailAddress()));
    }

    private ApiResponse<String> getSendTokenResponse(GuestAuth auth, String emailAddress) {
        String otp = tokenService.generateOtp();
        auth.setExpiredAt(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_TIME));
        auth.setToken(passwordEncoder.encode(otp));
        auth.setEmailAddress(emailAddress);
        guestAuthRepository.save(auth);
        /// TODO::: Send pricing email
        return new ApiResponse<>("OTP Sent", HttpStatus.OK);
    }

    @Override
    public ApiResponse<String> confirmExistingEmailIdentityWithToken(VerifyEmailRequest request) {
        Guest guest = guestRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new SharedException("Guest not found"));
        GuestAuth auth = guestAuthRepository.findByEmailAddressIgnoreCase(guest.getEmailAddress())
                .orElseThrow(() -> new SharedException("No verification requested"));

        return getVerifyResponse(request, auth);
    }

    @NotNull
    private ApiResponse<String> getVerifyResponse(VerifyEmailRequest request, GuestAuth auth) {
        if(TimeUtil.isOtpExpired(auth.getExpiredAt(), OTP_EXPIRATION_TIME)) {
            throw new SharedException("OTP is expired. Request for another.");
        } else {
            if(passwordEncoder.matches(request.getToken(), auth.getToken())) {
                auth.setConfirmedAt(LocalDateTime.now());
                auth.setUpdatedAt(LocalDateTime.now());
                guestAuthRepository.save(auth);
                return new ApiResponse<>("Email confirmed", HttpStatus.OK);
            } else {
                throw new SharedException("Incorrect token");
            }
        }
    }

    @Override
    public GuestResponse response(SharedLink link, Guest guest) {
        GuestResponse response = new GuestResponse();
        response.setData(SharedMapper.INSTANCE.response(link));
        response.setProfile(SharedMapper.INSTANCE.response(guest));
        response.setProvider(SharedMapper.INSTANCE.response(link.getProvider()));
        response.getProvider().setName(link.getProvider().getFullName());
        response.setUser(SharedMapper.INSTANCE.response(link.getUser()));
        response.getUser().setName(link.getUser().getFullName());

        if(!link.getPricing().isEmpty()) {
            response.setPricing(
                    link.getPricing()
                            .stream()
                            .sorted(Comparator.comparing(SharedPricing::getCreatedAt))
                            .filter(pricing -> pricing.getGuest().getId().equals(guest.getId()))
                            .map(pricing -> getSharedPricingData(link, pricing))
                            .toList()
            );
        }
        return response;
    }

    @Override
    public SharedPricingData getSharedPricingData(SharedLink link, SharedPricing pricing) {
        SharedPricingData data = SharedMapper.INSTANCE.data(pricing);
        data.setGuest(pricing.getAmount().subtract(pricing.getProvider().add(pricing.getUser())));
        data.setLabel(TimeUtil.formatDay(pricing.getCreatedAt()));
        data.setMore(
                MoneyUtil.getSharedInfo(
                        link.getProvider().getFullName(),
                        link.getUser().getFullName(),
                        link.getId(), pricing.getAmount(),
                        pricing.getUser(), pricing.getProvider()
                )
        );
        data.setName(pricing.getGuest().getFullName());
        data.setAvatar(pricing.getGuest().getAvatar());
        data.setJoinedAt(TimeUtil.formatDay(pricing.getGuest().getCreatedAt()));
        return data;
    }

    @Override
    public ApiResponse<GuestResponse> create(CreateGuestRequest request) {
        Optional<User> user = userRepository.findByEmailAddressIgnoreCase(request.getEmailAddress());

        if(user.isPresent()) {
            throw new SharedException("Email address exists as a user", ExceptionCodes.EXISTING_USER);
        } else {
            Optional<Guest> existing = guestRepository.findByEmailAddressIgnoreCase(request.getEmailAddress());

            if(existing.isPresent() && existing.get().getSharedLinks().stream().anyMatch(link -> request.getLink().equals(link.getLink()))) {
                throw new SharedException("Guest already exists", ExceptionCodes.GUEST_AND_LINK_EXIST_TOGETHER);
            } else if(existing.isPresent()) {
                throw new SharedException("Guest already exists", ExceptionCodes.EXISTING_LINK);
            } else {
                SharedLink link = sharedLinkRepository.findByLink(request.getLink())
                        .orElseThrow(() -> new SharedException("Link not found"));

                if(request.getAvatar().isEmpty()) {
                    throw new SharedException("A picture is needed to facilitate your trips. Upload one");
                }

                UploadRequest upload = new UploadRequest();
                upload.setFile(request.getAvatar());
                ApiResponse<String> response = storageService.upload(upload);
                if(response.getStatus().is2xxSuccessful()) {
                    Guest guest = getGuest(request, response.getData(), link);
                    link.getGuests().add(guest);
                    sharedLinkRepository.save(link);
                    return new ApiResponse<>(
                            "Guest profile created",
                            response(link, guest),
                            HttpStatus.CREATED
                    );
                } else {
                    return new ApiResponse<>(response.getMessage());
                }
            }
        }
    }

    @Override
    public ApiResponse<GuestResponse> login(VerifyEmailRequest request) {
        Guest guest = guestRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new SharedException("Guest not found"));
        GuestAuth auth = guestAuthRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new SharedException("Email address not found"));
        LocalDateTime current = LocalDateTime.now();
        if(auth.isEmailConfirmed() && (auth.getConfirmedAt().isAfter(current) && auth.getConfirmedAt().isBefore(current.plusHours(24)))) {
            SharedLink link = sharedLinkRepository.findByLink(request.getLink())
                    .orElseThrow(() -> new SharedException("Link not found"));
            if(link.getGuests().stream().anyMatch(linkGuest -> linkGuest.getId().equals(guest.getId()))) {
                return new ApiResponse<>(
                        "Guest profile created",
                        response(link, guest),
                        HttpStatus.CREATED
                );
            } else {
                throw new SharedException("Guest is not attached to link");
            }
        } else {
            throw new SharedException(
                    "Email confirmation has exceeded 24 hours. Confirm again",
                    ExceptionCodes.EMAIL_NOT_VERIFIED
            );
        }
    }

    private Guest getGuest(CreateGuestRequest request, String avatar, SharedLink link) {
        Guest guest = SharedMapper.INSTANCE.guest(request);
        guest.setAvatar(avatar);
        guest.getSharedLinks().add(link);
        return guestRepository.save(guest);
    }

    @Override
    public ApiResponse<GuestResponse> createFromExistingUser(CreateGuestRequest request) {
        User user = userRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new SharedException("User not found"));
        Profile profile = profileRepository.findById(user.getId())
                .orElseThrow(() -> new SharedException("User has not profile"));
        SharedLink link = sharedLinkRepository.findByLink(request.getLink())
                .orElseThrow(() -> new SharedException("Link not found"));

        if((profile.getAvatar() == null || profile.getAvatar().isEmpty()) && request.getAvatar().isEmpty()) {
            throw new SharedException("A picture is needed to facilitate your trips. Upload one");
        } else {
            String avatar;
            if(profile.getAvatar() != null && !profile.getAvatar().isEmpty()) {
                avatar = profile.getAvatar();
            } else {
                UploadRequest upload = new UploadRequest();
                upload.setFile(request.getAvatar());
                ApiResponse<String> response = storageService.upload(upload);
                if(response.getStatus().is2xxSuccessful()) {
                    avatar = response.getData();
                } else {
                    return new ApiResponse<>(response.getMessage());
                }
            }

            if(request.getFcmToken() == null || request.getFcmToken().isEmpty()) {
                request.setFcmToken(profile.getFcmToken());
            }
            Guest guest = getGuest(request, avatar, link);
            link.getGuests().add(guest);
            sharedLinkRepository.save(link);
            return new ApiResponse<>(
                    "Guest profile created",
                    response(link, guest),
                    HttpStatus.CREATED
            );
        }
    }

    @Override
    public ApiResponse<GuestResponse> addGuestAccountToLink(CreateGuestRequest request) {
        SharedLink link = sharedLinkRepository.findByLink(request.getLink())
                .orElseThrow(() -> new SharedException("Link not found"));
        Guest guest = guestRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new SharedException("Guest not found"));
        GuestAuth auth = guestAuthRepository.findByEmailAddressIgnoreCase(guest.getEmailAddress())
                .orElseThrow(() -> new SharedException("No verification requested"));

        if(auth.isEmailConfirmed()) {
            LocalDateTime current = LocalDateTime.now();
            if(auth.getConfirmedAt().isAfter(current) && auth.getConfirmedAt().isBefore(current.plusHours(24))) {
                link.getGuests().add(guest);
                sharedLinkRepository.save(link);
                guest.getSharedLinks().add(link);
                guestRepository.save(guest);
                return new ApiResponse<>(
                        "Guest added to the shared link successfully",
                        response(link, guest),
                        HttpStatus.OK
                );
            } else {
                throw new SharedException(
                        "Email confirmation has exceeded 24 hours. Confirm again",
                        ExceptionCodes.EMAIL_NOT_VERIFIED
                );
            }
        } else {
            throw new SharedException(
                    "Email is not verified",
                    ExceptionCodes.EMAIL_NOT_VERIFIED
            );
        }
    }
}

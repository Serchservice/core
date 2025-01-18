package com.serch.server.admin.services.auth;

import com.serch.server.admin.enums.ActivityMode;
import com.serch.server.admin.enums.UserAction;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.services.account.services.AdminActivityService;
import com.serch.server.admin.services.auth.requests.*;
import com.serch.server.admin.services.permission.services.PermissionService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.core.code.TokenService;
import com.serch.server.core.email.EmailService;
import com.serch.server.core.jwt.JwtService;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.email.EmailType;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.models.auth.User;
import com.serch.server.models.email.SendEmail;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.domains.auth.requests.RequestDevice;
import com.serch.server.domains.auth.requests.RequestLogin;
import com.serch.server.domains.auth.responses.AuthResponse;
import com.serch.server.domains.auth.responses.MFADataResponse;
import com.serch.server.domains.auth.services.*;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @see ApiResponse
 * @see AuthResponse
 * @see AdminAuthService
 */
@Service
@RequiredArgsConstructor
public class AdminAuthImplementation implements AdminAuthService {
    private final AuthService authService;
    private final TokenService tokenService;
    private final JwtService jwtService;
    private final MFAService mfaService;
    private final AccountStatusTrackerService trackerService;
    private final EmailService emailService;
    private final AdminActivityService activityService;
    private final PermissionService permissionService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Value("${application.link.admin.invite}")
    private String ADMIN_INVITE_LINK;

    @Value("${application.link.admin.reset}")
    private String ADMIN_RESET_PASSWORD_LINK;

    @Value("${application.security.otp-expiration-time}")
    protected Integer OTP_EXPIRATION_TIME;

    @Value("${application.admin.super-email-address}")
    protected String SUPER_ADMIN_EMAIL_ADDRESS;

    @Value("${application.admin.default-password}")
    protected String ADMIN_DEFAULT_PASSWORD;

    boolean isValidEmail(String email) {
        Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@serchservice\\.com$");
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private String generateEmployeeId() {
        return "SEMP-" + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }

    private String getSecret(User user, UserAction mode, Admin admin) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("admin", admin.getId());
        claims.put("user", user.getId());
        claims.put("role", user.getRole());
        claims.put("action", mode);
        return jwtService.generateToken(claims, user.getEmailAddress());
    }

    private void sendToken(User user, UserAction mode, Admin admin) {
        if(mode == UserAction.INVITE) {
            String secret = getSecret(user, mode, admin);

            user.setSignInToken(passwordEncoder.encode(secret));
            user.setAction(mode);
            userRepository.save(user);

            SendEmail email = new SendEmail();
            email.setTo(user.getEmailAddress());
            email.setFirstName(user.getFirstName());
            email.setPrimary(admin.getUser().getFullName());
            email.setSecondary(admin.getPosition());
            email.setType(EmailType.ADMIN_INVITE);
            email.setContent(String.format("%s?invite=%s&role=%s", ADMIN_INVITE_LINK, secret, user.getRole()));
            emailService.send(email);
        } else if(mode == UserAction.PASSWORD_RESET) {
            String secret = getSecret(user, mode, admin);

            user.setPasswordRecoveryToken(passwordEncoder.encode(secret));
            user.setPasswordRecoveryExpiresAt(TimeUtil.now().plusMinutes(OTP_EXPIRATION_TIME));
            user.setPasswordRecoveryConfirmedAt(null);
            user.setAction(mode);
            userRepository.save(user);

            SendEmail email = new SendEmail();
            email.setTo(user.getEmailAddress());
            email.setType(EmailType.ADMIN_RESET_PASSWORD);
            email.setFirstName(user.getFirstName());
            email.setContent(String.format("%s?invite=%s", ADMIN_RESET_PASSWORD_LINK, secret));
            emailService.send(email);
        } else {
            String otp = tokenService.generateOtp();
            user.setSignInToken(passwordEncoder.encode(otp));
            user.setAction(mode);
            user.setSignInTokenExpiresAt(TimeUtil.now().plusMinutes(OTP_EXPIRATION_TIME));
            userRepository.save(user);

            SendEmail email = new SendEmail();
            email.setContent(otp);
            email.setType(mode == UserAction.LOGIN ? EmailType.ADMIN_LOGIN : EmailType.ADMIN_SIGNUP);
            email.setTo(user.getEmailAddress());
            email.setFirstName(user.getFirstName());
            emailService.send(email);
        }
    }

    @Override
    @Transactional
    public ApiResponse<String> login(AdminLoginRequest request) {
        if(isValidEmail(request.getEmailAddress())) {
            User user = userRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            user.check();
            if(user.isAdmin()) {
                if(request.getPassword().equalsIgnoreCase(ADMIN_DEFAULT_PASSWORD)) {
                    throw new AuthException("Check your email address for invite link to finish account setup");
                } else {
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            request.getEmailAddress(),
                            request.getPassword()
                    );
                    authenticationManager.authenticate(token);
                    sendToken(user, UserAction.LOGIN, null);
                    return new ApiResponse<>("Finish your login with the token sent to your email address", HttpStatus.OK);
                }
            } else {
                throw new AuthException("This email does not belong to a Serch Admin", ExceptionCodes.ACCESS_DENIED);
            }
        } else {
            throw new AuthException("Invalid user details");
        }
    }

    @Override
    @Transactional
    public ApiResponse<MFADataResponse> signup(AdminSignupRequest request) {
        if(isValidEmail(request.getEmailAddress())) {
            if(isSuperAdminEmail(request.getEmailAddress())) {
                Optional<User> existing = userRepository.findByEmailAddressIgnoreCase(request.getEmailAddress());
                if(existing.isPresent()) {
                    if(!adminRepository.existsById(existing.get().getId())) {
                        sendToken(existing.get(), UserAction.SUPER_SIGNUP, null);

                        return new ApiResponse<>(
                                "Finish your signup with the token sent to your email address",
                                mfaService.getMFAData(existing.get()),
                                HttpStatus.CREATED
                        );
                    }
                    throw new AuthException("This email already exists");
                } else {
                    if(HelperUtil.validatePassword(request.getPassword())) {
                        User user = createSuperUser(request);
                        trackerService.create(user);
                        sendToken(user, UserAction.SUPER_SIGNUP, null);

                        return new ApiResponse<>(
                                "Finish your signup with the token sent to your email address",
                                mfaService.getMFAData(user),
                                HttpStatus.CREATED
                        );
                    } else {
                        throw new AuthException("Password must contain a lowercase, uppercase, special character and number");
                    }
                }
            } else {
                throw new AuthException("Access denied", ExceptionCodes.ACCESS_DENIED);
            }
        } else {
            throw new AuthException("Invalid email domain. Only company domain is permitted");
        }
    }

    private User createSuperUser(AdminSignupRequest request) {
        User user = new User();
        user.setEmailAddress(request.getEmailAddress());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.SUPER_ADMIN);
        user.setEmailConfirmedAt(TimeUtil.now());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        return userRepository.save(user);
    }

    private boolean isSuperAdminEmail(String emailAddress) {
        return emailAddress.equalsIgnoreCase(SUPER_ADMIN_EMAIL_ADDRESS);
    }

    @Override
    @Transactional
    public ApiResponse<String> add(AddAdminRequest request) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));

        if(request.getRole() == Role.SUPER_ADMIN) {
            throw new AuthException("Role cannot be assigned");
        } else {
            if(isValidEmail(request.getEmailAddress())) {
                Optional<User> existing = userRepository.findByEmailAddressIgnoreCase(request.getEmailAddress());
                if(existing.isPresent()) {
                    throw new AuthException("This email already exists");
                } else {
                    User user = getAdminUser(request);
                    trackerService.create(user);

                    Admin team = createTeam(request, admin, user);
                    if(request.getScopes() != null && !request.getScopes().isEmpty()) {
                        permissionService.create(request.getScopes(), team);
                    } else {
                        permissionService.attach(admin, team);
                    }

                    activityService.create(ActivityMode.TEAM_ADD, team.getPass(), null, admin);
                    sendToken(user, UserAction.INVITE, admin);
                    return new ApiResponse<>("An invite link has been sent to the email address", HttpStatus.CREATED);
                }
            } else {
                throw new AuthException("Invalid email domain. Only company domain is permitted");
            }
        }
    }

    private Admin createTeam(AddAdminRequest request, Admin admin, User user) {
        Admin team = new Admin();
        team.setAdmin(admin);
        team.setUser(user);
        team.setPosition(request.getPosition());
        team.setPass(generateEmployeeId());
        team.setDepartment(request.getDepartment());
        return adminRepository.save(team);
    }

    private User getAdminUser(AddAdminRequest request) {
        User user = new User();
        user.setEmailAddress(request.getEmailAddress());
        user.setPassword(passwordEncoder.encode(ADMIN_DEFAULT_PASSWORD));
        user.setRole(request.getRole());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setStatus(AccountStatus.SUSPENDED);
        user.setEmailConfirmedAt(TimeUtil.now());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public ApiResponse<AuthResponse> finishSignup(FinishAdminSetupRequest request) {
        UUID userId = UUID.fromString(jwtService.getItemFromToken(request.getSecret(), "user"));
        User user = userRepository.findById(userId).orElseThrow(() -> new AuthException("User not found"));
        if(isSecretValid(request.getSecret())) {
            if(request.getPassword().equalsIgnoreCase(ADMIN_DEFAULT_PASSWORD)) {
                throw new AuthException("Password not accepted since it is not unique");
            }
            if(user.getAction() != UserAction.INVITE) {
                throw new AuthException("Cannot proceed with this action. Invite accepted");
            }
            if(HelperUtil.validatePassword(request.getPassword())) {
                user.setUpdatedAt(TimeUtil.now());
                user.setSignInToken(null);
                user.setAction(UserAction.LOGIN);
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setSignInTokenConfirmedAt(TimeUtil.now());
                user.setStatus(AccountStatus.ACTIVE);
                userRepository.save(user);

                Admin admin = adminRepository.findById(user.getId()).orElseThrow(() -> new AuthException("Admin not found"));
                activityService.create(ActivityMode.INVITE_ACCEPT, admin.getAdmin().getPass(), null, admin);

                RequestLogin login = new RequestLogin();
                login.setDevice(request.getDevice());
                login.setState(request.getState());
                login.setCountry(request.getCountry());
                return authService.getAuthResponse(login, user);
            } else {
                throw new AuthException("Password must contain a lowercase, uppercase, special character and number");
            }
        } else {
            throw new AuthException("Access denied. Couldn't verify request");
        }
    }

    private boolean isSecretValid(String secret) {
        UUID adminId = UUID.fromString(jwtService.getItemFromToken(secret, "admin"));
        UUID userId = UUID.fromString(jwtService.getItemFromToken(secret, "user"));
        Role role = Role.valueOf(jwtService.getItemFromToken(secret, "role"));
        UserAction action = UserAction.valueOf(jwtService.getItemFromToken(secret, "action"));

        User user = userRepository.findById(userId).orElseThrow(() -> new AuthException("User not found"));
        return jwtService.isTokenIssuedBySerch(secret) && adminRepository.existsById(adminId)
                && userRepository.existsById(userId) && user.getRole() == role && user.getAction() == action
                && passwordEncoder.matches(secret, user.getSignInToken());
    }

    @Override
    @Transactional
    public ApiResponse<String> resend(String emailAddress) {
        User user = userRepository.findByEmailAddressIgnoreCase(emailAddress)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        sendToken(user, user.getAction(), null);
        return new ApiResponse<>("Token sent. Check your email address", HttpStatus.OK);
    }

    @Override
    @Transactional
    public ApiResponse<MFADataResponse> verifyLink(String secret) {
        if(isSecretValid(secret)) {
            UUID userId = UUID.fromString(jwtService.getItemFromToken(secret, "user"));
            User user = userRepository.findById(userId).orElseThrow(() -> new AuthException("User not found"));
            return new ApiResponse<>(
                    user.getFirstName(),
                    mfaService.getMFAData(user),
                    HttpStatus.OK
            );
        } else {
            throw new AuthException("Access denied. Invalid link token");
        }
    }

    @Override
    @Transactional
    public ApiResponse<AuthResponse> confirm(AdminAuthTokenRequest request) {
        User user = userRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(passwordEncoder.matches(request.getToken(), user.getSignInToken())) {
            if(request.getAction() == user.getAction()) {
                if(TimeUtil.isOtpExpired(user.getSignInTokenExpiresAt(), user.getTimezone(), OTP_EXPIRATION_TIME)) {
                    throw new AuthException("OTP already expired");
                } else {
                    if(user.getAction() == UserAction.SUPER_SIGNUP && user.getRole() != Role.SUPER_ADMIN) {
                        throw new AuthException("Access denied");
                    }

                    Admin admin;
                    if(user.getAction() == UserAction.SUPER_SIGNUP) {
                        admin = createSuper(user);
                        activityService.create(ActivityMode.SUPER_CREATE, null, null, admin);
                    } else {
                        admin = adminRepository.findById(user.getId()).orElseThrow(() -> new AuthException("Admin not found"));
                        activityService.create(ActivityMode.LOGIN, null, null, admin);
                    }

                    user.setUpdatedAt(TimeUtil.now());
                    user.setSignInToken(null);
                    user.setSignInTokenExpiresAt(null);
                    user.setSignInTokenConfirmedAt(TimeUtil.now());
                    userRepository.save(user);

                    return getAuthResponse(user, admin, request.getDevice(), request.getState(), request.getCountry(), false);
                }
            } else {
                throw new AuthException("Incorrect request details");
            }
        } else {
            throw new AuthException("Incorrect user token");
        }
    }

    private Admin createSuper(User user) {
        Optional<Admin> existingSuper = adminRepository.findByUser_EmailAddressIgnoreCase(user.getEmailAddress());
        if(existingSuper.isPresent()) {
            return existingSuper.get();
        } else {
            Admin admin = new Admin();
            admin.setUser(user);
            admin.setPass(generateEmployeeId());
            admin.setPosition("Chief Executive Officer");
            admin.setDepartment("Office of the CEO");
            return adminRepository.save(admin);
        }
    }

    @Override
    @Transactional
    public ApiResponse<String> resendInvite(UUID id) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        Admin user = adminRepository.findById(id).orElseThrow(() -> new AuthException("Admin not found"));
        if(admin.getUser().getRole() == Role.SUPER_ADMIN || admin.getId().equals(user.getAdmin().getId())) {
            sendToken(user.getUser(), UserAction.INVITE, admin);
            return new ApiResponse<>("Invite sent. Let the team member check inbox", HttpStatus.OK);
        } else {
            throw new AuthException("Access denied. Only the super admin or admin who created the team member can invite this email");
        }
    }

    @Override
    @Transactional
    public ApiResponse<String> resetPassword(UUID id) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("Admin not found"));
        Admin user = adminRepository.findById(id).orElseThrow(() -> new AuthException("Admin not found"));
        if(admin.getUser().getRole() == Role.SUPER_ADMIN || admin.getId().equals(user.getAdmin().getId())) {
            sendToken(user.getUser(), UserAction.PASSWORD_RESET, admin);
            user.setPasswordResetBy(admin);
            activityService.create(ActivityMode.PASSWORD_CHANGE_REQUEST, user.getPass(), null, admin);
            return new ApiResponse<>("Reset Password link sent. Let the team member check inbox", HttpStatus.OK);
        } else {
            throw new AuthException("Access denied. Only the super admin or admin who created the team member can perform this action");
        }
    }

    @Override
    @Transactional
    public ApiResponse<String> verifyResetLink(String token) {
        if(isSecretValid(token)) {
            UUID userId = UUID.fromString(jwtService.getItemFromToken(token, "user"));
            User user = userRepository.findById(userId).orElseThrow(() -> new AuthException("User not found"));
            return new ApiResponse<>(user.getFirstName(), user.getEmailAddress(), HttpStatus.OK);
        } else {
            throw new AuthException("Access denied. Invalid link token");
        }
    }

    @Override
    @Transactional
    public ApiResponse<AuthResponse> resetPassword(AdminResetPasswordRequest request) {
        UUID userId = UUID.fromString(jwtService.getItemFromToken(request.getToken(), "user"));
        User user = userRepository.findById(userId).orElseThrow(() -> new AuthException("User not found"));
        if(isSecretValid(request.getToken())) {
            if(request.getPassword().equalsIgnoreCase(ADMIN_DEFAULT_PASSWORD)) {
                throw new AuthException("Password not accepted since it is not unique");
            }
            if(user.getAction() != UserAction.PASSWORD_RESET) {
                throw new AuthException("Cannot proceed with this action. Action has changed");
            }
            if(HelperUtil.validatePassword(request.getPassword())) {
                user.setUpdatedAt(TimeUtil.now());
                user.setPasswordRecoveryToken(null);
                user.setPasswordRecoveryExpiresAt(null);
                user.setPasswordRecoveryConfirmedAt(TimeUtil.now());
                user.setAction(UserAction.LOGIN);
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                userRepository.save(user);

                Admin admin = adminRepository.findById(user.getId()).orElseThrow(() -> new AuthException("Admin not found"));
                activityService.create(ActivityMode.PASSWORD_CHANGE, admin.getPasswordResetBy().getPass(), null, admin);

                return getAuthResponse(user, admin, request.getDevice(), request.getState(), request.getCountry(), true);
            } else {
                throw new AuthException("Password must contain a lowercase, uppercase, special character and number");
            }
        } else {
            throw new AuthException("Access denied. Couldn't verify request");
        }
    }

    private ApiResponse<AuthResponse> getAuthResponse(User user, Admin admin, RequestDevice device, String state, String country, boolean isReset) {
        RequestLogin login = new RequestLogin();
        login.setDevice(device);
        login.setState(state);
        login.setCountry(country);
        ApiResponse<AuthResponse> response = authService.getAuthResponse(login, user);
        if(response.getStatus().is2xxSuccessful()) {
            return new ApiResponse<>(
                    isReset ? "Password reset successful" : user.hasMFA()
                            ? "Authentication successful"
                            : admin.mfaEnforced() ? "Two-factor authentication is needed to continue request"
                            : "Setup two-factor authentication",
                    response.getData(),
                    admin.mfaEnforced() || !user.hasMFA() ? HttpStatus.CREATED : HttpStatus.OK
            );
        } else {
            return response;
        }
    }
}

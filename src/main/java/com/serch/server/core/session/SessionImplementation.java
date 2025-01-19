package com.serch.server.core.session;

import com.serch.server.admin.models.Admin;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.auth.AuthLevel;
import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.exceptions.auth.SessionException;
import com.serch.server.mappers.AuthMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.RefreshToken;
import com.serch.server.models.auth.Session;
import com.serch.server.models.auth.User;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.RefreshTokenRepository;
import com.serch.server.repositories.auth.SessionRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.domains.auth.requests.RequestSession;
import com.serch.server.domains.auth.requests.RequestSessionToken;
import com.serch.server.domains.auth.responses.AuthResponse;
import com.serch.server.domains.auth.responses.SessionResponse;
import com.serch.server.core.token.JwtService;
import com.serch.server.core.token.TokenService;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service responsible for managing user sessions.
 * It implements its wrapper class {@link SessionService}
 *
 * @see SessionRepository
 * @see JwtService
 * @see RefreshTokenRepository
 * @see TokenService
 * @see UserRepository
 */
@Service
@RequiredArgsConstructor
public class SessionImplementation implements SessionService {
    private final SessionRepository sessionRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final AdminRepository adminRepository;

    @Override
    public void revokeRefreshToken(UUID userId, UUID refreshTokenId) {
        refreshTokenRepository.findByIdAndUser_Id(refreshTokenId, userId).ifPresent(refreshToken -> {
            if(!refreshToken.getRevoked()) {
                refreshToken.setRevoked(true);
                refreshToken.setUpdatedAt(TimeUtil.now());
                refreshTokenRepository.save(refreshToken);
            }
        });
    }

    @Override
    public void revokeSession(UUID userId, UUID sessionId) {
        sessionRepository.findByIdAndUser_Id(sessionId, userId).ifPresent(session -> {
            if(!session.getRevoked()) {
                session.setRevoked(true);
                session.setUpdatedAt(TimeUtil.now());
                sessionRepository.save(session);
            }
        });
    }

    @Override
    public ApiResponse<AuthResponse> generateSession(RequestSession request) {
        request.getUser().check();
        revokeSameDeviceOrIpAddress(request.getUser().getId(), request.getDevice().getIpAddress(), request.getDevice().getName());

        String token = tokenService.generateRefreshToken();

        return new ApiResponse<>("Successful authentication", auth(request, getNewSessionToken(request, token), token), HttpStatus.CREATED);
    }

    private void revokeSameDeviceOrIpAddress(UUID user, String ipAddress, String name) {
        var sessions = sessionRepository.findByUser_IdAndIpAddressOrName(user, ipAddress, name);
        if(sessions != null && !sessions.isEmpty()) {
            sessions.forEach(session -> revokeSession(user, session.getId()));
            sessions.stream().flatMap(session -> session.getRefreshTokens().stream()).forEach(refreshToken -> revokeRefreshToken(user, refreshToken.getId()));
        }
    }

    private String getNewSessionToken(RequestSession request, String token) {
        Session session = sessionRepository.save(getNewSession(request));
        RefreshToken refreshToken = refreshTokenRepository.save(getNewRefreshToken(request, session, token));

        RequestSessionToken sessionToken = new RequestSessionToken();
        sessionToken.setSessionId(session.getId());
        sessionToken.setRole(request.getUser().getRole().name());
        sessionToken.setEmailAddress(request.getUser().getEmailAddress());
        sessionToken.setId(request.getUser().getId());
        sessionToken.setRefreshId(refreshToken.getId());

        return jwtService.generateToken(sessionToken);
    }

    private Session getNewSession(RequestSession request) {
        Session session = AuthMapper.INSTANCE.session(request.getDevice());
        session.setUser(request.getUser());
        session.setMethod(request.getMethod());

        if(request.getMethod() == AuthMethod.MFA) {
            session.setAuthLevel(AuthLevel.LEVEL_2);
        } else {
            session.setAuthLevel(AuthLevel.LEVEL_1);
        }

        return session;
    }

    private RefreshToken getNewRefreshToken(RequestSession request, Session session, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setSession(session);
        refreshToken.setUser(request.getUser());
        refreshToken.setToken(token);

        return refreshToken;
    }

    private AuthResponse auth(RequestSession request, String accessToken, String token) {
        SerchCategory category = getUserCategory(request);

        return AuthResponse.builder()
                .mfaEnabled(request.getUser().getMfaEnabled())
                .session(new SessionResponse(accessToken, token))
                .firstName(getUserFirstName(request))
                .lastName(getUserLastName(request))
                .role(request.getUser().getRole().name())
                .category(category.getType())
                .image(category.getImage())
                .rating(getUserRating(request))
                .avatar(getUserAvatar(request))
                .id(request.getUser().getId())
                .recoveryCodesEnabled(request.getUser().getRecoveryCodeEnabled())
                .build();
    }

    private String getUserLastName(RequestSession request) {
        return businessProfileRepository.findById(request.getUser().getId())
                .map(business -> "")
                .orElse(request.getUser().getLastName());
    }

    private String getUserFirstName(RequestSession request) {
        return businessProfileRepository.findById(request.getUser().getId())
                .map(BusinessProfile::getBusinessName)
                .orElse(request.getUser().getFirstName());
    }

    private Double getUserRating(RequestSession request) {
        return profileRepository.findById(request.getUser().getId())
                .map(Profile::getRating)
                .orElse(
                        businessProfileRepository.findById(request.getUser().getId())
                                .map(BusinessProfile::getRating)
                                .orElse(5.0)
                );
    }

    private String getUserAvatar(RequestSession request) {
        return profileRepository.findById(request.getUser().getId())
                .map(Profile::getAvatar)
                .orElse(
                        businessProfileRepository.findById(request.getUser().getId())
                                .map(BusinessProfile::getBusinessLogo)
                                .orElse(
                                        adminRepository.findById(request.getUser().getId())
                                                .map(Admin::getAvatar)
                                                .orElse("")
                                )
                );
    }

    private SerchCategory getUserCategory(RequestSession request) {
        return profileRepository.findById(request.getUser().getId())
                .map(Profile::getCategory)
                .orElse(
                        businessProfileRepository.findById(request.getUser().getId())
                                .map(BusinessProfile::getCategory)
                                .orElse(SerchCategory.USER)
                );
    }

    @Override
    public ApiResponse<SessionResponse> refreshSession(String token) {
        try {
            var userId = UUID.fromString(jwtService.getItemFromToken(token, "serch_id"));
            var sessionId = UUID.fromString(jwtService.getItemFromToken(token, "session_id"));
            var refreshId = UUID.fromString(jwtService.getItemFromToken(token, "refresh_id"));

            var session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new SessionException("Invalid session id", ExceptionCodes.INCORRECT_TOKEN));
            var refreshToken = refreshTokenRepository.findById(refreshId)
                    .orElseThrow(() -> new SessionException("Invalid refresh token id", ExceptionCodes.INCORRECT_TOKEN));
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new SessionException("User not found", ExceptionCodes.USER_NOT_FOUND))
                    .check();

            if(refreshToken.getRevoked()) {
                throw new SessionException("Invalid refresh token. Please login", ExceptionCodes.INCORRECT_TOKEN);
            } else {
                revokeSession(userId, sessionId);
                revokeRefreshToken(userId, refreshId);
                String newToken = tokenService.generateRefreshToken();
                String newAccessToken = getNewAccessToken(session, user, newToken, refreshToken);

                return new ApiResponse<>(new SessionResponse(newAccessToken, newToken));
            }
        } catch (IllegalArgumentException e) {
            throw new SessionException("Invalid session. Please login", ExceptionCodes.IMPROPER_USER_ID_FORMAT);
        }
    }

    private String getNewAccessToken(Session session, User user, String newToken, RefreshToken refreshToken) {
        RefreshToken newRefreshToken = getNewRefreshToken(session, user, newToken, refreshToken);

        RequestSessionToken sessionToken = new RequestSessionToken();
        sessionToken.setSessionId(session.getId());
        sessionToken.setRole(user.getRole().name());
        sessionToken.setEmailAddress(user.getEmailAddress());
        sessionToken.setId(user.getId());
        sessionToken.setRefreshId(newRefreshToken.getId());

        return jwtService.generateToken(sessionToken);
    }

    private RefreshToken getNewRefreshToken(Session session, User user, String newToken, RefreshToken refreshToken) {
        RefreshToken token = new RefreshToken();
        token.setSession(session);
        token.setUser(user);
        token.setToken(newToken);
        token.setParent(refreshToken);

        return refreshTokenRepository.save(token);
    }

    @Override
    public ApiResponse<String> validateSession(String token, String state, String country) {
        try {
            if(jwtService.isTokenExpired(token)) {
                throw new SessionException("Your session has expired. Please login", ExceptionCodes.EXPIRED_SESSION);
            }

            String email = jwtService.getEmailFromToken(token);
            try {
                var user = userRepository.findByEmailAddressIgnoreCase(email)
                        .orElseThrow(() -> new SessionException("Invalid token"))
                        .check();
                var session = sessionRepository.findById(UUID.fromString(jwtService.getItemFromToken(token, "session_id")))
                        .orElseThrow(() -> new SessionException("Invalid token"));
                var refreshToken = refreshTokenRepository.findById(UUID.fromString(jwtService.getItemFromToken(token, "refresh_id")))
                        .orElseThrow(() -> new SessionException("Invalid token"));

                if(user.getId().equals(UUID.fromString(jwtService.getItemFromToken(token, "serch_id"))) && jwtService.isTokenIssuedBySerch(token) && (!session.getRevoked() && !refreshToken.getRevoked())) {
                    if(user.isAdmin()) {
                        Admin admin = adminRepository.findById(user.getId()).orElseThrow(() -> new AuthException("Admin not found"));
                        if(admin.mfaEnforced()) {
                            throw new SessionException("Two-factor authentication is required", ExceptionCodes.MFA_COMPULSORY);
                        }
                    }

                    user.setLastSignedIn(TimeUtil.now());
                    if(state != null) {
                        user.setState(state);
                    }
                    if(country != null) {
                        user.setCountry(country);
                    }

                    userRepository.save(user);
                    return new ApiResponse<>("Token is valid", email, HttpStatus.OK);
                } else {
                    return new ApiResponse<>("Invalid token");
                }
            } catch (IllegalArgumentException e) {
                throw new SessionException("Invalid session. Please login", ExceptionCodes.IMPROPER_USER_ID_FORMAT);
            }
        } catch (ExpiredJwtException e) {
            return new ApiResponse<>("Token has expired. Please login");
        } catch (MalformedJwtException e) {
            return new ApiResponse<>("Invalid token. Please login to continue");
        } catch (Exception e) {
            return new ApiResponse<>("Invalid token. Please verify your token or login again");
        }
    }

    @Override
    public void signOut(String jwt) {
        try {
            var user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                    .orElseThrow(() -> new AuthException("User not found", ExceptionCodes.USER_NOT_FOUND));

            revokeSession(user.getId(), UUID.fromString(jwtService.getItemFromToken(jwt, "session_id")));
            revokeRefreshToken(user.getId(), UUID.fromString(jwtService.getItemFromToken(jwt, "refresh_id")));
        } catch (IllegalArgumentException e) {
            throw new SessionException("Invalid session. Please login", ExceptionCodes.IMPROPER_USER_ID_FORMAT);
        }
    }

    @Override
    public void updateLastSignedIn() {
        userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser()).ifPresent(user -> {
            user.setLastSignedIn(TimeUtil.now());
            userRepository.save(user);
        });
    }

    @Override
    public void updateSessionDetails(String ipAddress, String token) {
        try {
            sessionRepository.findById(UUID.fromString(jwtService.getItemFromToken(token, "session_id")))
                    .ifPresent(session -> {
                        session.setIpAddress(ipAddress);
                        session.setUpdatedAt(TimeUtil.now());
                        sessionRepository.save(session);
                    });
        } catch (Exception ignored) {}
    }
}

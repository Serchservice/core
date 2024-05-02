package com.serch.server.services.auth.services.implementations;

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
import com.serch.server.models.business.BusinessProfile;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.RefreshTokenRepository;
import com.serch.server.repositories.auth.SessionRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.business.BusinessProfileRepository;
import com.serch.server.services.auth.requests.RequestSession;
import com.serch.server.services.auth.requests.RequestSessionToken;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.responses.SessionResponse;
import com.serch.server.services.auth.services.JwtService;
import com.serch.server.services.auth.services.SessionService;
import com.serch.server.services.auth.services.TokenService;
import com.serch.server.utils.UserUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Override
    public void revokeAllRefreshTokens(UUID userId) {
        var refreshTokens = refreshTokenRepository.findByUser_Id(userId);
        if(!refreshTokens.isEmpty()) {
            refreshTokens.forEach(refreshToken -> {
                if(!refreshToken.getRevoked()) {
                    refreshToken.setRevoked(true);
                    refreshToken.setUpdatedAt(LocalDateTime.now());
                    refreshTokenRepository.save(refreshToken);
                }
            });
        }
    }

    @Override
    public void revokeAllSessions(UUID userId) {
        var sessions = sessionRepository.findByUser_Id(userId);
        if(!sessions.isEmpty()) {
            sessions.forEach(session -> {
                if(!session.getRevoked()) {
                    session.setRevoked(true);
                    session.setUpdatedAt(LocalDateTime.now());
                    sessionRepository.save(session);
                }
            });
        }
    }

    private static Session getSession(RequestSession request) {
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

    private static RefreshToken getRefreshToken(RequestSession request, Session session, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setSession(session);
        refreshToken.setUser(request.getUser());
        refreshToken.setToken(token);
        return refreshToken;
    }

    @Override
    public ApiResponse<AuthResponse> generateSession(RequestSession request) {
        request.getUser().check();
        revokeAllSessions(request.getUser().getId());
        revokeAllRefreshTokens(request.getUser().getId());

        String token = tokenService.generateRefreshToken();
        String accessToken = getAccessToken(request, token);
        SerchCategory category = profileRepository.findById(request.getUser().getId())
                .map(Profile::getCategory)
                .orElse(
                        businessProfileRepository.findById(request.getUser().getId())
                                .map(BusinessProfile::getCategory)
                                .orElse(SerchCategory.USER)
                );
        String avatar = profileRepository.findById(request.getUser().getId())
                .map(Profile::getAvatar)
                .orElse(
                        businessProfileRepository.findById(request.getUser().getId())
                                .map(BusinessProfile::getAvatar)
                                .orElse("")
                );
        Double rating = profileRepository.findById(request.getUser().getId())
                .map(Profile::getRating)
                .orElse(
                        businessProfileRepository.findById(request.getUser().getId())
                                .map(BusinessProfile::getRating)
                                .orElse(5.0)
                );
        return new ApiResponse<>(
                "Successful",
                AuthResponse.builder()
                        .mfaEnabled(request.getUser().getMfaEnabled())
                        .session(new SessionResponse(accessToken, token))
                        .firstName(request.getUser().getFirstName())
                        .lastName(request.getUser().getLastName())
                        .role(request.getUser().getRole().name())
                        .category(category.getType())
                        .image(category.getImage())
                        .rating(rating)
                        .avatar(avatar)
                        .recoveryCodesEnabled(request.getUser().getRecoveryCodeEnabled())
                        .build(),
                HttpStatus.CREATED
        );
    }

    private String getAccessToken(RequestSession request, String token) {
        Session session = sessionRepository.save(getSession(request));
        RefreshToken refreshToken = refreshTokenRepository.save(getRefreshToken(request, session, token));

        RequestSessionToken sessionToken = new RequestSessionToken();
        sessionToken.setSessionId(session.getId());
        sessionToken.setRole(request.getUser().getRole().name());
        sessionToken.setEmailAddress(request.getUser().getEmailAddress());
        sessionToken.setId(request.getUser().getId());
        sessionToken.setRefreshId(refreshToken.getId());

        return jwtService.generateToken(sessionToken);
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
                revokeAllSessions(userId);
                revokeAllRefreshTokens(userId);
                String newToken = tokenService.generateRefreshToken();
                String newAccessToken = getNewAccessToken(session, user, newToken, refreshToken);
                return new ApiResponse<>(new SessionResponse(newAccessToken, newToken));
            }
        } catch (IllegalArgumentException e) {
            throw new SessionException(
                    "Invalid session. Please login",
                    ExceptionCodes.IMPROPER_USER_ID_FORMAT
            );
        }
    }

    private String getNewAccessToken(Session session, User user, String newToken, RefreshToken refreshToken) {
        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setSession(session);
        newRefreshToken.setUser(user);
        newRefreshToken.setToken(newToken);
        newRefreshToken.setParent(refreshToken);
        refreshTokenRepository.save(newRefreshToken);

        RequestSessionToken sessionToken = new RequestSessionToken();
        sessionToken.setSessionId(session.getId());
        sessionToken.setRole(user.getRole().name());
        sessionToken.setEmailAddress(user.getEmailAddress());
        sessionToken.setId(user.getId());
        sessionToken.setRefreshId(newRefreshToken.getId());

        return jwtService.generateToken(sessionToken);
    }

    @Override
    public ApiResponse<String> validateSession(String token) {
        try {
            if(jwtService.isTokenExpired(token)) {
                throw new SessionException(
                        "Your session has expired. Please login",
                        ExceptionCodes.EXPIRED_SESSION
                );
            }
            String email = jwtService.getEmailFromToken(token);
            try {
                var userId = UUID.fromString(jwtService.getItemFromToken(token, "serch_id"));
                var sessionId = UUID.fromString(jwtService.getItemFromToken(token, "session_id"));
                var refreshId = UUID.fromString(jwtService.getItemFromToken(token, "refresh_id"));

                var user = userRepository.findByEmailAddressIgnoreCase(email)
                        .orElseThrow(() -> new SessionException("Invalid token"))
                        .check();
                var session = sessionRepository.findById(sessionId)
                        .orElseThrow(() -> new SessionException("Invalid token"));
                var refreshToken = refreshTokenRepository.findById(refreshId)
                        .orElseThrow(() -> new SessionException("Invalid token"));

                if(user.getId().equals(userId) && jwtService.isTokenIssuedBySerch(token) && (!session.getRevoked() && !refreshToken.getRevoked())) {
                    user.setLastSignedIn(LocalDateTime.now());
                    user.setUpdatedAt(LocalDateTime.now());
                    userRepository.save(user);
                    return new ApiResponse<>("Token is valid", email, HttpStatus.OK);
                } else {
                    return new ApiResponse<>("Invalid token");
                }
            } catch (IllegalArgumentException e) {
                throw new SessionException(
                        "Invalid session. Please login",
                        ExceptionCodes.IMPROPER_USER_ID_FORMAT
                );
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
    public User validate(String token) {
        return null;
    }

    @Override
    public void signOut() {
        var user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new AuthException("User not found", ExceptionCodes.USER_NOT_FOUND));
        revokeAllSessions(user.getId());
        revokeAllRefreshTokens(user.getId());
    }

    @Override
    public void updateLastSignedIn() {
        userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser()).ifPresent(user -> {
            user.setLastSignedIn(LocalDateTime.now());
            userRepository.save(user);
        });
    }
}

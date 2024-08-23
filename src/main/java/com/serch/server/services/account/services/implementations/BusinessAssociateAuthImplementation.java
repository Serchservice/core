package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.models.auth.Pending;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.auth.PendingRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.services.auth.requests.RequestSession;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.JwtService;
import com.serch.server.services.auth.services.SessionService;
import com.serch.server.services.auth.services.TokenService;
import com.serch.server.services.account.requests.ChangePasswordInviteRequest;
import com.serch.server.services.account.responses.VerifiedInviteResponse;
import com.serch.server.services.account.services.BusinessAssociateAuthService;
import com.serch.server.utils.DatabaseUtil;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusinessAssociateAuthImplementation implements BusinessAssociateAuthService {
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;
    private final BusinessProfileRepository businessProfileRepository;
    private final UserRepository userRepository;
    private final PendingRepository pendingRepository;

    @Override
    public ApiResponse<VerifiedInviteResponse> verifyLink(String token) {
        UUID businessId = UUID.fromString(jwtService.getItemFromToken(token, "business"));
        UUID userId = UUID.fromString(jwtService.getItemFromToken(token, "user"));
        Role role = Role.valueOf(jwtService.getItemFromToken(token, "role"));

        businessProfileRepository.findById(businessId)
                .orElseThrow(() -> new AccountException("Business not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AccountException("User not found"));

        Pending pending = pendingRepository.findByUser_Id(userId)
                .orElseThrow(() -> new AccountException("Account not found"));

        if(jwtService.isTokenIssuedBySerch(token) && role == Role.ASSOCIATE_PROVIDER) {
            String scope = tokenService.generate(30);
            pending.setScope(DatabaseUtil.encodeData(scope));
            pendingRepository.save(pending);

            VerifiedInviteResponse response = new VerifiedInviteResponse();
            response.setScope(scope);
            response.setName(user.getFirstName());
            response.setEmailAddress(user.getEmailAddress());
            return new ApiResponse<>(response);
        } else {
            throw new AccountException("Access denied. Incorrect or corrupt link");
        }
    }

    @Override
    public ApiResponse<AuthResponse> acceptInvite(ChangePasswordInviteRequest request) {
        Pending pending = pendingRepository.findByUser_EmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new AccountException("Account not found"));
        if(DatabaseUtil.decodeData(pending.getScope()).equals(request.getScope())) {
            if(HelperUtil.validatePassword(request.getPassword())) {
                pending.getUser().setPassword(passwordEncoder.encode(request.getPassword()));
                pending.getUser().setIsEmailConfirmed(true);
                pending.getUser().setStatus(AccountStatus.ACTIVE);
                pending.getUser().setLastSignedIn(LocalDateTime.now());
                pending.getUser().setCountry(request.getCountry());
                pending.getUser().setState(request.getState());
                userRepository.save(pending.getUser());
                RequestSession session = new RequestSession();
                session.setUser(pending.getUser());
                session.setMethod(AuthMethod.PASSWORD_CHANGE);
                session.setDevice(request.getDevice());

                ApiResponse<AuthResponse> response = sessionService.generateSession(session);
                if(response.getStatus().is2xxSuccessful()) {
                    pendingRepository.delete(pending);
                    return response;
                } else {
                    throw new AccountException(response.getMessage());
                }
            } else {
                throw new AuthException(
                        "Password must contain a lowercase, uppercase, special character and number",
                        ExceptionCodes.INCORRECT_TOKEN
                );
            }
        } else {
            throw new AccountException("Access denied. Cannot verify request");
        }
    }
}

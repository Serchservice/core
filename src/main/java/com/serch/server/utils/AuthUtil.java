package com.serch.server.utils;

import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shared.SharedLogin;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.repositories.go.GoUserRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.shared.SharedLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The UserUtil class provides utility methods related to user authentication and retrieval.
 */
@Service
@RequiredArgsConstructor
public class AuthUtil {
    private final UserRepository userRepository;
    private final SharedLoginRepository sharedLoginRepository;
    private final GoUserRepository goUserRepository;

    /**
     * Retrieves the username of the currently logged-in user from {@link SecurityContextHolder}.
     * @return The username of the logged-in user.
     */
    public static String getAuth() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    /**
     * Retrieves the user entity corresponding to the currently logged-in user.
     * @return The {@linkplain User} user entity.
     * @throws AuthException If the user is not found.
     */
    public User getUser() {
        return userRepository.findByEmailAddressIgnoreCase(getAuth())
                .orElseThrow(() -> new AuthException("User not found", ExceptionCodes.USER_NOT_FOUND));
    }

    public GoUser getGoUser() {
        return goUserRepository.findByEmailAddressIgnoreCase(getAuth())
                .orElseThrow(() -> new AuthException("User not found", ExceptionCodes.USER_NOT_FOUND));
    }

    public Optional<GoUser> getOptionalGoUser() {
        return goUserRepository.findByEmailAddressIgnoreCase(getAuth());
    }

    public String getLinkId() {
        try {
            return sharedLoginRepository.findById(Long.parseLong(getAuth()))
                    .map(SharedLogin::getSharedLink)
                    .map(SharedLink::getId)
                    .orElse("");
        } catch (Exception ignored) {
            return "";
        }
    }

    public SharedLogin getShared() {
        try {
            return sharedLoginRepository.findById(Long.parseLong(getAuth()))
                    .orElseThrow(() -> new AuthException("Guest not found", ExceptionCodes.USER_NOT_FOUND));
        } catch (Exception ignored) {
            throw new AuthException("Guest not found", ExceptionCodes.USER_NOT_FOUND);
        }
    }

    public Guest getGuest() {
        return getShared().getGuest();
    }

    public String getGuestId() {
        try {
            return sharedLoginRepository.findById(Long.parseLong(getAuth()))
                    .map(SharedLogin::getGuest)
                    .map(Guest::getId)
                    .orElse("");
        } catch (Exception ignored) {
            return "";
        }
    }
}

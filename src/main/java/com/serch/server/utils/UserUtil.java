package com.serch.server.utils;

import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * The UserUtil class provides utility methods related to user authentication and retrieval.
 */
@Service
@RequiredArgsConstructor
public class UserUtil {
    private final UserRepository userRepository;

    /**
     * Retrieves the username of the currently logged-in user.
     * @return The username of the logged-in user.
     */
    public static String getLoginUser () {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    /**
     * Retrieves the user entity corresponding to the currently logged-in user.
     * @return The user entity.
     * @throws AuthException If the user is not found.
     */
    public User getUser() {
        return userRepository.findByEmailAddressIgnoreCase(getLoginUser())
                .orElseThrow(() -> new AuthException("User not found", ExceptionCodes.USER_NOT_FOUND));
    }

    public static String getBucket(Role role) {
        if(role == null) {
            return "guest";
        } else if(role == Role.USER) {
            return "user";
        } else if(role == Role.BUSINESS) {
            return "business";
        } else {
            return "provider";
        }
    }
}

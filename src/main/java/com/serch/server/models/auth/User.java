package com.serch.server.models.auth;

import com.serch.server.admin.enums.UserAction;
import com.serch.server.bases.BaseEntity;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.models.account.AccountSetting;
import com.serch.server.models.auth.mfa.MFAFactor;
import com.serch.server.models.auth.verified.Verification;
import com.serch.server.models.referral.ReferralProgram;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.serch.server.enums.auth.Role.USER;

/**
 * The User class represents a user entity in the system.
 * It extends the BaseEntity class and implements the UserDetails interface provided by Spring Security.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link MFAFactor} - The multi-factor authentication factor associated with the user.</li>
 *     <li>{@link Session} - The sessions associated with the user.</li>
 * </ul>
 * Enums:
 * <ul>
 *     <li>{@link AccountStatus}</li>
 * </ul>
 *
 * @see BaseEntity
 * @see UserDetails
 * @see ReferralProgram
 */
@Getter
@Setter
@Entity(name = "users")
@Table(schema = "identity", name = "users")
public class User extends BaseEntity implements UserDetails {
    @Column(name = "email_address", unique = true, nullable = false, columnDefinition = "TEXT")
    @Email(
            message = "Email address must be properly formatted",
            regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    )
    private String emailAddress;

    @Column(name = "password", nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Password cannot be empty")
    private String password;

    @Column(name = "first_name", columnDefinition = "TEXT", nullable = false)
    private String firstName;

    @Column(name = "last_name", columnDefinition = "TEXT", nullable = false)
    private String lastName;

    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "Role cannot be null")
    private Role role;

    @Column(name = "last_signed_in", columnDefinition = "timestamptz")
    private ZonedDateTime lastSignedIn = ZonedDateTime.now(ZoneOffset.UTC);

    @Column(name = "last_updated_at", columnDefinition = "timestamptz")
    private ZonedDateTime lastUpdatedAt = ZonedDateTime.now(ZoneOffset.UTC);

    @Column(name = "profile_last_updated_at")
    private ZonedDateTime profileLastUpdatedAt;

    @Column(name = "email_confirmed_at", updatable = false, nullable = false, columnDefinition = "timestamptz")
    @NotNull(message = "Email Confirmation Date cannot be null")
    private ZonedDateTime emailConfirmedAt;

    @Column(name = "password_recovery_token", columnDefinition = "TEXT")
    private String passwordRecoveryToken = null;

    @Column(name = "password_recovery_token_expires_at", columnDefinition = "timestamptz")
    private ZonedDateTime passwordRecoveryExpiresAt = null;

    @Column(name = "password_recovery_token_confirmed_at", columnDefinition = "timestamptz")
    private ZonedDateTime passwordRecoveryConfirmedAt = null;

    @Enumerated(value = EnumType.STRING)
    private UserAction action;

    @Column(name = "sign_in_token", columnDefinition = "TEXT")
    private String signInToken = null;

    @Column(name = "sign_in_token_expires_at", columnDefinition = "timestamptz")
    private ZonedDateTime signInTokenExpiresAt = null;

    @Column(name = "sign_in_token_confirmed_at", columnDefinition = "timestamptz")
    private ZonedDateTime signInTokenConfirmedAt = null;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "enabled_mfa", nullable = false)
    private Boolean mfaEnabled = false;

    @Column(name = "enabled_recovery_code", nullable = false)
    private Boolean recoveryCodeEnabled = false;

    @Column(name = "is_email_confirmed", nullable = false)
    private Boolean isEmailConfirmed = true;

    @Column(columnDefinition = "TEXT")
    private String state;

    @Column(columnDefinition = "TEXT")
    private String country;

    @Column(columnDefinition = "TEXT default 'Africa/Lagos'")
    private String timezone = "Africa/Lagos";

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Session> sessions;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AccountStatusTracker> trackers;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private MFAFactor mfaFactor;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private ReferralProgram program;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private AccountSetting setting;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Verification verification;

    /**
     * Retrieves the authorities granted to the user.
     *
     * @return A collection of authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    /**
     * Retrieves the user's password.
     *
     * @return The user's password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Retrieves the user's username (email address).
     *
     * @return The user's email address
     */
    @Override
    public String getUsername() {
        return emailAddress;
    }

    /**
     * Checks if the user's account is not expired.
     *
     * @return true if the account is not expired, otherwise false
     */
    @Override
    public boolean isAccountNonExpired() {
        return status == AccountStatus.ACTIVE;
    }

    /**
     * Checks if the user's account is not locked.
     *
     * @return true if the account is not locked, otherwise false
     */
    @Override
    public boolean isAccountNonLocked() {
        return status == AccountStatus.ACTIVE;
    }

    /**
     * Checks if the user's credentials are not expired.
     *
     * @return true if the credentials are not expired, otherwise false
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Checks if the user's account is enabled.
     *
     * @return true if the account is enabled, otherwise false
     */
    @Override
    public boolean isEnabled() {
        return status == AccountStatus.ACTIVE;
    }

    /**
     * Checks if the user's profile is set and role is either {@link Role#USER}, {@link Role#ASSOCIATE_PROVIDER}
     * or {@link Role#PROVIDER}.
     *
     * @return true if the profile is set, otherwise false
     *
     * @see Role
     */
    public boolean isProfile() {
        return getRole() == USER || getRole() == Role.PROVIDER ||
                getRole() == Role.ASSOCIATE_PROVIDER;
    }

    /**
     * Checks if the user matches the provided ID.
     *
     * @param id The ID to check against
     * @return true if the user matches the ID, otherwise false
     */
    public boolean isUser(UUID id) {
        return getId().equals(id);
    }

    /**
     * Retrieves the user's full name.
     *
     * @return The user's full name
     */
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    /**
     * Checks if the account is deactivated by a business
     * @return True or False
     */
    public boolean isBusinessLocked() {
        return status == AccountStatus.BUSINESS_DEACTIVATED;
    }

    /**
     * Checks for the logged-in user's account status and validates its presence.
     *
     * @return User - Logged In User
     */
    public User check() {
        if(status == AccountStatus.DELETED) {
            throw new LockedException(
                    "Access is denied. Contact support if this is your account."
            );
        } else if(status == AccountStatus.BUSINESS_DELETED) {
            throw new LockedException(
                    "This account has been deleted. Let your business admin contact support for more."
            );
        } else if(isBusinessLocked()) {
            throw new DisabledException(
                    "This account is locked by your business administrator. Contact your business admin"
            );
        } else if(status == AccountStatus.DEACTIVATED) {
            throw new DisabledException(
                    "This account is deactivated. Contact support for more information"
            );
        } else if(status == AccountStatus.SUSPENDED) {
            throw new LockedException(
                    "This account is suspended at the moment. Contact support for more information"
            );
        } else if(status == AccountStatus.HAS_REPORTED_ISSUES) {
            throw new LockedException(
                    "This account has issues Serch is following up on. Contact support for more information"
            );
        } else {
            return this;
        }
    }

    /**
     * Checks if the user is a Serch admin, combining {@link Role#SUPER_ADMIN}
     * , {@link Role#ADMIN}, {@link Role#MANAGER} and {@link Role#TEAM}
     *
     * @return true or false
     */
    public boolean isAdmin() {
        return getRole() == Role.SUPER_ADMIN || getRole() == Role.ADMIN || getRole() == Role.MANAGER || getRole() == Role.TEAM;
    }

    /**
     * Checks if the user is a provider or associate provider.
     * Uses the {@link Role#PROVIDER} and {@link Role#ASSOCIATE_PROVIDER} to perform check
     *
     * @return boolean - True or false
     */
    public boolean isProvider() {
        return getRole() == Role.PROVIDER || getRole() == Role.ASSOCIATE_PROVIDER;
    }

    /**
     * Checks if the user is business.
     * Uses the {@link Role#BUSINESS} to perform check
     *
     * @return boolean - True or false
     */
    public boolean isBusiness() {
        return getRole() == Role.BUSINESS;
    }

    public boolean isUser() {
        return role == USER;
    }

    public boolean hasMFA() {
        return getMfaFactor() != null && getMfaEnabled();
    }
}

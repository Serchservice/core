package com.serch.server.models.auth;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseEntity;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.models.auth.mfa.MFAFactor;
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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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
    @SerchEnum(message = "Role must be an enum")
    private Role role;

    @Column(name = "last_signed_in")
    private LocalDateTime lastSignedIn = LocalDateTime.now();

    @Column(name = "last_seen")
    private LocalDateTime lastSeen = LocalDateTime.now();

    @Column(name = "email_confirmed_at", updatable = false, nullable = false)
    @NotNull(message = "Email Confirmation Date cannot be null")
    private LocalDateTime emailConfirmedAt;

    @Column(name = "password_recovery_token", columnDefinition = "TEXT")
    private String passwordRecoveryToken = null;

    @Column(name = "password_recovery_token_expires_at", columnDefinition = "TEXT")
    private LocalDateTime passwordRecoveryExpiresAt = null;

    @Column(name = "password_recovery_token_confirmed_at")
    private LocalDateTime passwordRecoveryConfirmedAt = null;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "AuthStatus must be an enum")
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Column(name = "enabled_mfa", nullable = false)
    private Boolean mfaEnabled = false;

    @Column(name = "enabled_recovery_code", nullable = false)
    private Boolean recoveryCodeEnabled = false;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Session> sessions;

    @OneToOne(mappedBy = "user")
    private MFAFactor mfaFactor;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return emailAddress;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountStatus == AccountStatus.ACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountStatus == AccountStatus.ACTIVE;
    }

    public boolean isBusinessLocked() {
        return accountStatus == AccountStatus.BUSINESS_DEACTIVATED;
    }

    public User check() {
        if(accountStatus == AccountStatus.DELETED) {
            throw new LockedException(
                    "Access is denied. Contact support if this is your account."
            );
        } else if(accountStatus == AccountStatus.BUSINESS_DELETED) {
            throw new LockedException(
                    "This account has been deleted. Let your business admin contact support for more."
            );
        } else if(accountStatus == AccountStatus.BUSINESS_DEACTIVATED) {
            throw new DisabledException(
                    "This account is locked by your business administrator. Contact your business admin"
            );
        } else if(accountStatus == AccountStatus.DEACTIVATED) {
            throw new DisabledException(
                    "This account is deactivated. Contact support for more information"
            );
        } else if(accountStatus == AccountStatus.SUSPENDED) {
            throw new LockedException(
                    "This account is suspended at the moment. Contact support for more information"
            );
        } else if(accountStatus == AccountStatus.HAS_REPORTED_ISSUES) {
            throw new LockedException(
                    "This account has issues Serch is following up on. Contact support for more information"
            );
        } else {
            return this;
        }
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return accountStatus == AccountStatus.ACTIVE;
    }

    public boolean isProfile() {
        return getRole() == Role.USER || getRole() == Role.PROVIDER ||
                getRole() == Role.ASSOCIATE_PROVIDER;
    }

    public boolean isUser(UUID id) {
        return getId() == id;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}

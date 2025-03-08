package com.serch.server.domains.nearby.models.go.user;

import com.serch.server.bases.BaseDevice;
import com.serch.server.domains.nearby.models.go.GoLocation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Table(schema = "nearby", name = "go_users")
public class GoUser extends BaseDevice implements UserDetails {
    @NotBlank(message = "First name cannot be empty or blank")
    @Column(name = "first_name", nullable = false, columnDefinition = "TEXT")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty or blank")
    @Column(name = "last_name", nullable = false, columnDefinition = "TEXT")
    private String lastName;

    @NotBlank(message = "Email address cannot be empty or blank")
    @Column(name = "email_address", nullable = false, columnDefinition = "TEXT", unique = true)
    private String emailAddress;

    @Column(nullable = false, columnDefinition = "TEXT DEFAULT ''")
    private String contact = "";

    @Column(nullable = false, name = "messaging_token", columnDefinition = "TEXT DEFAULT ''")
    private String messagingToken = "";

    @Column(nullable = false, columnDefinition = "TEXT DEFAULT ''")
    private String avatar = "";

    @NotBlank(message = "Password cannot be empty or blank")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String password;

    @Column(columnDefinition = "TEXT default 'Africa/Lagos'")
    private String timezone = "Africa/Lagos";

    @Column(nullable = false, name = "search_radius")
    private Double searchRadius = 5000.0;

    @Column(columnDefinition = "TEXT", name = "customer_payment_code")
    private String customerCode;

    @Column(nullable = false)
    private Integer trials = 0;

    @Column(name = "email_confirmation_token", columnDefinition = "TEXT")
    private String emailConfirmationToken = null;

    @Column(name = "email_confirmation_token_expires_at", columnDefinition = "timestamptz")
    private ZonedDateTime emailConfirmationTokenExpiresAt = null;

    @Column(name = "email_confirmed_at", columnDefinition = "timestamptz")
    private ZonedDateTime emailConfirmedAt;

    @Column(name = "password_recovery_token", columnDefinition = "TEXT")
    private String passwordRecoveryToken = null;

    @Column(name = "password_recovery_token_expires_at", columnDefinition = "timestamptz")
    private ZonedDateTime passwordRecoveryTokenExpiresAt = null;

    @Column(name = "password_recovery_token_confirmed_at", columnDefinition = "timestamptz")
    private ZonedDateTime passwordRecoveryTokenConfirmedAt = null;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "user", orphanRemoval = true)
    private GoLocation location;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<GoUserInterest> interests;

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public Boolean isEmailAddressVerified() {
        return getEmailConfirmedAt() != null;
    }

    /**
     * Retrieves the authorities granted to the user.
     *
     * @return A collection of authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
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

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
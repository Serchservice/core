package com.serch.server.models.shared;

import com.serch.server.bases.BaseEntity;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(schema = "providesharing", name = "guest_profiles")
public class Guest extends BaseEntity {
    @Column(name = "email_address", nullable = false, columnDefinition = "TEXT")
    @Email(
            message = "Email address must be properly formatted",
            regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    )
    private String emailAddress;

    @Column(name = "first_name", nullable = false, columnDefinition = "TEXT")
    @Size(min = 3, message = "First Name must be above 3 characters")
    @NotBlank(message = "First name cannot be empty or null")
    private String firstName;

    @Column(name = "last_name", nullable = false, columnDefinition = "TEXT")
    @Size(min = 3, message = "Last Name must be above 3 characters")
    @NotBlank(message = "Last name cannot be empty or null")
    private String lastName;

    @Column(name = "gender", nullable = false, columnDefinition = "TEXT")
    @Size(min = 3, message = "Gender must be above 3 characters")
    @NotBlank(message = "Gender cannot be empty or null")
    private String gender;

    @Column(name = "avatar", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Avatar cannot be empty or null")
    private String avatar;

    @Column(name = "messaging_token", columnDefinition = "TEXT")
    private String fcmToken = null;

    @Column(name = "auth_token", columnDefinition = "TEXT")
    private String token;

    @Column(name = "auth_token_expires_at")
    private LocalDateTime tokenExpiresAt = null;

    @Column(name = "auth_token_confirmed_at")
    private LocalDateTime tokenConfirmedAt = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "invited_by_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "invited_by_id_fkey")
    )
    private Profile invitedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "shared_link",
            referencedColumnName = "link",
            nullable = false,
            foreignKey = @ForeignKey(name = "sharing_link_fkey")
    )
    private SharedLink sharedLink;

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
    public boolean isTokenExpired() {
        return tokenExpiresAt != null && tokenExpiresAt.isBefore(LocalDateTime.now());
    }
    public boolean isEmailConfirmed() {
        return tokenConfirmedAt != null;
    }
}

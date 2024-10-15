package com.serch.server.models.auth.incomplete;

import com.serch.server.bases.BaseModel;
import com.serch.server.enums.auth.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * The Incomplete class represents incomplete registration information in the system.
 * It stores information about incomplete user registrations, including email, referral code, and token details.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link IncompleteProfile} - The profile associated with the incomplete registration.</li>
 *     <li>{@link IncompleteReferral} - The referral information associated with the incomplete registration.</li>
 *     <li>{@link IncompletePhoneInformation} - The phone information associated with the incomplete registration.</li>
 *     <li>{@link IncompleteCategory} - The category information associated with the incomplete registration.</li>
 *     <li>{@link IncompleteSpecialty} - The specialty information associated with the incomplete registration.</li>
 * </ul>
 * Constraints:
 * <ul>
 *     <li>{@link Email} - The field {@code emailAddress} must be properly formatted.</li>
 * </ul>
 * Methods:
 * <ul>
 *     <li>{@link Incomplete#isEmailConfirmed()} - Checks if the email is confirmed.</li>
 *     <li>{@link Incomplete#hasProfile()} - Checks if the registration has a profile associated with it.</li>
 *     <li>{@link Incomplete#hasCategory()} - Checks if the registration has a category associated with it.</li>
 * </ul>
 * @see BaseModel
 */
@Getter
@Setter
@Entity
@Table(name = "incomplete_registrations", schema = "identity")
public class Incomplete extends BaseModel {
    @Column(name = "email_address", unique = true, nullable = false, columnDefinition = "TEXT")
    @Email(
            message = "Email address must be properly formatted",
            regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    )
    private String emailAddress;

    @Column(name = "referral", columnDefinition = "TEXT")
    private String referral = null;

    @Column(name = "token", columnDefinition = "TEXT")
    private String token = null;

    @Column(nullable = false)
    private Integer trials = 0;

    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;

    @Column(name = "token_expires_at", columnDefinition = "timestamptz")
    private ZonedDateTime tokenExpiresAt = null;

    @Column(name = "token_confirmed_at", columnDefinition = "timestamptz")
    private ZonedDateTime tokenConfirmedAt = null;

    @OneToOne(mappedBy = "incomplete", cascade = CascadeType.ALL)
    private IncompleteProfile profile;

    @OneToOne(mappedBy = "incomplete", cascade = CascadeType.ALL)
    private IncompleteReferral referredBy;

    @OneToOne(mappedBy = "incomplete", cascade = CascadeType.ALL)
    private IncompletePhoneInformation phoneInfo;

    @OneToOne(mappedBy = "incomplete", cascade = CascadeType.ALL)
    private IncompleteCategory category;

    @OneToMany(mappedBy = "incomplete", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IncompleteSpecialty> specializations;

    public boolean isEmailConfirmed() {
        return tokenConfirmedAt != null;
    }
    public boolean hasProfile() {
        return profile != null;
    }
    public boolean hasCategory() {
        return category != null;
    }
}

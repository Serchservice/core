package com.serch.server.models.auth.incomplete;

import com.serch.server.bases.BaseModel;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.enums.auth.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;

    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt = null;

    @Column(name = "token_confirmed_at")
    private LocalDateTime tokenConfirmedAt = null;

    @OneToOne(mappedBy = "incomplete", cascade = CascadeType.REMOVE)
    private IncompleteProfile profile;

    @OneToOne(mappedBy = "incomplete", cascade = CascadeType.REMOVE)
    private IncompleteReferral referredBy;

    @OneToOne(mappedBy = "incomplete", cascade = CascadeType.REMOVE)
    private IncompletePhoneInformation phoneInfo;

    @OneToOne(mappedBy = "incomplete", cascade = CascadeType.REMOVE)
    private IncompleteCategory category;

    @OneToMany(mappedBy = "incomplete", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<IncompleteSpecialty> specializations;

    @OneToOne(mappedBy = "incomplete", cascade = CascadeType.REMOVE)
    private IncompleteAdditional additional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "business_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "incomplete_business_id_fkey")
    )
    private BusinessProfile business = null;

    public boolean isEmailConfirmed() {
        return tokenConfirmedAt != null;
    }
    public boolean hasProfile() {
        return profile != null;
    }
    public boolean hasCategory() {
        return category != null;
    }
    public boolean hasSpecialty() {
        return !specializations.isEmpty();
    }
    public boolean hasAdditional() {
        return additional != null;
    }
}

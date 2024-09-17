package com.serch.server.models.auth.mfa;

import com.serch.server.bases.BaseDevice;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

/**
 * The MFAChallenge class represents a multifactor authentication challenge in the system.
 * It stores information about challenges used during multifactor authentication.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link MFAFactor} - The multi-factor authentication factor associated with this challenge.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "mfa_challenges", schema = "identity")
public class MFAChallenge extends BaseDevice {
    @Column(name = "verified_at", columnDefinition = "timestamptz")
    private ZonedDateTime verifiedAt = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "mfa_factor_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "mfa_challenge_factor_fkey"
            )
    )
    private MFAFactor mfaFactor;
}
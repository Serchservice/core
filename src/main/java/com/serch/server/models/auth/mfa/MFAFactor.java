package com.serch.server.models.auth.mfa;

import com.serch.server.bases.BaseEntity;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

/**
 * The MFAFactor class represents a multifactor authentication factor associated with a user.
 * It stores information about the multifactor authentication factor, including its secret and associated user.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link User} - The user associated with this multi-factor authentication factor.</li>
 *     <li>{@link MFARecoveryCode} - The recovery codes associated with this multi-factor authentication factor.</li>
 *     <li>{@link MFAChallenge} - The challenges associated with this multi-factor authentication factor.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "identity", name = "mfa_factor")
public class MFAFactor extends BaseEntity {
    @Column(name = "secret", nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Secret cannot be empty")
    private String secret;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "mfa_factor_user_id_fkey")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @OneToMany(mappedBy = "factor", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<MFARecoveryCode> recoveryCodes;

    @OneToMany(mappedBy = "mfaFactor", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<MFAChallenge> mfaChallenges;
}

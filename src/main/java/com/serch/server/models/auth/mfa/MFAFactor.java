package com.serch.server.models.auth.mfa;

import com.serch.server.bases.BaseEntity;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    private User user;

    @OneToMany(mappedBy = "factor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MFARecoveryCode> recoveryCodes;

    @OneToMany(mappedBy = "mfaFactor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MFAChallenge> mfaChallenges;
}

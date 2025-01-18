package com.serch.server.models.auth.mfa;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * The MFARecoveryCode class represents a multi-factor authentication recovery code in the system.
 * It stores information about recovery codes used for multi-factor authentication.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link MFAFactor} - The multi-factor authentication factor associated with the recovery code.</li>
 * </ul>
 * @see BaseModel
 */
@Getter
@Setter
@Entity
@Table(schema = "identity", name = "recovery_codes")
public class MFARecoveryCode extends BaseModel {
    @Column(name = "code", nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(name = "recovery", nullable = false, columnDefinition = "TEXT")
    private String recovery;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "factor_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "factor_recovery_code_fkey")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MFAFactor factor;
}

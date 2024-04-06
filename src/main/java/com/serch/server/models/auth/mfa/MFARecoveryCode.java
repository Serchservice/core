package com.serch.server.models.auth.mfa;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "identity", name = "recovery_codes")
public class MFARecoveryCode extends BaseModel {
    @Column(name = "code", nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "factor_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "factor_recovery_code_fkey")
    )
    private MFAFactor factor;
}

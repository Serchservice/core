package com.serch.server.models.verified;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.verified.ConsentType;
import com.serch.server.enums.verified.DocumentType;
import com.serch.server.enums.verified.OperationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "verified", name = "stage_two")
public class StageTwo extends BaseDateTime {
    @Id
    @Column(name = "vid", nullable = false)
    private UUID verificationId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(
            name = "vid",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "user_vid_fkey")
    )
    private Verification verification;

    @Column(name = "skill_document", nullable = false)
    private String skillDocument;

    @Column(name = "association", nullable = false)
    private String association;

    @Column(name = "experience", nullable = false)
    private String yearsOfExperience;

    @Column(name = "work_history", nullable = false)
    private String workHistoryUrl;

    @Column(name = "operation", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "OperationType must be an enum")
    private OperationType operationType = OperationType.NONE;

    @Column(name = "business_number", nullable = false)
    private String businessNumber;

    @Column(name = "business_number_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "DocumentType must be an enum")
    private DocumentType businessNumberType = DocumentType.LICENSE;

    @Column(name = "tools_consent", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "ConsentType must be an enum")
    private ConsentType toolsConsent = ConsentType.NONE;

    @Column(name = "maintenance_consent", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "ConsentType must be an enum")
    private ConsentType maintenanceConsent = ConsentType.NONE;
}

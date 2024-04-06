package com.serch.server.models.verified;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.verified.ConsentType;
import com.serch.server.enums.verified.VerificationStage;
import com.serch.server.enums.verified.VerificationStatus;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "verified", name = "verification")
public class Verification extends BaseDateTime {
    @Id
    @Column(name = "serch_id", nullable = false)
    private UUID serchId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "serch_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "profile_user_id_fkey")
    )
    private Profile profile;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "business_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "business_id_fkey")
    )
    private BusinessProfile business = null;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "VerificationStatus must be an enum")
    private VerificationStatus status = VerificationStatus.NOT_VERIFIED;

    @Column(name = "stage", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "VerificationStage must be an enum")
    private VerificationStage stage = VerificationStage.NOT_STARTED;

    @Column(name = "use_of_data_consent", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "ConsentType must be an enum")
    private ConsentType useOfDataConsent = ConsentType.NO;

    @Column(name = "trip_consent", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "ConsentType must be an enum")
    private ConsentType tripConsent = ConsentType.NO;

    @Column(name = "manner_consent", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "ConsentType must be an enum")
    private ConsentType mannerConsent = ConsentType.NO;

    @Column(name = "regulation_consent", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "ConsentType must be an enum")
    private ConsentType regulationConsent = ConsentType.NO;

    @Column(name = "liability_consent", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "ConsentType must be an enum")
    private ConsentType liabilityConsent = ConsentType.NO;

    @OneToOne(mappedBy = "verification", cascade = CascadeType.ALL)
    private StageOne stageOne;

    @OneToOne(mappedBy = "verification", cascade = CascadeType.ALL)
    private StageTwo stageTwo;

    @OneToOne(mappedBy = "verification", cascade = CascadeType.ALL)
    private StageThree stageThree;

    public boolean hasStageOne() {
        return getStageOne() != null;
    }
    public boolean hasStageTwo() {
        return getStageTwo() != null;
    }
    public boolean hasStageThree() {
        return getStageThree() != null;
    }
    public boolean areAllYes() {
        return getLiabilityConsent() == ConsentType.YES
                || getMannerConsent() == ConsentType.YES
                || getRegulationConsent() == ConsentType.YES
                || getTripConsent() == ConsentType.YES
                || getUseOfDataConsent() == ConsentType.YES;
    }
    public boolean isVerified() {
        return getStatus() == VerificationStatus.VERIFIED;
    }
}

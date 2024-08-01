package com.serch.server.models.auth.verified;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.verified.ConsentType;
import com.serch.server.enums.verified.VerificationStatus;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * The Verification class represents the verification process for entities in a verified system.
 * It includes information about the verification status, consent, and stages of verification.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>One-to-one with {@link User} using serchId.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "identity", name = "verification")
public class Verification extends BaseDateTime {
    @Id
    @Column(name = "serch_id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "serch_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "user_id_verification_fkey")
    )
    private User user;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "VerificationStatus must be an enum")
    private VerificationStatus status = VerificationStatus.NOT_VERIFIED;

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

    @Column(name = "user_data_status")
    @Enumerated(value = EnumType.STRING)
    private VerificationStatus userStatus = VerificationStatus.NOT_VERIFIED;

    private String link;

    /**
     * Checks if user has given consent - STEP 1.
     *
     * @return true if all consent has been given for these columns:
     * <ol>
     *     <li>{@link Verification#mannerConsent}</li>
     *     <li>{@link Verification#useOfDataConsent}</li>
     *     <li>{@link Verification#tripConsent}</li>
     *     <li>{@link Verification#regulationConsent}</li>
     *     <li>{@link Verification#liabilityConsent}</li>
     * </ol>
     *
     * @see ConsentType
     */
    public boolean hasConsent() {
        return getUseOfDataConsent() == ConsentType.YES && getTripConsent() == ConsentType.YES
                && getMannerConsent() == ConsentType.YES && getRegulationConsent() == ConsentType.YES
                && getLiabilityConsent() == ConsentType.YES;
    }

    /**
     * Checks if the verification status is set to VERIFIED.
     * @return true if the verification status is VERIFIED, false otherwise.
     */
    public boolean isVerified() {
        return getStatus() == VerificationStatus.VERIFIED;
    }
}
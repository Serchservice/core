package com.serch.server.models.verified;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.verified.ConsentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * The StageOne class represents the first stage of verification for entities in a verified system.
 * It stores basic information such as legal name, contact details, and addresses.
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Getter}</li>
 *     <li>{@link Setter}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Table}</li>
 * </ul>
 * Relationships:
 * <ul>
 *     <li>One-to-one with {@link Verification} using verificationId.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "verified", name = "stage_one")
public class StageOne extends BaseDateTime {
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

    @Column(name = "legal_name", nullable = false)
    private String legalName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    @Column(name = "home_address", nullable = false)
    private String homeAddress;

    @Column(name = "address_consent", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "ConsentType must be an enum")
    private ConsentType addressConsent = ConsentType.NO;

    @Column(name = "business_address", nullable = false)
    private String businessAddress;

    @Column(name = "passport", nullable = false)
    private String passportUrl;

    @Column(name = "id_card", nullable = false)
    private String idCardUrl;

    @Column(name = "surety_id_card", nullable = false)
    private String suretyIdCard;
}

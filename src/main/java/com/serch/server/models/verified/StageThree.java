package com.serch.server.models.verified;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.verified.ConsentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * The StageThree class represents the third stage of verification for entities in a verified system.
 * It includes consent statuses for criminal background checks and crime-related consents.
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
 *     <li>One-to-many with {@link SocialMediaLink} through the links field.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "verified", name = "stage_three")
public class StageThree extends BaseDateTime {
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

    @Column(name = "criminal_check_consent", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "ConsentType must be an enum")
    private ConsentType criminalCheckConsent = ConsentType.NO;

    @Column(name = "crime_consent", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "ConsentType must be an enum")
    private ConsentType crimeConsent = ConsentType.NO;

    @OneToMany(mappedBy = "stageThree", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SocialMediaLink> links;
}
package com.serch.server.models.auth.mfa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The MFAChallenge class represents a multifactor authentication challenge in the system.
 * It stores information about challenges used during multifactor authentication.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link MFAFactor} - The multi-factor authentication factor associated with this challenge.</li>
 * </ul>
 * Annotations:
 * <ul>
 *     <li>{@link Column}</li>
 *     <li>{@link ManyToOne}</li>
 *     <li>{@link JoinColumn}</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "mfa_challenges", schema = "identity")
public class MFAChallenge {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "mfa_factor_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "mfa_challenge_factor_fkey"
            )
    )
    private MFAFactor mfaFactor;

    @CreatedDate
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt = null;

    @Column(name = "ip_address", columnDefinition = "TEXT")
    private String ipAddress = null;

    @Column(name = "platform", columnDefinition = "TEXT")
    private String platform = null;

    @Column(name = "device_name", columnDefinition = "TEXT", nullable = false)
    private String deviceName;

    @Column(name = "device_id", columnDefinition = "TEXT")
    private String deviceId = null;
}
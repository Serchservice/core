package com.serch.server.models.trip;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "platform", name = "service_trip_auth")
public class TripAuthentication extends BaseModel {
    @Column(name = "code", nullable = false, columnDefinition = "TEXT")
    private String authCode;

    @Column(name = "authenticator", nullable = false)
    private UUID authenticator;

    @Column(name = "invite_verified", nullable = false)
    private Boolean inviteVerified = false;

    @Column(name = "verified", nullable = false)
    private Boolean verified = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt = null;

    @Column(name = "invite_verified_at")
    private LocalDateTime inviteVerifiedAt = null;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "trip_id_fkey")
    )
    private Trip trip;
}

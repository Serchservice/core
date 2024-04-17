package com.serch.server.models.trip;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(schema = "platform", name = "service_trip_times")
public class TripTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt = null;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt = null;

    @Column(name = "invited_at")
    private LocalDateTime invitedAt = null;

    @Column(name = "invite_accepted_at")
    private LocalDateTime inviteAcceptedAt = null;

    @Column(name = "invite_verified_at")
    private LocalDateTime inviteVerifiedAt = null;

    @Column(name = "left_at")
    private LocalDateTime leftAt = null;

    @Column(name = "stopped_at")
    private LocalDateTime stoppedAt = null;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "time_trip_id_fkey")
    )
    private Trip trip;
}

package com.serch.server.models.schedule;

import com.serch.server.annotations.CoreID;
import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.schedule.ScheduleStatus;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * The Schedule class represents scheduled events between providers and users.
 * It stores information about the status, time, decline reason, closing details, and associated users.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link Profile} - The user associated with the schedule.</li>
 *     <li>{@link Profile} - The provider associated with the schedule.</li>
 * </ul>
 * Enums:
 * <ul>
 *     <li>{@link ScheduleStatus} - Represents the status of the schedule.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "platform", name = "schedules")
public class Schedule extends BaseDateTime {
    @Id
    @CoreID(name = "schedule_generator", prefix = "SSCH")
    @Column(nullable = false, columnDefinition = "TEXT")
    @GeneratedValue(generator = "schedule_seq")
    private String id;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "ScheduleStatus must be an enum")
    private ScheduleStatus status = ScheduleStatus.PENDING;

    @Column(name = "time", nullable = false, columnDefinition = "TEXT")
    private String time;

    @Column(name = "decline_reason", columnDefinition = "TEXT")
    private String reason = null;

    @Column(name = "closed_by")
    private UUID closedBy = null;

    @Column(name = "closed_at")
    private String closedAt = null;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "place_id", columnDefinition = "TEXT")
    private String placeId;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "closed_on_time", columnDefinition = "TEXT", nullable = false)
    private Boolean closedOnTime = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "provider_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "provider_id_fkey")
    )
    private Profile provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "user_id_fkey")
    )
    private Profile user;
}

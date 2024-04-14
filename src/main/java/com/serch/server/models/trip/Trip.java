package com.serch.server.models.trip;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.trip.TripArrivalStatus;
import com.serch.server.enums.trip.TripAuthStatus;
import com.serch.server.enums.trip.TripConnectionStatus;
import com.serch.server.generators.TripID;
import com.serch.server.models.account.Profile;
import com.serch.server.models.shared.SharedPricing;
import com.serch.server.models.transaction.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The Trip class represents service trips in the platform schema.
 * It stores information such as trip timing, status, cancellation reason,
 * authentication status, amount, and related entities.
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
 *     <li>One-to-one with {@link TripAuthentication} representing trip authentication details.</li>
 *     <li>One-to-one with {@link Transaction} representing transaction details associated with the trip.</li>
 *     <li>One-to-one with {@link Address} representing the address location of the trip.</li>
 *     <li>Many-to-one with {@link Profile} representing the service provider of the trip.</li>
 *     <li>Many-to-one with {@link Profile} representing the invited service provider (if any) of the trip.</li>
 *     <li>Many-to-one with {@link Profile} representing the user initiating the trip.</li>
 *     <li>One-to-one with {@link SharedPricing} representing shared pricing details of the trip.</li>
 * </ul>
 * Constraints:
 * <ul>
 *     <li>{@code isGuestOrUserNotNull()} - Asserts that either user or pricing must be provided.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "platform", name = "service_trips")
public class Trip extends BaseDateTime {
    @Id
    @Column(name = "id", nullable = false)
    @GenericGenerator(name = "trip_id_gen", type = TripID.class)
    @GeneratedValue(generator = "trip_id_gen")
    private String id;

    @CreatedDate
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime requestTime = LocalDateTime.now();

    @JsonProperty("start_time")
    private LocalDateTime startTime = null;

    @JsonProperty("stop_time")
    private LocalDateTime stopTime = null;

    @JsonProperty("left_at")
    private LocalDateTime leftAt = null;

    @JsonProperty("invited_at")
    private LocalDateTime invitedAt = null;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String cancelReason = null;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "TripConnectionStatus must be an enum")
    private TripConnectionStatus status = TripConnectionStatus.PENDING;

    @Column(name = "invite_status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "TripConnectionStatus - Invite must be an enum")
    private TripConnectionStatus inviteStatus = null;

    @Column(name = "arrival_status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "TripArrivalStatus must be an enum")
    private TripArrivalStatus arrivalStatus = null;

    @Column(name = "invite_arrival_status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "TripArrivalStatus - Invite must be an enum")
    private TripArrivalStatus inviteArrivalStatus = null;

    @Column(name = "auth_status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "TripAuthStatus must be an enum")
    private TripAuthStatus authStatus = TripAuthStatus.NONE;

    @Column(name = "invite_auth_status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "TripAuthStatus - Invite must be an enum")
    private TripAuthStatus inviteAuthStatus = TripAuthStatus.NONE;

    @Column(name = "cancelled_by")
    private UUID cancelledBy;

    @Column(name = "trip_amount", columnDefinition = "TEXT")
    private String amount = null;

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL)
    private TripAuthentication authentication;

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL)
    private Transaction transaction;

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL)
    private Address address;

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
            name = "invited_provider_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "invited_provider_id_fkey")
    )
    private Profile invitedProvider = null;

    @OneToOne(mappedBy = "trip")
    private SharedPricing pricing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "user_id_fkey")
    )
    private Profile user;

    @AssertTrue(message = "Either user or pricing must be provided")
    private boolean isGuestOrUserNotNull() {
        return user != null || pricing != null;
    }
}

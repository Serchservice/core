package com.serch.server.models.trip;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.trip.TripConnectionStatus;
import com.serch.server.generators.TripID;
import com.serch.server.models.account.Profile;
import com.serch.server.models.shared.SharedPricing;
import com.serch.server.models.transaction.Transaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

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
 *     <li>One-to-one with {@link TripTime} representing trip time details.</li>
 *     <li>One-to-one with {@link SharedPricing} representing shared pricing details of the trip.</li>
 *     <li>One-to-one with {@link Transaction} representing transaction details associated with the trip.</li>
 *     <li>One-to-one with {@link Address} representing the address location of the user.</li>
 *     <li>Many-to-one with {@link Profile} representing the service provider of the trip.</li>
 *     <li>Many-to-one with {@link Profile} representing the invited service provider (if any) of the trip.</li>
 *     <li>Many-to-one with {@link Profile} representing the user initiating the trip.</li>
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

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason = null;

    @Column(name = "invite_cancel_reason", columnDefinition = "TEXT")
    private String inviteCancelReason = null;

    @Column(name = "account", nullable = false, columnDefinition = "TEXT")
    private String account;

    @Column(nullable = false, name = "can_share")
    private Boolean canShare = false;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "TripConnectionStatus must be an enum")
    private TripConnectionStatus status = TripConnectionStatus.PENDING;

    @Column(name = "invite_status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "TripConnectionStatus - Invite must be an enum")
    private TripConnectionStatus inviteStatus = null;

    @Column(name = "trip_amount", columnDefinition = "TEXT")
    private Integer amount = null;

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL)
    private TripTime time;

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL)
    private TripAuthentication authentication;

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL)
    private Transaction transaction;

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL)
    private Address address;

    @OneToOne(mappedBy = "trip")
    private SharedPricing pricing;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(
            name = "invited_provider_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "trip_invited_provider_id_fkey")
    )
    private Profile invitedProvider = null;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(
            name = "provider_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "trip_provider_id_fkey")
    )
    private Profile provider;
}
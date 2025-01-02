package com.serch.server.models.trip;

import com.serch.server.bases.BaseTrip;
import com.serch.server.enums.trip.TripMode;
import com.serch.server.enums.trip.TripShareAccess;
import com.serch.server.enums.trip.TripStatus;
import com.serch.server.enums.trip.TripType;
import com.serch.server.models.account.Profile;
import com.serch.server.models.shared.SharedStatus;
import com.serch.server.models.transaction.Transaction;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * The Trip class represents service trips in the platform schema.
 * It stores information such as trip timing, status, cancellation reason,
 * authentication status, amount, and related entities.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>One-to-one with {@link TripAuthentication} representing trip authentication details.</li>
 *     <li>One-to-one with {@link SharedStatus} representing shared status details of the trip.</li>
 *     <li>One-to-one with {@link Transaction} representing transaction details associated with the trip.</li>
 *     <li>Many-to-one with {@link Profile} representing the service provider of the trip.</li>
 *     <li>Many-to-one with {@link Profile} representing the invited service provider (if any) of the trip.</li>
 *     <li>Many-to-one with {@link Profile} representing the user initiating the trip.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "trip", name = "trips")
public class Trip extends BaseTrip {
    @Column(nullable = false, updatable = false)
    @Enumerated(value = EnumType.STRING)
    private TripType type;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TripStatus status;

    @Column(nullable = false, name = "is_active")
    private Boolean isActive = true;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TripShareAccess access = TripShareAccess.DENIED;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "user_share", nullable = false)
    private BigDecimal userShare = BigDecimal.ZERO;

    @Column(name = "service_fee", nullable = false)
    private BigDecimal serviceFee = BigDecimal.ZERO;

    @Column(name = "is_service_fee_paid", nullable = false)
    private Boolean isServiceFeePaid = Boolean.FALSE;

    @Column(name = "service_fee_reference")
    private String serviceFeeReference;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason = null;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<TripTimeline> timelines;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<ShoppingItem> shoppingItems;

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL)
    private TripShare invited;

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL)
    private TripAuthentication authentication;

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL)
    private ShoppingLocation location;

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL)
    private TripPayment payment;

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL)
    private SharedStatus shared;

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL)
    private MapView mapView;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(
            name = "provider_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "trip_provider_id_fkey")
    )
    private Profile provider;

    public boolean withUserShare() {
        return getMode() == TripMode.FROM_GUEST;
    }

    public boolean isActive() {
        return getStatus() == TripStatus.ACTIVE;
    }

    public boolean isRequested() {
        return getStatus() == TripStatus.WAITING;
    }
}
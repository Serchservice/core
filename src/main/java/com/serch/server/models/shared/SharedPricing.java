package com.serch.server.models.shared;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.shared.UseStatus;
import com.serch.server.models.trip.Trip;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * The SharedPricing class represents the pricing details for a shared link in a sharing platform.
 * It stores information such as the pricing amount for the user and provider, status, and related guest and shared link.
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
 *     <li>Many-to-one with {@link Guest} as the guest.</li>
 *     <li>Many-to-one with {@link SharedLink} as the shared link.</li>
 *     <li>One-to-one with {@link Trip} as the trip.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "providesharing", name = "pricings")
public class SharedPricing extends BaseModel {
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "user_share", nullable = false)
    private BigDecimal user;

    @Column(name = "provider_share", nullable = false)
    private BigDecimal provider;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "UseStatus must be an enum")
    private UseStatus status = UseStatus.NOT_USED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "guest_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "guest_id_pricing_fkey")
    )
    private Guest guest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "shared_link_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "shared_pricing_link_id_fkey")
    )
    private SharedLink sharedLink;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "trip_pricing_id_fkey")
    )
    private Trip trip;
}

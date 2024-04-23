package com.serch.server.models.shared;

import com.serch.server.bases.BaseModel;
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
@Table(schema = "sharing", name = "pricings")
public class SharedPricing extends BaseModel {
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "user_share", nullable = false)
    private BigDecimal user;

    @Column(name = "provider_share", nullable = false)
    private BigDecimal provider;

    @OneToOne(mappedBy = "pricing", cascade = CascadeType.ALL)
    private SharedStatus status;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "trip_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "trip_pricing_id_fkey")
    )
    private Trip trip;
}
package com.serch.server.models.shared;

import com.serch.server.bases.BaseModel;
import com.serch.server.models.trip.Trip;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(schema = "providesharing", name = "shared_pricings")
public class SharedPricing extends BaseModel {
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "user_share", nullable = false)
    private BigDecimal userShare;

    @Column(name = "provider_share", nullable = false)
    private BigDecimal providerShare;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "shared_link",
            referencedColumnName = "link",
            nullable = false,
            foreignKey = @ForeignKey(name = "shared_link_id_fkey")
    )
    private SharedLink sharedLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            referencedColumnName = "trip_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "trip_id_fkey")
    )
    private Trip trip;
}

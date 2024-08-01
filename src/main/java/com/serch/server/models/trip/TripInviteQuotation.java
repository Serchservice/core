package com.serch.server.models.trip;

import com.serch.server.bases.BaseModel;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(schema = "trip", name = "invite_quotations")
public class TripInviteQuotation extends BaseModel {
    @Column(name = "amount", nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "provider_id",
            nullable = false,
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "trip_pricing_provider_id_fkey")
    )
    private Profile provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "trip_pricing_request_id_fkey")
    )
    private TripInvite invite;
}
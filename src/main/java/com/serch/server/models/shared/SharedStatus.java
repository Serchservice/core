package com.serch.server.models.shared;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.shared.UseStatus;
import com.serch.server.models.trip.Trip;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(schema = "sharing", name = "statuses")
public class SharedStatus extends BaseModel {
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "user_share", nullable = false)
    private BigDecimal user;

    @Column(name = "provider_share", nullable = false)
    private BigDecimal provider;

    @Column(nullable = false, name = "is_expired")
    private Boolean isExpired = false;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "UseStatus must be an enum")
    private UseStatus status = UseStatus.NOT_USED;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "trip_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "trip_status_id_fkey")
    )
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "shared_login_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "shared_status_login_id_fkey")
    )
    private SharedLogin sharedLogin;
}
package com.serch.server.models.shared;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseEntity;
import com.serch.server.enums.shared.UseStatus;
import com.serch.server.models.account.Profile;
import com.serch.server.models.trip.Trip;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(schema = "providesharing", name = "shared_links")
public class SharedLink extends BaseEntity {
    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "UseStatus must be an enum")
    private UseStatus useStatus = UseStatus.NOT_USED;

    @Column(name = "link", unique = true, nullable = false, columnDefinition = "TEXT")
    @URL(message = "Shared Link must be formatted as a link")
    private String link;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @OneToMany(mappedBy = "sharedLink", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SharedPricing> pricingList;

    @OneToMany(mappedBy = "sharedLink", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
    private List<Guest> guests;

    @OneToMany(mappedBy = "sharedLink", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    private List<Trip> trips;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "user_id_fkey")
    )
    private Profile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "provider_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "provider_id_fkey")
    )
    private Profile provider;
}

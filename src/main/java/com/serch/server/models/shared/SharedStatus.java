package com.serch.server.models.shared;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.shared.UseStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "sharing", name = "statuses")
public class SharedStatus extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String account;

    @Column(nullable = false, name = "is_expired")
    private Boolean isExpired = false;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "UseStatus must be an enum")
    private UseStatus status = UseStatus.NOT_USED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "shared_link_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "shared_status_link_id_fkey")
    )
    private SharedLink sharedLink;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "shared_pricing_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "shared_pricing_link_id_fkey")
    )
    private SharedPricing pricing;
}

package com.serch.server.models.shared;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.shared.UseStatus;
import com.serch.server.enums.trip.TripConnectionStatus;
import com.serch.server.models.trip.Trip;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "sharing", name = "statuses")
public class SharedStatus extends BaseModel {
    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "UseStatus must be an enum")
    private UseStatus useStatus = UseStatus.COUNT_1;

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
            name = "link_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "shared_status_link_id_fkey")
    )
    private SharedLink sharedLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "shared_login_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "shared_status_login_id_fkey")
    )
    private SharedLogin sharedLogin;

    public boolean isExpired() {
        return trip != null && trip.getTimelines() != null && !trip.getTimelines().isEmpty() && (
                trip.getTimelines().stream().anyMatch(timeline -> timeline.getStatus() == TripConnectionStatus.COMPLETED)
                        || trip.getInvited().getTimelines() != null && !trip.getInvited().getTimelines().isEmpty()
                        && trip.getInvited().getTimelines().stream().anyMatch(timeline -> timeline.getStatus() == TripConnectionStatus.COMPLETED)
        );
    }
}
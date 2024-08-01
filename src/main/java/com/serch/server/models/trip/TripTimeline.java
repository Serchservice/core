package com.serch.server.models.trip;

import com.serch.server.bases.BaseModel;
import com.serch.server.enums.trip.TripConnectionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static com.serch.server.enums.trip.TripConnectionStatus.COMPLETED;

@Getter
@Setter
@Entity
@Table(schema = "trip", name = "timelines")
public class TripTimeline extends BaseModel {
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private TripConnectionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "trip_timeline_id_fkey")
    )
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "invited_trip_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "invited_trip_timeline_id_fkey")
    )
    private TripShare sharing;

    public boolean isCompleted() {
        return status == COMPLETED;
    }
}

package com.serch.server.models.trip;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "trip", name = "locations")
public class MapView extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String place;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double bearing = 0.0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "trip_authentication_id_fkey")
    )
    private Trip trip;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "invited_trip_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "invited_trip_authentication_id_fkey")
    )
    private TripShare sharing;
}

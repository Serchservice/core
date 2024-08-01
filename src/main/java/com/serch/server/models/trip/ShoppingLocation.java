package com.serch.server.models.trip;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "trip", name = "shopping_locations")
public class ShoppingLocation extends BaseModel {
    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "place_id", columnDefinition = "TEXT")
    private String placeId;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_request_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "trip_request_shopping_item_id_fkey")
    )
    private TripInvite invite;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "trip_shopping_item_id_fkey")
    )
    private Trip trip;
}

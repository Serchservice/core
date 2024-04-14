package com.serch.server.models.trip;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The Address class represents address locations in the platform schema.
 * It stores information such as country, state, city, place, latitude, and longitude.
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
 *     <li>One-to-one with {@link Trip} representing the trip associated with the address location.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "platform", name = "address_locations")
public class Address extends BaseModel {
    @Column(name = "country", nullable = false, columnDefinition = "TEXT")
    private String country;

    @Column(name = "state", nullable = false, columnDefinition = "TEXT")
    private String state;

    @Column(name = "city", nullable = false, columnDefinition = "TEXT")
    private String city;

    @Column(name = "place", columnDefinition = "TEXT")
    private String place;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "trip_id_fkey")
    )
    private Trip trip;
}

package com.serch.server.models.trip;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.trip.TripStatus;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The Active class represents active providers in the platform schema.
 * It stores information such as status, country, state, city, place, latitude, and longitude.
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
 *     <li>One-to-one with {@link Profile} representing the profile of the active provider.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "platform", name = "active_providers")
public class Active extends BaseModel {
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "TripStatus must be an enum")
    private TripStatus tripStatus = TripStatus.ONLINE;

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
            name = "serch_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "active_serch_id_fkey")
    )
    private Profile profile;
}

package com.serch.server.models.trip;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.account.ProviderStatus;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The Active class represents active providers in the platform schema.
 * It stores information such as status, country, state, city, place, latitude, and longitude.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>One-to-one with {@link Profile} representing the profile of the active provider.</li>
 * </ul>
 *
 * @see ProviderStatus
 * @see BaseModel
 */
@Getter
@Setter
@Entity
@Table(schema = "platform", name = "active_providers")
public class Active extends BaseModel {
    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "place_id", columnDefinition = "TEXT")
    private String placeId;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "TripStatus must be an enum")
    private ProviderStatus status = ProviderStatus.ONLINE;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "serch_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "active_serch_id_fkey")
    )
    private Profile profile;

    public boolean isActive() {
        return status == ProviderStatus.ONLINE;
    }
}
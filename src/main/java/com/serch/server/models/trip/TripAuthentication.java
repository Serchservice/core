package com.serch.server.models.trip;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The TripAuthentication class represents authentication details associated with service trips.
 * It stores authentication code, authenticator ID, verification status, and related timestamps.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>One-to-one with {@link Trip} representing the associated trip.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "trip", name = "authentications")
public class TripAuthentication extends BaseModel {
    @Column(name = "code", nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

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

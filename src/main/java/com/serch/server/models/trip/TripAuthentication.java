package com.serch.server.models.trip;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The TripAuthentication class represents authentication details associated with service trips.
 * It stores authentication code, authenticator ID, verification status, and related timestamps.
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
 *     <li>One-to-one with {@link Trip} representing the associated trip.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "platform", name = "service_trip_auth")
public class TripAuthentication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "code", nullable = false, columnDefinition = "TEXT")
    private String code;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "auth_trip_id_fkey")
    )
    private Trip trip;
}

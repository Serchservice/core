package com.serch.server.bases;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * The BaseAddress class serves as a base entity for address locations in the system,
 * providing common fields such as country, state, etc.
 * <p></p>
 * It extends the BaseModel which extends the BaseDateTime class to inherit timestamp tracking functionality.
 * This class defines a Long identifier for entities and is annotated with JPA annotations
 * for ID generation and mapping to the database.
 * <p></p>
 * Additionally, it is annotated with @MappedSuperclass to indicate that it should be mapped
 * to the database but not as its own entity, and @EntityListeners to specify auditing behavior.
 *
 * @see BaseModel
 * @see MappedSuperclass
 * @see org.springframework.data.jpa.domain.support.AuditingEntityListener
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseAddress extends BaseModel {
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
}

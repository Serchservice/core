package com.serch.server.bases;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

/**
 * The BaseEntity class serves as a base entity for all entities in the system,
 * providing common fields such as ID and timestamps for creation and update.
 * <p></p>
 * It extends the BaseDateTime class to inherit timestamp tracking functionality.
 * This class defines a UUID identifier for entities and is annotated with JPA annotations
 * for ID generation and mapping to the database.
 * <p></p>
 * Additionally, it is annotated with @MappedSuperclass to indicate that it should be mapped
 * to the database but not as its own entity, and @EntityListeners to specify auditing behavior.
 *
 * @see BaseDateTime
 * @see MappedSuperclass
 * @see org.springframework.data.jpa.domain.support.AuditingEntityListener
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity extends BaseDateTime {
    /**
     * The unique identifier for the entity.
     * Generated automatically using UUID strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
}

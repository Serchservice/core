package com.serch.server.bases;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * The BaseModel class serves as a base entity for entities in the system,
 * providing common fields such as ID and timestamps for creation and update.
 * <p></p>
 * It extends the BaseDateTime class to inherit timestamp tracking functionality.
 * This class defines a Long identifier for entities and is annotated with JPA annotations
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
public class BaseModel extends BaseDateTime {
    /**
     * The unique identifier for the entity.
     * Generated automatically using an identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
}
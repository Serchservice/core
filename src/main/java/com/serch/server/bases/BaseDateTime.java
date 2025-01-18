package com.serch.server.bases;

import com.serch.server.utils.TimeUtil;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;

/**
 * The BaseDateTime class serves as a base entity for entities that require timestamp tracking,
 * such as creation and last modification dates.
 * It includes fields for createdAt and updatedAt timestamps.
 * <p></p>
 * This class is annotated with @MappedSuperclass to indicate that it should be mapped to the database
 * but not as its own entity.
 * <p></p>
 * It also utilizes JPA auditing annotations to automatically populate createdAt and updatedAt fields
 * when entities are persisted or updated.
 *
 * @see MappedSuperclass
 * @see org.springframework.data.jpa.domain.support.AuditingEntityListener
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseDateTime {
    /**
     * The timestamp indicating when the entity was created.
     * Automatically populated upon entity creation.
     */
    @CreatedDate
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "timestamptz")
    private ZonedDateTime createdAt = TimeUtil.now();

    /**
     * The timestamp indicating when the entity was last updated.
     * Automatically updated upon entity modification.
     */
    @UpdateTimestamp
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamptz")
    private ZonedDateTime updatedAt = TimeUtil.now();
}


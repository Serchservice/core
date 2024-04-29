package com.serch.server.bases;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * The BaseDevice class serves as a base entity for platform devices,
 * providing common fields such as ID and timestamps for creation and update.
 * <p></p>
 * It extends {@link BaseEntity}.
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
public class BaseDevice extends BaseEntity {
    @Column(name = "platform", columnDefinition = "TEXT")
    private String platform = null;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String device = null;

    @Column(name = "ip_address", columnDefinition = "TEXT")
    private String ipAddress = null;

    @Column(columnDefinition = "TEXT")
    private String host = null;

    @Column(name = "operating_system", columnDefinition = "TEXT")
    private String operatingSystem = null;

    @Column(name = "operating_system_version", columnDefinition = "TEXT")
    private String operatingSystemVersion = null;

    @Column(name = "local_host", columnDefinition = "TEXT")
    private String localHost = null;
}
package com.serch.server.models.media;

import com.serch.server.bases.BaseDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The MediaAsset class represents media assets stored in the system.
 * It stores information about the asset ID, title, and the asset itself.
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Getter}</li>
 *     <li>{@link Setter}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Table}</li>
 * </ul>
 * Relationships: None
 */
@Getter
@Setter
@Entity
@Table(schema = "company", name = "assets")
public class MediaAsset extends BaseDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String asset;
}

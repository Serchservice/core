package com.serch.server.models.media;

import com.serch.server.bases.BaseDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * The MediaBlog class represents blogs stored in the system.
 * It stores information about the blog ID, image, content, region, and title.
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
@Table(schema = "company", name = "blogs")
public class MediaBlog extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String image;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String blog;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String region;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false)
    private Integer views = 0;
}

package com.serch.server.models.help;

import com.serch.server.bases.BaseDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The HelpCategory class represents categories for organizing help or support sections.
 * It stores information about the category ID, image, category name, and associated help sections.
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
 *     <li>{@link HelpSection} - The list of help sections associated with the category.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "help_categories", schema = "company")
public class HelpCategory extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String image;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String category;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<HelpSection> sections;
}

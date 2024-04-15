package com.serch.server.models.help;

import com.serch.server.bases.BaseDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The HelpSection class represents sections within help categories.
 * It stores information about the section ID, section name, associated help category, and the list of associated help groups.
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
 *     <li>{@link HelpCategory} - The help category associated with the section.</li>
 *     <li>{@link HelpGroup} - The list of help groups associated with the section.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "help_sections", schema = "company")
public class HelpSection extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String section;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String image;

    @ManyToOne
    @JoinColumn(
            name = "category_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "category_id_fkey")
    )
    private HelpCategory category;

    @OneToMany(mappedBy = "section", fetch = FetchType.LAZY)
    private List<HelpGroup> groups;
}

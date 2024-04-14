package com.serch.server.models.help;

import com.serch.server.bases.BaseDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The HelpGroup class represents groups for organizing help or support within help sections.
 * It stores information about the group ID, group name, associated help section, and the list of associated help entries.
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
 *     <li>{@link HelpSection} - The help section associated with the group.</li>
 *     <li>{@link Help} - The list of help entries associated with the group.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "help_groups", schema = "company")
public class HelpGroup extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT", name = "name")
    private String group;

    @ManyToOne
    @JoinColumn(
            name = "section_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "section_id_fkey")
    )
    private HelpSection section;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<Help> helps;
}

package com.serch.server.services.help.models;

import com.serch.server.bases.BaseDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

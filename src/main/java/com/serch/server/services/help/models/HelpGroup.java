package com.serch.server.services.help.models;

import com.serch.server.bases.BaseDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "help_groups", schema = "company")
public class HelpGroup extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    private String key;

    @Column(nullable = false, columnDefinition = "TEXT", name = "name")
    private String group;

    @ManyToOne
    @JoinColumn(
            name = "section_key",
            referencedColumnName = "key",
            nullable = false,
            foreignKey = @ForeignKey(name = "section_id_fkey")
    )
    private HelpSection section;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private List<Help> helps;
}

package com.serch.server.services.help.models;

import com.serch.server.bases.BaseDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "help_categories", schema = "company")
public class HelpCategory extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    private String key;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String image;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String category;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<HelpSection> sections;
}

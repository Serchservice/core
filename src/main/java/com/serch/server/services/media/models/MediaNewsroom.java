package com.serch.server.services.media.models;

import com.serch.server.bases.BaseDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "company", name = "newsroom")
public class MediaNewsroom extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    private String key;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String image;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String news;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String region;
}
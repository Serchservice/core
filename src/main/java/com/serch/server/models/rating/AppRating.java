package com.serch.server.models.rating;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "company", name = "in_app_ratings")
public class AppRating extends BaseModel {
    @Column(name = "rating", nullable = false)
    private Double rating = 0.0;

    private String comment;

    @Column(nullable = false)
    private String app;

    @Column(nullable = false)
    private String account;
}

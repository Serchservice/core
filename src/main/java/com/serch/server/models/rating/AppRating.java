package com.serch.server.models.rating;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * The AppRating class represents ratings given by users within the app.
 * It stores information about the rating, comment, app identifier, and account identifier.
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Getter}</li>
 *     <li>{@link Setter}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Table}</li>
 * </ul>
 * Relationships: None
 * Enums: None
 */
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

package com.serch.server.models.rating;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The Rating class represents ratings given by users.
 * It stores information about the comment, rating, type of rating, rater, and rated user.
 * <p></p>
 */
@Getter
@Setter
@Entity
@Table(schema = "platform", name = "ratings")
public class Rating extends BaseModel {
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "rating", nullable = false)
    private Double rating = 5.0;

    private String event;

    @Column(name = "rater", nullable = false)
    private String rater;

    @Column(name = "rated", nullable = false)
    private String rated;
}

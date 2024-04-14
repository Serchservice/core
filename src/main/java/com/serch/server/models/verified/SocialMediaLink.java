package com.serch.server.models.verified;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The SocialMediaLink class represents social media links associated with verified stage four entities.
 * It stores the link to a social media profile and its association with a stage three entity.
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
 *     <li>Many-to-one with {@link StageThree} representing the associated stage three entity.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "verified", name = "stage_four")
public class SocialMediaLink extends BaseModel {
    @Column(name = "link", nullable = false)
    private String link;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "vid",
        referencedColumnName = "vid",
        nullable = false,
        foreignKey = @ForeignKey(name = "stage_three_vid_fkey")
    )
    private StageThree stageThree;
}
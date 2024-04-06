package com.serch.server.models.verified;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
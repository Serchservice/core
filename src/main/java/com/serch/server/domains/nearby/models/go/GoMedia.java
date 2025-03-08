package com.serch.server.domains.nearby.models.go;

import com.serch.server.bases.BaseModel;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(schema = "nearby", name = "go_medias")
public class GoMedia extends BaseModel {
    @NotBlank(message = "Media file cannot be empty or blank")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String file;

    @Column(nullable = false, columnDefinition = "TEXT DEFAULT ''")
    private String size;

    @Column(nullable = false, columnDefinition = "TEXT DEFAULT ''")
    private String duration;

    @Column(nullable = false, columnDefinition = "TEXT DEFAULT 'IMAGE'")
    private String type;

    @Column(name = "asset_id", columnDefinition = "TEXT")
    private String assetId;

    @Column(name = "public_id", columnDefinition = "TEXT")
    private String publicId;

    @OnDelete(action = OnDeleteAction.SET_NULL)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(
            name = "activity_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "go_media_activity_id_fkey")
    )
    private GoActivity activity;

    @OnDelete(action = OnDeleteAction.SET_NULL)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(
            name = "bcap_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "go_media_bcap_id_fkey")
    )
    private GoBCap bcap;
}
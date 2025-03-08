package com.serch.server.domains.nearby.models.go;

import com.serch.server.annotations.CoreID;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Getter
@Setter
@Table(schema = "nearby", name = "go_bcaps")
public class GoBCap extends BaseDateTime {
    @Id
    @CoreID(name = "go_bcap_generator", prefix = "gb", end = 10, replaceSymbols = true)
    @GeneratedValue(generator = "go_bcap_gen")
    @Column(name = "id", nullable = false, columnDefinition = "TEXT", updatable = false)
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // ✅ Delete GoBCap when GoActivity is deleted
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
            name = "activity_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "go_bcap_activity_id_fkey")
    )
    private GoActivity activity;

    // ❌ Do NOT delete GoUser when GoBCap is deleted
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "go_bcap_user_id_fkey")
    )
    private GoUser user;

    // ✅ Delete all related media when GoBCap is deleted
    @OneToMany(mappedBy = "bcap", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<GoMedia> media;
}
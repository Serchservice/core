package com.serch.server.domains.nearby.models.go.addon;

import com.serch.server.bases.BaseModel;
import com.serch.server.enums.nearby.GoAddonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Getter
@Setter
@Table(schema = "nearby", name = "go_addons")
public class GoAddon extends BaseModel {
    @Column(columnDefinition = "TEXT", nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private GoAddonType type;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "addon")
    private List<GoAddonPlan> plans;
}
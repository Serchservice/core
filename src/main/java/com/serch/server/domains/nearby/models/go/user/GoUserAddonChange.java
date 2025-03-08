package com.serch.server.domains.nearby.models.go.user;

import com.serch.server.bases.BaseModel;
import com.serch.server.domains.nearby.models.go.addon.GoAddonPlan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(schema = "nearby", name = "go_user_addon_changes")
public class GoUserAddonChange extends BaseModel {
    @Column(name = "use_existing_authorization")
    private Boolean useExistingAuthorization = true;

    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(
            name = "addon_plan_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "go_user_addon_change_plan_id_fkey")
    )
    private GoAddonPlan plan;

    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(
            name = "user_addon_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "go_user_addon_change_user_addon_id_fkey")
    )
    private GoUserAddon addon;
}
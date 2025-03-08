package com.serch.server.domains.nearby.models.go.activity;

import com.serch.server.bases.BaseModel;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(schema = "nearby", name = "go_attending_users")
public class GoAttendingUser extends BaseModel {
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "go_attending_user_id_fkey")
    )
    private GoUser user;

    @OnDelete(action = OnDeleteAction.SET_NULL)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(
            name = "activity_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "go_attending_activity_id_fkey")
    )
    private GoActivity activity;
}
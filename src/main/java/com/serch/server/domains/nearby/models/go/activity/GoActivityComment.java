package com.serch.server.domains.nearby.models.go.activity;

import com.serch.server.bases.BaseModel;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(schema = "nearby", name = "go_activity_comments")
public class GoActivityComment extends BaseModel {
    @NotBlank(message = "Event comment cannot be empty or blank")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "go_attending_user_id_fkey")
    )
    private GoUser user;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(
            name = "activity_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "go_attending_activity_id_fkey")
    )
    private GoActivity activity;
}
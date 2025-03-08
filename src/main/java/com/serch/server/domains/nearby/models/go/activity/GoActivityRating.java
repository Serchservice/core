package com.serch.server.domains.nearby.models.go.activity;

import com.serch.server.bases.BaseModel;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(schema = "nearby", name = "go_activity_ratings")
public class GoActivityRating extends BaseModel {
    @Column(nullable = false)
    @Min(value = 0, message = "GoEvent rating must not be less than 0")
    @Max(value = 5, message = "GoEvent rating must not be more than 5")
    private Double rating = 0.0;

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
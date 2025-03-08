package com.serch.server.domains.nearby.models.go.user;

import com.serch.server.bases.BaseModel;
import com.serch.server.domains.nearby.listeners.GoUserInterestListener;
import com.serch.server.domains.nearby.models.go.interest.GoInterest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@EntityListeners(GoUserInterestListener.class)
@Table(schema = "nearby", name = "go_user_interests")
public class GoUserInterest extends BaseModel {
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(
            name = "interest_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "go_user_interest_interest_id_fkey")
    )
    private GoInterest interest;

    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "go_user_interest_user_id_fkey")
    )
    private GoUser user;
}
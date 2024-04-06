package com.serch.server.models.countries;

import com.serch.server.bases.BaseModel;
import com.serch.server.enums.auth.AccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "company", name = "launched_cities")
public class LaunchedCity extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "state_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "state_state_fkey")
    )
    private LaunchedState launchedState;

    public boolean isNotActive() {
        return status != AccountStatus.ACTIVE;
    }
}

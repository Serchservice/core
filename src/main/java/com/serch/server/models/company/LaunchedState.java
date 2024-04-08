package com.serch.server.models.company;

import com.serch.server.bases.BaseModel;
import com.serch.server.enums.auth.AccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(schema = "company", name = "launched_states")
public class LaunchedState extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "country_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "country_state_fkey")
    )
    private LaunchedCountry launchedCountry;

    @OneToMany(mappedBy = "launchedState", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LaunchedCity> launchedCities;

    public boolean isNotActive() {
        return status != AccountStatus.ACTIVE;
    }
}

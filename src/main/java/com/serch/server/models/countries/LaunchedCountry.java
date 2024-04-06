package com.serch.server.models.countries;

import com.serch.server.bases.BaseModel;
import com.serch.server.enums.auth.AccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(schema = "company", name = "launched_countries")
public class LaunchedCountry extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String flag;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(name = "dial_code", nullable = false)
    private Integer dialCode;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    @OneToMany(mappedBy = "launchedCountry", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LaunchedState> launchedStates;

    public boolean isNotActive() {
        return status != AccountStatus.ACTIVE;
    }
}

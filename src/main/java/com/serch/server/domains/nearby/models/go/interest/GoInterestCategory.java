package com.serch.server.domains.nearby.models.go.interest;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Getter
@Setter
@Table(schema = "nearby", name = "go_interest_categories")
public class GoInterestCategory extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<GoInterest> interests;
}
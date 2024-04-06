package com.serch.server.models.countries;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "company", name = "requested_cities")
public class RequestCity extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "state_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "state_state_fkey")
    )
    private RequestState requestState;
}

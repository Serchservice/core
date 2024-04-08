package com.serch.server.models.company;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(schema = "company", name = "requested_states")
public class RequestState extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "country_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "country_state_fkey")
    )
    private RequestCountry requestCountry;

    @OneToMany(mappedBy = "requestState", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequestCity> requestedCities;
}

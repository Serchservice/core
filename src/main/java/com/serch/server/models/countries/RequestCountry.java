package com.serch.server.models.countries;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(schema = "company", name = "requested_countries")
public class RequestCountry extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @OneToMany(mappedBy = "requestCountry", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequestState> requestedStates;
}

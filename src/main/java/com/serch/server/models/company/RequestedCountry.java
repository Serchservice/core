package com.serch.server.models.company;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The RequestCountry class represents a requested country entity in the system.
 * It stores information about countries that have been requested, including the name and the associated requested states.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link RequestedState} - The requested states associated with the country.</li>
 * </ul>
 * Methods: None
 * @see BaseModel
 */
@Getter
@Setter
@Entity
@Table(schema = "company", name = "requested_countries")
public class RequestedCountry extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @OneToMany(mappedBy = "requestedCountry", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequestedState> requestedStates;
}

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
 * Annotations:
 * <ul>
 *     <li>{@link Column}</li>
 *     <li>{@link OneToMany}</li>
 *     <li>{@link JoinColumn}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Table}</li>
 * </ul>
 * Relationships:
 * <ul>
 *     <li>{@link RequestState} - The requested states associated with the country.</li>
 * </ul>
 * Methods: None
 * @see BaseModel
 */
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

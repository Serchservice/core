package com.serch.server.models.company;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The RequestState class represents a requested state entity in the system.
 * It stores information about states that have been requested, including the name, the associated requested country, and the requested cities.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link RequestedCountry} - The requested country associated with the state.</li>
 *     <li>{@link RequestedCity} - The requested cities associated with the state.</li>
 * </ul>
 * Methods: None
 * @see BaseModel
 */
@Getter
@Setter
@Entity
@Table(schema = "company", name = "requested_states")
public class RequestedState extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "country_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "country_state_fkey")
    )
    private RequestedCountry requestedCountry;

    @OneToMany(mappedBy = "requestedState", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequestedCity> requestedCities;
}

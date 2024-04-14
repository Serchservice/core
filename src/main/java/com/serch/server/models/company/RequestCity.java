package com.serch.server.models.company;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The RequestCity class represents a requested city entity in the system.
 * It stores information about cities that have been requested, including the name and the associated requested state.
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Column}</li>
 *     <li>{@link ManyToOne}</li>
 *     <li>{@link JoinColumn}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Table}</li>
 * </ul>
 * Relationships:
 * <ul>
 *     <li>{@link RequestState} - The requested state associated with the city.</li>
 * </ul>
 * Methods: None
 * @see BaseModel
 */
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

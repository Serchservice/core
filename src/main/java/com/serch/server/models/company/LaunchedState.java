package com.serch.server.models.company;

import com.serch.server.bases.BaseModel;
import com.serch.server.enums.auth.AccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The LaunchedState class represents a launched state entity in the system.
 * It stores information about launched states, including the name, status, the country it belongs to, and the cities within it.
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Column}</li>
 *     <li>{@link Enumerated}</li>
 *     <li>{@link ManyToOne}</li>
 *     <li>{@link OneToMany}</li>
 *     <li>{@link JoinColumn}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Table}</li>
 * </ul>
 * Enums:
 * <ul>
 *     <li>{@link AccountStatus}</li>
 * </ul>
 * Relationships:
 * <ul>
 *     <li>{@link LaunchedCountry} - The country to which the state belongs.</li>
 *     <li>{@link LaunchedCity} - The cities within the state.</li>
 * </ul>
 * Methods:
 * <ul>
 *     <li>{@link LaunchedState#isNotActive()} - Checks if the state is not active.</li>
 * </ul>
 * @see BaseModel
 */
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

    /**
     * Checks if the state is not active.
     *
     * @return true if the state is not active, otherwise false
     */
    public boolean isNotActive() {
        return status != AccountStatus.ACTIVE;
    }
}

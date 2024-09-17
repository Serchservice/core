package com.serch.server.models.company;

import com.serch.server.bases.BaseModel;
import com.serch.server.enums.account.AccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The LaunchedCity class represents a launched city entity in the system.
 * It stores information about launched cities, including the name, status, and the state it belongs to.
 * <p></p>
 * Enums:
 * <ul>
 *     <li>{@link AccountStatus}</li>
 * </ul>
 * Relationships:
 * <ul>
 *     <li>{@link LaunchedState} - The state to which the city belongs.</li>
 * </ul>
 * Methods:
 * <ul>
 *     <li>{@link LaunchedCity#isNotActive()} - Checks if the city is not active.</li>
 * </ul>
 * @see BaseModel
 */
@Getter
@Setter
@Entity
@Table(schema = "company", name = "launched_cities")
public class LaunchedCity extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "state_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "state_state_fkey")
    )
    private LaunchedState launchedState;

    /**
     * Checks if the city is not active.
     *
     * @return true if the city is not active, otherwise false
     */
    public boolean isNotActive() {
        return status != AccountStatus.ACTIVE;
    }
}

package com.serch.server.models.company;

import com.serch.server.bases.BaseModel;
import com.serch.server.enums.account.AccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The LaunchedCountry class represents a launched country entity in the system.
 * It stores information about launched countries, including the name, flag, dial code, status, and the states it contains.
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Column}</li>
 *     <li>{@link Enumerated}</li>
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
 *     <li>{@link LaunchedState} - The states contained within the country.</li>
 * </ul>
 * Methods:
 * <ul>
 *     <li>{@link LaunchedCountry#isNotActive()} - Checks if the country is not active.</li>
 * </ul>
 * @see BaseModel
 */
@Getter
@Setter
@Entity
@Table(schema = "company", name = "launched_countries")
public class LaunchedCountry extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String flag;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(name = "dial_code", nullable = false)
    private Integer dialCode;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    @OneToMany(mappedBy = "launchedCountry", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LaunchedState> launchedStates;

    /**
     * Checks if the country is not active.
     *
     * @return true if the country is not active, otherwise false
     */
    public boolean isNotActive() {
        return status != AccountStatus.ACTIVE;
    }
}

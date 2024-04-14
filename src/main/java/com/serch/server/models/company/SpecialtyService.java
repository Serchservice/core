package com.serch.server.models.company;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.account.Specialty;
import com.serch.server.models.auth.incomplete.IncompleteSpecialty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The SpecialtyService class represents a specialty service entity in the system.
 * It stores information about specialty services, including keywords, timeline, difficulty, minimum and maximum amounts, estimated amount, category, and associated specialties and incomplete specialties.
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
 * Relationships:
 * <ul>
 *     <li>{@link Specialty} - The specialties associated with the service.</li>
 *     <li>{@link IncompleteSpecialty} - The incomplete specialties associated with the service.</li>
 * </ul>
 * Methods: None
 * @see BaseModel
 * @see SerchEnum
 * @see Specialty
 * @see IncompleteSpecialty
 */
@Getter
@Setter
@Entity
@Table(schema = "company", name = "service_keywords")
public class SpecialtyService extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String keyword;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String timeline;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String difficulty;

    @Column(name = "minimum_amount", nullable = false)
    private Double minimumAmount = 1000.00;

    @Column(name = "maximum_amount", nullable = false)
    private Double maximumAmount = 1000.00;

    @Column(name = "estimated_amount", nullable = false)
    private Double estimatedAmount;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @NotBlank(message = "Category cannot be blank")
    @SerchEnum(message = "Category must be an enum")
    private SerchCategory category;

    @OneToMany(mappedBy = "service", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    private List<Specialty> specialties;

    @OneToMany(mappedBy = "service", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    private List<IncompleteSpecialty> incompleteSpecialties;
}

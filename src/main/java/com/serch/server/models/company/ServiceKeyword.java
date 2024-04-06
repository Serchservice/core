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

@Getter
@Setter
@Entity
@Table(schema = "company", name = "service_keywords")
public class ServiceKeyword extends BaseModel {
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

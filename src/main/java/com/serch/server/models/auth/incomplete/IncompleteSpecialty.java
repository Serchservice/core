package com.serch.server.models.auth.incomplete;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The IncompleteSpecialty class represents incomplete specialties in the system.
 * It stores information about incomplete specialties related to specialty services and incomplete objects.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link Incomplete} - The incomplete object associated with the incomplete specialty.</li>
 * </ul>
 * Annotations:
 * <ul>
 *     <li>{@link ManyToOne}</li>
 *     <li>{@link JoinColumn}</li>
 * </ul>
 * @see BaseModel
 */
@ToString
@Getter
@Setter
@Entity
@Table(schema = "identity", name = "incomplete_specializations")
public class IncompleteSpecialty extends BaseModel {
    @Column(nullable = false, updatable = false)
    private String specialty;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "incomplete_email",
            referencedColumnName = "email_address",
            nullable = false,
            foreignKey = @ForeignKey(name = "special_email_fkey")
    )
    @ToString.Exclude
    private Incomplete incomplete;
}
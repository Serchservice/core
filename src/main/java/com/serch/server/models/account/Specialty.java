package com.serch.server.models.account;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a specialty entity, storing information about specialties associated with profiles.
 * <p></p>
 * Relationships:
 * <ol>
 *     <li>{@link Profile} - Many to one</li>
 * </ol>
 *
 * @see BaseModel
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "account", name = "specializations")
public class Specialty extends BaseModel {
    @Column(nullable = false, updatable = false, columnDefinition = "TEXT default ''")
    private String specialty;

    /**
     * The profile associated with the specialty.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "serch_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "special_serch_id_fkey")
    )
    private Profile profile;
}
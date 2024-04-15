package com.serch.server.models.account;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseProfile;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.auth.Role;
import com.serch.server.models.certificate.Certificate;
import com.serch.server.models.verified.Verification;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * Represents a user profile entity, extending the base profile with additional attributes and relationships.
 * Relationships:
 * <ol>
 *     <li>One to one - {@link Verification}</li>
 *     <li>One to one - {@link Certificate}</li>
 *     <li>One to many - {@link Specialty}</li>
 *     <li>Many to one - {@link BusinessProfile}</li>
 * </ol>
 *
 * Enums:
 * <ol>
 *     <li>{@link SerchCategory} - Shows the category of the profile</li>
 * </ol>
 *
 * @see SerchEnum
 * @see BaseProfile
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "account", name = "profiles")
public class Profile extends BaseProfile {
    /**
     * The category of the profile, indicating whether it belongs to a user or a business.
     */
    @Column(name = "serch_category", nullable = false)
    @SerchEnum(message = "SerchCategory must be an enum")
    @Enumerated(value = EnumType.STRING)
    private SerchCategory category;

    /**
     * The certificate associated with the profile.
     */
    @OneToOne(mappedBy = "profile")
    private Certificate certificate;

    /**
     * The verification status associated with the profile.
     */
    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL)
    private Verification verification;

    /**
     * The list of specialties associated with the profile.
     */
    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY)
    private List<Specialty> specializations;

    /**
     * The business profile associated with the user, if applicable.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "business_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "profile_business_id_fkey")
    )
    private BusinessProfile business = null;

    /**
     * Gets the full name of the profile.
     *
     * @return The full name of the profile.
     */
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    /**
     * Checks if the profile belongs to the specified business.
     *
     * @param serchId The search ID of the business.
     * @return True if the profile belongs to the business, otherwise false.
     */
    public boolean belongsToBusiness(UUID serchId) {
        return getBusiness() != null && getBusiness().getId() == serchId
                && getUser().getRole() == Role.ASSOCIATE_PROVIDER;
    }

    /**
     * Checks if the profile is associated with a business.
     *
     * @return True if the profile is associated with a business, otherwise false.
     */
    public boolean isAssociate() {
        return getBusiness() != null && getUser().getRole() == Role.ASSOCIATE_PROVIDER;
    }

    /**
     * Checks if the profile is the same as the specified user.
     *
     * @param user The UUID of the user to compare with.
     * @return True if the profile is the same as the specified user, otherwise false.
     */
    public boolean isSameAs(UUID user) {
        return getId() != null && getId() == user;
    }
}
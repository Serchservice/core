package com.serch.server.models.account;

import com.serch.server.bases.BaseProfile;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.auth.verified.Verification;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Represents a business profile entity, storing information about a business entity and its attributes.
 * <p></p>
 * Relationships:
 * <ol>
 *     <li>{@link Verification} - One to one</li>
 *     <li>{@link Profile} - One to many</li>
 * </ol>
 * Enums:
 * <ol>
 *     <li>{@link SerchCategory} - Category of the business profile</li>
 * </ol>
 *
 * @see BaseProfile
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "account", name = "business_profiles")
public class BusinessProfile extends BaseProfile {
    /**
     * The name of the business.
     */
    @Column(name = "business_name", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Business name cannot be empty or null")
    private String businessName;

    /**
     * The logo of the business.
     */
    @Column(name = "business_logo", columnDefinition = "TEXT")
    private String businessLogo;

    /**
     * The description of the business.
     */
    @Column(name = "business_description", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Business description cannot be empty or null")
    private String businessDescription;

    /**
     * The address of the business.
     */
    @Column(name = "business_address", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Business address cannot be empty or null")
    private String businessAddress;

    /**
     * The default password associated with the business.
     */
    @Column(name = "default_password", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Default Password cannot be empty or null")
    private String defaultPassword;

    /**
     * The default password associated with the business.
     */
    @Column(name = "contact", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Contact cannot be empty or null")
    private String contact;

    /**
     * The place id location of the business.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String place = "";

    /**
     * The longitude of the business.
     */
    @Column(nullable = false)
    private Double longitude = 0.0;

    /**
     * The latitude of the business.
     */
    @Column(nullable = false)
    private Double latitude = 0.0;

    /**
     * The category of the business.
     */
    @Column(name = "category", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SerchCategory category;

    /**
     * The list of profiles associated with the business as associates.
     */
    @OneToMany(mappedBy = "business", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Profile> associates;

    /**
     * Gets the full name of the profile.
     *
     * @return The full name of the profile.
     */
    public String getFullName() {
        return getUser().getFirstName() + " " + getUser().getLastName();
    }
}

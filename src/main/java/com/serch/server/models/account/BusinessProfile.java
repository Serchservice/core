package com.serch.server.models.account;

import com.serch.server.bases.BaseProfile;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.subscription.SubscriptionAssociate;
import com.serch.server.models.verified.Verification;
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
 *     <li>{@link SubscriptionAssociate} - One to many</li>
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
     * The category of the business.
     */
    @Column(name = "category", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SerchCategory category;

    /**
     * The verification status associated with the business.
     */
    @OneToOne(mappedBy = "business", cascade = CascadeType.ALL)
    private Verification verification;

    /**
     * The list of profiles associated with the business as associates.
     */
    @OneToMany(mappedBy = "business", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Profile> associates;

    /**
     * The list of subscriptions associated with the business's associates.
     */
    @OneToMany(mappedBy = "business", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SubscriptionAssociate> associateSubscriptions;
}
package com.serch.server.models.account;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an additional information entity, storing extra details about a profile.
 * <p></p>
 * Relationships:
 * <ol>
 *     <li>{@link Profile} - One to one</li>
 * </ol>
 *
 * @see BaseModel
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "account", name = "additional_information")
public class AdditionalInformation extends BaseModel {
    /**
     * The street address of the profile.
     */
    @Column(name = "street_address", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Street address cannot be null")
    @NotEmpty(message = "Street address cannot be empty")
    private String streetAddress;

    /**
     * The landmark associated with the profile's address.
     */
    @Column(name = "landmark", columnDefinition = "TEXT")
    private String landMark = null;

    /**
     * The city of the profile.
     */
    @Column(name = "city", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "City cannot be null")
    @NotEmpty(message = "City cannot be empty")
    private String city;

    /**
     * The state of the profile.
     */
    @Column(name = "state", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "State cannot be null")
    @NotEmpty(message = "State cannot be empty")
    private String state;

    /**
     * The country of the profile.
     */
    @Column(name = "country", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Country cannot be null")
    @NotEmpty(message = "Country cannot be empty")
    private String country;

    /**
     * The surety status associated with the profile.
     */
    @Column(name = "surety_status", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Surety status cannot be null")
    @NotEmpty(message = "Surety status cannot be empty")
    private String suretyStatus;

    /**
     * The first name of the surety associated with the profile.
     */
    @Column(name = "surety_first_name", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Surety first name cannot be null")
    @NotEmpty(message = "Surety first name cannot be empty")
    private String suretyFirstName;

    /**
     * The last name of the surety associated with the profile.
     */
    @Column(name = "surety_last_name", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Surety last name cannot be null")
    @NotEmpty(message = "Surety last name cannot be empty")
    private String suretyLastName;

    /**
     * The email address of the surety associated with the profile.
     */
    @Column(name = "surety_email", nullable = false, columnDefinition = "TEXT")
    @Email(message = "Email must be properly formatted")
    @NotNull(message = "Surety email address cannot be null")
    @NotEmpty(message = "Surety email address cannot be empty")
    private String suretyEmail;

    /**
     * The phone number of the surety associated with the profile.
     */
    @Column(name = "surety_phone_number", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Surety phone number cannot be null")
    private String suretyPhone;

    /**
     * The home address of the surety associated with the profile.
     */
    @Column(name = "surety_address", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Surety home address cannot be null")
    @NotEmpty(message = "Surety home address cannot be empty")
    private String suretyAddress;

    /**
     * The profile associated with the additional information.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "serch_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "additional_user_id")
    )
    private Profile profile;
}
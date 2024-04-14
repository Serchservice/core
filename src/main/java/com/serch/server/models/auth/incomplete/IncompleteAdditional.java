package com.serch.server.models.auth.incomplete;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The IncompleteAdditional class represents additional incomplete information in the system.
 * It stores information about additional incomplete details, including surety information and address details.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link Incomplete} - The incomplete object associated with the incomplete additional information.</li>
 * </ul>
 * Annotations:
 * <ul>
 *     <li>{@link Column}</li>
 *     <li>{@link OneToOne}</li>
 *     <li>{@link JoinColumn}</li>
 * </ul>
 * Constraints:
 * <ul>
 *     <li> {@link NotNull} - The fields {@code suretyStatus}, {@code suretyFirstName}, {@code suretyLastName},
 *     {@code suretyEmail}, {@code suretyPhone}, {@code suretyAddress}, {@code streetAddress}, {@code city},
 *     {@code state}, {@code country} cannot be null.</li>
 *     <li> {@link NotEmpty} - The fields {@code suretyStatus}, {@code suretyFirstName}, {@code suretyLastName},
 *     {@code suretyEmail}, {@code suretyAddress}, {@code streetAddress}, {@code city}, {@code state}, {@code country}
 *     cannot be empty.</li>
 *     <li> {@link Email} - The field {@code suretyEmail} must be properly formatted.</li>
 * </ul>
 * @see BaseModel
 */
@ToString
@Getter
@Setter
@Entity
@Table(schema = "identity", name = "incomplete_additional")
public class IncompleteAdditional extends BaseModel {
    @Column(name = "surety_status", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Surety status cannot be null")
    @NotEmpty(message = "Surety status cannot be empty")
    private String suretyStatus;

    @Column(name = "surety_first_name", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Surety first name cannot be null")
    @NotEmpty(message = "Surety first name cannot be empty")
    private String suretyFirstName;

    @Column(name = "surety_last_name", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Surety last name cannot be null")
    @NotEmpty(message = "Surety last name cannot be empty")
    private String suretyLastName;

    @Column(name = "surety_email", nullable = false, columnDefinition = "TEXT")
    @Email(message = "Email must be properly formatted")
    @NotNull(message = "Surety email address cannot be null")
    @NotEmpty(message = "Surety email address cannot be empty")
    private String suretyEmail;

    @Column(name = "surety_phone_number", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Surety phone number cannot be null")
    private String suretyPhone;

    @Column(name = "surety_address", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Surety home address cannot be null")
    @NotEmpty(message = "Surety home address cannot be empty")
    private String suretyAddress;

    @Column(name = "street_address", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Street address cannot be null")
    @NotEmpty(message = "Street address cannot be empty")
    private String streetAddress;

    @Column(name = "landmark", columnDefinition = "TEXT")
    private String landMark = null;

    @Column(name = "city", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "City cannot be null")
    @NotEmpty(message = "City cannot be empty")
    private String city;

    @Column(name = "state", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "State cannot be null")
    @NotEmpty(message = "State cannot be empty")
    private String state;

    @Column(name = "country", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Country cannot be null")
    @NotEmpty(message = "Country cannot be empty")
    private String country;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "incomplete_email",
            referencedColumnName = "email_address",
            nullable = false,
            foreignKey = @ForeignKey(name = "additional_email_fkey")
    )
    @ToString.Exclude
    private Incomplete incomplete;
}

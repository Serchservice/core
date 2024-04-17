package com.serch.server.models.account;

import com.serch.server.bases.BaseModel;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a phone information entity, storing details about a user's phone number.
 * <p></p>
 * Relationships:
 * <ol>
 *     <li>{@link User} - One to one</li>
 * </ol>
 *
 * @see BaseModel
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "account", name = "phone_information")
public class PhoneInformation extends BaseModel {
    /**
     * The phone number associated with the user.
     */
    @Column(name = "number", nullable = false, columnDefinition = "TEXT")
    private String phoneNumber;

    /**
     * The country code of the phone number.
     */
    @Column(name = "country_code", nullable = false, columnDefinition = "TEXT")
    private String countryCode;

    /**
     * The ISO code of the phone number.
     */
    @Column(name = "iso_code", nullable = false, columnDefinition = "TEXT")
    private String isoCode;

    /**
     * The country of the phone number.
     */
    @Column(name = "country", nullable = false, columnDefinition = "TEXT")
    private String country;

    /**
     * The user associated with the phone information.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "user_phone_fkey")
    )
    private User user;
}
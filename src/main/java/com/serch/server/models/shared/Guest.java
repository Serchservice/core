package com.serch.server.models.shared;

import com.serch.server.annotations.CoreID;
import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.account.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

/**
 * The Guest class represents guest profiles in a sharing platform.
 * It stores information such as email address, first name, last name, gender, avatar,
 * messaging token, platform, and shared links.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link SharedLink} - The shared links associated with the guest.</li>
 * </ul>
 * Methods:
 * <ul>
 *     <li>{@link Guest#getFullName()} - Retrieves the full name of the guest by concatenating the first name and last name.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "sharing", name = "guests")
public class Guest extends BaseDateTime {
    @Id
    @CoreID(name = "guest_generator", prefix = "SGST", toUpperCase = true)
    @GeneratedValue(generator = "guest_id_seq")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String id;

    @Column(name = "email_address", unique = true, nullable = false, columnDefinition = "TEXT")
    @Email(
            message = "Email address must be properly formatted",
            regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    )
    private String emailAddress;

    @Column(name = "first_name", nullable = false, columnDefinition = "TEXT")
    @Size(min = 3, message = "First Name must be above 3 characters")
    @NotBlank(message = "First name cannot be empty or null")
    private String firstName;

    @Column(name = "last_name", nullable = false, columnDefinition = "TEXT")
    @Size(min = 3, message = "Last Name must be above 3 characters")
    @NotBlank(message = "Last name cannot be empty or null")
    private String lastName;

    @Column(name = "gender", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Gender must be an enum")
    private Gender gender = Gender.ANY;

    @Column(name = "avatar", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Avatar cannot be empty or null")
    private String avatar;

    @Column(name = "messaging_token", columnDefinition = "TEXT")
    private String fcmToken = null;

    @Column(columnDefinition = "TEXT default 'Africa/Lagos'")
    private String timezone = "Africa/Lagos";

    @Column(name = "phone_number", columnDefinition = "TEXT")
    private String phoneNumber = null;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String platform;

    @Column(name = "token", columnDefinition = "TEXT")
    private String token;

    @Column(nullable = false)
    private Double rating = 5.0;

    @Column(name = "expires_at", columnDefinition = "timestamptz")
    private ZonedDateTime expiresAt = null;

    @Column(name = "confirmed_at", columnDefinition = "timestamptz")
    private ZonedDateTime confirmedAt = null;

    @Column(columnDefinition = "TEXT")
    private String state;

    @Column(columnDefinition = "TEXT")
    private String country;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String device = null;

    @Column(name = "ip_address", columnDefinition = "TEXT")
    private String ipAddress = null;

    @Column(columnDefinition = "TEXT")
    private String host = null;

    @Column(name = "operating_system", columnDefinition = "TEXT")
    private String operatingSystem = null;

    @Column(name = "operating_system_version", columnDefinition = "TEXT")
    private String operatingSystemVersion = null;

    @Column(name = "local_host", columnDefinition = "TEXT")
    private String localHost = null;

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
    public boolean isEmailConfirmed() {
        return confirmedAt != null;
    }
}
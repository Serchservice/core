package com.serch.server.bases;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.enums.account.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * The BaseProfile class serves as a base entity for user profiles in the system,
 * extending the functionality of BaseUser with additional profile-specific attributes.
 * <p></p>
 * It includes fields such as email address, referral link, referral code, avatar,
 * rating, number of ratings, total service trips, total shared trips, and FCM token.
 * <p></p>
 * This class is annotated with JPA annotations for entity mapping and auditing behavior.
 * It also provides constructors for creating instances with and without arguments.
 *
 * @see BaseUser
 * @see MappedSuperclass
 * @see org.springframework.data.jpa.domain.support.AuditingEntityListener
 */
@Getter
@Setter
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BaseProfile extends BaseUser {
    /**
     * The first name associated with the profile.
     */
    @Column(name = "first_name", nullable = false, columnDefinition = "TEXT")
    @Size(min = 3, message = "First name must be above 3 characters")
    @NotBlank(message = "First name cannot be empty or null")
    private String firstName;

    /**
     * The last name associated with the profile.
     */
    @Column(name = "last_name", nullable = false, columnDefinition = "TEXT")
    @Size(min = 3, message = "Last name must be above 3 characters")
    @NotBlank(message = "Last name cannot be empty or null")
    private String lastName;

    /**
     * The gender associated with the profile.
     */
    @Column(name = "gender", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Gender must be an enum")
    private Gender gender = Gender.ANY;

    /**
     * The email address associated with the profile.
     */
    @Column(name = "email_address", unique = true, nullable = false, columnDefinition = "TEXT")
    @Email(message = "Email address must be properly formatted")
    private String emailAddress;

    /**
     * The URL of the profile picture.
     */
    @Column(name = "profile_picture", columnDefinition = "TEXT")
    private String avatar = null;

    /**
     * The rating of the profile, default is 5.0.
     */
    @Column(name = "rating", nullable = false)
    private Double rating = 5.0;

    /**
     * The FCM (Firebase Cloud Messaging) token associated with the profile.
     */
    @Column(name = "fcm_token", columnDefinition = "TEXT")
    private String fcmToken = null;
}
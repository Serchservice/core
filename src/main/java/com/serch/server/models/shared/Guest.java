package com.serch.server.models.shared;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.account.Gender;
import com.serch.server.generators.shared.GuestID;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;

/**
 * The Guest class represents guest profiles in a sharing platform.
 * It stores information such as email address, first name, last name, gender, avatar,
 * messaging token, platform, and shared links.
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Getter}</li>
 *     <li>{@link Setter}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Table}</li>
 * </ul>
 * Relationships:
 * <ul>
 *     <li>{@link SharedLink} - The shared links associated with the guest.</li>
 * </ul>
 * Constraints:
 * <ul>
 *     <li>{@link Email} - Email address validation.</li>
 *     <li>{@link Size} - Minimum size constraints for first name, last name, and gender.</li>
 *     <li>{@link NotBlank} - Not blank constraints for first name, last name, gender, and avatar.</li>
 * </ul>
 * Methods:
 * <ul>
 *     <li>{@link Guest#getFullName()} - Retrieves the full name of the guest by concatenating the first name and last name.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "sharing", name = "profiles")
public class Guest extends BaseDateTime {
    @Id
    @GenericGenerator(name = "guest_id_seq", type = GuestID.class)
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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String platform;

    @ManyToMany(mappedBy = "guests")
    private Set<SharedLink> sharedLinks;

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}
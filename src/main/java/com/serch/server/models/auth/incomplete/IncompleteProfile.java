package com.serch.server.models.auth.incomplete;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.account.Gender;
import com.serch.server.enums.auth.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The IncompleteProfile class represents incomplete user profiles in the system.
 * It stores information about incomplete user profiles,
 * including first name, last name, gender, business details, and more.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link Incomplete} - The incomplete object associated with the incomplete referral.</li>
 * </ul>
 * @see BaseModel
 */
@ToString
@Getter
@Setter
@Entity
@Table(schema = "identity", name = "incomplete_profile")
public class IncompleteProfile extends BaseModel {
    @Column(name = "first_name", nullable = false, columnDefinition = "TEXT")
    @Size(min = 3, message = "First name must be above 3 characters")
    @NotBlank(message = "First name cannot be empty or null")
    private String firstName;

    @Column(name = "last_name", nullable = false, columnDefinition = "TEXT")
    @Size(min = 3, message = "Last name must be above 3 characters")
    @NotBlank(message = "Last name cannot be empty or null")
    private String lastName;

    @Column(name = "gender", nullable = false, columnDefinition = "TEXT")
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Gender must be an enum")
    private Gender gender = Gender.ANY;

    @Column(name = "password", nullable = false, columnDefinition = "TEXT")
    private String password;

    @Column(name = "fcm_token", columnDefinition = "TEXT")
    private String fcmToken = null;

    @Column(name = "referral", columnDefinition = "TEXT")
    private String referralCode = null;

    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role = Role.USER;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "incomplete_email",
            referencedColumnName = "email_address",
            nullable = false,
            foreignKey = @ForeignKey(name = "profile_email_fkey")
    )
    @ToString.Exclude
    private Incomplete incomplete;
}

package com.serch.server.admin.models;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "admin", name = "organization")
public class Organization extends BaseModel {
    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Column(name = "email_address", unique = true, nullable = false, columnDefinition = "TEXT")
    @Email(
            message = "Email address must be properly formatted and end with @serchservice.com",
            regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@serchservice\\.com$"
    )
    private String emailAddress;

    @Column(name = "first_name", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @Column(name = "last_name", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

    @Column(nullable = false, name = "phone_number", columnDefinition = "TEXT")
    @NotBlank(message = "Phone number cannot be empty")
    private String phoneNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    @URL(message = "Avatar data must be a link")
    @NotBlank(message = "Avatar cannot be empty")
    private String avatar;

    @Column(nullable = false, columnDefinition = "TEXT")
    @URL(
            regexp = "^$|https?://(www\\.)?instagram\\.com/.*",
            message = "Instagram link must be a valid URL starting with https://instagram.com or be empty"
    )
    private String instagram = "";

    @Column(nullable = false, columnDefinition = "TEXT")
    @URL(
            regexp = "^$|https?://(www\\.)?(twitter\\.com|x\\.com)/.*",
            message = "Twitter link must be a valid URL starting with https://twitter.com, https://x.com, or be empty"
    )
    private String twitter = "";

    @Column(nullable = false, columnDefinition = "TEXT")
    @URL(
            regexp = "^$|https?://(www\\.)?linkedin\\.com/.*",
            message = "LinkedIn link must be a valid URL starting with https://linkedin.com or be empty"
    )
    private String linkedIn = "";

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Position in Serchservice cannot be empty")
    private String position;

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    @NotBlank(message = "Secret token cannot be empty")
    private String secret;

    @ManyToOne
    @JoinColumn(
            name = "added_by_id",
            referencedColumnName = "serch_id",
            updatable = false,
            foreignKey = @ForeignKey(name = "org_added_by_fkey")
    )
    private Admin admin;
}
package com.serch.server.bases;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BaseProfile extends BaseUser {
    @Column(name = "email_address", unique = true, nullable = false, columnDefinition = "TEXT")
    @Email(
            message = "Email address must be properly formatted",
            regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    )
    private String emailAddress;

    @Column(name = "referral_link", nullable = false, columnDefinition = "TEXT")
    @URL(message = "Referral Link must be a url link")
    @NotBlank(message = "Referral link cannot be empty or blank")
    private String referLink;

    @Column(name = "referral_code", nullable = false, columnDefinition = "TEXT")
    private String referralCode;

    @Column(name = "profile_picture", columnDefinition = "TEXT")
    private String avatar = null;

    @Column(name = "rating", nullable = false)
    private Double rating = 5.0;

    @Column(name = "number_of_ratings", nullable = false)
    private Integer numberOfRating = 0;

    @Column(name = "all_service_trips", nullable = false)
    private Integer totalServiceTrips = 0;

    @Column(name = "all_shared_trips", nullable = false)
    private Integer totalShared = 0;

    @Column(name = "fcm_token", columnDefinition = "TEXT")
    private String fcmToken = null;
}
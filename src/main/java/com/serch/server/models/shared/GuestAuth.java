package com.serch.server.models.shared;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * The GuestAuth class represents authentication information for guest profiles in a sharing platform.
 * It stores the email address, authentication token, expiration time, and confirmation time.
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Getter}</li>
 *     <li>{@link Setter}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Table}</li>
 * </ul>
 * Constraints:
 * <ul>
 *     <li>{@link Email} - Email address validation.</li>
 * </ul>
 * Methods:
 * <ul>
 *     <li>{@link GuestAuth#isTokenExpired()} - Checks if the authentication token is expired.</li>
 *     <li>{@link GuestAuth#isEmailConfirmed()} - Checks if the email address is confirmed.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "providesharing", name = "auth")
public class GuestAuth extends BaseModel {
    @Column(name = "email_address", nullable = false, columnDefinition = "TEXT")
    @Email(
            message = "Email address must be properly formatted",
            regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    )
    private String emailAddress;

    @Column(name = "token", columnDefinition = "TEXT")
    private String token;

    @Column(name = "expires_at")
    private LocalDateTime expiredAt = null;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt = null;

    public boolean isTokenExpired() {
        return expiredAt != null && expiredAt.isBefore(LocalDateTime.now());
    }
    public boolean isEmailConfirmed() {
        return confirmedAt != null;
    }
}

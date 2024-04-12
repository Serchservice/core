package com.serch.server.models.shared;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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

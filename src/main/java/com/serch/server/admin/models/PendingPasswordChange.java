package com.serch.server.admin.models;

import com.serch.server.bases.BaseDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "admin", name = "admin_pending_password_changes")
public class PendingPasswordChange extends BaseDateTime {
    @Id
    @Column(nullable = false)
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(
            name = "account_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "ppc_account_fkey")
    )
    private Admin account;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Password cannot be empty")
    private String password;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Secret cannot be empty")
    private String secret;

    @ManyToOne
    @JoinColumn(
            name = "admin_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "ppc_admin_fkey")
    )
    private Admin admin;
}

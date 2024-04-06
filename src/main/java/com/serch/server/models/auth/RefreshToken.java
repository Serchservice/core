package com.serch.server.models.auth;

import com.serch.server.bases.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(schema = "identity", name = "refresh_tokens")
public class RefreshToken extends BaseEntity {
    @Column(name = "token", unique = true, nullable = false)
    private String token;

    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RefreshToken> refreshTokens;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent",
            referencedColumnName = "token",
            foreignKey = @ForeignKey(
                    name = "refresh_token_parent_fkey"
            )
    )
    private RefreshToken parent = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "user_session_fkey")
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "session_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "refresh_token_session_fkey"
            )
    )
    private Session session;
}

package com.serch.server.models.auth;

import com.serch.server.bases.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The RefreshToken class represents a refresh token in the system.
 * It stores information about refresh tokens used for authentication.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link User} - The user associated with this refresh token.</li>
 *     <li>{@link Session} - The session associated with this refresh token.</li>
 * </ul>
 * Annotations:
 * <ul>
 *     <li>{@link Column}</li>
 *     <li>{@link OneToMany}</li>
 *     <li>{@link ManyToOne}</li>
 *     <li>{@link JoinColumn}</li>
 * </ul>
 * <p></p>
 * @see BaseEntity
 */
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

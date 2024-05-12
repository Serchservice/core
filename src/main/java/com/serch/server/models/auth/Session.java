package com.serch.server.models.auth;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDevice;
import com.serch.server.enums.auth.AuthLevel;
import com.serch.server.enums.auth.AuthMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * The Session class represents a user session in the system.
 * It stores information about the user's session, such as authentication level, method, and device details.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link User} - The user associated with this session.</li>
 *     <li>{@link RefreshToken} - The refresh tokens associated with this session.</li>
 * </ul>
 * Enums:
 * <ul>
 *     <li>{@link AuthMethod}</li>
 *     <li>{@link AuthLevel}</li>
 * </ul>
 * Annotations:
 * <ul>
 *     <li>{@link Column}</li>
 *     <li>{@link Enumerated}</li>
 *     <li>{@link JoinColumn}</li>
 * </ul>
 * @see BaseDevice
 * @see SerchEnum
 */
@Getter
@Setter
@Entity
@ToString
@Table(schema = "identity", name = "sessions")
public class Session extends BaseDevice {
    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "level", nullable = false)
    @SerchEnum(message = "AuthenticationLevel must be an enum")
    private AuthLevel authLevel = AuthLevel.LEVEL_1;

    @Column(name = "method", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "AuthenticationMethod must be an enum")
    private AuthMethod method = AuthMethod.TOKEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "user_session_fkey")
    )
    @ToString.Exclude
    private User user;

    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<RefreshToken> refreshTokens;
}
package com.serch.server.models.auth;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseEntity;
import com.serch.server.enums.auth.AuthLevel;
import com.serch.server.enums.auth.AuthMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(schema = "identity", name = "sessions")
public class Session extends BaseEntity {
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

    @Column(name = "platform", columnDefinition = "TEXT")
    private String platform = null;

    @Column(name = "device_name", columnDefinition = "TEXT", nullable = false)
    private String deviceName;

    @Column(name = "device_id", columnDefinition = "TEXT")
    private String deviceId = null;

    @Column(name = "device_address", columnDefinition = "TEXT")
    private String deviceIpAddress = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "user_session_fkey")
    )
    @ToString.Exclude
    private User user;

    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<RefreshToken> refreshTokens;
}

package com.serch.server.admin.models;

import com.serch.server.admin.enums.Permission;
import com.serch.server.admin.enums.PermissionScope;
import com.serch.server.admin.enums.PermissionStatus;
import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.utils.TimeUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "admin", name = "permission_requests")
public class RequestedPermission extends BaseModel {
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PermissionScope scope;

    @Column(columnDefinition = "TEXT")
    private String account;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Permission must be an enum")
    private Permission permission;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PermissionStatus status = PermissionStatus.PENDING;

    @Column(name = "expiration_period")
    private Long expirationPeriod;

    @Column(name = "granted_at", columnDefinition = "timestamptz")
    private ZonedDateTime grantedAt;

    @ManyToOne
    @JoinColumn(
            name = "requested_by",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "scope_admin_fkey")
    )
    private Admin requestedBy;

    @ManyToOne
    @JoinColumn(
            name = "updated_by",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "scope_updated_by_admin_fkey")
    )
    private Admin updatedBy;

    public boolean isGranted() {
        return getStatus() == PermissionStatus.APPROVED && grantedAt != null;
    }

    public boolean isExpired() {
        return TimeUtil.now().isAfter(getGrantedAt().plusDays(getExpirationPeriod()));
    }

    public String getExpirationTime(String timezone) {
        if (isGranted() && getExpirationPeriod() != null) {
            ZonedDateTime expiration = ZonedDateTime.of(grantedAt.plusDays(getExpirationPeriod()).toLocalDateTime(), TimeUtil.zoneId(timezone));
            ZonedDateTime now = ZonedDateTime.now(TimeUtil.zoneId(timezone));

            Duration remainingTime = Duration.between(now, expiration);
            long days = remainingTime.toDays();
            long hours = remainingTime.toHours() % 24;
            long minutes = remainingTime.toMinutes() % 60;

            StringBuilder msg = new StringBuilder();

            if (days > 0) {
                msg.append(" ").append(days);
            }
            if (hours > 0) {
                msg.append(days > 0 ? ":" : "").append(hours);
            }
            if (minutes > 0) {
                msg.append((days > 0 || hours > 0) ? ":" : "").append(minutes);
            }

            return msg.toString().trim();
        } else {
            return "";
        }
    }
}

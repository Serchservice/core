package com.serch.server.admin.models.permission;

import com.serch.server.admin.enums.Permission;
import com.serch.server.admin.enums.PermissionScope;
import com.serch.server.admin.enums.PermissionStatus;
import com.serch.server.admin.models.Admin;
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

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Permission must be an enum")
    private Permission permission;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PermissionStatus status = PermissionStatus.PENDING;

    @Column(name = "expiration_period", columnDefinition = "timestamptz")
    private ZonedDateTime expirationPeriod;

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

    public boolean isExpired(String timezone) {
        Duration remainingTime = getDuration(timezone);
        return remainingTime.isNegative() || remainingTime.isZero();
    }

    private Duration getDuration(String timezone) {
        ZonedDateTime now = ZonedDateTime.now(TimeUtil.zoneId(timezone)).withNano(0);
        ZonedDateTime expiration = getExpirationPeriod().withZoneSameInstant(TimeUtil.zoneId(timezone)).withNano(0);
        return Duration.between(now, expiration);
    }

    public String getExpirationTime(String timezone) {
        if (isGranted() && getExpirationPeriod() != null) {
            Duration remainingTime = getDuration(timezone);

            // Avoid negative durations
            if (remainingTime.isNegative() || remainingTime.isZero()) {
                return "Expired";
            }

            long days = remainingTime.toDays();
            long hours = remainingTime.toHours() % 24;
            long minutes = remainingTime.toMinutes() % 60;

            // Apply formatting rules
            StringBuilder msg = new StringBuilder();

            if (days > 0) {
                msg.append(days);
                msg.append(" : ").append(hours > 0 ? hours : "00").append(" : ").append(minutes > 0 ? minutes : "00");
            } else if (hours > 0) {
                msg.append(hours).append(" : ").append(minutes > 0 ? minutes : "00");
            } else {
                msg.append(minutes);
            }

            return msg.toString().trim();
        } else {
            return "";
        }
    }

    public boolean isPending() {
        return getStatus() == PermissionStatus.PENDING;
    }
}
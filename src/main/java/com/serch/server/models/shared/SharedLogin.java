package com.serch.server.models.shared;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.shared.UseStatus;
import com.serch.server.models.trip.Trip;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The SharedLogin class represents the login details for a shared link in a sharing platform.
 * It stores the login information of the guest whenever they use a shared link.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>Many-to-one with {@link Guest} as the guest.</li>
 *     <li>Many-to-one with {@link SharedLink} as the shared link.</li>
 *     <li>One-to-one with {@link Trip} as the trip.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "sharing", name = "logins")
public class SharedLogin extends BaseModel {
    @Column(name = "status", nullable = false, updatable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "UseStatus must be an enum")
    private UseStatus status = UseStatus.COUNT_1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "shared_link_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "shared_login_link_id_fkey")
    )
    private SharedLink sharedLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "guest_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "guest_login_id_fkey")
    )
    private Guest guest;

    @OneToMany(mappedBy = "sharedLogin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SharedStatus> statuses;

    public boolean isExpired() {
        return statuses != null && !statuses.isEmpty() && statuses.stream().allMatch(SharedStatus::isExpired);
    }

    public UseStatus nextUsageStatus() {
        if(statuses != null && !statuses.isEmpty()) {
            UseStatus latestStatus = statuses.getLast().getUseStatus();
            return latestStatus.next();
        } else {
            return UseStatus.COUNT_1;
        }
    }
}
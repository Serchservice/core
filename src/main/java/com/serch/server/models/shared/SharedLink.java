package com.serch.server.models.shared;

import com.serch.server.annotations.CoreID;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.shared.UseStatus;
import com.serch.server.exceptions.others.SharedException;
import com.serch.server.models.account.Profile;
import com.serch.server.models.trip.TripTimeline;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * The SharedLink class represents a shared link in a sharing platform.
 * It stores information such as the link status, URL, amount, and related profiles and guests.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>Many-to-one with {@link Profile} as the user.</li>
 *     <li>Many-to-one with {@link Profile} as the provider.</li>
 *     <li>Many-to-many with {@link Guest} as the guests.</li>
 *     <li>One-to-many with {@link SharedStatus} as the status.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "sharing", name = "links")
public class SharedLink extends BaseDateTime {
    @Id
    @CoreID(name = "shared_link_generator", prefix = "SSLINK", toUpperCase = true)
    @GeneratedValue(generator = "shared_link_seq")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String id;

    @Column(unique = true, nullable = false, columnDefinition = "TEXT")
    private String secret;

    @Column(nullable = false)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "shared_link_user_id_fkey")
    )
    private Profile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "provider_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "shared_link_provider_id_fkey")
    )
    private Profile provider;

    @OneToMany(mappedBy = "sharedLink", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SharedLogin> logins;

    @OneToMany(mappedBy = "sharedLink", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SharedStatus> statuses;

    public boolean isExpired() {
        return statuses != null && !statuses.isEmpty() && statuses.stream().anyMatch(shared -> shared.getUseStatus() == UseStatus.USED &&
                ((shared.getTrip().getTimelines() != null && shared.getTrip().getTimelines().stream().anyMatch(TripTimeline::isCompleted))
                || (shared.getTrip().getInvited() != null && shared.getTrip().getInvited().getTimelines() != null && shared.getTrip().getInvited().getTimelines().stream().anyMatch(TripTimeline::isCompleted))));
    }

    public boolean cannotLogin() {
        return (logins != null && !logins.isEmpty() && logins.stream().anyMatch(login -> login.getStatus() == UseStatus.USED))
                || isExpired();
    }

    public UseStatus nextLoginStatus() {
        if(logins != null && !logins.isEmpty()) {
            UseStatus latestStatus = logins.getLast().getStatus();
            return latestStatus.next();
        } else {
            return UseStatus.COUNT_1;
        }
    }

    public UseStatus status() {
        if(logins != null && !logins.isEmpty()) {
            return logins.getLast().getStatus();
        } else {
            return null;
        }
    }

    public boolean is(String emailAddress) {
        return user.getUser().getEmailAddress().equalsIgnoreCase(emailAddress)
                || provider.getUser().getEmailAddress().equalsIgnoreCase(emailAddress);
    }

    public void validate(String emailAddress) {
        if(is(emailAddress)) {
            throw new SharedException("This email address cannot use this link since it belongs to one of the shared identities in the link");
        }
    }
}
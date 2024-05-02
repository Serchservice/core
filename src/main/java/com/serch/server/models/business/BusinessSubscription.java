package com.serch.server.models.business;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * This contains the providers a business's subscription covers
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Getter}</li>
 *     <li>{@link Setter}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Table}</li>
 * </ul>
 * Relationships:
 * <ul>
 *     <li>Many-to-one with {@link Profile} as the profile.</li>
 *     <li>Many-to-one with {@link BusinessProfile} as the business.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "account", name = "business_subscriptions")
public class BusinessSubscription extends BaseModel {
    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "AuthStatus must be an enum")
    private AccountStatus status = AccountStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "associate_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "sub_profile_id_fkey")
    )
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "business_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "sub_business_id_fkey")
    )
    private BusinessProfile business;

    /**
     * Checks if subscription is active
     * @return true or false
     *
     * @see AccountStatus
     */
    public boolean isNotActive() {
        return getStatus() != AccountStatus.ACTIVE;
    }

    /**
     * Checks if the business admin has removed the account from a list of subscriptions
     * @return true or false
     *
     * @see AccountStatus
     */
    public boolean isSuspended() {
        return getStatus() == AccountStatus.SUSPENDED;
    }
}

package com.serch.server.models.account;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.shared.UseStatus;
import com.serch.server.generators.account.ReferralID;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

/**
 * Represents a referral entity, storing information about referrals made by users.
 * <p></p>
 * Relationships:
 * <ol>
 *     <li>{@link User} - One to one (The user who used the referral code}</li>
 *     <li>{@link User} - Many to one (The user whose referral code was used)</li>
 * </ol>
 * Enums:
 * <ol>
 *     <li>{@link UseStatus} - Status of the referral program</li>
 * </ol>
 * @see BaseDateTime
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "platform", name = "referrals")
public class Referral extends BaseDateTime {
    /**
     * The unique identifier for the referral.
     */
    @Id
    @GenericGenerator(name = "referral_seq", type = ReferralID.class)
    @GeneratedValue(generator = "referral_seq")
    @Column(name = "referral_id", nullable = false, columnDefinition = "TEXT")
    private String referId;

    /**
     * The status of the referral.
     */
    @Column(name = "referral_status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "UseStatus must be an enum")
    private UseStatus status = UseStatus.NOT_USED;

    /**
     * The user who made the referral.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "referred_by",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "referred_by_user_id_fkey")
    )
    private User referredBy;

    /**
     * The user who was referred.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "referral",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "referral_user_id_fkey")
    )
    private User referral;
}
package com.serch.server.models.referral;

import com.serch.server.bases.BaseDateTime;
import com.serch.server.bases.BaseEntity;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a referral program entity, showing the referral program the user is working with
 * <p></p>
 * Relationships:
 * <ol>
 *     <li>{@link User} - Many to one (The user)</li>
 * </ol>
 *
 * @see BaseDateTime
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "platform", name = "referral_programs")
public class ReferralProgram extends BaseEntity {
    /**
     * The referral code associated with the profile.
     */
    @Column(name = "referral_code", nullable = false, columnDefinition = "TEXT")
    private String referralCode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "refer_pro_user_id_fkey")
    )
    private User user;
}

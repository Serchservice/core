package com.serch.server.models.referral;

import com.serch.server.bases.BaseDateTime;
import com.serch.server.bases.BaseEntity;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

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
     * The referral link associated with the profile.
     */
    @Column(name = "referral_link", nullable = false, columnDefinition = "TEXT")
    @URL(message = "Referral Link must be a URL")
    @NotBlank(message = "Referral link cannot be empty or blank")
    private String referLink;

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

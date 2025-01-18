package com.serch.server.models.auth.incomplete;

import com.serch.server.bases.BaseModel;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * The IncompleteReferral class represents incomplete referrals in the system.
 * It stores information about incomplete referrals related to referring users and incomplete objects.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link User} - The user who referred the incomplete referral.</li>
 *     <li>{@link Incomplete} - The incomplete object associated with the incomplete referral.</li>
 * </ul>
 * @see BaseModel
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "identity", name = "incomplete_referrals")
public class IncompleteReferral extends BaseModel {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "referred_by",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "referred_by_user_id_fkey")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User referredBy;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "incomplete_email",
            referencedColumnName = "email_address",
            nullable = false,
            foreignKey = @ForeignKey(name = "incomplete_email_fkey")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Incomplete incomplete;
}

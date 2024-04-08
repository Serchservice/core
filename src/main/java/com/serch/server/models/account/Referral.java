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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "platform", name = "referrals")
public class Referral extends BaseDateTime {
    @Id
    @GenericGenerator(name = "referral_seq", type = ReferralID.class)
    @GeneratedValue(generator = "referral_seq")
    @Column(name = "referral_id", nullable = false, columnDefinition = "TEXT")
    private String referId;

    @Column(name = "referral_status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "UseStatus must be an enum")
    private UseStatus status = UseStatus.NOT_USED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "referred_by",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "referred_by_user_id_fkey")
    )
    private User referredBy;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "referral",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "referral_user_id_fkey")
    )
    private User referral;
}

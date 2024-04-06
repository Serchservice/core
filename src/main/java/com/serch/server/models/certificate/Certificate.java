package com.serch.server.models.certificate;

import com.serch.server.bases.BaseDateTime;
import com.serch.server.generators.CertificateID;
import com.serch.server.models.account.Profile;
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
@Table(schema = "platform", name = "certificates")
public class Certificate extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    @GenericGenerator(type = CertificateID.class, name = "account_del_seq")
    @GeneratedValue(generator = "account_del_seq")
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "total_trips", nullable = false)
    private Integer totalTrips = 0;

    @Column(name = "total_tip2fix", nullable = false)
    private Integer totalTip2Fix = 0;

    @Column(name = "average_rating", nullable = false)
    private Double averageRating = 5.0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "user_id_fkey")
    )
    private Profile profile;
}

package com.serch.server.models.shared;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.shared.UseStatus;
import com.serch.server.generators.shared.SharedLinkID;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(schema = "providesharing", name = "links")
public class SharedLink extends BaseDateTime {
    @Id
    @GenericGenerator(name = "shared_link_seq", type = SharedLinkID.class)
    @GeneratedValue(generator = "shared_link_seq")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String id;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "UseStatus must be an enum")
    private UseStatus status = UseStatus.NOT_USED;

    @Column(name = "link", unique = true, nullable = false, columnDefinition = "TEXT")
    @URL(message = "Shared Link must be formatted as a link")
    private String link;

    @Column(name = "amount", nullable = false)
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "shared_link_guest",
            joinColumns = @JoinColumn(
                    name = "shared_link_id",
                    nullable = false,
                    referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "guest_shared_link_id_fkey")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "guest_id",
                    nullable = false,
                    referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "guest_id_fkey")
            )
    )
    private Set<Guest> guests;

    @OneToMany(mappedBy = "sharedLink", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SharedPricing> pricing;
}

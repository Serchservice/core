package com.serch.server.models.shared;

import com.serch.server.bases.BaseDateTime;
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

/**
 * The SharedLink class represents a shared link in a sharing platform.
 * It stores information such as the link status, URL, amount, and related profiles and guests.
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Getter}</li>
 *     <li>{@link Setter}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Table}</li>
 * </ul>
 * Constraints:
 * <ul>
 *     <li>{@link URL} - Validates the link format.</li>
 * </ul>
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
    @GenericGenerator(name = "shared_link_seq", type = SharedLinkID.class)
    @GeneratedValue(generator = "shared_link_seq")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String id;

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
            schema = "sharing",
            inverseJoinColumns = @JoinColumn(
                    name = "guest_id",
                    nullable = false,
                    referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "guest_id_fkey")
            )
    )
    private Set<Guest> guests;

    @OneToMany(mappedBy = "sharedLink", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SharedStatus> statuses;
}
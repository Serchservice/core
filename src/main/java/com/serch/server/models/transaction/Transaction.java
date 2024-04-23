package com.serch.server.models.transaction;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.enums.transaction.TransactionType;
import com.serch.server.generators.transaction.TransactionID;
import com.serch.server.models.account.Profile;
import com.serch.server.models.conversation.Call;
import com.serch.server.models.trip.Trip;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

/**
 * The Transaction class represents a financial transaction in the platform schema.
 * It stores information such as amount, reference, account, status, type, and error reason.
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
 *     <li>One-to-one with {@link Trip} as the associated trip.</li>
 *     <li>Many-to-one with {@link Call} as the associated call.</li>
 *     <li>Many-to-one with {@link Profile} as the associated profile (associate).</li>
 *     <li>Many-to-one with {@link Wallet} as the sender wallet.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "platform", name = "transactions")
public class Transaction extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    @GenericGenerator(name = "transaction_gen", type = TransactionID.class)
    @GeneratedValue(generator = "transaction_gen")
    private String id;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(unique = true, nullable = false, columnDefinition = "TEXT")
    private String reference;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String account;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Transaction Status must be an enum")
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Transaction Type must be an enum")
    private TransactionType type = TransactionType.WITHDRAW;

    @Column(name = "error_reason", columnDefinition = "TEXT")
    private String reason;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "trip_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "trip_id_fkey")
    )
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "call_id",
            referencedColumnName = "channel",
            foreignKey = @ForeignKey(name = "call_id_fkey")
    )
    private Call call;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "associate_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "associate_id_fkey")
    )
    private Profile associate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "sender_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "sender_id_fkey")
    )
    private Wallet sender;
}

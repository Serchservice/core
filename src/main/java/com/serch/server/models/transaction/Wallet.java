package com.serch.server.models.transaction;

import com.serch.server.bases.BaseDateTime;
import com.serch.server.generators.transaction.WalletID;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.List;

/**
 * The Wallet class represents a user's wallet in the account schema.
 * It stores information such as account number, account name, bank name, deposit, balance, and uncleared amount.
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
 *     <li>One-to-one with {@link User} representing the user who owns the wallet.</li>
 *     <li>One-to-many with {@link Transaction} as transactions made from this wallet.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "account", name = "wallets")
public class Wallet extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    @GenericGenerator(name = "wallet_id_gen", type = WalletID.class)
    @GeneratedValue(generator = "wallet_id_gen")
    private String id;

    @Column(name = "account_number", columnDefinition = "TEXT")
    private String accountNumber = null;

    @Column(name = "account_name", columnDefinition = "TEXT")
    private String accountName = null;

    @Column(name = "bank_name", columnDefinition = "TEXT")
    private String bankName = null;

    @Column(name = "deposit", nullable = false)
    private BigDecimal deposit = BigDecimal.ZERO;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "uncleared", nullable = false)
    private BigDecimal uncleared = BigDecimal.ZERO;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "wallet_user_id_fkey")
    )
    private User user;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;
}

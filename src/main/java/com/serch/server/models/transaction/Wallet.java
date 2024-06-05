package com.serch.server.models.transaction;

import com.serch.server.bases.BaseDateTime;
import com.serch.server.generators.transaction.WalletID;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * The Wallet class represents a user's wallet in the account schema.
 * It stores information such as account number, account name, bank name, deposit, balance, and uncleared amount.
 * <p></p>
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

    @Column(nullable = false)
    private Integer payday = 3;

    @Column(nullable = false, name = "payout_on_payday")
    private Boolean payoutOnPayday = true;

    @Column(name = "payout", nullable = false)
    private BigDecimal payout = BigDecimal.ZERO;

    @Column(name = "last_payday", nullable = false)
    private LocalDate lastPayday = LocalDate.now();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "wallet_user_id_fkey")
    )
    private User user;

    public boolean hasBank() {
        return getAccountName() != null && !getAccountName().isEmpty()
                && getBankName() != null && !getBankName().isEmpty()
                && getAccountNumber() != null && !getAccountNumber().isEmpty();
    }
}

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

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "cleared_balance", nullable = false)
    private BigDecimal clearedBalance = BigDecimal.ZERO;

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

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> incomeList;
}

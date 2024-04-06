package com.serch.server.models.transaction;

import com.serch.server.bases.BaseModel;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(schema = "account", name = "wallets")
public class Wallet extends BaseModel {
    @Column(name = "account_number", columnDefinition = "TEXT")
    private String accountNumber = null;

    @Column(name = "account_name", columnDefinition = "TEXT")
    private String accountName = null;

    @Column(name = "bank_name", columnDefinition = "TEXT")
    private String bankName = null;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "withdrawable_amount", nullable = false)
    private BigDecimal withdrawableAmount = BigDecimal.ZERO;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "serch_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "wallet_serch_id_fkey")
    )
    private Profile profile = null;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "business_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "wallet_business_id_fkey")
    )
    private BusinessProfile businessProfile = null;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> incomeList;
}

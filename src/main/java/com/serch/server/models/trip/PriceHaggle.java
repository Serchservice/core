package com.serch.server.models.trip;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseEntity;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.models.shared.SharedStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(schema = "pricing", name = "haggles")
public class PriceHaggle extends BaseEntity {
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(unique = true, nullable = false, columnDefinition = "TEXT")
    private String reference;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Transaction Status must be an enum")
    private TransactionStatus status = TransactionStatus.PENDING;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "status_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "status_chat_id_fkey")
    )
    private SharedStatus sharedStatus;
}
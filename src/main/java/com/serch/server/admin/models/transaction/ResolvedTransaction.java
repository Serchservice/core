package com.serch.server.admin.models.transaction;

import com.serch.server.admin.models.Admin;
import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.models.transaction.Transaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "admin_action", name = "resolved_transactions")
public class ResolvedTransaction extends BaseModel {
    @OneToOne
    @JoinColumn(
            name = "transaction_id",
            referencedColumnName = "id",
            updatable = false,
            foreignKey = @ForeignKey(name = "resolved_transaction_id_fkey")
    )
    private Transaction transaction;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Transaction Status must be an enum")
    private TransactionStatus status = TransactionStatus.PENDING;

    @ManyToOne
    @JoinColumn(
            name = "resolved_by_id",
            referencedColumnName = "serch_id",
            updatable = false,
            foreignKey = @ForeignKey(name = "admin_transaction_resolved_by_fkey")
    )
    private Admin resolvedBy;
}
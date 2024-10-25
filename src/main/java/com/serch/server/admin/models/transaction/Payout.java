package com.serch.server.admin.models.transaction;

import com.serch.server.admin.models.Admin;
import com.serch.server.bases.BaseModel;
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
@Table(schema = "admin_action", name = "payouts")
public class Payout extends BaseModel {
    @OneToOne
    @JoinColumn(
            name = "transaction_id",
            referencedColumnName = "id",
            updatable = false,
            foreignKey = @ForeignKey(name = "payout_transaction_id_fkey")
    )
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(
            name = "payout_by_id",
            referencedColumnName = "serch_id",
            updatable = false,
            foreignKey = @ForeignKey(name = "admin_transaction_payout_by_fkey")
    )
    private Admin payoutBy;
}
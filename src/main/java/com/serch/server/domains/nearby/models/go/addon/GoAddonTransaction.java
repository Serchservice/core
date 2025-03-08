package com.serch.server.domains.nearby.models.go.addon;

import com.serch.server.annotations.CoreID;
import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.enums.transaction.TransactionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(schema = "nearby", name = "go_addon_transactions")
public class GoAddonTransaction extends BaseDateTime {
    @Id
    @CoreID(prefix = "NET", end = 7, replaceSymbols = true)
    @Column(nullable = false, columnDefinition = "TEXT", updatable = false)
    @GeneratedValue(generator = "addon_transaction_gen")
    private String id;

    @Column(unique = true, nullable = false, columnDefinition = "TEXT", updatable = false)
    private String reference;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Transaction Status must be an enum")
    private TransactionStatus status = TransactionStatus.PENDING;

    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(
            name = "addon_plan_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "go_addon_transaction_addon_plan_id_fkey")
    )
    private GoAddonPlan plan;

    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "go_addon_transaction_user_id_fkey")
    )
    private GoUser user;
}
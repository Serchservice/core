package com.serch.server.models.auth;

import com.serch.server.bases.BaseModel;
import com.serch.server.enums.account.AccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "identity", name = "account_status_trackers")
public class AccountStatusTracker extends BaseModel {
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(
            name = "serch_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "acc_user_id_fkey")
    )
    private User user;
}
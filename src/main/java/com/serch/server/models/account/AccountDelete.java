package com.serch.server.models.account;

import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.generators.AccountDeleteID;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Entity
@Table(schema = "account", name = "delete_requests")
public class AccountDelete extends BaseDateTime {
    @Id
    @GenericGenerator(type = AccountDeleteID.class, name = "acct_del_seq")
    @GeneratedValue(generator = "acct_del_seq")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String id;

    @Enumerated(value = EnumType.STRING)
    private IssueStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "serch_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "user_id_fkey")
    )
    private User user;
}

package com.serch.server.models.account;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.generators.account.AccountReportID;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@Entity
@Table(schema = "account", name = "reported_accounts")
public class AccountReport extends BaseDateTime {
    @Id
    @GenericGenerator(name = "report_ticket", type = AccountReportID.class)
    @GeneratedValue(generator = "report_ticket")
    @Column(name = "ticket", nullable = false, columnDefinition = "TEXT")
    private String ticket;

    @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(name = "status", nullable = false)
    @SerchEnum(message = "ReportStatus must be an enum")
    private IssueStatus status = IssueStatus.OPENED;

    @Column(nullable = false)
    private String account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "user_id_fkey")
    )
    private User user;
}

package com.serch.server.models.account;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.generators.account.AccountReportID;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reporter_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "reporter_id_fkey")
    )
    private Profile reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reported_id",
            nullable = false,
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "reported_id_fkey")
    )
    private Profile reported;
}

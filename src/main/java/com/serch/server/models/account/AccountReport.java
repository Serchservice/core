package com.serch.server.models.account;

import com.serch.server.annotations.CoreID;
import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an account report entity, storing information about reported accounts.
 * Relationships:
 * <ol>
 *     <li>{@link User} - One to one</li>
 * </ol>
 * Enums:
 * <ol>
 *     <li>{@link IssueStatus} - Status of the report request</li>
 * </ol>
 *
 * @see BaseDateTime
 * @see SerchEnum
 */
@Getter
@Setter
@Entity
@Table(schema = "account", name = "reported_accounts")
public class AccountReport extends BaseDateTime {
    /**
     * The unique ticket identifier for the report.
     */
    @Id
    @CoreID(name = "report_generator", prefix = "SREP")
    @GeneratedValue(generator = "report_ticket")
    @Column(name = "ticket", nullable = false, columnDefinition = "TEXT")
    private String ticket;

    /**
     * The comment associated with the report.
     */
    @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
    private String comment;

    /**
     * The status of the report.
     */
    @Column(name = "status", nullable = false)
    @SerchEnum(message = "ReportStatus must be an enum")
    private IssueStatus status = IssueStatus.OPENED;

    /**
     * The account being reported.
     */
    @Column(nullable = false)
    private String account;

    /**
     * The user who reported the account.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "user_id_fkey")
    )
    private User user;
}
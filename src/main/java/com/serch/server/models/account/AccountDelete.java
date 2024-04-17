package com.serch.server.models.account;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.generators.account.AccountDeleteID;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

/**
 * Represents an account deletion request entity, storing information about the user requesting account deletion.
 * <p></p>
 * Relationships:
 * <ol>
 *     <li>{@link User} - One to one</li>
 * </ol>
 * Enums:
 * <ol>
 *     <li>{@link IssueStatus} - Status of the deletion request</li>
 * </ol>
 *
 * @see BaseDateTime
 * @see SerchEnum
 */
@Getter
@Setter
@Entity
@Table(schema = "account", name = "delete_requests")
public class AccountDelete extends BaseDateTime {
    /**
     * The unique identifier for the delete request.
     */
    @Id
    @GenericGenerator(type = AccountDeleteID.class, name = "acct_del_seq")
    @GeneratedValue(generator = "acct_del_seq")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String id;

    /**
     * The status of the deletion request.
     */
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Status must be an enum")
    private IssueStatus status;

    /**
     * The user associated with the deletion request.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "serch_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "user_id_fkey")
    )
    private User user;
}
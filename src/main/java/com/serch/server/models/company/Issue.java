package com.serch.server.models.company;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.generators.IssueTicketID;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

/**
 * The Issue class represents an issue entity in the system.
 * It stores information about issues, including the ticket ID, comment, status, associated product, and user.
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Id}</li>
 *     <li>{@link Column}</li>
 *     <li>{@link Enumerated}</li>
 *     <li>{@link ManyToOne}</li>
 *     <li>{@link JoinColumn}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Table}</li>
 * </ul>
 * Enums:
 * <ul>
 *     <li>{@link IssueStatus}</li>
 * </ul>
 * Relationships:
 * <ul>
 *     <li>{@link Product} - The product associated with the issue.</li>
 *     <li>{@link User} - The user who reported the issue.</li>
 * </ul>
 * @see BaseDateTime
 * @see SerchEnum
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "company", name = "issues")
public class Issue extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    @GenericGenerator(name = "issue_seq", type = IssueTicketID.class)
    @GeneratedValue(generator = "issue_seq")
    private String ticket;

    @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Comment cannot be empty")
    private String comment;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "IssueStatus must be an enum")
    private IssueStatus status = IssueStatus.OPENED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "product_id_fkey")
    )
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "user_id_fkey")
    )
    private User user;
}

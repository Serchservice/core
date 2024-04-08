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

package com.serch.server.models.company;

import com.serch.server.admin.models.Admin;
import com.serch.server.annotations.CoreID;
import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.company.IssueStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class is the record for any complaints about Serch platform or website, even product.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "company", name = "complaints")
public class Complaint extends BaseDateTime {
    @Id
    @CoreID(name = "complaint_generator", prefix = "SCIM", replaceSymbols = true, end = 6)
    @Column(nullable = false, columnDefinition = "TEXT")
    @GeneratedValue(generator = "complaint_seq")
    private String id;

    @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Comment cannot be empty")
    private String comment;

    @Column(name = "first_name", nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Sender cannot be empty")
    private String firstName;

    @Column(name = "last_name", nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Sender cannot be empty")
    private String lastName;

    @Column(name = "email_address", nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Sender cannot be empty")
    private String emailAddress;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "IssueStatus must be an enum")
    private IssueStatus status = IssueStatus.OPENED;

    @ManyToOne
    @JoinColumn(
            name = "admin_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "complaint_admin_fkey")
    )
    private Admin admin;
}
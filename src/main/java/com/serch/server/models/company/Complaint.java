package com.serch.server.models.company;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.models.auth.User;
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
public class Complaint extends BaseModel {
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
}
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
 * The Issue class represents an issue entity in the system.
 * It stores information about issues, including the ticket ID, comment, status, associated product, and user.
 * <p></p>
 * Enums:
 * <ul>
 *     <li>{@link IssueStatus}</li>
 * </ul>
 * Relationships:
 * <ul>
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
public class Issue extends BaseModel {
    @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Comment cannot be empty")
    private String comment;

    @Column(name = "sender", nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Sender cannot be empty")
    private String sender;

    @Column(nullable = false, name = "is_read")
    private Boolean isRead = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "speak_with_serch_ticket",
            referencedColumnName = "ticket",
            foreignKey = @ForeignKey(name = "user_id_fkey")
    )
    private SpeakWithSerch speakWithSerch;
}
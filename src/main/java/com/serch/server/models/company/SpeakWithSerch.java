package com.serch.server.models.company;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.generators.IssueTicketID;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

/**
 * This handles all Speak With Serch Tickets
 * <p></p>
 * Enums:
 * <ul>
 *     <li>{@link IssueStatus}</li>
 * </ul>
 * Relationships:
 * <ul>
 *     <li>{@link User} - The user who reported the issue.</li>
 * </ul>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "company", name = "speak_with_serch")
public class SpeakWithSerch extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    @GenericGenerator(name = "issue_seq", type = IssueTicketID.class)
    @GeneratedValue(generator = "issue_seq")
    private String ticket;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "IssueStatus must be an enum")
    private IssueStatus status = IssueStatus.OPENED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "user_id_fkey")
    )
    private User user;

    @OneToMany(mappedBy = "speakWithSerch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Issue> issue;
}
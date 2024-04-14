package com.serch.server.models.help;

import com.serch.server.bases.BaseDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

/**
 * The HelpAsk class represents user inquiries or questions submitted to the help system.
 * It stores information about the user's full name, email address, and their inquiry or comment.
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Getter}</li>
 *     <li>{@link Setter}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Table}</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(name = "help_ask", schema = "company")
public class HelpAsk extends BaseDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(columnDefinition = "TEXT", nullable = false, name = "full_name")
    private String fullName;

    @Column(columnDefinition = "TEXT", nullable = false, name = "email_address")
    private String emailAddress;

    @Column(columnDefinition = "TEXT", nullable = false, name = "comment")
    private String comment;
}

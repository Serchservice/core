package com.serch.server.services.help.models;

import com.serch.server.bases.BaseDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

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

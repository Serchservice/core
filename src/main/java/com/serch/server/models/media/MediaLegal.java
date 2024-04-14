package com.serch.server.models.media;

import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.media.LegalLOB;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The MediaLegal class represents legal documents stored in the system.
 * It stores information about the document ID, image, content, title, and line of business (LOB).
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Getter}</li>
 *     <li>{@link Setter}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Table}</li>
 * </ul>
 * Relationships: None
 * Enums:
 * <ul>
 *     <li>{@link LegalLOB}</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "company", name = "legal_documents")
public class MediaLegal extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String image;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String legal;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, name = "line_of_business")
    private LegalLOB lob;
}

package com.serch.server.models.bookmark;

import com.serch.server.annotations.CoreID;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The Bookmark class represents a bookmark entity in the system.
 * It stores information about bookmarks, including the bookmark ID, user, and provider.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link Profile} - The user associated with the bookmark.</li>
 *     <li>{@link Profile} - The provider associated with the bookmark.</li>
 * </ul>
 * @see BaseDateTime
 */
@Getter
@Setter
@Entity
@Table(schema = "platform", name = "bookmarks")
public class Bookmark extends BaseDateTime {
    @Id
    @CoreID(name = "bookmark_generator", prefix = "SBKM")
    @Column(nullable = false, columnDefinition = "TEXT")
    @GeneratedValue(generator = "bookmark_seq")
    private String bookmarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "bookmark_user_fkey")
    )
    private Profile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "provider_id",
            nullable = false,
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "bookmark_provider_fkey")
    )
    private Profile provider;
}

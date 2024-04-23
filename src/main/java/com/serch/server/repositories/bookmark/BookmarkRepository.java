package com.serch.server.repositories.bookmark;

import com.serch.server.models.bookmark.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, String> {
    Optional<Bookmark> findByUser_IdAndProvider_Id(@NonNull UUID serchId, @NonNull UUID serchId1);
    Optional<Bookmark> findByBookmarkIdAndUser_Id(@NonNull String bookmarkId, @NonNull UUID serchId);
    @Query("select b from Bookmark b where b.user.id = ?1 or b.provider.id = ?1 or b.provider.business.id = ?1")
    List<Bookmark> findByUserId(@NonNull UUID serchId);
}
package com.serch.server.repositories.bookmark;

import com.serch.server.models.bookmark.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, String> {
    Optional<Bookmark> findByUser_SerchIdAndProvider_SerchId(@NonNull UUID serchId, @NonNull UUID serchId1);
    Optional<Bookmark> findByBookmarkIdAndUser_SerchId(@NonNull String bookmarkId, @NonNull UUID serchId);
    @Query("select b from Bookmark b where b.user.serchId = ?1 or b.provider.serchId = ?1")
    List<Bookmark> findBySerchId(@NonNull UUID serchId);
}
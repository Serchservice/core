package com.serch.server.core.file.data.repositories;

import com.serch.server.core.file.data.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {
    @Query("select f from FileUpload f where f.assetId = ?1 or f.publicId = ?1")
    Optional<FileUpload> findByAssetIdOrPublicId(@NonNull String id);

    @Query("select count(f) > 0 from FileUpload f where f.publicId = ?1")
    boolean existsByPublicId(@NonNull String id);

    @Query("select count(f) > 0 from FileUpload f where f.assetId = ?1")
    boolean existsByAssetId(@NonNull String id);
}
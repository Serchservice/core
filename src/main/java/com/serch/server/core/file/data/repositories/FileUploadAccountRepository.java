package com.serch.server.core.file.data.repositories;

import com.serch.server.core.file.data.FileUploadAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileUploadAccountRepository extends JpaRepository<FileUploadAccount, Long> {
}
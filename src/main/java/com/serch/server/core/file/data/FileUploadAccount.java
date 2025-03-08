package com.serch.server.core.file.data;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "file_upload_accounts")
public class FileUploadAccount extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String account;

    @Column(columnDefinition = "TEXT")
    private String type;
}
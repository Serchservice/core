package com.serch.server.core.file.data;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(name = "file_uploads")
public class FileUpload extends BaseModel {
    @Column(columnDefinition = "TEXT")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String type;

    @Column(columnDefinition = "TEXT")
    private String file;

    @Column(columnDefinition = "TEXT")
    private String duration;

    @Column(columnDefinition = "TEXT")
    private String size;

    @Column(name = "asset_id", columnDefinition = "TEXT")
    private String assetId;

    @Column(name = "display_name", columnDefinition = "TEXT")
    private String displayName;

    private Integer height;
    private Integer width;

    @Column(name = "resource_type", columnDefinition = "TEXT")
    private String resourceType;

    @Column(columnDefinition = "TEXT")
    private String folder;

    @Column(name = "public_id", columnDefinition = "TEXT")
    private String publicId;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "account_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "file_upload_account_fkey"
            )
    )
    private FileUploadAccount account;
}
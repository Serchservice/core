package com.serch.server.domains.linked.models;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "dynamic_urls")
public class LinkedDynamicUrl extends BaseModel {
    @NotBlank(message = "Redirect url cannot be empty or null")
    @Column(nullable = false, name = "redirect_url", updatable = false, columnDefinition = "TEXT")
    private String redirectUrl;

    @NotBlank(message = "Identifier cannot be empty or null")
    @Column(nullable = false, unique = true, updatable = false, columnDefinition = "TEXT")
    private String identifier;

    @NotBlank(message = "Android bundle id cannot be empty or null")
    @Column(nullable = false, name = "android_bundle_id", columnDefinition = "TEXT")
    private String androidBundleId;

    @NotBlank(message = "iOS bundle id cannot be empty or null")
    @Column(nullable = false, name = "ios_bundle_id", columnDefinition = "TEXT")
    private String iosBundleId;

    @NotBlank(message = "Android scheme cannot be empty or null")
    @Column(nullable = false, name = "android_scheme", columnDefinition = "TEXT")
    private String androidScheme;

    @NotBlank(message = "iOS scheme cannot be empty or null")
    @Column(nullable = false, name = "ios_scheme", columnDefinition = "TEXT")
    private String iosScheme;

    @NotBlank(message = "Link title cannot be empty or null")
    @Column(nullable = false, updatable = false, columnDefinition = "TEXT")
    private String title;

    @NotBlank(message = "Link description cannot be empty or null")
    @Column(nullable = false, updatable = false, columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Link content cannot be empty or null")
    @Column(nullable = false, name = "content_url", updatable = false, columnDefinition = "TEXT")
    private String contentUrl;

    @NotBlank(message = "Context cannot be empty or null")
    @Column(nullable = false, updatable = false, columnDefinition = "TEXT")
    private String context;

    @Column(nullable = false, name = "redirect_to_store")
    private Boolean redirectToStore = true;

    public boolean isNearby() {
        return getContext().equalsIgnoreCase("nearby");
    }
}
package com.serch.server.domains.linked.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkedDynamicUrlDto {
    @NotBlank(message = "Redirect URL cannot be empty or null")
    @JsonProperty(value = "redirect_url", required = true)
    private String redirectUrl;

    @NotBlank(message = "Short URL cannot be empty or null")
    @JsonProperty(value = "short_url", required = true)
    private String identifier;

    @NotBlank(message = "Android bundle id cannot be empty or null")
    @JsonProperty(value = "android_bundle_id", required = true)
    private String androidBundleId;

    @NotBlank(message = "iOS bundle id cannot be empty or null")
    @JsonProperty(value = "ios_bundle_id", required = true)
    private String iosBundleId;

    @NotBlank(message = "Android scheme cannot be empty or null")
    @JsonProperty(value = "android_scheme", required = true)
    private String androidScheme;

    @NotBlank(message = "iOS scheme cannot be empty or null")
    @JsonProperty(value = "ios_scheme", required = true)
    private String iosScheme;

    @NotBlank(message = "Title cannot be empty or null")
    @JsonProperty(value = "title", required = true)
    private String title;

    @NotBlank(message = "Description cannot be empty or null")
    @JsonProperty(value = "description", required = true)
    private String description;

    @NotBlank(message = "Content URL cannot be empty or null")
    @JsonProperty(value = "content_url", required = true)
    private String contentUrl;

    @NotBlank(message = "Context of the url cannot be empty or null")
    @JsonProperty(value = "context", required = true)
    private String context;

    @JsonProperty(value = "redirect_to_store", required = true)
    private Boolean redirectToStore = true;

    public static LinkedDynamicUrlDto nearby(String op, String id, String title, String description, String contentUrl) {
        LinkedDynamicUrlDto dto = new LinkedDynamicUrlDto();
        dto.setContentUrl(contentUrl);
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setAndroidScheme("usenearby://");
        dto.setIosScheme("usenearby://");
        dto.setIosBundleId("com.serchservice.drive");
        dto.setAndroidBundleId("com.serchservice.drive");
        dto.setRedirectToStore(true);
        dto.setRedirectUrl("https://nearby.serchservice.com/%s/%s".formatted(op, id));
        dto.setIdentifier(id);
        dto.setContext("Nearby");

        return dto;
    }
}

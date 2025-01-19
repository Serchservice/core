package com.serch.server.domains.bookmark.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
public class AddBookmarkRequest {
    private UUID user;
}
